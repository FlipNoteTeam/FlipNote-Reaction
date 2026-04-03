package flipnote.reaction.application.common;

import cardset.Cardset.CardSetSummary;

public record CardSetSummaryResult(
	Long id,
	String name,
	Long groupId,
	String visibility,
	String category,
	String hashtag,
	Long imageRefId,
	Long cardCount
) {
	public static CardSetSummaryResult from(CardSetSummary summary) {
		return new CardSetSummaryResult(
			summary.getId(),
			summary.getName(),
			summary.getGroupId(),
			summary.getVisibility(),
			summary.getCategory(),
			summary.getHashtag(),
			summary.hasImageRefId() ? summary.getImageRefId() : null,
			summary.getCardCount()
		);
	}
}
