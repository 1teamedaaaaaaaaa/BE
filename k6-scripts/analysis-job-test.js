import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
    scenarios: {
        analysis_job_load_test: {
            executor: "constant-arrival-rate",

            // 5분 동안 총 1000건 요청
            rate: 200,              // 1분당 200회 요청
            timeUnit: "1m",         // 200 req / 1분
            duration: "5m",         // 5분 동안 실행 → 총 약 1000건

            preAllocatedVUs: 50,    // 미리 준비할 VU 수
            maxVUs: 50,             // 최대 동시 VU 수
        },
    },

    thresholds: {
        http_req_failed: ["rate<0.01"],       // 실패율 1% 미만
        http_req_duration: ["p(95)<3000"],    // p95 응답시간 3초 미만
        checks: ["rate>0.95"],                // check 성공률 95% 초과
    },
};

const BASE_URL = __ENV.BASE_URL || "https://api.musicpeak.site";
const TOKEN = __ENV.TOKEN;
const PROMOTION_ID = __ENV.PROMOTION_ID || "102";

export default function () {
    const url = `${BASE_URL}/api/ai/analysis-jobs/${PROMOTION_ID}`;

    const payload = JSON.stringify({
        sinceDate: "2023-10-03",
        instagramUsername: "sejungmuz",
    });

    const params = {
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${TOKEN}`,
        },
    };

    const res = http.post(url, payload, params);

    check(res, {
        "status is 200 or 202": (r) => r.status === 200 || r.status === 202,
        "response time < 3s": (r) => r.timings.duration < 3000,
        "response body exists": (r) => r.body && r.body.length > 0,
    });

    sleep(1);
}