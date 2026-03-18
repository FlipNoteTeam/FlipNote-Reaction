package flipnote.reaction.bookmark.model.response;

import java.time.LocalDateTime;

import cardset.Cardset.CardSetSummary;
import flipnote.reaction.bookmark.entity.Bookmark;

public record BookmarkResponse(
	BookmarkTargetResponse target,
	LocalDateTime bookmarkedAt
) {
	public static BookmarkResponse from(Bookmark bookmark, CardSetSummary summary) {
		return new BookmarkResponse(
			new BookmarkTargetResponse(
				bookmark.getTargetType().name(),
				bookmark.getTargetId(),
				summary != null ? summary.getGroupId() : null,
				summary != null ? summary.getName() : null
			),
			bookmark.getCreatedAt()
		);
	}
}
