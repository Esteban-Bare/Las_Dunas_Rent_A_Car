package dev.esteban.mspromo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "promotions")
public class Promotion {

    @MongoId
    @Field(name = "promo_id")
    private String promoId;

    @Field(name = "vehicle_id")
    private String vehicleId;

    private String title;

    private String description;

    @Field(name = "start_date")
    private String startDate;

    @Field(name = "end_date")
    private String endDate;

    @Field(name = "discount_percentage")
    private int discountPercentage;

    public Promotion(String vehicleId, String title, String description, String startDate, String endDate, int discountPercentage) {
        this.vehicleId = vehicleId;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.discountPercentage = discountPercentage;
    }
}
