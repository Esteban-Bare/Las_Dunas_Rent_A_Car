package dev.esteban.mscomments.controller;

import dev.esteban.mscomments.dto.CommentDto;
import dev.esteban.mscomments.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    public ResponseEntity<?> addComment(@RequestBody CommentDto comment) {
        try {
            String response = commentService.saveComment(comment);
            return ResponseEntity.ok().body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllComments() {
        try {
            return ResponseEntity.ok().body(commentService.getAllComments());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<?> getCommentsByVehicleId(@PathVariable String vehicleId) {
        try {
            return ResponseEntity.ok().body(commentService.getCommentsByVehicleId(vehicleId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }
}
