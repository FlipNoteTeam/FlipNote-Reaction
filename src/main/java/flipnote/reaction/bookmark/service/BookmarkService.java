package flipnote.reaction.bookmark.service;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cardset.Cardset;
import cardset.Cardset.CardSetSummary;
import flipnote.reaction.bookmark.entity.Bookmark;
import flipnote.reaction.bookmark.entity.BookmarkTargetType;
import flipnote.reaction.bookmark.exception.BookmarkErrorCode;
import flipnote.reaction.bookmark.model.request.BookmarkSearchRequest;
import flipnote.reaction.bookmark.model.response.BookmarkResponse;
import flipnote.reaction.bookmark.repository.BookmarkRepository;
import flipnote.reaction.common.config.RabbitMqConfig;
import flipnote.reaction.common.event.ReactionEventPublisher;
import flipnote.reaction.common.exception.BizException;
import flipnote.reaction.common.exception.CommonErrorCode;
import flipnote.reaction.common.grpc.CardSetGrpcClient;
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
	private final ReactionEventPublisher eventPublisher;
	private final CardSetGrpcClient cardSetGrpcClient;

	@Transactional
	public IdResponse addBookmark(BookmarkTargetType targetType, Long targetId, Long userId) {
		if (targetType == BookmarkTargetType.CARD_SET) {
			if (!cardSetGrpcClient.isCardSetViewable(targetId, userId)) {
				throw new BizException(CommonErrorCode.TARGET_NOT_VIEWABLE);
			}
		}

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

		eventPublisher.publish(RabbitMqConfig.ROUTING_KEY_BOOKMARK_ADDED, "BOOKMARK_ADDED",
			targetType.name(), targetId, userId);

		return IdResponse.from(bookmark.getId());
	}

	@Transactional
	public void removeBookmark(BookmarkTargetType targetType, Long targetId, Long userId) {
		Bookmark bookmark = bookmarkReader.findByTargetAndUserId(targetType, targetId, userId);
		bookmarkRepository.delete(bookmark);

		eventPublisher.publish(RabbitMqConfig.ROUTING_KEY_BOOKMARK_REMOVED, "BOOKMARK_REMOVED",
			targetType.name(), targetId, userId);
	}

	public PagingResponse<BookmarkResponse> getBookmarks(BookmarkTargetType targetType, Long userId,
		BookmarkSearchRequest request) {
		Page<Bookmark> bookmarkPage = bookmarkRepository.findByTargetTypeAndUserId(
			targetType, userId, request.getPageRequest()
		);

		List<Long> targetIds = bookmarkPage.getContent().stream()
			.map(Bookmark::getTargetId)
			.toList();

		Map<Long, CardSetSummary> summaryMap = targetIds.isEmpty()
			? Map.of()
			: cardSetGrpcClient.getCardSetsByIds(targetIds, userId);

		Page<BookmarkResponse> responsePage = bookmarkPage.map(
			bookmark -> BookmarkResponse.from(bookmark, summaryMap.get(bookmark.getTargetId()))
		);
		return PagingResponse.from(responsePage);
	}
}
