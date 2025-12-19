package com.tfm.edcuaweb.service;

import com.tfm.edcuaweb.model.Role;
import com.tfm.edcuaweb.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import com.tfm.edcuaweb.model.User;
import java.security.Key;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
@Slf4j
//@RequiredArgsConstructor
public class JwtService {

	@Value("${jwt.secret}")
	private String secretKey;
    @Value("${app.jwt.expiration-seconds:900}") // Duración del access token en milisegundos (opcional)
    private long jwtExpTime;
    private final UserRepository userRepository;
    @Value("${app.jwt.refresh-expiration-days}")
    private long refreshExpTime;
    public JwtService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /*=============== GENERACION de TOKEN ===================*/
    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(jwtExpTime*60);
        String roleName = user.getRole().name();
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", roleName);
        claims.put("email",user.getEmail());
        claims.put("id",user.getId());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(getSignInKey(),SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(User user) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(jwtExpTime*24*60*60);

        return Jwts.builder().setSubject(user.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(getSignInKey(),SignatureAlgorithm.HS256)
                .compact();
    }

    /*=============== VALIDACION de TOKEN ===============*/
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder()
			.setSigningKey(getSignInKey())
			.build()
			.parseClaimsJws(token);
			return true;
		}catch(JwtException | IllegalArgumentException e){
			return false;
		}
	}
    public User validateAndGetUserFromRefresh(String token) {
        try {
            Claims claims = parseToken(token);
            String username = claims.getSubject();
            return userRepository.findByUsername(username).orElseThrow(()->
                    new RuntimeException("USUARIO noo encontrado para el refresh"));
        }catch(JwtException e){
            throw new RuntimeException("Refresh token inválido o expirado.", e);
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    // Comprueba si el token ha expirado
    private boolean isTokenExpired(String token) {
       return extractExpiration(token).before(new Date());
    }

    /*=============== HELPERS ===============*/
    // Parsea el token y obtiene todas las claims
    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    // Convierte el secret en clave HMAC SHA
    private Key getSignInKey() {
        if (secretKey == null) {
            throw new IllegalStateException("jwt.secret no está definido en application.properties");
        }
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            // Si la clave no está en Base64, usar los bytes directos
            log.warn("⚠️ Clave JWT no codificada en Base64, usando directamente el texto plano");
            return Keys.hmacShaKeyFor(secretKey.getBytes());
        }
    }
    public String extractRole(String token) {
        Claims claims = parseToken(token);
        return (String) claims.get("role"); // un único rol simple
    }
    /*======== OTRAS FUNCIONES ======= */
	public Set<String> getUsernameFromToken(String token) {
		Claims claims = Jwts.parserBuilder()
				.setSigningKey(getSignInKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
		
		Object roles = claims.get("roles");
		if (roles instanceof Iterable<?> roleList) {
			return ((Set<String>) roleList).stream()
					.map(Object::toString)
					.collect(Collectors.toSet());
		}
		return Set.of();
	}
	
	// Extrae el username (o email) del token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    // Extrae una claim concreta
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = parseToken(token);
        return claimsResolver.apply(claims);
    }
    // Devuelve la fecha de expiración del token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
