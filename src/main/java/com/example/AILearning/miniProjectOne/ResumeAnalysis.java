package com.example.AILearning.miniProjectOne;

import java.util.List;

public record ResumeAnalysis(
        String candidateName,
        int matchScore,
        List<String> strengths,
        List<String> gaps,
        String recommendation,
        List<String> suggestedQuestions
) {
}
