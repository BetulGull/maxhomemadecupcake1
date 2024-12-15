package com.example.demo.security;

import com.example.demo.Model.Role;
import com.example.demo.Model.User;
import com.example.demo.Repository.RoleRepository;
import com.example.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Collections;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Allow public access to login, logout, home, register, and index pages
                        .requestMatchers("/login", "/logout", "/", "/register", "/index", "/error", "/errorPage","/cart").permitAll()

                        // Admin-only access to customers and products
                        .requestMatchers("/customers/**", "/orders/orderAdd", "/admin/**").hasAuthority("ROLE_ADMIN")

                        // Both admin and user can access orders
                        .requestMatchers("/Product/**", "/orders/orderList", "/orders/orderEdit").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        // Deny access to static resources for non-admin users
                        .requestMatchers("/webjars/**", "/css/**", "/js/**", "/images/**").permitAll()

                        // Other requests require authentication
                        .anyRequest().authenticated()
                )

                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/index", true)  // Or another page for authenticated users
                        .failureUrl("/login?error=true")
                        .permitAll()
                )





                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .permitAll()
                )

                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/access-denied")
                );

        return http.build();
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {

            if (roleRepository.findByName("ROLE_USER").isEmpty()) {
                Role userRole = new Role();
                userRole.setName("ROLE_USER");
                roleRepository.save(userRole);
            }

            if (userRepository.findByUsername("admin") == null) {
                Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                        .orElseGet(() -> {
                            Role newAdminRole = new Role();
                            newAdminRole.setName("ROLE_ADMIN");
                            return roleRepository.save(newAdminRole);
                        });


                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setPassword(passwordEncoder().encode("admin123"));
                adminUser.setEmail("admin@example.com");
                adminUser.setRoles(Collections.singletonList(adminRole));
                userRepository.save(adminUser);
            }
        };
    }
}
