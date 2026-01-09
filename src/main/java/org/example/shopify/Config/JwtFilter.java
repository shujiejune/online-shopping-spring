package org.example.shopify.Config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String SIGNING_KEY;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // 1. Check if the header contains a Bearer token
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.replace("Bearer ", "");

        try {
            // 2. Prepare the key
            byte[] keyBytes = SIGNING_KEY.getBytes(StandardCharsets.UTF_8);
            Key key = Keys.hmacShaKeyFor(keyBytes);

            // 3. Parse the token to get Claims
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();

            // 4. Extract the "role" claim (Integer)
            // This matches the .claim("role", user.getRole()) in JwtProvider
            Integer roleInt = claims.get("role", Integer.class);

            // 5. Convert Integer to Spring Security Authority
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();

            if (roleInt != null) {
                // ASSUMPTION: 1 = Admin, 0 = User
                if (roleInt == 1) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                } else {
                    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                }
            }

            if (username != null) {
                // 6. Create Auth Token WITH AUTHORITIES
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);

                // 7. Tell Spring Security: "This user is logged in with these roles"
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            // If token is expired or invalid, clear context so user is treated as anonymous
            SecurityContextHolder.clearContext();
        }

        // 8. Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
