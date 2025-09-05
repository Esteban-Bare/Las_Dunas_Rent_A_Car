package dev.esteban.mscomments;

import dev.esteban.mscomments.model.Comment;
import dev.esteban.mscomments.repository.CommentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
@EnableFeignClients
public class MsCommentsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsCommentsApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(CommentRepository commentRepository) {
        return args -> {
            commentRepository.deleteAll();
            Comment comment1 = new Comment("1","2", 4, "Comment 1", LocalDate.now().toString());
            Comment comment2 = new Comment("2","3", 1, "Comment 2", LocalDate.now().toString());
            Comment comment2v2 = new Comment("2","1", 3, "Comment 2", LocalDate.now().toString());
            Comment comment3 = new Comment("3","1", 5, "Comment 3", LocalDate.now().toString());
            commentRepository.save(comment1);
            commentRepository.save(comment2);
            commentRepository.save(comment2v2);
            commentRepository.save(comment3);
        };
    }
}
