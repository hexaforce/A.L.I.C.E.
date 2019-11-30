package io.hexaforce.alice.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebSocketServerHandler extends AbstractWebSocketHandler {

	@Override
	protected void openSession(WebSocketSession session) {
		log.info(session.getId());
		
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message, String text) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message, byte[] binary) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void closeSession(WebSocketSession session) {
		log.info(session.getId());
		
	}

}
