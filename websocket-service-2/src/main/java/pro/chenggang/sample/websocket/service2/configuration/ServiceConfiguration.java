package pro.chenggang.sample.websocket.service2.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import pro.chenggang.sample.websocket.service2.handler.EchoWebSocketHandler;

import java.util.Collections;
import java.util.Map;

/**
 * @author: chenggang
 * @date 2020-03-13.
 */
@Slf4j
@Configuration
public class ServiceConfiguration {

    @Bean
    public WebSocketHandler echoWebSocketHandler(){
        return new EchoWebSocketHandler();
    }

    @Bean
    public HandlerMapping webSocketHandlerMapping(WebSocketHandler echoWebSocketHandler) {
        Map<String, WebSocketHandler> map = Collections.singletonMap("/echo",echoWebSocketHandler);
        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(1);
        handlerMapping.setUrlMap(map);
        return handlerMapping;
    }
    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

}
