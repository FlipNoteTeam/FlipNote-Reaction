package flipnote.reaction.like.model.request;

import flipnote.reaction.common.exception.BizException;
import flipnote.reaction.like.entity.LikeTargetType;
import flipnote.reaction.like.exception.LikeErrorCode;

public enum LikeTargetTypeRequest {
	card_set;

	public LikeTargetType toEntity() {
		return switch (this) {
			case card_set -> LikeTargetType.CARD_SET;
		};
	}

	public static LikeTargetTypeRequest from(String value) {
		try {
			return LikeTargetTypeRequest.valueOf(value);
		} catch (IllegalArgumentException e) {
			throw new BizException(LikeErrorCode.INVALID_LIKE_TYPE);
		}
	}
}
