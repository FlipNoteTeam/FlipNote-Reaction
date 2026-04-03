package flipnote.reaction.interfaces.grpc;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import flipnote.reaction.application.bookmark.BookmarkReader;
import flipnote.reaction.application.like.LikeReader;
import flipnote.reaction.domain.bookmark.BookmarkTargetType;
import flipnote.reaction.domain.like.LikeTargetType;
import io.grpc.stub.StreamObserver;
import reaction.Reaction.AreReactedRequest;
import reaction.Reaction.AreReactedResponse;
import reaction.Reaction.IsReactedRequest;
import reaction.Reaction.IsReactedResponse;
import reaction.ReactionServiceGrpc;

@Component
public class ReactionGrpcService extends ReactionServiceGrpc.ReactionServiceImplBase {

	private final LikeReader likeReader;
	private final BookmarkReader bookmarkReader;

	public ReactionGrpcService(LikeReader likeReader, BookmarkReader bookmarkReader) {
		this.likeReader = likeReader;
		this.bookmarkReader = bookmarkReader;
	}

	@Override
	public void isLiked(IsReactedRequest request, StreamObserver<IsReactedResponse> responseObserver) {
		LikeTargetType targetType = LikeTargetType.valueOf(request.getTargetType());
		boolean reacted = likeReader.isLiked(targetType, request.getTargetId(), request.getUserId());

		responseObserver.onNext(IsReactedResponse.newBuilder().setReacted(reacted).build());
		responseObserver.onCompleted();
	}

	@Override
	public void areLiked(AreReactedRequest request, StreamObserver<AreReactedResponse> responseObserver) {
		LikeTargetType targetType = LikeTargetType.valueOf(request.getTargetType());
		List<Long> targetIds = request.getTargetIdsList();
		Map<Long, Boolean> results = likeReader.areLiked(targetType, targetIds, request.getUserId());

		responseObserver.onNext(AreReactedResponse.newBuilder().putAllResults(results).build());
		responseObserver.onCompleted();
	}

	@Override
	public void isBookmarked(IsReactedRequest request, StreamObserver<IsReactedResponse> responseObserver) {
		BookmarkTargetType targetType = BookmarkTargetType.valueOf(request.getTargetType());
		boolean reacted = bookmarkReader.isBookmarked(targetType, request.getTargetId(), request.getUserId());

		responseObserver.onNext(IsReactedResponse.newBuilder().setReacted(reacted).build());
		responseObserver.onCompleted();
	}

	@Override
	public void areBookmarked(AreReactedRequest request, StreamObserver<AreReactedResponse> responseObserver) {
		BookmarkTargetType targetType = BookmarkTargetType.valueOf(request.getTargetType());
		List<Long> targetIds = request.getTargetIdsList();
		Map<Long, Boolean> results = bookmarkReader.areBookmarked(targetType, targetIds, request.getUserId());

		responseObserver.onNext(AreReactedResponse.newBuilder().putAllResults(results).build());
		responseObserver.onCompleted();
	}
}
