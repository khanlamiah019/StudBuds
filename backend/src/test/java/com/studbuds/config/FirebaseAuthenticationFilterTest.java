package com.studbuds.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class FirebaseAuthenticationFilterTest extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.equals("Bearer test-token")) {
            // For testing purposes, always set a dummy authentication with a fixed email.
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("test@cooper.edu", null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}