package com.mgmtp.easyquizy.security;

import com.mgmtp.easyquizy.model.role.RoleName;
import com.mgmtp.easyquizy.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserServiceImpl userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods((List.of("GET", "POST", "PUT", "DELETE")));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers(
                        "/api/auth/**",
                        "/api/h2-console/**",
                        "/api/v2/api-docs",
                        "/api/swagger-resources",
                        "/api/swagger-resources/**",
                        "/api/configuration/ui",
                        "/api/configuration/security",
                        "/api/swagger-ui/**",
                        "/api/docs/**",
                        "/api/docs.json/**",
                        "/api/webjars/**"
                ).permitAll()
                .antMatchers(HttpMethod.GET, "/api/categories/**").hasAnyAuthority(RoleName.QUESTION_MAKER.toString(), RoleName.ADMIN.toString(), RoleName.ORGANIZER.toString())
                .antMatchers(HttpMethod.POST, "/api/categories/**").hasAnyAuthority(RoleName.QUESTION_MAKER.toString(), RoleName.ADMIN.toString())
                .antMatchers(HttpMethod.DELETE, "/api/categories/**").hasAnyAuthority(RoleName.QUESTION_MAKER.toString(), RoleName.ADMIN.toString())
                .antMatchers(HttpMethod.PUT, "/api/categories/**").hasAnyAuthority(RoleName.QUESTION_MAKER.toString(), RoleName.ADMIN.toString())
                .antMatchers("/api/questions/**").hasAnyAuthority(RoleName.QUESTION_MAKER.toString(), RoleName.ADMIN.toString())
                .antMatchers("/api/quizzes/**").hasAnyAuthority(RoleName.ORGANIZER.toString(), RoleName.ADMIN.toString())
                .antMatchers("/api/events/**").hasAnyAuthority(RoleName.ORGANIZER.toString(), RoleName.ADMIN.toString())
                .antMatchers("/api/users/me").hasAnyAuthority(RoleName.ORGANIZER.toString(), RoleName.ADMIN.toString(), RoleName.QUESTION_MAKER.toString())
                .antMatchers("/api/kahoot/**").hasAnyAuthority(RoleName.ORGANIZER.toString(), RoleName.ADMIN.toString(), RoleName.QUESTION_MAKER.toString())
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .headers().frameOptions().disable();
    }
}