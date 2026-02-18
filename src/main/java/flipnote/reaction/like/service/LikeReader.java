package flipnote.reaction.like.service;

import org.springframework.stereotype.Component;

import flipnote.reaction.common.exception.BizException;
import flipnote.reaction.like.entity.Like;
import flipnote.reaction.like.entity.LikeTargetType;
import flipnote.reaction.like.exception.LikeErrorCode;
import flipnote.reaction.like.repository.LikeRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LikeReader {

	private final LikeRepository likeRepository;

	public Like findByTargetAndUserId(LikeTargetType targetType, Long targetId, Long userId) {
		return likeRepository.findByTargetTypeAndTargetIdAndUserId(targetType, targetId, userId)
			.orElseThrow(() -> new BizException(LikeErrorCode.LIKE_NOT_FOUND));
	}

	public boolean isLiked(LikeTargetType targetType, Long targetId, Long userId) {
		return likeRepository.existsByTargetTypeAndTargetIdAndUserId(targetType, targetId, userId);
	}
}
