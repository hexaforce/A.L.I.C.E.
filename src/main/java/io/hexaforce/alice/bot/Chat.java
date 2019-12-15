package io.hexaforce.alice.bot;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Chat {
	final Bot bot;
	String multiSentenceRespond(String request) {
		return request;
	}
}
