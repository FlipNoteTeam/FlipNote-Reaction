package flipnote.reaction.like.model.response;

import java.time.LocalDateTime;

import cardset.Cardset.CardSetSummary;
import flipnote.reaction.like.entity.Like;

public record LikeResponse(
	String targetType,
	Long targetId,
	Long targetGroupId,
	LocalDateTime likedAt
) {
	public static LikeResponse from(Like like, CardSetSummary summary) {
		return new LikeResponse(
			like.getTargetType().name(),
			like.getTargetId(),
			summary != null ? summary.getGroupId() : null,
			like.getCreatedAt()
		);
	}
}
