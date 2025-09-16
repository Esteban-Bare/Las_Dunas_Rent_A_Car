package dev.esteban.springcloudgateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class JwtAuthenticationFilter implements GlobalFilter {

    private final WebClient.Builder webClientBuilder;
    private final List<String> excludedPaths = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh-token",
            "/api/auth/logout"
    );

    @Autowired
    public JwtAuthenticationFilter(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (isSecured(request)) {
            String token = extractTokenFromCookies(request);
            if (token == null) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }
            return isValidToken(token, exchange)
                .flatMap(isValid -> {
                    if (!isValid) {
                        return onError(exchange, HttpStatus.UNAUTHORIZED);
                    }

                    // Propagation des informations utilisateur aux services en aval
                    ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                            .header("X-User-Role",
                                    exchange.getAttribute("USER_ROLE").toString())
                            .header("X-User-Email",
                                    exchange.getAttribute("USER_EMAIL").toString())
                            .header("x-User-Id",
                                    exchange.getAttribute("USER_ID").toString())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .build();

                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                });
        }

        return chain.filter(exchange);
    }

    private String extractTokenFromCookies(ServerHttpRequest request) {
        HttpCookie cookie = request.getCookies().getFirst("JWT");
        return cookie != null ? cookie.getValue() : null;
    }

    private Mono<Boolean> isValidToken(String token, ServerWebExchange exchange) {
        System.out.println("Token: " + token.substring(0, 10) + "...");

        return webClientBuilder.build()
                .post()
                .uri("http://security/api/auth/validate")  // Utilisation du schéma lb:// pour Load Balancing
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(r -> {
                    System.out.println("Response: " + r);
                    Boolean isValid = (Boolean) r.get("valid");
                    if (isValid != null && isValid) {
                        exchange.getAttributes().put("USER_ID", r.get("userId"));
                        exchange.getAttributes().put("USER_ROLE", r.get("role"));
                        exchange.getAttributes().put("USER_EMAIL", r.get("subject"));
                        return Mono.just(true);
                    }
                    System.out.println("Token is invalid");
                    return Mono.just(false);
                })
                .onErrorResume(e -> {
                    System.err.println("Error: " + e);
                    return Mono.just(false);
                });
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> errorBody = Map.of(
                "error", httpStatus.getReasonPhrase(),
                "status", httpStatus.value(),
                "message", "Unauthorized access - invalid or missing token"
        );

        try {
            byte[] bytes = new ObjectMapper().writeValueAsBytes(errorBody);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }


    private boolean isSecured(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        return excludedPaths.stream().noneMatch(path::contains);
    }
}