package dev.esteban.mscomments.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private String vehicleId;
    private String userId;
    private Integer rating;
    private String comment;
}
