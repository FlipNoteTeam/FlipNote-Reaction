package flipnote.reaction.interfaces.http;

import static flipnote.reaction.interfaces.http.common.HeaderConstants.USER_ID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import flipnote.reaction.application.like.LikeService;
import flipnote.reaction.domain.like.LikeTargetType;
import flipnote.reaction.interfaces.http.common.IdResponse;
import flipnote.reaction.interfaces.http.common.PagingResponse;
import flipnote.reaction.interfaces.http.dto.request.LikeSearchRequest;
import flipnote.reaction.interfaces.http.dto.request.LikeTargetTypeRequest;
import flipnote.reaction.interfaces.http.dto.response.LikeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/likes")
@RequiredArgsConstructor
public class LikeController {

	private final LikeService likeService;

	@PostMapping("/{targetType}/{targetId}")
	public IdResponse addLike(
		@RequestHeader(USER_ID) Long userId,
		@PathVariable String targetType,
		@PathVariable Long targetId
	) {
		LikeTargetType type = LikeTargetTypeRequest.from(targetType).toEntity();
		return likeService.addLike(type, targetId, userId);
	}

	@DeleteMapping("/{targetType}/{targetId}")
	public void removeLike(
		@RequestHeader(USER_ID) Long userId,
		@PathVariable String targetType,
		@PathVariable Long targetId
	) {
		LikeTargetType type = LikeTargetTypeRequest.from(targetType).toEntity();
		likeService.removeLike(type, targetId, userId);
	}

	@GetMapping("/{targetType}")
	public PagingResponse<LikeResponse> getLikes(
		@RequestHeader(USER_ID) Long userId,
		@PathVariable String targetType,
		@Valid @ModelAttribute LikeSearchRequest request
	) {
		LikeTargetType type = LikeTargetTypeRequest.from(targetType).toEntity();
		return likeService.getLikes(type, userId, request);
	}
}
