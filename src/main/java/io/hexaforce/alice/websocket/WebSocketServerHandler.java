package io.hexaforce.alice.websocket;

import java.io.IOException;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.AIMLProcessorExtensionPC;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.Properties;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebSocketServerHandler extends AbstractWebSocketHandler {

	Chat chatSession;

	@Override
	protected void openSession(WebSocketSession session) {
		log.info(session.getId());

		AIMLProcessor.extension = new AIMLProcessorExtensionPC();

//		String botName = "alice1.5";
		String botName = "alice2";
//		String botName = "アリス";
		String workingDirectory = System.getProperty("user.dir");

		log.info(Properties.program_name_version);
		log.debug("Working Directory = " + workingDirectory);
		Bot bot;
		try {
			bot = new Bot(workingDirectory, botName);
			this.chatSession = new Chat(bot, "0");
			bot.brain.nodeStats();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message, String request) throws Exception {

		String response = chatSession.multisentenceRespond(request);
		while (response.contains("&lt;")) {
			response = response.replace("&lt;", "<");
		}
		while (response.contains("&gt;")) {
			response = response.replace("&gt;", ">");
		}
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
