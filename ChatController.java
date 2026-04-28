package com.example.chatbot.controller;

import com.example.chatbot.entity.ChatMessage;
import com.example.chatbot.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/history/{userId}")
    public List<ChatMessage> getHistory(@PathVariable Long userId) {
        return chatService.getChatHistory(userId);
    }
}