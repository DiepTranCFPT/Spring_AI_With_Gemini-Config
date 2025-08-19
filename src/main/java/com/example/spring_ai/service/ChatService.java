package com.example.spring_ai.service;

import com.example.spring_ai.dto.ChatRequest;
import com.example.spring_ai.dto.Info;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
public class ChatService {
    private final ChatClient chatClient;

    private final JdbcChatMemoryRepository jdbcChatMemoryRepository;


    public ChatService(ChatClient.Builder builder, JdbcChatMemoryRepository jdbcChatMemoryRepository) {
        this.jdbcChatMemoryRepository = jdbcChatMemoryRepository;

        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .maxMessages(30)
                .build();

        chatClient = builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();

    }

    public String chat(ChatRequest request){
        String conversationId = "conversationId01";
        SystemMessage systemMessage = new SystemMessage(" YOU ARE AI CRE by DIEP" +
                "YOU ARE DP.AI" +
                "Year : 2025");

        UserMessage userMessage = new UserMessage(request.message());

        Prompt prompt = new Prompt(systemMessage, userMessage);

        return chatClient
                    .prompt(prompt)
                    .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID,conversationId))
                    .call()

//                    .entity((new ParameterizedTypeReference<List<Info>>() {//build with json de luu DB
//                    }));
                    .content();
    }

    public String chatWithImage(MultipartFile file, String message) {
        Media media = Media.builder()
                .mimeType(MimeTypeUtils.parseMimeType(Objects.requireNonNull(file.getContentType())))
                .data(file.getResource())
                .build();

        ChatOptions chatOptions = ChatOptions.builder()
                .temperature(0.0)// do chinh xac
                .build();

        return chatClient.prompt().system("YOU ARE DP.AI")
                .options(chatOptions)
                .user(
                promptUserSpec
                        -> promptUserSpec.media(media)
                        .text(message))
                .call()
                .content();


    }
}
