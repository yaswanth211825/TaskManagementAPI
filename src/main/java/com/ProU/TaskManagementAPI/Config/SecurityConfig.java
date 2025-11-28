package com.ProU.TaskManagementAPI.Config;

import com.ProU.TaskManagementAPI.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

import java.util.List;

@Configuration
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		// Accept existing plaintext passwords (development convenience) or BCrypt hashes.
		// If the stored password looks like a BCrypt hash (starts with $2), use BCrypt matching,
		// otherwise fall back to plain-text equality. This is a dev-only convenience.
		var bcrypt = new BCryptPasswordEncoder();
		return new PasswordEncoder() {
			@Override
			public String encode(CharSequence rawPassword) {
				return bcrypt.encode(rawPassword);
			}

			@Override
			public boolean matches(CharSequence rawPassword, String encodedPassword) {
				if (encodedPassword == null) return false;
				String ep = encodedPassword;
				if (ep.startsWith("$2a$") || ep.startsWith("$2b$") || ep.startsWith("$2y$")) {
					return bcrypt.matches(rawPassword, ep);
				}
				// fallback: plain-text comparison
				return ep.equals(rawPassword.toString());
			}
		};
	}

	@Bean
	    public UserDetailsService userDetailsService(UserRepository userRepository) {
		return username -> userRepository.findByUsername(username)
			.map(u -> {
			    String r = u.getRole() == null ? "USER" : u.getRole();
			    // normalize role: if it already starts with ROLE_ don't double-prefix
			    String authority = r.startsWith("ROLE_") ? r : "ROLE_" + r;
			    return new org.springframework.security.core.userdetails.User(
				    u.getUsername(),
				    u.getPassword(),
				    List.of(new SimpleGrantedAuthority(authority))
			    );
			})
			.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
	    }

	@Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/auth/**").permitAll()
				.requestMatchers("/signup").permitAll()
				.requestMatchers("/login", "/login/admin").permitAll()
				// employee endpoints: listing and viewing allowed for authenticated users
				.requestMatchers(HttpMethod.GET, "/employees", "/tasks", "/tasks/*", "/tasks/dashboard").hasAnyRole("ADMIN","EMPLOYEE")
				// only ADMIN can create employees and tasks or delete tasks
				.requestMatchers(HttpMethod.POST, "/employees").hasRole("ADMIN")
				.requestMatchers(HttpMethod.POST, "/tasks").hasRole("ADMIN")
				.requestMatchers(HttpMethod.DELETE, "/tasks/*").hasRole("ADMIN")
				// updates allowed for admin or employee
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.PUT, "/tasks/*").hasAnyRole("ADMIN","EMPLOYEE")
				.anyRequest().authenticated()
			)
			.httpBasic(Customizer.withDefaults());

		// allow frames (for h2 console if needed)
		http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

		return http.build();
	    }

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}
}

