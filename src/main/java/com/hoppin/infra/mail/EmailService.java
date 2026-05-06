package com.hoppin.infra.mail;

import com.hoppin.infra.ai.dto.response.ActionCardDto;
import com.hoppin.infra.ai.dto.response.AnalysisResponseDto;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.mail.from-name}")
    private String fromName;

    public void sendAnalysisResult(String to, AnalysisResponseDto response) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(from, fromName);
            helper.setTo(to);
            helper.setSubject("PEAK AI 홍보 진단 결과가 도착했습니다");
            helper.setText(buildContent(response), false);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("이메일 발송 실패", e);
        }
    }

    private String buildContent(AnalysisResponseDto response) {
        StringBuilder sb = new StringBuilder();

        sb.append("[PEAK AI 홍보 진단 결과]\n\n");

        sb.append("핵심 진단\n");
        sb.append(response.getHeadline()).append("\n\n");

        if (response.getDiagnosis() != null) {
            sb.append("분석 내용\n");
            sb.append(response.getDiagnosis().getInterpretation()).append("\n\n");
        }

        sb.append("추천 액션\n");

        if (response.getActions() != null) {
            int order = 1;
            for (ActionCardDto action : response.getActions()) {
                sb.append(order++).append(". ").append(action.getTitle()).append("\n");
                sb.append("- 이유: ").append(action.getReason()).append("\n");
                sb.append("- 지표: ").append(action.getMetric()).append("\n");
                sb.append("- 예시: ").append(action.getExample()).append("\n\n");
            }
        }

        sb.append("PEAK AI가 더 나은 홍보 운영을 응원합니다.");

        return sb.toString();
    }
}