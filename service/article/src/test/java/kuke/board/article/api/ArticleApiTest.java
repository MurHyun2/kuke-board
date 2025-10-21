package kuke.board.article.api;

import kuke.board.article.service.response.ArticlePageResponse;
import kuke.board.article.service.response.ArticleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

public class ArticleApiTest {
    RestClient restClient = RestClient.create("http://localhost:9000");

    @Test
    void createTest() {
        ArticleResponse response = create(new ArticleCreateRequest(
                "hi", "my content", 1L, 1L
        ));
        System.out.println("response: " + response);
    }

    @Test
    void readTest() {
        ArticleResponse response = read(238861424495734784L);
        System.out.println("response: " + response);
    }

    @Test
    void updateTest() {
        update(238861424495734784L);
        ArticleResponse response = read(238861424495734784L);
        System.out.println("response: " + response);
    }

    @Test
    void deleteTest() {
        delete(238861424495734784L);
    }

    @Test
    void readAllTest() {
        ArticlePageResponse response = restClient.get()
                .uri("/v1/articles?boardId=1&pageSize=30&page=55551")
                .retrieve()
                .body(ArticlePageResponse.class);

        System.out.println(response.getArticleCount());
        for (ArticleResponse article: response.getArticles()) {
            System.out.println(article.getArticleId());
        }
    }

    @Test
    void readAllInfiniteScrollTest() {
        List<ArticleResponse> articles = restClient.get()
                .uri("v1/articles/infinite-scroll?boardId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {
                });

        System.out.println("first Page");
        for (ArticleResponse article : articles) {
            System.out.println(article.getArticleId());
        }

        Long lastArticleId = articles.getLast().getArticleId();
        List<ArticleResponse> articles2 = restClient.get()
                .uri("v1/articles/infinite-scroll?boardId=1&pageSize=5&lastArticleId=%s".formatted(lastArticleId))
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {
                });

        System.out.println("next Page");
        for (ArticleResponse article : articles2) {
            System.out.println(article.getArticleId());
        }
    }

    void delete(Long articleId) {
        restClient.delete()
                .uri("/v1/articles/{articleId}", articleId)
                .retrieve();
    }

    void update(Long articleId) {
        restClient.put()
                .uri("/v1/articles/{articleId}", articleId)
                .body(new ArticleUpdateRequest("fix hi", "fix content"))
                .retrieve();
    }

    ArticleResponse read(Long articleId) {
        return restClient.get()
                .uri("/v1/articles/{articleId}", articleId)
                .retrieve()
                .body(ArticleResponse.class);
    }

    ArticleResponse create(ArticleCreateRequest request) {
        return restClient.post()
                .uri("/v1/articles")
                .body(request)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Getter
    @AllArgsConstructor
    static class ArticleCreateRequest {
        private String title;
        private String content;
        private Long writerId;
        private Long boardId;
    }

    @Getter
    @AllArgsConstructor
    static class ArticleUpdateRequest {
        private String title;
        private String content;
    }
}
