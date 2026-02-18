package flipnote.reaction.bookmark.service;

import org.springframework.stereotype.Component;

import flipnote.reaction.bookmark.entity.Bookmark;
import flipnote.reaction.bookmark.entity.BookmarkTargetType;
import flipnote.reaction.bookmark.exception.BookmarkErrorCode;
import flipnote.reaction.bookmark.repository.BookmarkRepository;
import flipnote.reaction.common.exception.BizException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookmarkReader {

	private final BookmarkRepository bookmarkRepository;

	public Bookmark findByTargetAndUserId(BookmarkTargetType targetType, Long targetId, Long userId) {
		return bookmarkRepository.findByTargetTypeAndTargetIdAndUserId(targetType, targetId, userId)
			.orElseThrow(() -> new BizException(BookmarkErrorCode.BOOKMARK_NOT_FOUND));
	}

	public boolean isBookmarked(BookmarkTargetType targetType, Long targetId, Long userId) {
		return bookmarkRepository.existsByTargetTypeAndTargetIdAndUserId(targetType, targetId, userId);
	}
}
