package flipnote.reaction.common.model.event;

public record ReactionMessage(
	String eventType,
	String targetType,
	Long targetId,
	Long userId
) {
}
