package com.example.chatbot.service;

import com.example.chatbot.entity.ChatMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ChatService {

    private final RestTemplate restTemplate = new RestTemplate();

    public ChatMessage sendMessage(Long userId, String message) {

        String url = "http://localhost:11434/api/generate";

        Map<String, Object> request = Map.of(
                "model", "llama3",
                "prompt", message,
                "stream", false
        );

        Map response = restTemplate.postForObject(url, request, Map.class);

        String aiReply = response != null && response.get("response") != null
                ? response.get("response").toString()
                : "No response from AI";

        ChatMessage chat = new ChatMessage();
        chat.setUserId(userId);
        chat.setMessage(message);
        chat.setSender("BOT");
        chat.setResponse(aiReply);

        return chat;
    }
}