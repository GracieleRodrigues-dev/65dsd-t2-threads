package com.threads.controllers;

import com.threads.services.SseEmitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/traffic")
public class TrafficStreamController {

    @Autowired
    private SseEmitterService sseEmitterService;

    @GetMapping("/stream")
    public SseEmitter stream() {
        String connectionId = String.valueOf(System.currentTimeMillis());
        System.out.println("Novo cliente SSE se conectou: " + connectionId);
        return sseEmitterService.subscribe(connectionId);
    }
}