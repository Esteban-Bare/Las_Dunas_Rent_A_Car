package dev.esteban.mssecurity;

import dev.esteban.mssecurity.model.User;
import dev.esteban.mssecurity.repository.UserRepository;
import dev.esteban.mssecurity.util.RoleUser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableDiscoveryClient
public class MsSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsSecurityApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        String password = passwordEncoder.encode("123456");
        return args -> {
        userRepository.save(new User("Esteban", "Bare","admin@gmail.com", password, RoleUser.ADMIN, null));
        userRepository.save(new User("Ale", "Cesar","manager@gmail.com", password, RoleUser.MANAGER, null));
        userRepository.save(new User("John", "Doe","user@gmail.com", password, RoleUser.CLIENT, null));
        };
    }
}
