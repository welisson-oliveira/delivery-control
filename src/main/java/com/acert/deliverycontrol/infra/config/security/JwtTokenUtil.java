package com.acert.deliverycontrol.infra.config.security;

import com.acert.deliverycontrol.domain.client.Client;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Component
@RequiredArgsConstructor
public class JwtTokenUtil {
    private static final long JWT_TOKEN_VALIDITY = 5 * 60 * 50;

    @Value("${jwt.secret}")
    private String secret;
    private final TokenService tokenService;

    /**
     * Retorna o username do token jwt
     *
     * @param token
     * @return
     */
    public String getUsernameFromToken(final String token) {
        return this.getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(final String token, final Function<Claims, T> claimsResolver) {
        final Claims claims = this.getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);

    }

    /**
     * Para retornar qualquer informação do token nos iremos precisar da secret key
     *
     * @param token
     * @return
     */
    private Claims getAllClaimsFromToken(final String token) {
        return Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).getBody();
    }

    /**
     * Gera token para user
     *
     * @param userDetails
     * @return
     */
    public String generateToken(final Client userDetails) {
        final String token = this.doGenerateToken(this.getClaims(userDetails), userDetails.getUsername());
        this.setToken(token);
        return token;
    }

    private Map<String, Object> getClaims(final Client userDetails) {

        final Map<String, Object> claims = new HashMap<>();
        claims.put("name", userDetails.getName());
        claims.put("roles", userDetails.getRoles());
        claims.put("authorities", userDetails.getAuthorities());
        claims.put("active", userDetails.isEnabled());
        return claims;
    }

    /**
     * Cria o token e define o tempo de expiração
     *
     * @param claims
     * @param subject
     * @return
     */
    private String doGenerateToken(final Map<String, Object> claims, final String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JwtTokenUtil.JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, this.secret).compact();
    }

    /**
     * Valida o token
     *
     * @param token
     * @return
     */
    public Boolean validateToken(final String token) {
        return token.equals(this.getCachedToken());
    }

    public String getCachedToken() {

        return this.tokenService.getCachedToken();
    }

    public void setToken(final String token) {
        this.tokenService.setToken(token);
    }

}
