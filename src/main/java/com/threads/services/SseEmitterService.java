package com.threads.services;

import com.threads.models.Vehicle;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseEmitterService {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String connectionId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(connectionId, emitter);
        System.out.println("Cliente conectado: " + connectionId);

        emitter.onCompletion(() -> {
            System.out.println("Conexão encerrada: " + connectionId);
            emitters.remove(connectionId);
        });

        emitter.onTimeout(() -> {
            System.out.println("Conexão expirada: " + connectionId);
            emitters.remove(connectionId);
        });

        emitter.onError((e) -> {
            System.out.println("Erro na conexão SSE: " + e.getMessage());
            emitters.remove(connectionId);
        });

        return emitter;
    }


    public void sendPositionUpdate(Vehicle vehicle) {
    	System.out.println("emitters size: " + emitters.size());
    	for (Map.Entry<String, SseEmitter> entry : emitters.entrySet()) {
    	    System.out.println("Emitters key: " + entry.getKey() + ", value: " + entry.getValue());
    	}
    	
        emitters.entrySet().removeIf(entry -> {
            try {
            	System.out.println("BATATA: " + entry.getValue());
            	
                entry.getValue().send(SseEmitter.event()
                    .name("vehicle-update")
                    .data("Veículo ID: " + vehicle.getId() + " na posição " + vehicle.getCurrentPosition()));
                return false;
            } catch (IOException e) {
                System.err.println("Erro ao enviar veículo via SSE: " + e.getMessage());
                return true; // remove this emitter
            }
        });
    }
}
