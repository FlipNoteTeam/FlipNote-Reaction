package flipnote.reaction.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
	INTERNAL_SERVER_ERROR(500, "COMMON_001", "예기치 않은 오류가 발생했습니다."),
	INVALID_INPUT_VALUE(400, "COMMON_002", "입력값이 올바르지 않습니다.");

	private final int status;
	private final String code;
	private final String message;
}
