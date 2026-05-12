package com.hoppin.infra.mail.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.mail.from-name}")
    private String fromName;

    /**
     * @param to 받는 사람 이메일
     * @param musicianName 뮤지션명
     * @param albumName 앨범명
     * @param diagnosisDate 진단일
     * @param reportImageUrl 4.1.6 캡쳐 이미지 URL
     * @param detailPageUrl 4.1.3 상세 페이지 URL
     */
    public void sendAnalysisResult(
            String to,
            String musicianName,
            String albumName,
            LocalDate diagnosisDate,
            String reportImageUrl,
            String detailPageUrl
    ) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            String formattedDate = diagnosisDate.format(DateTimeFormatter.ofPattern("yy.MM.dd"));

            helper.setFrom(from, fromName);
            helper.setTo(to);
            helper.setSubject("PEAK AI 홍보 진단 결과가 도착했습니다(" + albumName + "-진단일(" + formattedDate + "))");

            helper.setText(
                    buildContent(
                            musicianName,
                            albumName,
                            reportImageUrl,
                            detailPageUrl
                    ),
                    true
            );

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("이메일 발송 실패", e);
        }
    }

    private String buildContent(
            String musicianName,
            String albumName,
            String reportImageUrl,
            String detailPageUrl
    ) {
        String safeMusicianName = HtmlUtils.htmlEscape(musicianName);
        String safeAlbumName = HtmlUtils.htmlEscape(albumName);
        String safeReportImageUrl = HtmlUtils.htmlEscape(reportImageUrl);
        String safeDetailPageUrl = HtmlUtils.htmlEscape(detailPageUrl);

        return """
                <!DOCTYPE html>
                <html lang="ko">
                <head>
                    <meta charset="UTF-8">
                    <title>PEAK AI 홍보 진단 결과</title>
                </head>
                <body style="margin:0; padding:0; background-color:#f5f5f5; font-family:Arial, 'Apple SD Gothic Neo', 'Malgun Gothic', sans-serif;">
                    <div style="max-width:400px; margin:0 auto; background-color:#ffffff; padding:32px 24px; color:#222222;">
                        
                        <h2 style="margin:0 0 24px; font-size:22px; line-height:1.4; color:#111111;">
                            PEAK AI 홍보 진단 결과가 도착했습니다
                        </h2>
                
                        <p style="margin:0 0 16px; font-size:15px; line-height:1.7;">
                            안녕하세요, <strong>“%s”</strong>님,
                        </p>
                
                        <p style="margin:0 0 20px; font-size:15px; line-height:1.7;">
                            뮤지션 앨범 홍보 진단 서비스 <strong>PEAK</strong>가<br>
                            <strong>&lt;“%s”&gt;</strong>의 홍보는 잘 되고 있는지 살펴봤어요.<br>
                            어디 한 번 볼까요?
                        </p>
                
                        <div style="margin:24px 0; text-align:center;">
                            <img src="%s"
                                 alt="PEAK AI 홍보 진단 결과 이미지"
                                 width="400"
                                 style="width:400px; max-width:100%%; height:auto; display:block; border-radius:12px;">
                        </div>
                
                        <p style="margin:0 0 12px; font-size:15px; line-height:1.7;">
                            마이페이지에서 더 자세한 내용을 확인하실 수 있어요.
                        </p>
                
                        <p style="margin:0 0 12px; font-size:15px; line-height:1.7;">
                            발매 직후 14일, 리스너들이 어디에서 반응하는지 잘 관찰해보세요.
                        </p>
                
                        <p style="margin:0 0 28px; font-size:15px; line-height:1.7;">
                            약한 지점은 보완하고, 잘 되는 요소는 살려서<br>
                            이번 앨범은 <strong>PEAK 찍기</strong> 도전!
                        </p>
                
                        <div style="text-align:center; margin:28px 0 36px;">
                            <a href="%s"
                               target="_blank"
                               rel="noopener noreferrer"
                               style="display:inline-block; width:100%%; max-width:320px; padding:15px 0; background-color:#111111; color:#ffffff; text-decoration:none; border-radius:10px; font-size:15px; font-weight:bold;">
                                실시간 홍보 현황 확인하기
                            </a>
                        </div>
                
                        <div style="max-width:400px; margin:0 auto; padding:18px 16px; background-color:#f1f1f1; border-radius:10px; color:#666666; font-size:12px; line-height:1.6;">
                            <p style="margin:0 0 10px;">
                                문의 : <a href="mailto:csmusicpeak@gmail.com" style="color:#555555; text-decoration:none;">csmusicpeak@gmail.com</a>
                            </p>
                            <p style="margin:0;">
                                본 메일은 ‘PEAK’ 서비스 소셜 로그인 가입 시 동의하신 ‘서비스이용약관’ 및 ‘개인정보처리방침’에 따라
                                앨범 홍보 진단 신청 대상자에게 발송되는 결과 안내입니다.
                            </p>
                        </div>
                
                    </div>
                </body>
                </html>
                """.formatted(
                safeMusicianName,
                safeAlbumName,
                safeReportImageUrl,
                safeDetailPageUrl
        );
    }
}