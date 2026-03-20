package com.example.AILearning;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.content.Media;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;


import java.io.IOException;
import java.util.Base64;
import java.util.Map;

@RestController
public class AiController {

    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;
    private final ChatMemory chatMemory;
    private final SafetyAdvisor safetyAdvisor;
    private final AppTool appTool;

    public AiController(ChatClient.Builder chatClientBuilder, EmbeddingModel embeddingModel, ChatMemory chatMemory, SafetyAdvisor safetyAdvisor, AppTool appTool) {
        this.chatClient = chatClientBuilder.build();
        this.embeddingModel = embeddingModel;
        this.chatMemory = chatMemory;
        this.safetyAdvisor = safetyAdvisor;
        this.appTool = appTool;
    }

    @GetMapping("/ai")
    String firstPrompt(@RequestParam String prompt) {
       return this.chatClient.prompt()
               .system("\"You are Aria, a helpful banking assistant for HDFC Bank. \" +\n" +
                       "                \"Only answer questions related to banking, accounts, loans, and finance. \" +\n" +
                       "                \"If asked anything outside banking, politely decline.\"")
               .user(prompt).call().content();
    }


    @GetMapping("/structured")
    public Developer structuredOutput(@RequestParam String query) {
       return this.chatClient.prompt().user(query).call().entity(Developer.class);
    }



    @GetMapping("/job-posting")
    public JobPosting structJobPosting(@RequestParam String prompt) {
        return this.chatClient.prompt().user(prompt).call().entity(JobPosting.class);
    }



    @PostMapping("/image")
    public String analyzeImage(@RequestParam String prompt,
                               @RequestParam MultipartFile image) throws IOException {
        byte[] imageBytes = image.getBytes();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        MimeType mimeType = MimeTypeUtils.parseMimeType(
                image.getContentType()
        );

        var imageContent = new Media(mimeType, new ByteArrayResource((imageBytes)));
        return chatClient.prompt()
                .user(u->u.text(prompt).media(imageContent)).call().content();


    }

    @GetMapping("/chat")
    public String chat(@RequestParam String message, @RequestParam String conversationId) {
        return chatClient.prompt().user(message)
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory).conversationId(conversationId).build())
                .call().content();
    }


    @GetMapping("/smart-chat")
    public String smartChat(@RequestParam String message, @RequestParam String conversationId) {
        return chatClient.prompt().user(message).advisors(
               safetyAdvisor,
               MessageChatMemoryAdvisor.builder(chatMemory)
                       .conversationId(conversationId).build(),
                new SimpleLoggerAdvisor()


        ).call().content();


    }


    @GetMapping("/agent")
    public String agenticReply(@RequestParam String prompt) {
        return chatClient.prompt()
                .system("""
                You are a helpful assistant with access to tools.
                Use tools ONLY when you need real time data or
                specific information from the tools.
                For general knowledge questions answer directly
                from your training data without using any tools.
                When using tool results, always include the COMPLETE 
                information returned by the tool — do not summarize 
                or paraphrase tool responses.
                """)
                .user(prompt).tools(appTool).call().content();
    }


    @GetMapping(value = "/streaming-response",  produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam String prompt) {
        return this.chatClient.prompt().user(prompt).stream().content();
    }


























    @GetMapping("/embed-texts")
    public float[] embed(@RequestParam String text) {
        return embeddingModel.embed(text);
    }
    @GetMapping("/embedding/similarity")
    public Map<String, Object> similarity(
            @RequestParam String text1,
            @RequestParam String text2) {

        float[] vec1 = embeddingModel.embed(text1);
        float[] vec2 = embeddingModel.embed(text2);
        double score = cosineSimilarity(vec1, vec2);

        return Map.of(
                "text1", text1,
                "text2", text2,
                "similarity", Math.round(score * 100.0) / 100.0,
                "interpretation", interpret(score)
        );
    }

    private double cosineSimilarity(float[] a, float[] b) {
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot   += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private String interpret(double score) {
        if (score > 0.9)  return "Nearly identical";
        if (score > 0.75) return "Very similar";
        if (score > 0.5)  return "Somewhat related";
        return "Not related";
    }

}
