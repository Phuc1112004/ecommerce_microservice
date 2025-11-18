package org.example.paymentservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // Skip cho các endpoint nội bộ
        String url = template.url();
        if (url != null && url.contains("/internal-")) {
            System.out.println("[FeignInterceptor] Skipping Authorization for internal URL: " + url);
            return;
        }

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            String token = jwtAuth.getToken().getTokenValue();
            template.header("Authorization", "Bearer " + token);
            System.out.println("[FeignInterceptor] Forwarded token for URL: " + url);
        } else {
            System.out.println("[FeignInterceptor] No authentication in SecurityContextHolder!");
        }
    }
}
