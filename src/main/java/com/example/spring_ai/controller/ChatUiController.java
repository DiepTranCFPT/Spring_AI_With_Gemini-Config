package com.example.spring_ai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatUiController {

    @GetMapping("/")
    public String chatPage() {
        return "chat";
    }

    @GetMapping("/chat-ui")
    public String chatUi() {
        return "chat";
    }
}
