package flipnote.reaction.like.model.response;

import java.time.LocalDateTime;

import cardset.Cardset.CardSetSummary;
import flipnote.reaction.like.entity.Like;

public record LikeResponse(
	LikeTargetResponse target,
	LocalDateTime likedAt
) {
	public static LikeResponse from(Like like, CardSetSummary summary) {
		return new LikeResponse(
			new LikeTargetResponse(
				like.getTargetType().name(),
				like.getTargetId(),
				summary != null ? summary.getGroupId() : null,
				summary != null ? summary.getName() : null
			),
			like.getCreatedAt()
		);
	}
}
