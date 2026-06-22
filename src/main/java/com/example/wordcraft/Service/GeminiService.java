package com.example.wordcraft.Service;

import com.example.wordcraft.DTO.AI.WordAnalysisDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<WordAnalysisDTO> analyzeWords(String apiKey, List<String> words, String tag) {
        if (words == null || words.isEmpty()) {
            throw new IllegalArgumentException("단어 목록이 비어 있습니다.");
        }
        String url = API_URL + "?key=" + apiKey;

        String prompt = buildPrompt(words, tag);

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

    private String buildPrompt(List<String> words, String tag) {
        String wordList = String.join(", ", words);
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
                """.formatted(tag, wordList);
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
}
