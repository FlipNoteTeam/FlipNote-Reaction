package flipnote.reaction.bookmark.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

	public Map<Long, Boolean> areBookmarked(BookmarkTargetType targetType, List<Long> targetIds, Long userId) {
		Set<Long> bookmarkedIds = bookmarkRepository.findByTargetTypeAndTargetIdInAndUserId(targetType, targetIds, userId)
			.stream()
			.map(Bookmark::getTargetId)
			.collect(Collectors.toSet());

		return targetIds.stream()
			.collect(Collectors.toMap(id -> id, bookmarkedIds::contains));
	}
}
