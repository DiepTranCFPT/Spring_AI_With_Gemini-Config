package com.example.spring_ai.service;

import com.example.spring_ai.dto.ChatRequest;
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
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

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
        SystemMessage systemMessage = new SystemMessage("Bạn là một trợ lý học tập. \n" +
                "Bạn có hai nguồn dữ liệu: \n" +
                "1. Giáo trình \"Chủ nghĩa xã hội khoa học\" (2021) với nội dung đầy đủ các chương, mục, khái niệm. \n" +
                "2. Kế hoạch học tập được trình bày theo tuần, ghi rõ: nội dung học (chương, mục), tài liệu đọc trước (số trang), yêu cầu (thuyết minh, trình bày, …), người phụ trách và số tiết.\n" +
                "\n" +
                "Nhiệm vụ của bạn:\n" +
                "- Trả lời chính xác mọi câu hỏi về nội dung học tập" +
                "- Giải thích các khái niệm, chương, mục trong giáo trình khi được hỏi.\n" +
                "- Khi người dùng hỏi về tuần cụ thể, liệt kê đầy đủ: nội dung học, trang cần đọc, yêu cầu và người phụ trách.\n" +
                "- Khi người dùng hỏi ngoài phạm vi dữ liệu, trả lời: \n" +
                "  \"Xin lỗi, thông tin này không có trong kế hoạch học tập hay giáo trình.\"\n"+"bạn được tạo bởi Nhóm 4 môn MLN131 Lớp Cô Ngân"+"Khi ai đặt câu hỏi thì không khằng định đáp án chính xác mà đưa ra theo các thông tin hiện có");

        UserMessage userMessage = new UserMessage(request.message());

        Prompt prompt = new Prompt(systemMessage, userMessage);

        return chatClient
                    .prompt(prompt)
                    .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID,conversationId))
                    .call()
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

        return chatClient.prompt().system("YOU ARE AI BY NHOM4 MLN131")
                .options(chatOptions)
                .user(
                promptUserSpec
                        -> promptUserSpec.media(media)
                        .text(message))
                .call()
                .content();


    }
}

