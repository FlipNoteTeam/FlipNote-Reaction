package flipnote.reaction.interfaces.http.dto.response;

import java.time.LocalDateTime;

import flipnote.reaction.domain.bookmark.Bookmark;

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
