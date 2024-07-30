package com.PrathihasProjects.PrathihasSplitwise.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    private JwtUtil jwtUtil;
    private SecretKey secretKey;

    @BeforeEach
    public void setUp() {
        jwtUtil = new JwtUtil();
        secretKey = jwtUtil.secret;
    }

    @Test
    public void testGenerateToken() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        assertNotNull(token);

        String usernameFromToken = jwtUtil.getUsernameFromToken(token);
        assertEquals(username, usernameFromToken);
    }

    @Test
    public void testValidateToken_ValidToken() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        Boolean isValid = jwtUtil.validateToken(token, username);
        assertTrue(isValid);
    }

    @Test
    public void testValidateToken_InvalidToken() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        Boolean isValid = jwtUtil.validateToken(token, "wronguser");
        assertFalse(isValid);
    }

    @Test
    public void testGetUsernameFromToken() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        String usernameFromToken = jwtUtil.getUsernameFromToken(token);
        assertEquals(username, usernameFromToken);
    }

    @Test
    public void testValidateToken_ExpiredToken() {
        String username = "testuser";
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 11)) // 11 hours ago
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60)) // 1 hour ago
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        try {
            Boolean isValid = jwtUtil.validateToken(token, username);
            assertFalse(isValid);
        } catch (ExpiredJwtException e) {
            // This is expected, so the test should pass
            assertTrue(true);
        }
    }

    @Test
    public void testGetClaimFromToken() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        String usernameFromClaim = jwtUtil.getClaimFromToken(token, Claims::getSubject);
        assertEquals(username, usernameFromClaim);
    }
}
