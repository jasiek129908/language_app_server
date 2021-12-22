package com.example.server_foregin_languages.sockets;

import com.example.server_foregin_languages.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@AllArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic/","/queue");
        //wszystko co sie zaczyna na /app bedzie szlo do kontrolleraara
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/duel").setHandshakeHandler(new CustomHandshakeHandler()).setAllowedOrigins("http://localhost:4200","http://192.168.0.103:4200");
        registry.addEndpoint("/duel").setHandshakeHandler(new CustomHandshakeHandler()).setAllowedOrigins("*");
    }
}
