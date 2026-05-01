package com.hoppin.security.filter;

import com.hoppin.domain.musician.entity.Musician;
import com.hoppin.domain.musician.repository.MusicianRepository;
import com.hoppin.security.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final MusicianRepository musicianRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("==== JWT FILTER ====");
        System.out.println("URI = " + request.getRequestURI());
        System.out.println("Query = " + request.getQueryString());
        System.out.println("Origin = " + request.getHeader("Origin"));
        System.out.println("Referer = " + request.getHeader("Referer"));
        System.out.println("Authorization = " + request.getHeader("Authorization"));
        System.out.println("Cookie Header = " + request.getHeader("Cookie"));

        if (request.getCookies() == null) {
            System.out.println("cookies = null");
        } else {
            for (var cookie : request.getCookies()) {
                System.out.println("cookie name = " + cookie.getName());
            }
        }

        String token = resolveToken(request);

        System.out.println("resolved token exists = " + (token != null));

        if (token != null && jwtTokenProvider.validateToken(token)) {

            Long musicianId = jwtTokenProvider.getMusicianId(token);
            String role = jwtTokenProvider.getRole(token);

            Musician musician = musicianRepository.findById(musicianId)
                    .orElse(null);

            if (musician == null || musician.isWithdrawn()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"message\":\"탈퇴했거나 존재하지 않는 회원입니다.\"}");
                return;
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            musicianId,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {

        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }

        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}