package com.hoppin.infra.crawling.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstagramProfileValidateResponse {

    private boolean valid;
    private String status;
    private String message;
}