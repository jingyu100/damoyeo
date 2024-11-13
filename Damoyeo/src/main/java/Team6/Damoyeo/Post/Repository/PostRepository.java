package Team6.Damoyeo.Post.Repository;

import Team6.Damoyeo.Post.Entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {

//    @Query("SELECT p FROM Post p ORDER BY p.postId DESC LIMIT :limit OFFSET :offset")
//    List<Post> findPostsByPage(@Param("offset") int offset, @Param("limit") int limit);

    // 좋아요 기능
//    boolean exitByPost_Id_like(int postid, int userid);


    //조회수 기능
    @Modifying
    @Query("update Post p set p.viewCount = p.viewCount + 1 where p.postId = :id")
    int updateViews(@Param("id") int id);

    // 주변 모임 기능
    @Query("SELECT p FROM Post p WHERE p.roadAddress LIKE %:keyword% AND p.postId <> :postId")
    List<Post> findByroadAddress(@Param("keyword") String keyword, @Param("postId") int postId);


    // 검색 기능
    Page<Post> findByTitleContaining(String title, Pageable pageable);

    //태그 검색 기능
    Page<Post> findByTagContaining(String tag, Pageable pageable);
}
