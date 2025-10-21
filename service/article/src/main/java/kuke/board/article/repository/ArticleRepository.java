package kuke.board.article.repository;

import kuke.board.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query(
            value = "select a.article_id, a.title, a.content, a.board_id, a.writer_id, " +
                    "a.created_at, a.modified_at " +
                    "from (" +
                    "   select article_id from article " +
                    "   where board_id = :boardId " +
                    "   order by article_id desc" +
                    " limit :limit offset :offset" +
                    ") t join article a on a.article_id = t.article_id",
            nativeQuery = true
    )
    List<Article> findAll(
            @Param("boardId") Long boardid,
            @Param("offset") Long offset,
            @Param("limit") Long limit
    );

    @Query(
            value = "select count(*) from (" +
                    "   select article_id from article where board_id = :boardId limit :limit " +
                    ") t",
            nativeQuery = true
    )
    Long count(@Param("boardId") Long boardId, @Param("limit") Long limit);

    @Query(
            value = """
                SELECT a.article_id, a.title, a.content, a.board_id, a.writer_id,
                       a.created_at, a.modified_at
                FROM article a
                WHERE a.board_id = :boardId
                ORDER BY a.article_id DESC
                LIMIT :limit
                """,
            nativeQuery = true
            )
    List<Article> findAllInfiniteScroll(@Param("boardId") Long boardId, @Param("limit") Long limit);

    @Query(
            value = """
                SELECT a.article_id, a.title, a.content, a.board_id, a.writer_id,
                       a.created_at, a.modified_at
                FROM article a
                WHERE a.board_id = :boardId and a.article_id < :lastArticleId
                ORDER BY a.article_id DESC
                LIMIT :limit
                """,
            nativeQuery = true
    )
    List<Article> findAllInfiniteScroll(
            @Param("boardId") Long boardId,
            @Param("limit") Long limit,
            @Param("lastArticleId") Long lastArticleId);
}
