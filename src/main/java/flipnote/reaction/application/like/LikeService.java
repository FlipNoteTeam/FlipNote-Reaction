package flipnote.reaction.application.like;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cardset.Cardset.CardSetSummary;
import flipnote.reaction.domain.common.BizException;
import flipnote.reaction.domain.common.CommonErrorCode;
import flipnote.reaction.domain.like.Like;
import flipnote.reaction.domain.like.LikeErrorCode;
import flipnote.reaction.domain.like.LikeRepository;
import flipnote.reaction.domain.like.LikeTargetType;
import flipnote.reaction.infrastructure.messaging.RabbitMqConfig;
import flipnote.reaction.infrastructure.grpc.CardSetGrpcClient;
import flipnote.reaction.infrastructure.messaging.ReactionEventPublisher;
import flipnote.reaction.interfaces.http.dto.request.LikeSearchRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

	private final LikeRepository likeRepository;
	private final LikeReader likeReader;
	private final ReactionEventPublisher eventPublisher;
	private final CardSetGrpcClient cardSetGrpcClient;

	@Transactional(noRollbackFor = DataIntegrityViolationException.class)
	public Long addLike(LikeTargetType targetType, Long targetId, Long userId) {
		if (!cardSetGrpcClient.isCardSetViewable(targetId, userId)) {
			throw new BizException(CommonErrorCode.TARGET_NOT_VIEWABLE);
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

		eventPublisher.publish(
			RabbitMqConfig.ROUTING_KEY_LIKE_ADDED,
			"LIKE_ADDED",
			targetType.name(),
			targetId,
			userId
		);

		return like.getId();
	}

	@Transactional
	public void removeLike(LikeTargetType targetType, Long targetId, Long userId) {
		Like like = likeReader.findByTargetAndUserId(targetType, targetId, userId);
		likeRepository.delete(like);

		eventPublisher.publish(
			RabbitMqConfig.ROUTING_KEY_LIKE_REMOVED,
			"LIKE_REMOVED",
			targetType.name(),
			targetId,
			userId
		);
	}

	public Page<LikeResult> getLikes(LikeTargetType targetType, Long userId, LikeSearchRequest request) {
		Page<Like> likePage = likeRepository.findByTargetTypeAndUserId(
			targetType, userId, request.getPageRequest()
		);

		List<Long> targetIds = likePage.getContent().stream()
			.map(Like::getTargetId)
			.toList();

		Map<Long, CardSetSummary> cardSetMap = targetIds.isEmpty()
			? Map.of()
			: cardSetGrpcClient.getCardSetsByIds(targetIds, userId);

		return likePage.map(l -> LikeResult.from(l, cardSetMap.get(l.getTargetId())));
	}
}
