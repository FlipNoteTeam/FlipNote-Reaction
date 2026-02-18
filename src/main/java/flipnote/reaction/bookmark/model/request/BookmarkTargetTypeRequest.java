package flipnote.reaction.bookmark.model.request;

import flipnote.reaction.bookmark.entity.BookmarkTargetType;
import flipnote.reaction.bookmark.exception.BookmarkErrorCode;
import flipnote.reaction.common.exception.BizException;

public enum BookmarkTargetTypeRequest {
	card_set;

	public BookmarkTargetType toEntity() {
		return switch (this) {
			case card_set -> BookmarkTargetType.CARD_SET;
		};
	}

	public static BookmarkTargetTypeRequest from(String value) {
		try {
			return BookmarkTargetTypeRequest.valueOf(value);
		} catch (IllegalArgumentException e) {
			throw new BizException(BookmarkErrorCode.INVALID_BOOKMARK_TYPE);
		}
	}
}
