package pro.chenggang.sample.addresslookupservice.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author: chenggang
 * @date 2020-03-13.
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class LookupResult {

    private Boolean result;
    private String host;
    private Integer port;
    private String path;
}
