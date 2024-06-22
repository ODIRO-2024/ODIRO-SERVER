package odiro.config.security;


import lombok.Builder;
import lombok.RequiredArgsConstructor;
//import odiro.config.jwt.JwtAccessDeniedException;
//import odiro.config.jwt.JwtAuthenticationEntryPoint;
//import odiro.config.jwt.JwtToken;
//import odiro.config.jwt.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import odiro.domain.Member;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Value;
import java.util.Collection;

@Slf4j
@RequiredArgsConstructor // final arg constructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Value("${security.secret}")
    private String secretNumber;
    //권한 설정
    @Bean
    @Builder
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((auth)->auth
                .requestMatchers("/").permitAll()
                .anyRequest().authenticated()) // "/"외 모든 주소 로그인 필요
        ;
        return http.build();
    }

    //(더미 데이터) 유저 생성
    public UserDetailsService userDetailsService(HttpSecurity http) throws Exception {
        UserDetails user = User.withUsername("user").password("{none}1234").authorities("ROLE_USER").build()
        ;
        return new InMemoryUserDetailsManager(user);
    }
}


