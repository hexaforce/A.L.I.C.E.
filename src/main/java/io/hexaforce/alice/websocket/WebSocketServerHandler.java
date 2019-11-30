package io.hexaforce.alice.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import io.hexaforce.alice.AB;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebSocketServerHandler extends AbstractWebSocketHandler {
	
	@Override
	protected void openSession(WebSocketSession session) {
		log.info(session.getId());
		AB.mainFunction();
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message, String request) throws Exception {
		String response = AB.chatSession.multisentenceRespond(request);
		while (response.contains("&lt;"))
			response = response.replace("&lt;", "<");
		while (response.contains("&gt;"))
			response = response.replace("&gt;", ">");
		session.sendMessage(new TextMessage(response, true));
	}

	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message, byte[] binary) throws Exception {
		
	}

	@Override
	protected void closeSession(WebSocketSession session) {
		log.info(session.getId());
		
	}

}
