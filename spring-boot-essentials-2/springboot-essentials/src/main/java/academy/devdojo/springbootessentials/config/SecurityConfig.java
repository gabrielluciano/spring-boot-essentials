package academy.devdojo.springbootessentials.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Log4j2
public class SecurityConfig {

//    @Bean
//    public UserDetailsService userDetailsService() {
//        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
//
//        UserDetails u1 = User.withUsername("jack")
//                .password(passwordEncoder().encode("password"))
//                .roles("USER", "ADMIN")
//                .build();
//
//        UserDetails u2 = User.withUsername("maria")
//                .password(passwordEncoder().encode("password"))
//                .roles("USER")
//                .build();
//
//        inMemoryUserDetailsManager.createUser(u1);
//        inMemoryUserDetailsManager.createUser(u2);
//
//        return inMemoryUserDetailsManager;
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info(passwordEncoder().encode("academy"));
        return http
                .csrf().disable() // TODO remove this line and uncomment the line below
//                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
                .authorizeHttpRequests()
                    .mvcMatchers("/animes/admin/**").hasRole("ADMIN")
                    .mvcMatchers("/animes/**").hasRole("USER")
                    .anyRequest().authenticated()
                .and().formLogin()
                .and().httpBasic()
                .and().build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
