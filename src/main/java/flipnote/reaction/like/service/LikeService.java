package flipnote.reaction.like.service;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cardset.Cardset.CardSetSummary;
import flipnote.reaction.common.config.RabbitMqConfig;
import flipnote.reaction.common.event.ReactionEventPublisher;
import flipnote.reaction.common.exception.BizException;
import flipnote.reaction.common.exception.CommonErrorCode;
import flipnote.reaction.common.grpc.CardSetGrpcClient;
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
	private final ReactionEventPublisher eventPublisher;
	private final CardSetGrpcClient cardSetGrpcClient;

	@Transactional
	public IdResponse addLike(LikeTargetType targetType, Long targetId, Long userId) {
		if (targetType == LikeTargetType.CARD_SET) {
			if (!cardSetGrpcClient.isCardSetViewable(targetId, userId)) {
				throw new BizException(CommonErrorCode.TARGET_NOT_VIEWABLE);
			}
		}

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

		eventPublisher.publish(RabbitMqConfig.ROUTING_KEY_LIKE_ADDED, "LIKE_ADDED",
			targetType.name(), targetId, userId);

		return IdResponse.from(like.getId());
	}

	@Transactional
	public void removeLike(LikeTargetType targetType, Long targetId, Long userId) {
		Like like = likeReader.findByTargetAndUserId(targetType, targetId, userId);
		likeRepository.delete(like);

		eventPublisher.publish(RabbitMqConfig.ROUTING_KEY_LIKE_REMOVED, "LIKE_REMOVED",
			targetType.name(), targetId, userId);
	}

	public PagingResponse<LikeResponse> getLikes(LikeTargetType targetType, Long userId, LikeSearchRequest request) {
		Page<Like> likePage = likeRepository.findByTargetTypeAndUserId(
			targetType, userId, request.getPageRequest()
		);

		List<Long> targetIds = likePage.getContent().stream()
			.map(Like::getTargetId)
			.toList();

		Map<Long, CardSetSummary> summaryMap = targetIds.isEmpty()
			? Map.of()
			: cardSetGrpcClient.getCardSetsByIds(targetIds, userId);

		Page<LikeResponse> responsePage = likePage.map(
			like -> LikeResponse.from(like, summaryMap.get(like.getTargetId()))
		);
		return PagingResponse.from(responsePage);
	}
}
