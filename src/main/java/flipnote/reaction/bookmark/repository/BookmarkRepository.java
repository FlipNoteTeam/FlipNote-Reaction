package flipnote.reaction.bookmark.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import flipnote.reaction.bookmark.entity.Bookmark;
import flipnote.reaction.bookmark.entity.BookmarkTargetType;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

	boolean existsByTargetTypeAndTargetIdAndUserId(BookmarkTargetType targetType, Long targetId, Long userId);

	Optional<Bookmark> findByTargetTypeAndTargetIdAndUserId(BookmarkTargetType targetType, Long targetId, Long userId);

	Page<Bookmark> findByTargetTypeAndUserId(BookmarkTargetType targetType, Long userId, Pageable pageable);
}
