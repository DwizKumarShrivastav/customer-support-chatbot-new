package com.example.chatbot.handler;

import com.example.chatbot.entity.ChatMessage;
import com.example.chatbot.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatService chatService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ConcurrentHashMap<String, Long> sessionUserMap = new ConcurrentHashMap<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        String payload = message.getPayload();
        System.out.println("Incoming: " + payload); // DEBUG

        Map<String, String> data = objectMapper.readValue(payload, Map.class);

        // ✅ Validate request
        if (!data.containsKey("userId") || !data.containsKey("message")) {
            session.sendMessage(new TextMessage("Invalid request"));
            return;
        }

        Long userId = Long.parseLong(data.get("userId"));
        sessionUserMap.put(session.getId(), userId);

        if ("CHAT".equals(data.get("type"))) {

            String userMsg = data.get("message");

            // ✅ Save user message (optional)
            ChatMessage userChat = new ChatMessage();
            userChat.setUserId(userId);
            userChat.setMessage(userMsg);
            userChat.setSender("USER");

            // 👉 Optional save
            // chatService.save(userChat);

            // ✅ FIX: Handle OpenAI/API failure safely
            ChatMessage botResponse;

            try {
                botResponse = chatService.sendMessage(userId, userMsg);
            } catch (Exception e) {
                e.printStackTrace();

                botResponse = new ChatMessage();
                botResponse.setResponse("⚠️ AI service unavailable. Try again later.");
            }

            // ✅ Safety fallback
            String reply = (botResponse != null && botResponse.getResponse() != null)
                    ? botResponse.getResponse()
                    : "Bot: No response generated";

            // ✅ Send response back
            String json = objectMapper.writeValueAsString(Map.of(
                    "type", "RESPONSE",
                    "message", reply,
                    "sender", "BOT"
            ));

            session.sendMessage(new TextMessage(json));
        }
    }
}