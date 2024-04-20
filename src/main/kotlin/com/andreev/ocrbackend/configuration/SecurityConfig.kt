package com.andreev.ocrbackend.configuration

import com.andreev.ocrbackend.core.service.security.UserDetailsServiceImpl
import com.andreev.ocrbackend.core.service.security.jwt.JwtAuthEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.password.PasswordEncoder

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val userDetailsService: UserDetailsServiceImpl,
    private val passwordEncoder: PasswordEncoder,
    private val unauthorizedHandler: JwtAuthEntryPoint
) : WebSecurityConfigurerAdapter() {

    override fun configure(authenticationManagerBuilder: AuthenticationManagerBuilder) {
        authenticationManagerBuilder
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder)
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    override fun configure(http: HttpSecurity) {
        val whiteListEndpoints = arrayOf(
            "/v1/**",
            "/security/*",
            "/actuator/*",
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/swagger-ui/**"
        )

        http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers(*whiteListEndpoints).permitAll()
            .anyRequest().authenticated()
            .and().httpBasic()
            .and().sessionManagement().disable()
            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
    }

}