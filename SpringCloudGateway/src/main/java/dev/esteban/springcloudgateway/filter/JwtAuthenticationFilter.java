package dev.esteban.springcloudgateway.filter;

import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
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
            "/api/auth/refresh-token"
    );

    @Autowired
    public JwtAuthenticationFilter(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (isSecured(request)) {
            if (isAuthMissing(request)) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            final String token = extractToken(request);

            return isValidToken(token, exchange)
                    .flatMap(isValid -> {
                        if (!isValid) {
                            return onError(exchange, HttpStatus.UNAUTHORIZED);
                        }

                        // Propagation des informations utilisateur aux services en aval
                        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                                .header("X-User-Role", exchange.getAttribute("USER_ROLE").toString())
                                .header("X-User-Email", exchange.getAttribute("USER_EMAIL").toString())
                                .build();

                        return chain.filter(exchange.mutate().request(modifiedRequest).build());
                    });
        }

        return chain.filter(exchange);
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

    private String extractToken(ServerHttpRequest request) {
        String authHeader = getAuthHeader(request);
        return authHeader.substring(7); // Retire le préfixe "Bearer "
    }

    private String getAuthHeader(ServerHttpRequest request) {
        return request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    private boolean isAuthMissing(ServerHttpRequest request) {
        String authHeader = getAuthHeader(request);
        return authHeader == null || !authHeader.startsWith("Bearer ");
    }

    private boolean isSecured(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        return excludedPaths.stream().noneMatch(path::contains);
    }
}