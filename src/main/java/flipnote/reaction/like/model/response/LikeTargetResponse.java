package flipnote.reaction.like.model.response;

public record LikeTargetResponse(
	String type,
	Long id,
	Long groupId,
	String name
) {
}
