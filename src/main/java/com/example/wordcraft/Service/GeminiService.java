package com.example.wordcraft.Service;

import com.example.wordcraft.DTO.AI.WordAnalysisDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<WordAnalysisDTO> analyzeWords(String apiKey, String text, String tag) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("empty text");
        }
        String url = API_URL + "?key=" + apiKey;

        String prompt = buildPrompt(text, tag);

        Map<String, Object> textPart = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(textPart));
        Map<String, Object> body = Map.of("contents", List.of(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.postForEntity(
                url, new HttpEntity<>(body, headers), String.class
        );

        return parseResponse(response.getBody());
    }

    private String buildPrompt(String text, String tag) {
        return """
                아래 단어들을 "%s" 맥락에서 각각 분석하여 JSON 배열 형식으로만 응답하세요.
                마크다운 코드블록, 설명 텍스트 없이 JSON 배열만 출력하세요.
                단어 목록: %s

                [
                  {
                    "word": "분석한 단어",
                    "ipa": "/발음기호/",
                    "pos": "품사 (Noun/Verb/Adjective/Adverb/Idiom/Compound Noun 중 하나)",
                    "meanings": ["태그 맥락의 주요 의미1", "의미2"],
                    "examples": [
                      {"en": "영어 예문", "ko": "한국어 해석"}
                    ],
                    "memoryTip": "기억에 도움이 되는 어원이나 연상법"
                  }
                ]
                """.formatted(tag, text);
    }

    private List<WordAnalysisDTO> parseResponse(String raw) {
        try {
            JsonNode root = objectMapper.readTree(raw);
            String text = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            text = text.replaceAll("(?s)```json\\s*", "")
                       .replaceAll("(?s)```\\s*", "").trim();

            return objectMapper.readValue(
                    text,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, WordAnalysisDTO.class)
            );
        } catch (Exception e) {
            throw new RuntimeException("AI 응답 파싱 실패: " + e.getMessage());
        }
    }

    public List<WordAnalysisDTO> fileAnalyzeResponse(MultipartFile file, String apiKey, String tag) throws IOException {
        String url = API_URL + "?key=" + apiKey;

        // 1. 파일 → Base64 변환
        byte[] bytes = file.getBytes();
        String base64Data = Base64.getEncoder().encodeToString(bytes);
        String mimeType = file.getContentType() != null ? file.getContentType() : "image/jpeg";

        // 2. filePart (inline_data)
        Map<String, Object> inlineData = Map.of(
                "mime_type", mimeType,
                "data", base64Data
        );
        Map<String, Object> filePart = Map.of("inline_data", inlineData);

        // 3. textPart (프롬프트) - 파일에서 단어 추출 + buildPrompt 스키마 지시
        String prompt = buildFilePrompt(tag);
        Map<String, Object> textPart = Map.of("text", prompt);

        // 4. parts 에 filePart + textPart 순서로 담기
        Map<String, Object> content = Map.of("parts", List.of(filePart, textPart));
        Map<String, Object> body = Map.of("contents", List.of(content));

        // 5. 요청 (analyzeWords 와 동일)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.postForEntity(
                url, new HttpEntity<>(body, headers), String.class
        );

        // 6. 파싱 (기존 parseResponse 재사용)
        return parseResponse(response.getBody());
    }

    private String buildFilePrompt(String tag) {
        return """
            이 문서에서 영어 단어들을 추출하고 "%s" 맥락에서 각각 분석하여 JSON 배열 형식으로만 응답하세요.
            마크다운 코드블록, 설명 텍스트 없이 JSON 배열만 출력하세요.

            [
              {
                "word": "추출한 단어",
                "ipa": "/발음기호/",
                "pos": "품사 (Noun/Verb/Adjective/Adverb/Idiom/Compound Noun 중 하나)",
                "meanings": ["태그 맥락의 주요 의미1", "의미2"],
                "examples": [
                  {"en": "영어 예문", "ko": "한국어 해석"}
                ],
                "memoryTip": "기억에 도움이 되는 어원이나 연상법"
              }
            ]
            """.formatted(tag);
    }
}
