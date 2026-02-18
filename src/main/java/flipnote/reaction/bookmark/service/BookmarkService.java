package flipnote.reaction.bookmark.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import flipnote.reaction.bookmark.entity.Bookmark;
import flipnote.reaction.bookmark.entity.BookmarkTargetType;
import flipnote.reaction.bookmark.exception.BookmarkErrorCode;
import flipnote.reaction.bookmark.model.request.BookmarkSearchRequest;
import flipnote.reaction.bookmark.model.response.BookmarkResponse;
import flipnote.reaction.bookmark.repository.BookmarkRepository;
import flipnote.reaction.common.config.RabbitMqConfig;
import flipnote.reaction.common.exception.BizException;
import flipnote.reaction.common.model.event.ReactionMessage;
import flipnote.reaction.common.model.response.IdResponse;
import flipnote.reaction.common.model.response.PagingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

	private final BookmarkRepository bookmarkRepository;
	private final BookmarkReader bookmarkReader;
	private final RabbitTemplate rabbitTemplate;

	@Transactional
	public IdResponse addBookmark(BookmarkTargetType targetType, Long targetId, Long userId) {
		// TODO: gRPC로 대상 존재 여부 검증 (CardSet/Group 서비스 호출)

		if (bookmarkReader.isBookmarked(targetType, targetId, userId)) {
			throw new BizException(BookmarkErrorCode.ALREADY_BOOKMARKED);
		}

		Bookmark bookmark = Bookmark.builder()
			.targetType(targetType)
			.targetId(targetId)
			.userId(userId)
			.build();

		try {
			bookmarkRepository.save(bookmark);
		} catch (DataIntegrityViolationException e) {
			throw new BizException(BookmarkErrorCode.ALREADY_BOOKMARKED);
		}

		publishEvent(RabbitMqConfig.ROUTING_KEY_BOOKMARK_ADDED, "BOOKMARK_ADDED", targetType, targetId, userId);

		return IdResponse.from(bookmark.getId());
	}

	@Transactional
	public void removeBookmark(BookmarkTargetType targetType, Long targetId, Long userId) {
		Bookmark bookmark = bookmarkReader.findByTargetAndUserId(targetType, targetId, userId);
		bookmarkRepository.delete(bookmark);

		publishEvent(RabbitMqConfig.ROUTING_KEY_BOOKMARK_REMOVED, "BOOKMARK_REMOVED", targetType, targetId, userId);
	}

	public PagingResponse<BookmarkResponse> getBookmarks(BookmarkTargetType targetType, Long userId,
		BookmarkSearchRequest request) {
		Page<Bookmark> bookmarkPage = bookmarkRepository.findByTargetTypeAndUserId(
			targetType, userId, request.getPageRequest()
		);

		// TODO: gRPC로 대상 상세 정보 fetch (CardSet 서비스 호출)

		Page<BookmarkResponse> responsePage = bookmarkPage.map(BookmarkResponse::from);
		return PagingResponse.from(responsePage);
	}

	private void publishEvent(String routingKey, String eventType,
		BookmarkTargetType targetType, Long targetId, Long userId) {
		try {
			rabbitTemplate.convertAndSend(
				RabbitMqConfig.EXCHANGE,
				routingKey,
				new ReactionMessage(eventType, targetType.name(), targetId, userId)
			);
		} catch (Exception e) {
			log.error("북마크 이벤트 발행 실패: eventType={}, targetType={}, targetId={}, userId={}",
				eventType, targetType, targetId, userId, e);
		}
	}
}
