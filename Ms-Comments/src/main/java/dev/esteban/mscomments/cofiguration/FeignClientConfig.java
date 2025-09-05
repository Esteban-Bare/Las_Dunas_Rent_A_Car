package dev.esteban.mscomments.cofiguration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeignClientConfig implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header("X-Internal-Service", "internal-service");
        template.header("X-User-Role", "ADMIN");
    }
}
