package dev.esteban.mscomments.service;

import dev.esteban.mscomments.dto.CommentDto;
import dev.esteban.mscomments.model.Comment;
import dev.esteban.mscomments.repository.CommentRepository;
import dev.esteban.mscomments.service.client.MsRentalFeignClient;
import dev.esteban.mscomments.service.client.MsSecurityFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MsRentalFeignClient msRentalFeignClient;

    @Autowired
    private MsSecurityFeignClient msSecurityFeignClient;

    public String saveComment(CommentDto comment) {
        try {
            Comment newComment = new Comment();
            if (comment.getVehicleId() == null) {
                throw new RuntimeException("Vehicle ID cannot be null.");
            }
            if (msRentalFeignClient.getVehicleById(Long.valueOf(comment.getVehicleId())) != null) {
                newComment.setVehicleId(comment.getVehicleId());
            } else {
                throw new RuntimeException("Vehicle with ID " + comment.getVehicleId() + " does not exist.");
            }
            if (comment.getUserId() != null) {
//                if (msSecurityFeignClient.getUserById(Long.valueOf(comment.getUserId())) != null) {
//                    newComment.setUserId(comment.getUserId());
//                } else {
//                    throw new RuntimeException("User with ID " + comment.getUserId() + " does not exist.");
//                }
                try {
                    msSecurityFeignClient.getUserById(Long.valueOf(comment.getUserId()));
                    newComment.setUserId(comment.getUserId());
                } catch (Exception e) {
                    throw new RuntimeException("User with ID " + comment.getUserId() + " does not exist.");
                }
            } else {
                throw new RuntimeException("User ID cannot be null.");
            }
            if (comment.getRating() < 1 || comment.getRating() > 5) {
                throw new RuntimeException("Rating must be between 1 and 5.");
            } else {
                newComment.setRating(comment.getRating());
            }
            if (comment.getComment() == null || comment.getComment().isEmpty()) {
                throw new RuntimeException("Comment cannot be empty.");
            } else {
                newComment.setComment(comment.getComment());
            }
            newComment.setTimestamp(LocalDate.now().toString());
            System.out.println(newComment);
            commentRepository.save(newComment);
            return "Comment saved successfully";
        } catch (Exception e) {
            throw new RuntimeException("Error saving comment: " + e.getMessage());
        }
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }
}
