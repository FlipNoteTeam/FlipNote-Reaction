package flipnote.reaction.interfaces.http.dto.response;

import java.time.LocalDateTime;

import flipnote.reaction.domain.like.Like;

public record LikeResponse(
	String targetType,
	Long targetId,
	LocalDateTime likedAt
) {
	public static LikeResponse from(Like like) {
		return new LikeResponse(
			like.getTargetType().name(),
			like.getTargetId(),
			like.getCreatedAt()
		);
	}
}
