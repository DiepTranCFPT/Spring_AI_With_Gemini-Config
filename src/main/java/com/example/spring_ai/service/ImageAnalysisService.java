package com.example.spring_ai.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageAnalysisService {

    @Autowired
    private GeminiChatService geminiChatService;

    public String analyzeImageWithMessage(MultipartFile file, String message) {
        try {
            // Create English prompt for image analysis - maintaining consistency with our English tutor
            String prompt = "I have received an image file named: " + file.getOriginalFilename() +
                          " (size: " + file.getSize() + " bytes). " +
                          "While I cannot analyze images directly at the moment, I can help you with your English learning question: " + message +
                          "\n\nPlease help me learn English related to this question.";

            return geminiChatService.chat(prompt);

        } catch (Exception e) {
            return "Hello! I'm your Uncle Ho English Mentor! 🇻🇳\n\n" +
                   "I received your image (" + file.getOriginalFilename() + "), but I'm currently experiencing technical difficulties with image processing.\n\n" +
                   "However, I can still help you with your English learning question: \"" + message + "\"\n\n" +
                   "📚 **LET ME HELP YOU WITH ENGLISH:**\n" +
                   "• Vocabulary explanations and meanings\n" +
                   "• Grammar rules and examples\n" +
                   "• Pronunciation guides with IPA\n" +
                   "• Translation from Vietnamese to English\n" +
                   "• Conversation practice\n\n" +
                   "Please ask your English question again, and I'll be happy to help! 🚀\n\n" +
                   "Remember: \"Learning is a lifelong journey!\" - Uncle Ho's wisdom";
        }
    }
}
