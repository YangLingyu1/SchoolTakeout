package api.cssc.ciallo.games.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // 公开访问的接口
                        .requestMatchers(antMatcher("/api/admin/auth/login")).permitAll()
                        // 暂时允许所有管理端接口公开访问（测试环境）
                        .requestMatchers(antMatcher("/api/admin/**")).permitAll()
                        // 允许所有接口公开访问（测试环境）
                        .requestMatchers(antMatcher("/**")).permitAll()
                        // 其他接口需要认证
                        .anyRequest().authenticated());

        return http.build();
    }
}

