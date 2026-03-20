package com.example.AILearning;


import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vectors")
public class VectorController {


    private final VectorStore vectorStore;

    public VectorController(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostMapping("/store")
    public String store(@RequestBody List<String> texts) {
        List<Document> documents = texts.stream().map(Document::new).toList();
        vectorStore.add(documents);
        return "strored" + documents.size() + " documents";
    }

    @GetMapping("/search")
    public List<String> similaritySearch(@RequestParam String query) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(5)
                .similarityThreshold(0.0).build();

        return vectorStore.similaritySearch(query).stream().map(Document::getText).toList();
    }
}
