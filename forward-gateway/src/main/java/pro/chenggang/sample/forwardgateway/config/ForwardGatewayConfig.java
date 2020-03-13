package pro.chenggang.sample.forwardgateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import pro.chenggang.sample.forwardgateway.filter.ForwardGatewayFilter;

/**
 * @author: chenggang
 * @date 2020-03-13.
 */
@Configuration
public class ForwardGatewayConfig {

    @Bean
    public WebClient webClient(){
        return WebClient.builder().build();
    }

    @Bean
    public GlobalFilter forwardGatewayFilter(WebClient webClient, ObjectMapper objectMapper){
        return new ForwardGatewayFilter(webClient,objectMapper);
    }
}
