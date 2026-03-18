package flipnote.reaction.bookmark.model.response;

public record BookmarkTargetResponse(
	String type,
	Long id,
	Long groupId,
	String name
) {
}
