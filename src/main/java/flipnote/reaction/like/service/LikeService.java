package flipnote.reaction.like.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import flipnote.reaction.common.config.RabbitMqConfig;
import flipnote.reaction.common.exception.BizException;
import flipnote.reaction.common.model.event.ReactionMessage;
import flipnote.reaction.common.model.response.IdResponse;
import flipnote.reaction.common.model.response.PagingResponse;
import flipnote.reaction.like.entity.Like;
import flipnote.reaction.like.entity.LikeTargetType;
import flipnote.reaction.like.exception.LikeErrorCode;
import flipnote.reaction.like.model.request.LikeSearchRequest;
import flipnote.reaction.like.model.response.LikeResponse;
import flipnote.reaction.like.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

	private final LikeRepository likeRepository;
	private final LikeReader likeReader;
	private final RabbitTemplate rabbitTemplate;

	@Transactional
	public IdResponse addLike(LikeTargetType targetType, Long targetId, Long userId) {
		// TODO: gRPC로 대상 존재 여부 검증 (CardSet/Group 서비스 호출)

		if (likeReader.isLiked(targetType, targetId, userId)) {
			throw new BizException(LikeErrorCode.ALREADY_LIKED);
		}

		Like like = Like.builder()
			.targetType(targetType)
			.targetId(targetId)
			.userId(userId)
			.build();

		try {
			likeRepository.save(like);
		} catch (DataIntegrityViolationException e) {
			throw new BizException(LikeErrorCode.ALREADY_LIKED);
		}

		publishEvent(RabbitMqConfig.ROUTING_KEY_LIKE_ADDED, "LIKE_ADDED", targetType, targetId, userId);

		return IdResponse.from(like.getId());
	}

	@Transactional
	public void removeLike(LikeTargetType targetType, Long targetId, Long userId) {
		Like like = likeReader.findByTargetAndUserId(targetType, targetId, userId);
		likeRepository.delete(like);

		publishEvent(RabbitMqConfig.ROUTING_KEY_LIKE_REMOVED, "LIKE_REMOVED", targetType, targetId, userId);
	}

	public PagingResponse<LikeResponse> getLikes(LikeTargetType targetType, Long userId, LikeSearchRequest request) {
		Page<Like> likePage = likeRepository.findByTargetTypeAndUserId(
			targetType, userId, request.getPageRequest()
		);

		// TODO: gRPC로 대상 상세 정보 fetch (CardSet 서비스 호출)

		Page<LikeResponse> responsePage = likePage.map(LikeResponse::from);
		return PagingResponse.from(responsePage);
	}

	private void publishEvent(String routingKey, String eventType,
		LikeTargetType targetType, Long targetId, Long userId) {
		try {
			rabbitTemplate.convertAndSend(
				RabbitMqConfig.EXCHANGE,
				routingKey,
				new ReactionMessage(eventType, targetType.name(), targetId, userId)
			);
		} catch (Exception e) {
			log.error("좋아요 이벤트 발행 실패: eventType={}, targetType={}, targetId={}, userId={}",
				eventType, targetType, targetId, userId, e);
		}
	}
}
