package dev.esteban.msrental.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.esteban.msrental.enums.StatusVehicle;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String model;

    @Column(name = "plateNumber", nullable = false)
    private String plateNumber;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusVehicle status;

    @Column(name = "price_par_day", precision = 10, scale = 2)
    private BigDecimal pricePerDay;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    @JsonBackReference
    private Store store;

    public Vehicle(String model, String plateNumber, BigDecimal pricePerDay,StatusVehicle status,Brand brand, Category category, Store store) {
        this.model = model;
        this.plateNumber = plateNumber;
        this.pricePerDay = pricePerDay;
        this.status = status;
        this.brand = brand;
        this.category = category;
        this.store = store;
    }
}
