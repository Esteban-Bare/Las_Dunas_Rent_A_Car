package dev.esteban.msrental.controller;

import dev.esteban.msrental.enums.StatusVehicle;
import dev.esteban.msrental.model.Brand;
import dev.esteban.msrental.model.Category;
import dev.esteban.msrental.model.Store;
import dev.esteban.msrental.model.Vehicle;
import dev.esteban.msrental.repository.VehicleRepository;
import dev.esteban.msrental.service.VehicleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(controllers = VehicleController.class)
@AutoConfigureMockMvc(addFilters = false) // disable Spring Security filters for this slice test
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VehicleRepository vehicleRepository;

    @MockitoBean
    private VehicleService vehicleService;

    private Vehicle buildVehicle(Long id, String model) {
        Brand brand = new Brand("Toyota");
        Category category = new Category("Sedan");
        Store store = new Store("Toulon Centre", "1 Place", "0123456789", "toulon");
        Vehicle v = new Vehicle(model, "PLATE123", new BigDecimal("45.00"),
                StatusVehicle.AVAILABLE, brand, category, store);
        v.setId(id);
        return v;
    }

    @Test
    void getAllVehicles_returnsList() throws Exception {
        when(vehicleRepository.findAll()).thenReturn(List.of(buildVehicle(1L, "Camry")));

        mockMvc.perform(get("/api/rental/vehicles/common/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getVehicleById_returnsDto() throws Exception {
        when(vehicleRepository.findById(anyLong())).thenReturn(Optional.of(buildVehicle(5L, "Corolla")));

        mockMvc.perform(get("/api/rental/vehicles/common/{id}", 5L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model", is("Corolla")));
    }

    @Test
    void getAllVehiclesBackoffice_deniedForUserRole() throws Exception {
        mockMvc.perform(get("/api/rental/vehicles/backoffice/all")
                        .header("X-User-Role", "USER")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Access denied"));
    }
}