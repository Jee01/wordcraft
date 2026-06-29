package com.example.wordcraft.Service.Test;

import com.example.wordcraft.DTO.Test.*;
import com.example.wordcraft.Entity.Test.TestResult;
import com.example.wordcraft.Entity.Test.TestResultWrongWord;
import com.example.wordcraft.Entity.Test.TestResultWrongWordDetail;
import com.example.wordcraft.Entity.Users;
import com.example.wordcraft.Entity.Voca.VocaWordDetail;
import com.example.wordcraft.Entity.Voca.VocaWords;
import com.example.wordcraft.Entity.Voca.Vocabularies;
import com.example.wordcraft.Repository.TestResultRepository;
import com.example.wordcraft.Repository.UserRepository;
import com.example.wordcraft.Repository.VocaWordDetailRepository;
import com.example.wordcraft.Repository.VocaWordsRepository;
import com.example.wordcraft.Repository.VocabulariesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestResultRepository testResultRepository;
    private final VocabulariesRepository vocabulariesRepository;
    private final VocaWordsRepository vocaWordsRepository;
    private final VocaWordDetailRepository vocaWordDetailRepository;
    private final UserRepository userRepository;

    // 뜻 맞추기 문제 생성
    @Transactional(readOnly = true)
    public List<MultipleChoiceQuestionDTO> getMultipleChoiceQuestions(Long vocabId, String email) {
        Vocabularies vocab = getVocabAndValidate(vocabId, email);
        List<VocaWords> words = vocaWordsRepository.findByVocabularyId(vocab.getId());

        if (words.size() < 2) {
            throw new RuntimeException("문제를 생성하려면 단어가 2개 이상 필요합니다.");
        }

        // 모든 단어의 detail을 미리 로드
        Map<Long, List<VocaWordDetail>> detailMap = new HashMap<>();
        for (VocaWords word : words) {
            List<VocaWordDetail> details = vocaWordDetailRepository.findByVocaWords(word);
            if (!details.isEmpty()) {
                detailMap.put(word.getId(), details);
            }
        }

        // 오답 후보 풀: 모든 detail 목록
        List<VocaWordDetail> allDetails = detailMap.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());

        List<MultipleChoiceQuestionDTO> questions = new ArrayList<>();
        Random random = new Random();

        for (VocaWords word : words) {
            List<VocaWordDetail> correctDetails = detailMap.get(word.getId());
            if (correctDetails == null || correctDetails.isEmpty()) continue;

            // 정답 detail 랜덤 선택
            VocaWordDetail correctDetail = correctDetails.get(random.nextInt(correctDetails.size()));

            // 오답 후보: 현재 단어 detail 제외
            List<VocaWordDetail> distractorPool = allDetails.stream()
                    .filter(d -> !d.getVocaWords().getId().equals(word.getId()))
                    .collect(Collectors.toList());

            Collections.shuffle(distractorPool);
            List<VocaWordDetail> distractors = distractorPool.subList(0, Math.min(3, distractorPool.size()));

            // 선택지 조합 후 셔플
            List<ChoiceOptionDTO> options = new ArrayList<>();
            options.add(ChoiceOptionDTO.builder()
                    .detailId(correctDetail.getId())
                    .pos(correctDetail.getPos())
                    .meaning(correctDetail.getMeanings())
                    .build());

            for (VocaWordDetail distractor : distractors) {
                options.add(ChoiceOptionDTO.builder()
                        .detailId(distractor.getId())
                        .pos(distractor.getPos())
                        .meaning(distractor.getMeanings())
                        .build());
            }
            Collections.shuffle(options);

            questions.add(MultipleChoiceQuestionDTO.builder()
                    .wordId(word.getId())
                    .word(word.getWord())
                    .ipa(word.getIpa())
                    .correctDetailId(correctDetail.getId())
                    .options(options)
                    .build());
        }

        return questions;
    }

    // 빈칸 채우기 문제 생성
    @Transactional(readOnly = true)
    public List<FillBlankQuestionDTO> getFillBlankQuestions(Long vocabId, String email) {
        Vocabularies vocab = getVocabAndValidate(vocabId, email);
        List<VocaWords> words = vocaWordsRepository.findByVocabularyId(vocab.getId());

        List<FillBlankQuestionDTO> questions = new ArrayList<>();

        for (VocaWords word : words) {
            List<VocaWordDetail> details = vocaWordDetailRepository.findByVocaWords(word);

            for (VocaWordDetail detail : details) {
                if (detail.getExamples() == null || detail.getExamples().isBlank()) continue;

                String example = detail.getExamples();
                // 단어를 ___ 로 대체 (대소문자 무시)
                String blanked = example.replaceAll("(?i)" + escapeRegex(word.getWord()), "___");

                if (blanked.equals(example)) continue; // 단어가 예문에 없으면 스킵

                questions.add(FillBlankQuestionDTO.builder()
                        .wordId(word.getId())
                        .word(word.getWord())
                        .detailId(detail.getId())
                        .pos(detail.getPos())
                        .meaning(detail.getMeanings())
                        .example(blanked)
                        .build());
            }
        }

        if (questions.isEmpty()) {
            throw new RuntimeException("예문이 있는 단어가 없습니다.");
        }

        Collections.shuffle(questions);
        return questions;
    }

    // 테스트 결과 저장
    @Transactional
    public void saveTestResult(TestResultRequestDTO request, String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("not found user"));

        Vocabularies vocab = vocabulariesRepository.findById(request.getVocabId())
                .orElseThrow(() -> new RuntimeException("vocabularies not found"));

        int wrongCount = request.getWrongWords() == null ? 0 : request.getWrongWords().size();

        TestResult testResult = TestResult.builder()
                .user(user)
                .vocabulary(vocab)
                .testType(request.getTestType())
                .totalCount(request.getTotalCount())
                .score(request.getScore())
                .wrongCount(wrongCount)
                .build();

        TestResult saved = testResultRepository.save(testResult);

        if (request.getWrongWords() != null) {
            for (WrongWordRequestDTO wrongWordDTO : request.getWrongWords()) {
                TestResultWrongWord wrongWord = TestResultWrongWord.builder()
                        .testResult(saved)
                        .wordId(wrongWordDTO.getWordId())
                        .word(wrongWordDTO.getWord())
                        .ipa(wrongWordDTO.getIpa())
                        .wrongCount(wrongWordDTO.getWrongCount() != null ? wrongWordDTO.getWrongCount() : 1)
                        .build();

                // details는 cascade로 저장되지 않으므로 직접 추가
                if (wrongWordDTO.getDetails() != null) {
                    for (WrongWordDetailRequestDTO detailDTO : wrongWordDTO.getDetails()) {
                        wrongWord.getDetails().add(TestResultWrongWordDetail.builder()
                                .wrongWord(wrongWord)
                                .pos(detailDTO.getPos())
                                .meanings(detailDTO.getMeanings())
                                .examples(detailDTO.getExamples())
                                .build());
                    }
                }

                saved.getWrongWords().add(wrongWord);
            }
            testResultRepository.save(saved);
        }
    }

    // 대시보드 최근 학습 활동
    @Transactional(readOnly = true)
    public List<RecentActivityDTO> getRecentActivities(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("not found user"));

        return testResultRepository.findTop10ByUserOrderByTakenAtDesc(user)
                .stream()
                .map(RecentActivityDTO::from)
                .collect(Collectors.toList());
    }

    private Vocabularies getVocabAndValidate(Long vocabId, String email) {
        Vocabularies vocab = vocabulariesRepository.findById(vocabId)
                .orElseThrow(() -> new RuntimeException("vocabularies not found"));
        if (!vocab.getUser().getEmail().equals(email)) {
            throw new RuntimeException("user's match error");
        }
        return vocab;
    }

    private String escapeRegex(String word) {
        return word.replaceAll("([\\[\\](){}.*+?^$|\\\\])", "\\\\$1");
    }
}
