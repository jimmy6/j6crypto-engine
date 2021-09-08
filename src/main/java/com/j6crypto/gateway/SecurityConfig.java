package com.j6crypto.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  @Bean
  public MapReactiveUserDetailsService userDetailsService() {
    UserDetails user = User.withUsername("foo")
      .password(passwordEncoder().encode("bar"))
      .roles("role")
      .build();

    return new MapReactiveUserDetailsService(user);
  }

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    http
      .csrf().disable()
      .authorizeExchange(exchanges -> exchanges
        .anyExchange().permitAll()////TODO .authenticated()
      )
      .httpBasic(Customizer.withDefaults());
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(8);
  }
}