import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    scenarios: {
        create_promotions_local: {
            executor: 'per-vu-iterations',
            vus: 10,
            iterations: 1,
            maxDuration: '30s',
        },
    },
};

const BASE_URL = 'http://localhost:8080';
const ACCESS_TOKEN = __ENV.ACCESS_TOKEN;

export default function () {
    const unique = `${__VU}-${__ITER}-${Date.now()}`;

    const payload = JSON.stringify({
        activityName: `k6 로컬 홍보 테스트 ${unique}`,
        songTitle: `테스트 곡 ${unique}`,
        releaseDate: '2026-04-30',
        streamingLinks: [
            {
                streamingCode: 'SPOTIFY',
                url: 'https://open.spotify.com/track/test'
            },
            {
                streamingCode: 'YOUTUBE',
                url: 'https://youtube.com/watch?v=test'
            }
        ],
        imageUrl: 'https://hoppin-bucket.s3.ap-northeast-2.amazonaws.com/music-promotions/d133fe80-51b2-4bb9-960e-3c3ffb560702.jpg',
        shortDescription: 'k6 로컬 홍보 생성 부하 테스트입니다.'
    });

    const res = http.post(`${BASE_URL}/api/music-promotions`, payload, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${ACCESS_TOKEN}`,
        },
    });

    check(res, {
        'status is 200 or 201': (r) => r.status === 200 || r.status === 201,
        'response time < 2s': (r) => r.timings.duration < 2000,
    });

    sleep(1);
}