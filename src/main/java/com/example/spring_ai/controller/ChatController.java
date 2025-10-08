package com.example.spring_ai.controller;

import com.example.spring_ai.dto.ChatRequest;
import com.example.spring_ai.service.GeminiChatService;
import com.example.spring_ai.service.ImageAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private GeminiChatService geminiChatService;

    @Autowired
    private ImageAnalysisService imageAnalysisService;

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody ChatRequest request) {
        try {
            String response = geminiChatService.chat(request.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Xin lỗi, đã có lỗi xảy ra. Vui lòng thử lại sau.");
        }
    }

    @PostMapping("/chat-with-image")
    public ResponseEntity<String> chatWithImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("message") String message) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Vui lòng chọn một hình ảnh.");
            }

            String response = imageAnalysisService.analyzeImageWithMessage(file, message);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Xin lỗi, đã có lỗi xảy ra khi xử lý hình ảnh. Vui lòng thử lại sau.");
        }
    }
}
