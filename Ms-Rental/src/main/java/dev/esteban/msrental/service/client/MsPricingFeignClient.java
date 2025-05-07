package dev.esteban.msrental.service.client;

import dev.esteban.msrental.dto.PriceDto;
import dev.esteban.msrental.dto.VehicleDto;
import dev.esteban.msrental.dto.VehiclePriceDto;
import dev.esteban.msrental.model.Vehicle;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("pricing")
public interface MsPricingFeignClient {

    @RequestMapping(method = RequestMethod.POST, value = "/pricing",   consumes = "application/json")
    ResponseEntity<PriceDto> getPricesByCar(@RequestBody VehiclePriceDto vehicle);
}
