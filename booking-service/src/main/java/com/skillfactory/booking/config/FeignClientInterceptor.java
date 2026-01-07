package com.skillfactory.booking.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class FeignClientInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TOKEN_TYPE = "Bearer";

    @Override
    public void apply(RequestTemplate template) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getCredentials() instanceof String) {
            String token = (String) authentication.getCredentials();
            // In our simple setup, we might need to trust that we stored the token or can
            // re-construct it.
            // However, Spring Security Context usually holds the Principal (UserDetail) and
            // Credentials (often erased).
            // Since we use a stateless filter, we might need to make sure we put the raw
            // token into the credentials or details
            // when we authenticated the request in JwtRequestFilter.

            // Let's check JwtRequestFilter logic. If it doesn't store the raw token, we
            // might have a problem.
            // But we can try to look at the "Authorization" header of the *incoming*
            // request if we are in a web context.
            // Better approach for a proxy:
        }

        // Easier approach: Get the header from the current servlet request
        // This requires RequestContextHolder

        try {
            org.springframework.web.context.request.ServletRequestAttributes attributes = (org.springframework.web.context.request.ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder
                    .getRequestAttributes();

            if (attributes != null) {
                String authHeader = attributes.getRequest().getHeader(AUTHORIZATION_HEADER);
                if (authHeader != null) {
                    template.header(AUTHORIZATION_HEADER, authHeader);
                }
            }
        } catch (Exception e) {
            // Ignore if not in request context
        }
    }
}
