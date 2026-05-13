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
                <!doctype html>
                      <html lang="ko">
                        <head>
                          <meta charset="UTF-8" />
                          <title>PEAK AI 홍보 진단 결과</title>
                        </head>
                        <body style="margin: 0; padding: 0; background-color: #f5f5f5; font-family: Arial, 'Apple SD Gothic Neo', 'Malgun Gothic', sans-serif;">
                
                          <!-- 외부 래퍼 -->
                          <table width="600" cellpadding="0" cellspacing="0" align="center" style="margin: 0 auto; background-color: #ffffff;">
                            <tr>
                              <td style="padding: 32px 24px; color: #222222;">
                
                                <!-- 내부 콘텐츠 -->
                                <table width="400" cellpadding="0" cellspacing="0" align="center" style="margin: 0 auto;">
                
                                  <!-- 인사말 -->
                                  <tr>
                                    <td style="padding-bottom: 16px; font-size: 14px; line-height: 1.5;">
                                      안녕하세요, <strong>%s</strong>님,<br />
                                      뮤지션 앨범 홍보 진단 서비스 <strong>PEAK</strong>가<br />
                                      <strong>&lt;%s&gt;</strong>의 홍보는 잘 되고 있는지 살펴봤어요.<br />
                                      아래 '홍보 진단 결과 보기' 버튼을 눌러 내용을 확인할 수 있어요.
                                    </td>
                                  </tr>
                
                                  <!-- 이미지 -->
                                  <tr>
                                    <td align="center" style="padding: 24px 0;">
                                      <img
                                        src="https://www.musicpeak.site/character/modal-mail-success.png"
                                        alt="PEAK"
                                        width="189"
                                        style="display: block; width: 189px; height: auto;"
                                      />
                                    </td>
                                  </tr>
                
                                  <!-- 버튼 -->
                                  <tr>
                                    <td align="center" style="padding-bottom: 36px;">
                                      <table width="300" cellpadding="0" cellspacing="0">
                                        <tr>
                                          <td align="center" bgcolor="#6a40fc" style="border-radius: 52px;">
                                            <a
                                              href="%s"
                                              target="_blank"
                                              rel="noreferrer noopener"
                                              style="display: block; padding: 15px 0; color: #ffffff; text-decoration: none; font-size: 16px; font-weight: bold;"
                                            >
                                              홍보 진단 결과 보기
                                            </a>
                                          </td>
                                        </tr>
                                      </table>
                                    </td>
                                  </tr>
                
                                  <!-- 본문 -->
                                  <tr>
                                    <td style="padding: 24px 0; font-size: 14px; line-height: 1.5;">
                                      발매 직후 14일, 리스너들이 어디에서 반응하는지 잘 관찰해보세요.<br />
                                      약한 지점은 보완하고, 잘 되는 요소는 살려서 <br />이번 앨범은
                                      <strong>PEAK 찍기</strong> 도전!
                                    </td>
                                  </tr>
                
                                  <!-- 푸터 -->
                                  <tr>
                                    <td bgcolor="#f4f1ff" style="padding: 14px 12px; border-radius: 8px; color: #6c6c6c; font-size: 12px; line-height: 1.6;">
                                      <ul style="padding-left: 20px; margin: 0;">
                                        <li style="margin: 0 0 4px;">
                                          문의 :
                                          <a href="mailto:csmusicpeak@gmail.com" style="color: #555555; text-decoration: none;">csmusicpeak@gmail.com</a>
                                        </li>
                                        <li style="margin: 0;">
                                          본 메일은 'PEAK' 서비스 소셜 로그인 가입 시 동의하신 '서비스이용약관'
                                          및 '개인정보처리방침'에 따라 앨범 홍보 진단 신청 대상자에게 발송되는
                                          결과 안내입니다.
                                        </li>
                                      </ul>
                                    </td>
                                  </tr>
                
                                </table>
                              </td>
                            </tr>
                          </table>
                
                        </body>
                      </html>
                """.formatted(
                safeMusicianName,
                safeAlbumName,
                safeDetailPageUrl
        );
    }
}