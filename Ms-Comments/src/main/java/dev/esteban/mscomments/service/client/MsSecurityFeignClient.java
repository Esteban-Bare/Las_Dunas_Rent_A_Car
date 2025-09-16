package dev.esteban.mscomments.service.client;

import dev.esteban.mscomments.cofiguration.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "security", configuration = FeignClientConfig.class)
public interface MsSecurityFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/user/{id}")
    ResponseEntity<?> getUserById(@PathVariable Long id);
}
