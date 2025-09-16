package dev.esteban.springcloudgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class SpringCloudGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("rental-service", r -> r.path("/rental/**")
                        .filters(f -> f
                                .prefixPath("/api")
                                .addResponseHeader("x-Powered-By", "Esteban's API")
                        )
                        .uri("lb://rental")
                )
                .route("security-auth-service", r -> r.path("/auth/**")
                        .filters(f -> f
                                .prefixPath("/api")
                                .addResponseHeader("x-Powered-By", "Esteban's API")
                        )
                        .uri("lb://security")
                )
                .route("security-user-service", r -> r.path("/user/**")
                        .filters(f -> f
                                .prefixPath("/api")
                                .addResponseHeader("x-Powered-By", "Esteban's API")
                        )
                        .uri("lb://security")
                )
                .route("comment-service", r -> r.path("/comments/**")
                        .filters(f -> f
                                .prefixPath("/api")
                                .addResponseHeader("x-Powered-By", "Esteban's API")
                        )
                        .uri("lb://comments")
                )
                .build();
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }

    @Bean
    public WebClient.Builder webClientBuilder(ReactorLoadBalancerExchangeFilterFunction rbf) {
        return WebClient.builder()
                .filter(rbf);
    }
}
