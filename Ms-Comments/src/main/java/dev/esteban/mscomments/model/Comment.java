package dev.esteban.mscomments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "comments")
public class Comment {

    @MongoId
    private String id;

    @Field(name = "vehicle_id")
    private String vehicleId;

    @Field(name = "user_id")
    private String userId;


    private Integer rating;

    private String comment;

    private String timestamp;

    public Comment(String vehicleId, String userId, Integer rating, String comment, String timestamp) {
        this.vehicleId = vehicleId;
        this.userId = userId;
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        } else {
            this.rating = rating;
        }
        this.comment = comment;
        this.timestamp = timestamp;
    }

    public void setRating(Integer rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        } else {
            this.rating = rating;
        }
    }
}
