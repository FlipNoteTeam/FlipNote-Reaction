package flipnote.reaction.application.bookmark;

import java.time.LocalDateTime;

import cardset.Cardset.CardSetSummary;
import flipnote.reaction.application.common.CardSetSummaryResult;
import flipnote.reaction.domain.bookmark.Bookmark;

public record BookmarkResult(
	String targetType,
	Long targetId,
	LocalDateTime bookmarkedAt,
	CardSetSummaryResult cardSet
) {
	public static BookmarkResult from(Bookmark bookmark, CardSetSummary cardSet) {
		return new BookmarkResult(
			bookmark.getTargetType().name(),
			bookmark.getTargetId(),
			bookmark.getCreatedAt(),
			cardSet != null ? CardSetSummaryResult.from(cardSet) : null
		);
	}
}
