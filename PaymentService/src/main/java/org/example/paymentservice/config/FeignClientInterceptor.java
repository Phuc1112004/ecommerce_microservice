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
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            String token = jwtAuth.getToken().getTokenValue();
            System.out.println("[FeignInterceptor] Forwarding token: " + token.substring(0, 10) + "..."); // log 1 phần token
            template.header("Authorization", "Bearer " + token);
        } else {
            System.out.println("[FeignInterceptor] No authentication in SecurityContextHolder!");
        }
    }
}
