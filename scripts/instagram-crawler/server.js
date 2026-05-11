const express = require("express");
const { execFile } = require("child_process");
const path = require("path");

const app = express();
app.use(express.json());

app.post("/profile/validate", (req, res) => {
    console.log("profile validate request body:", req.body);
    const { instagramUsername } = req.body;

    if (!instagramUsername || !instagramUsername.trim()) {
        return res.json({
            valid: false,
            status: "INVALID_USERNAME",
            message: "인스타그램 계정을 입력해 주세요."
        });
    }

    const scriptPath = path.join(__dirname, "crawl-instagram.js");

    execFile(
        "node",
        [
            scriptPath,
            "--mode",
            "validate",
            "--username",
            instagramUsername.trim(),
            "--headless",
            "true"
        ],
        {
            timeout: 60000
        },
        (error, stdout, stderr) => {
            if (stderr) {
                console.error(stderr);
            }

            if (error) {
                return res.json({
                    valid: false,
                    status: "VALIDATION_FAILED",
                    message: error.message || "인스타그램 계정 확인 중 오류가 발생했습니다."
                });
            }

            try {
                const output = stdout.trim();

                if (!output) {
                    return res.json({
                        valid: false,
                        status: "VALIDATION_FAILED",
                        message: "인스타그램 계정 확인 결과가 비어 있습니다."
                    });
                }

                const result = JSON.parse(output);
                return res.json(result);
            } catch (parseError) {
                console.error("validate parse error:", parseError);
                console.error("stdout:", stdout);

                return res.json({
                    valid: false,
                    status: "VALIDATION_FAILED",
                    message: "인스타그램 계정 확인 결과를 처리하지 못했습니다."
                });
            }
        }
    );
});

app.listen(3001, () => {
    console.log("instagram crawler server listening on port 3001");
});