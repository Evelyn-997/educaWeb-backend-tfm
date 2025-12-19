package com.tfm.edcuaweb.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.tfm.edcuaweb.service.JwtService;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;

		
	@Override 
	protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain filterChain) throws ServletException, IOException{
		//Permite prefligth request de CORS (OPTIONS)
		if("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_OK);
			filterChain.doFilter(request, response);
			return;
		}

		//Extaremos header de Authorization
        final String authHeader = request.getHeader("Authorization");
		//Si no hay token, continua sin autenticar
		if(authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		//Obtener Token puro "sin Bearer"
		final String token = authHeader.substring(7);
		final String username = jwtService.extractUsername(token);
		
		//Validamos el token y autenticamos
		if (username != null && SecurityContextHolder.getContext().getAuthentication()== null) {
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
				
			if(jwtService.isTokenValid(token,userDetails)) {
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				//Guardamos la autenticacion ene le contexto
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}
		//Continua con el resto de filtros
		filterChain.doFilter(request,response);
	}

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){ //throws ServletException {
        // Evita aplicar el filtro JWT a endpoints públicos
        String path = request.getServletPath();
        return path.startsWith("/auth/") || path.startsWith("/api/auth/");
    }
} 
