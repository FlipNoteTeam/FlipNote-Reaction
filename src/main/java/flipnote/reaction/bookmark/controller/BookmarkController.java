package flipnote.reaction.bookmark.controller;

import static flipnote.reaction.common.constants.HeaderConstants.USER_ID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import flipnote.reaction.bookmark.entity.BookmarkTargetType;
import flipnote.reaction.bookmark.model.request.BookmarkSearchRequest;
import flipnote.reaction.bookmark.model.request.BookmarkTargetTypeRequest;
import flipnote.reaction.bookmark.model.response.BookmarkResponse;
import flipnote.reaction.bookmark.service.BookmarkService;
import flipnote.reaction.common.model.response.IdResponse;
import flipnote.reaction.common.model.response.PagingResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

	private final BookmarkService bookmarkService;

	@PostMapping("/{targetType}/{targetId}")
	public IdResponse addBookmark(
		@RequestHeader(USER_ID) Long userId,
		@PathVariable String targetType,
		@PathVariable Long targetId
	) {
		BookmarkTargetType type = BookmarkTargetTypeRequest.from(targetType).toEntity();
		return bookmarkService.addBookmark(type, targetId, userId);
	}

	@DeleteMapping("/{targetType}/{targetId}")
	public void removeBookmark(
		@RequestHeader(USER_ID) Long userId,
		@PathVariable String targetType,
		@PathVariable Long targetId
	) {
		BookmarkTargetType type = BookmarkTargetTypeRequest.from(targetType).toEntity();
		bookmarkService.removeBookmark(type, targetId, userId);
	}

	@GetMapping("/{targetType}")
	public PagingResponse<BookmarkResponse> getBookmarks(
		@RequestHeader(USER_ID) Long userId,
		@PathVariable String targetType,
		@Valid @ModelAttribute BookmarkSearchRequest request
	) {
		BookmarkTargetType type = BookmarkTargetTypeRequest.from(targetType).toEntity();
		return bookmarkService.getBookmarks(type, userId, request);
	}
}
