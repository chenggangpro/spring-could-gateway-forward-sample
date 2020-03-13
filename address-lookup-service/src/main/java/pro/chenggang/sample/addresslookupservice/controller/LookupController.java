package pro.chenggang.sample.addresslookupservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pro.chenggang.sample.addresslookupservice.entity.LookupParam;
import pro.chenggang.sample.addresslookupservice.entity.LookupResult;
import reactor.core.publisher.Mono;

/**
 * @author: chenggang
 * @date 2020-03-13.
 */
@Slf4j
@RestController
public class LookupController {

    @PostMapping("/lookup")
    public Mono<LookupResult> lookup(@RequestBody Mono<LookupParam> paramMap){
        return paramMap
                .map(data->{
                    log.info("[Lookup Service]ParamData:{}",data);
                    return getWebSocketUrlResult(data.getCode());
                });

    }

    private LookupResult getWebSocketUrlResult(String code){
        if(StringUtils.isEmpty(code)){
            return new LookupResult().setResult(false);
        }
        switch (code){
            case "type1":
                return new LookupResult().setResult(true).setHost("127.0.0.1").setPort(9001).setPath("/echo");
            case "type2":
                return new LookupResult().setResult(true).setHost("127.0.0.1").setPort(9002).setPath("/echo");
            default:
                return new LookupResult().setResult(false);
        }
    }
}
