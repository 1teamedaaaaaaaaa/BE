package com.hoppin.infra.ai.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiagnosisDto {

    private String bottleneckType;

    private String highlightSection;

    private String interpretation;

    private Integer contentCount;

    private Integer totalLikeCount;

    private Integer totalCommentCount;

    private Integer trackingLinkClickCount;

    private Integer streamingLinkClickCount;

    private Integer totalLinkClickCount;
}