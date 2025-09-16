package dev.esteban.mscomments.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("rental")
public interface MsRentalFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/rental/vehicles/common/{id}")
    Object getVehicleById(@PathVariable Long id);
}
