package com.hoppin.infra.crawling.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AnalysisCrawlerResultRequest {

    private int contentCount;
    private int followerCount;
    private int totalLikeCount;
    private int totalCommentCount;
    private List<CrawledPost> posts;

    @Getter
    @Setter
    public static class CrawledPost {
        private String mediaId;
        private String caption;
        private String mediaType;
        private String permalink;
        private String timestamp;
        private int likeCount;
        private int commentCount;
    }
}
