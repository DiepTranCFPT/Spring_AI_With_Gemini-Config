package com.example.spring_ai.controller;

import com.example.spring_ai.dto.ChatRequest;
import com.example.spring_ai.service.ChatService;
import com.example.spring_ai.service.ImageAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ImageAnalysisService imageAnalysisService;

    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequest request) {
        return chatService.chat(request);
    }

    @PostMapping("/chat-with-image")
    public String chatWithImage(@RequestParam("file") MultipartFile file,
                                @RequestParam("message") String message) {
        return chatService.chatWithImage(file, message);
    }

    @PostMapping("/upload-image")
    public ResponseEntity<String> analyzeImage(@RequestParam("image") MultipartFile file) {
        try {
            if (file == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy file ảnh");
            }

            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Vui lòng chọn file ảnh");
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("File không phải là ảnh hợp lệ");
            }

            // Kiểm tra kích thước file (ví dụ: giới hạn 10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("Kích thước file quá lớn (tối đa 10MB)");
            }

            boolean isRelated = imageAnalysisService.isImageRelatedToHoChiMinh(file);
            if (isRelated) {
                return ResponseEntity.ok("Đây là hình ảnh liên quan đến môn học Tư tưởng Hồ Chí Minh. Bạn có thể đặt câu hỏi.");
            } else {
                return ResponseEntity.ok("Xin lỗi, hình ảnh này không liên quan đến môn học Tư tưởng Hồ Chí Minh.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi xử lý file: " + e.getMessage());
        }
    }
}
