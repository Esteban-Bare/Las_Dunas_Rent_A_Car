package dev.esteban.msrental.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.esteban.msrental.enums.StatusVehicle;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String plate_number;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusVehicle status;

    @Column(precision = 10, scale = 2)
    private BigDecimal price_per_day;

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    @JsonManagedReference
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @JsonManagedReference
    private Category category;

    @OneToMany(mappedBy = "vehicle")
    private Set<Reservation> reservations;

    public Vehicle(String model, String plate_number, BigDecimal price_per_day,StatusVehicle status,Brand brand, Category category) {
        this.model = model;
        this.plate_number = plate_number;
        this.price_per_day = price_per_day;
        this.status = status;
        this.brand = brand;
        this.category = category;
    }
}
