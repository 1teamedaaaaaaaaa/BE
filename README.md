> # MusicPeak Backend

뮤지션의 음악 및 콘텐츠를 효과적으로 홍보할 수 있도록 지원하는 플랫폼 **MusicPeak**의 백엔드 레포지토리입니다.  

---

> ## 프로젝트 소개

MusicPeak는 뮤지션이 자신의 음악과 SNS 콘텐츠를 기반으로 홍보 페이지를 생성하고, 더 많은 사용자에게 도달할 수 있도록 돕는 서비스입니다.

백엔드 담당 역할

- 소셜 로그인 기반 사용자 인증 및 회원 관리
- 뮤지션 정보 및 홍보 데이터 관리
- Instagram API 연동
- OpenAI API 연동
- SNS 공유 및 외부 링크 추적 기능 지원
- 회원 탈퇴 및 상태 관리
- CI/CD 자동 배포 환경 운영

---

> ## 백엔드 팀 구성

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/viviamm7-code" target="_blank">
        <img src="https://github.com/viviamm7-code.png" width="200px;" alt="김진성"/>
        <br />
        <span style="font-size:18px;"><b>김진성</b></span>
      </a>
      <br />
    </td>
    <td align="center">
      <a href="https://github.com/Hojin00" target="_blank">
        <img src="https://github.com/Hojin00.png" width="200px;" alt="류호진"/>
        <br />
        <span style="font-size:18px;"><b>류호진</b></span>
      </a>
      <br />
    </td>
  </tr>
</table>

---

> ## 기술 스택

### Backend
- Java 17
- Spring Boot 4.0.5
- Spring Security
- Spring Data JPA
- MySQL
- Redis
- JWT
- OAuth2

### Infra
- AWS EC2
- AWS RDS
- AWS S3
- Docker
- Docker Compose

### DevOps
- GitHub Actions
- CI/CD Pipeline

### External API
- Instagram API
- OpenAI API
- SNS API (Google / Kakao / Naver OAuth2)

---

> ## AI 기반 SNS 분석 솔루션

MusicPeak는 Instagram API를 통해 계정 및 콘텐츠 데이터를 수집하고,  
OpenAI API를 활용하여 데이터를 분석한 뒤 뮤지션 맞춤형 홍보 솔루션을 제공합니다.

### 주요 기능

- 게시물 성과 지표 분석  
  (조회수, 좋아요 수, 댓글 수, 공유 수, 저장 수 등)

- 계정 성장 지표 분석  
  (프로필 방문 수, 링크 클릭 수, 팔로워 반응 등)

- 최근 콘텐츠 성과 기반 홍보 방향 제안

- 업로드 시간대 및 콘텐츠 유형 추천

- 반응이 높은 게시물 패턴 분석

- 뮤지션 계정 맞춤형 SNS 운영 전략 제공

- AI 기반 개선 포인트 요약 및 실행 가이드 제공

### 기대 효과

- 감에 의존하지 않는 데이터 기반 홍보 전략 수립
- 뮤지션 개인 맞춤형 SNS 성장 방향 제시
- 콘텐츠 성과 분석 자동화로 운영 효율 향상
- 팬 유입 및 음악 홍보 효과 극대화

> ## 개발 문서
- [Notion Page](https://www.notion.so/goormkdx/NE-326c0ff4ce3180ecba95ced0c42444d3)
