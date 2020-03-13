package pro.chenggang.sample.forwardgateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.RouteToRequestUrlFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.PathContainer;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import pro.chenggang.sample.forwardgateway.entity.LookupParam;
import pro.chenggang.sample.forwardgateway.entity.LookupResult;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.containsEncodedParts;

/**
 * @author: chenggang
 * @date 2020-03-13.
 */
@Slf4j
@RequiredArgsConstructor
public class ForwardGatewayFilter implements GlobalFilter, Ordered {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
        if (route == null) {
            log.info("No Route Found...");
            return chain.filter(exchange);
        }
        if(!"websocket_route".equals(route.getId())){
            return chain.filter(exchange);
        }
        URI uri = exchange.getRequest().getURI();
        boolean encoded = containsEncodedParts(uri);
        URI routeUri = route.getUri();
        PathContainer pathContainer = exchange.getRequest().getPath().pathWithinApplication();
        String subPath = pathContainer.subPath(3, pathContainer.elements().size()).value();
        LookupParam lookupParam = new LookupParam().setCode(subPath).setOriginalUrl(pathContainer.value());
        String jsonParam = null;
        try {
            jsonParam = objectMapper.writer().writeValueAsString(lookupParam);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return webClient.post()
                .uri("http://127.0.0.1:9010/lookup")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(jsonParam))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> {
                    int httpCode = clientResponse.statusCode().value();
                    return Mono.error(new RuntimeException("Get Dynamic Address Response Status Error,HttpStatus:"+httpCode));
                })
                .bodyToMono(String.class)
                .doOnNext(body->{
                    LookupResult lookupResult = null;
                    try {
                        lookupResult = objectMapper.reader().forType(LookupResult.class).readValue(body);
                    }catch (Exception e){
                        log.error("Get Dynamic Address Error:{}",e.getMessage());
                    }
                    log.debug("Get Dynamic Address Response Body:{}",body);
                    if(null == lookupResult || null == lookupResult.getResult() || !lookupResult.getResult()){
                        log.debug("Get Dynamic Address Result Is Empty");
                        throw new RuntimeException("Get Dynamic Address Result Is Empty");
                    }
                    URI mergedUrl = UriComponentsBuilder.newInstance()
                            .scheme(routeUri.getScheme())
                            .host(lookupResult.getHost())
                            .port(lookupResult.getPort())
                            .path(lookupResult.getPath())
                            .build(encoded)
                            .toUri();
                    exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, mergedUrl);
                    exchange.getAttributes().remove(GATEWAY_ROUTE_ATTR);
                }).then(Mono.defer(()-> chain.filter(exchange)));
    }

    @Override
    public int getOrder() {
        return RouteToRequestUrlFilter.ROUTE_TO_URL_FILTER_ORDER-1;
    }

}
