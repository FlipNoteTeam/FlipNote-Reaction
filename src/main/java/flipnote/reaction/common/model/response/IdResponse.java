package flipnote.reaction.common.model.response;

public record IdResponse(
	Long id
) {
	public static IdResponse from(Long id) {
		return new IdResponse(id);
	}
}
