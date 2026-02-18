package flipnote.reaction.like.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import flipnote.reaction.like.entity.Like;
import flipnote.reaction.like.entity.LikeTargetType;

public interface LikeRepository extends JpaRepository<Like, Long> {

	boolean existsByTargetTypeAndTargetIdAndUserId(LikeTargetType targetType, Long targetId, Long userId);

	Optional<Like> findByTargetTypeAndTargetIdAndUserId(LikeTargetType targetType, Long targetId, Long userId);

	Page<Like> findByTargetTypeAndUserId(LikeTargetType targetType, Long userId, Pageable pageable);
}
