package com.example.AILearning.miniProjectOne;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.content.Media;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

@RestController
public class MiniProjectOneController {

    private final ChatClient chatClient;

    public MiniProjectOneController(ChatClient.Builder chatClientBuilder ) {
        this.chatClient = chatClientBuilder.build();
    }


    @PostMapping("/analyze-resume")
    public ResumeAnalysis analyzeResume(@RequestParam String jobRole, @RequestParam MultipartFile resume) throws IOException {

        byte[] imageBytes = resume.getBytes();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        MimeType imageType = MimeTypeUtils.parseMimeType(Objects.requireNonNull(resume.getContentType()));
        var imageCon = new Media(imageType,new ByteArrayResource(imageBytes));

        return chatClient.prompt().system("you are an excellent resume reviewer," +
                "review the attached resume and job role and provide the ample response like mentioned in the class")
                .user(u-> u.text(jobRole).media(imageCon)).call().entity(ResumeAnalysis.class);


    }
}
