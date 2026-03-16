package flipnote.reaction.like.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

	public Map<Long, Boolean> areLiked(LikeTargetType targetType, List<Long> targetIds, Long userId) {
		Set<Long> likedIds = likeRepository.findByTargetTypeAndTargetIdInAndUserId(targetType, targetIds, userId)
			.stream()
			.map(Like::getTargetId)
			.collect(Collectors.toSet());

		return targetIds.stream()
			.collect(Collectors.toMap(id -> id, likedIds::contains));
	}
}
