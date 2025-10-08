package com.example.spring_ai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Service
public class GeminiChatService {

    @Value("${gemini.api.key:AIzaSyBtiHfL1ItVV_FaU-SiVS-YWNfcumt8rgY}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String chat(String message) {
        // Try different model names that might work with your API key
        String[] modelNames = {
                "gemini-2.0-flash"
        };

        for (String modelName : modelNames) {
            try {
                String response = tryModelRequest(modelName, message);
                if (response != null && !response.contains("404") && !response.contains("NOT_FOUND")) {
                    return response;
                }
            } catch (Exception e) {
                // Continue to next model
                continue;
            }
        }

        // If all models fail, return helpful fallback response
        return getFallbackResponse(message);
    }

    private String tryModelRequest(String modelName, String message) {
        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + apiKey;

            // Create request body
            ObjectNode requestBody = objectMapper.createObjectNode();
            ArrayNode contents = objectMapper.createArrayNode();
            ObjectNode content = objectMapper.createObjectNode();
            ArrayNode parts = objectMapper.createArrayNode();
            ObjectNode part = objectMapper.createObjectNode();

            // Updated system prompt - Real English tutor inspired by Uncle Ho's spirit
            String systemPrompt = """
                You are "UNCLE HO ENGLISH MENTOR" - A professional English tutor inspired by Ho Chi Minh's spirit of learning and teaching.
                
                🎯 IMPORTANT RULES:
                - ALWAYS respond in ENGLISH ONLY (except for Vietnamese words being explained)
                - Act like a real English tutor/teacher
                - Be professional, patient, and encouraging
                - Focus ONLY on English language learning topics
                - If asked non-English questions, politely redirect: "I'm sorry, but I'm here to help you learn English. Please ask me about vocabulary, grammar, pronunciation, or English conversation skills."
                
                📚 YOUR TEACHING EXPERTISE:
                1. VOCABULARY BUILDING:
                   - Explain word meanings clearly
                   - Provide pronunciation guides with IPA
                   - Give 3-4 example sentences
                   - Explain word origins when helpful
                   - Teach synonyms and antonyms
                
                2. GRAMMAR INSTRUCTION:
                   - Explain grammar rules step by step
                   - Use clear examples
                   - Provide practice exercises
                   - Correct mistakes gently
                   - Focus on practical usage
                
                3. PRONUNCIATION TRAINING:
                   - Use International Phonetic Alphabet (IPA)
                   - Break down syllables
                   - Explain stress patterns
                   - Compare with Vietnamese sounds when helpful
                   - Give practice tips
                
                4. TRANSLATION SERVICES:
                   - Translate Vietnamese to English accurately
                   - Explain cultural context when needed
                   - Provide multiple translation options (formal/informal)
                   - Explain why certain translations work better
                
                5. CONVERSATION SKILLS:
                   - Teach practical phrases
                   - Role-play scenarios
                   - Correct speaking mistakes
                   - Build confidence in communication
                
                🌟 TEACHING STYLE:
                - Always be encouraging and positive
                - Use simple, clear explanations
                - Provide step-by-step guidance
                - Give practical examples students can use
                - Make learning enjoyable and memorable
                - Use Uncle Ho's wisdom about learning: "Learning is a lifelong journey"
                
                💡 RESPONSE FORMAT:
                - Start with a warm greeting if it's a new conversation
                - Give clear, structured answers
                - Use bullet points or numbered lists when helpful
                - End with encouragement or a follow-up question
                - Always invite more questions
                
                ❌ DO NOT:
                - Answer questions unrelated to English learning
                - Give medical, legal, or personal advice
                - Discuss politics (except for English vocabulary related to governance)
                - Provide information on non-English topics
                
                Remember: You're here to help students master English with the same dedication Uncle Ho showed in his own language learning journey!
                
                Student's question: """ + message;

            part.put("text", systemPrompt);
            parts.add(part);
            content.set("parts", parts);
            contents.add(content);
            requestBody.set("contents", contents);

            // Set generation config
            ObjectNode generationConfig = objectMapper.createObjectNode();
            generationConfig.put("temperature", 0.7);
            generationConfig.put("maxOutputTokens", 2048);
            requestBody.set("generationConfig", generationConfig);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

            // Make API call
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            // Parse response
            ObjectNode responseJson = (ObjectNode) objectMapper.readTree(response.getBody());
            if (responseJson.has("candidates") && responseJson.get("candidates").isArray() &&
                responseJson.get("candidates").size() > 0) {

                ObjectNode candidate = (ObjectNode) responseJson.get("candidates").get(0);
                if (candidate.has("content") && candidate.get("content").has("parts") &&
                    candidate.get("content").get("parts").isArray() &&
                    candidate.get("content").get("parts").size() > 0) {

                    return candidate.get("content").get("parts").get(0).get("text").asText();
                }
            }

            return null; // Try next model

        } catch (Exception e) {
            return null; // Try next model
        }
    }

    private String getFallbackResponse(String message) {
        // Provide helpful English learning responses even when API fails - ALL IN ENGLISH
        if (message.toLowerCase().contains("translate") || message.contains("dịch")) {
            if (message.contains("Độc lập - Tự do - Hạnh phúc") || message.contains("độc lập")) {
                return "Hello! I'm your Uncle Ho English Mentor! 🇻🇳\n\n" +
                       "**TRANSLATION LESSON:**\n\n" +
                       "\"**Độc lập - Tự do - Hạnh phúc**\" translates to:\n\n" +
                       "🎯 **\"Independence - Freedom - Happiness\"**\n\n" +
                       "**VOCABULARY BREAKDOWN:**\n" +
                       "• **Độc lập** = Independence /ˌɪndɪˈpendəns/\n" +
                       "• **Tự do** = Freedom /ˈfriːdəm/\n" +
                       "• **Hạnh phúc** = Happiness /ˈhæpɪnəs/\n\n" +
                       "**GRAMMAR:** This is a series of abstract nouns connected by dashes\n\n" +
                       "**CULTURAL NOTE:** This motto represents the highest ideals of the Vietnamese people!\n\n" +
                       "Would you like to practice using these words in sentences? 🚀";
            }
        }

        if (message.toLowerCase().contains("yêu nước") || message.toLowerCase().contains("patriot")) {
            return "Hello! I'm your Uncle Ho English Mentor! 🇻🇳\n\n" +
                   "**PATRIOTISM VOCABULARY LESSON:**\n\n" +
                   "• **Yêu nước** = Patriotism /ˈpeɪtrɪətɪzəm/\n" +
                   "• **Người yêu nước** = Patriot /ˈpeɪtrɪət/\n" +
                   "• **Tình yêu đất nước** = Love for one's country\n" +
                   "• **Lòng yêu nước** = Patriotic spirit\n\n" +
                   "**EXAMPLE SENTENCES:**\n" +
                   "1. \"He showed great **patriotism** during the war.\"\n" +
                   "2. \"Uncle Ho was a true **patriot** of Vietnam.\"\n" +
                   "3. \"Patriotic **spirit** runs deep in Vietnamese culture.\"\n\n" +
                   "**PRONUNCIATION TIP:** Stress the first syllable: **PA**-tri-ot-ism\n\n" +
                   "Can you make a sentence using 'patriot'? 🎯";
        }

        if (message.toLowerCase().contains("independence") || message.toLowerCase().contains("pronunciation")) {
            return "Hello! I'm your Uncle Ho English Mentor! 🇻🇳\n\n" +
                   "**PRONUNCIATION LESSON: INDEPENDENCE**\n\n" +
                   "🔤 **IPA:** /ˌɪndɪˈpendəns/\n\n" +
                   "**HOW TO PRONOUNCE:**\n" +
                   "• **In** - /ɪn/ (short 'i' sound)\n" +
                   "• **de** - /dɪ/ (weak 'di' sound)\n" +
                   "• **PEN** - /ˈpen/ (STRESSED syllable)\n" +
                   "• **dence** - /dəns/ (weak ending)\n\n" +
                   "**STRESS PATTERN:** Stress the 3rd syllable: inde-**PEN**-dence\n\n" +
                   "**PRACTICE SENTENCES:**\n" +
                   "1. \"Vietnam gained **independence** in 1945.\"\n" +
                   "2. \"**Independence** Day is celebrated annually.\"\n" +
                   "3. \"The **independence** declaration was historic.\"\n\n" +
                   "**TIP:** Break it down: In-de-PEN-dence\n\n" +
                   "Try saying it slowly, then faster! 🎯";
        }

        // Default fallback response - ALL IN ENGLISH
        return "Hello! I'm your **Uncle Ho English Mentor**! 🇻🇳\n\n" +
               "I'm currently experiencing some technical issues with the AI system, but I'm still here to help you learn English!\n\n" +
               "📚 **TRY ASKING ME ABOUT:**\n" +
               "• \"Translate: Độc lập - Tự do - Hạnh phúc\"\n" +
               "• \"How do you say 'yêu nước' in English?\"\n" +
               "• \"Teach me pronunciation of 'independence'\"\n" +
               "• \"Grammar: How to use past tense?\"\n" +
               "• \"What does 'freedom' mean?\"\n\n" +
               "🎯 **I CAN HELP YOU WITH:**\n" +
               "✅ English vocabulary and meanings\n" +
               "✅ Grammar explanations and examples\n" +
               "✅ Pronunciation with IPA guides\n" +
               "✅ Translation from Vietnamese to English\n" +
               "✅ Conversation practice and tips\n\n" +
               "Remember: \"Learning is a lifelong journey!\" - Uncle Ho's wisdom\n\n" +
               "What would you like to learn about English today? 🚀";
    }
}
