package flipnote.reaction.bookmark.model.response;

import java.time.LocalDateTime;

import flipnote.reaction.bookmark.entity.Bookmark;

public record BookmarkResponse(
	String targetType,
	Long targetId,
	LocalDateTime bookmarkedAt
) {
	public static BookmarkResponse from(Bookmark bookmark) {
		return new BookmarkResponse(
			bookmark.getTargetType().name(),
			bookmark.getTargetId(),
			bookmark.getCreatedAt()
		);
	}
}
