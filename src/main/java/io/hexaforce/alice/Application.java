package io.hexaforce.alice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import io.hexaforce.alice.websocket.WebSocketServerHandler;
import lombok.Data;

@SpringBootApplication
public class Application {

	@Autowired
	private WebSocketServerHandler webSocketServerHandler;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Configuration
	@EnableWebSocket
	public class WebSocketConfig implements WebSocketConfigurer {
		@Override
		public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
			registry.addHandler(webSocketServerHandler, WebSocketServerHandler.PATH);
		}
	}

	@Data
	@Component
	@ConfigurationProperties("org.alicebot.ab")
	public class ApplicationProperties {

		// SUNABA URL
		private String sunabaUtterance;
		private String sunabaApplicationid;

	}

}
