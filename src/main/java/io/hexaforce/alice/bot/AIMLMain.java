package io.hexaforce.alice.bot;

import java.io.IOException;

public class AIMLMain {

	public static void main(String[] args) throws IOException {

		final Bot bot = new Bot();
		final Chat chatSession = new Chat(bot);
		
		System.out.print(chatSession.multiSentenceRespond("こんにちは"));
	}

}
