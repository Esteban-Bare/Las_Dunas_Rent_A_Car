package dev.esteban.mscomments.repository;

import dev.esteban.mscomments.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, Long> {
    List<Comment> findCommentsByVehicleId(String vehicleId);

    Comment findById(String commentId);

    void deleteById(String commentId);

    List<Comment> findByVehicleId(String vehicleId);
}
