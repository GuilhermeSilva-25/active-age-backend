package com.activeage.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class SignalingWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(SignalingWebSocketHandler.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, CopyOnWriteArraySet<WebSocketSession>> rooms = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> decoratedSessions = new ConcurrentHashMap<>();

    private WebSocketSession getDecoratedSession(WebSocketSession session) {
        return decoratedSessions.computeIfAbsent(session.getId(), id -> 
            new ConcurrentWebSocketSessionDecorator(session, 5000, 512 * 1024, ConcurrentWebSocketSessionDecorator.OverflowStrategy.DROP)
        );
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        getDecoratedSession(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession rawSession, TextMessage message) throws Exception {
        WebSocketSession session = getDecoratedSession(rawSession);
        try {
            Map<String, Object> payload = mapper.readValue(message.getPayload(), Map.class);
            String type = (String) payload.get("type");
            String roomId = (String) payload.get("roomId");

            if (type == null) {
                return;
            }

            if ("ping".equals(type)) {
                session.sendMessage(new TextMessage("{\"type\":\"pong\"}"));
                return;
            }

            if (roomId == null) {
                return;
            }

            rooms.putIfAbsent(roomId, new CopyOnWriteArraySet<>());
            CopyOnWriteArraySet<WebSocketSession> sessionsInRoom = rooms.get(roomId);

            sessionsInRoom.removeIf(s -> !s.isOpen());

            switch (type) {
                case "join":
                    if (sessionsInRoom.size() >= 2 && !sessionsInRoom.contains(session)) {
                        session.sendMessage(new TextMessage(mapper.writeValueAsString(Map.of(
                                "type", "error",
                                "message", "A sala de consulta ja atingiu o limite maximo de participantes."
                        ))));
                        session.close(new CloseStatus(4003, "Room full"));
                        return;
                    }

                    sessionsInRoom.add(session);
                    session.getAttributes().put("roomId", roomId);

                    session.sendMessage(new TextMessage(mapper.writeValueAsString(Map.of(
                            "type", "joined",
                            "roomId", roomId,
                            "participantCount", sessionsInRoom.size()
                    ))));

                    if (sessionsInRoom.size() > 1) {
                        for (WebSocketSession s : sessionsInRoom) {
                            if (s.isOpen() && !s.getId().equals(session.getId())) {
                                s.sendMessage(new TextMessage(mapper.writeValueAsString(Map.of(
                                        "type", "user-joined",
                                        "roomId", roomId
                                ))));
                            }
                        }
                    }
                    break;

                case "offer":
                case "answer":
                case "ice-candidate":
                case "chat-message":
                    for (WebSocketSession peer : sessionsInRoom) {
                        if (peer.isOpen() && !peer.getId().equals(session.getId())) {
                            peer.sendMessage(message);
                        }
                    }
                    break;

                case "leave":
                    handleUserLeave(session);
                    break;

                default:
                    logger.warn("Tipo WebSocket desconhecido: {}", type);
            }
        } catch (Exception e) {
            logger.error("Erro no processamento da mensagem de sinalizacao: ", e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        handleUserLeave(session);
        decoratedSessions.remove(session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        handleUserLeave(session);
        decoratedSessions.remove(session.getId());
    }

    private void handleUserLeave(WebSocketSession session) {
        String roomId = (String) session.getAttributes().get("roomId");
        if (roomId != null && rooms.containsKey(roomId)) {
            CopyOnWriteArraySet<WebSocketSession> sessionsInRoom = rooms.get(roomId);
            sessionsInRoom.remove(session);

            for (WebSocketSession peer : sessionsInRoom) {
                if (peer.isOpen()) {
                    try {
                        peer.sendMessage(new TextMessage(mapper.writeValueAsString(Map.of(
                                "type", "user-left",
                                "roomId", roomId
                        ))));
                    } catch (IOException e) {
                        logger.error("Erro ao enviar user-left: ", e);
                    }
                }
            }

            if (sessionsInRoom.isEmpty()) {
                rooms.remove(roomId);
            }
        }
    }
}
