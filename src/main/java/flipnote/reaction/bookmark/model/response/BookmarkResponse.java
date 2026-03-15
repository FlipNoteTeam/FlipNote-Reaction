package flipnote.reaction.bookmark.model.response;

import java.time.LocalDateTime;

import cardset.Cardset.CardSetSummary;
import flipnote.reaction.bookmark.entity.Bookmark;

public record BookmarkResponse(
	String targetType,
	Long targetId,
	Long targetGroupId,
	LocalDateTime bookmarkedAt
) {
	public static BookmarkResponse from(Bookmark bookmark, CardSetSummary summary) {
		return new BookmarkResponse(
			bookmark.getTargetType().name(),
			bookmark.getTargetId(),
			summary != null ? summary.getGroupId() : null,
			bookmark.getCreatedAt()
		);
	}
}
