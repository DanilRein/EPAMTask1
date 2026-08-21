package com.example.EPAMtask1.auth;

import com.example.EPAMtask1.services.JwtService;
import com.example.EPAMtask1.services.TokenBlacklistService;
import com.example.EPAMtask1.services.UserDetailsServiceImpl;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwsAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    @Mock
    private TokenBlacklistService tokenBlacklistService;
    @Mock
    private FilterChain filterChain;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_shouldSkipAuthentication_whenNoAuthorizationHeader() throws Exception {
        JwsAuthenticationFilter filter = new JwsAuthenticationFilter(jwtService, userDetailsService, tokenBlacklistService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    void doFilterInternal_shouldSkipAuthentication_whenHeaderIsNotBearer() throws Exception {
        JwsAuthenticationFilter filter = new JwsAuthenticationFilter(jwtService, userDetailsService, tokenBlacklistService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic something");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    void doFilterInternal_shouldSkipAuthentication_whenTokenIsBlacklisted() throws Exception {
        JwsAuthenticationFilter filter = new JwsAuthenticationFilter(jwtService, userDetailsService, tokenBlacklistService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer blacklisted-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(tokenBlacklistService.isBlackListed("blacklisted-token")).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    void doFilterInternal_shouldSetAuthentication_whenTokenValid() throws Exception {
        JwsAuthenticationFilter filter = new JwsAuthenticationFilter(jwtService, userDetailsService, tokenBlacklistService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(tokenBlacklistService.isBlackListed("valid-token")).thenReturn(false);
        when(jwtService.extractUsername("valid-token")).thenReturn("john.doe");
        UserDetails userDetails = User.builder()
                .username("john.doe")
                .password("hashedPassword123")
                .authorities("USER")
                .build();
        when(userDetailsService.loadUserByUsername("john.doe")).thenReturn(userDetails);

        filter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertNotNull(SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldContinueChain_whenTokenInvalid() throws Exception {
        JwsAuthenticationFilter filter = new JwsAuthenticationFilter(jwtService, userDetailsService, tokenBlacklistService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(tokenBlacklistService.isBlackListed("invalid-token")).thenReturn(false);
        when(jwtService.extractUsername("invalid-token")).thenThrow(new JwtException("bad token"));

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userDetailsService);
    }
}
