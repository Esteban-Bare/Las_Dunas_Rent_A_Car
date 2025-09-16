package dev.esteban.msrental.service.client;

import dev.esteban.msrental.configuration.FeignClientConfig;
import dev.esteban.msrental.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "security", configuration = FeignClientConfig.class)
public interface MsSecurityFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/user/{id}")
    ResponseEntity<UserDto> getUserById(@PathVariable Long id);
}
