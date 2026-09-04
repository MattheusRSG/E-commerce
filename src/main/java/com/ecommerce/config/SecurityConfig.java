package com.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração do Spring Security.
 *
 * A autenticação da loja continua sendo feita à mão no UserController, que guarda
 * o usuário na sessão. O que esta classe liga é a infraestrutura que estava
 * desativada: proteção CSRF, codificação de senha e cabeçalhos de segurança.
 *
 * A autorização por rota permanece nos controllers. Centralizá-la em um
 * interceptor é um passo separado — enquanto isso, cada método administrativo
 * continua responsável pela própria checagem.
 */
@Configuration
public class SecurityConfig {

    /**
     * Usado pelo UserController para gravar e conferir senha, e pelo
     * DataInitializer para semear os usuários de teste já codificados.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Mantém imagens e ícones fora da cadeia de filtros. Além de evitar uma
     * sessão por requisição de imagem, impede que o cabeçalho no-store aplicado
     * às páginas desligue o cache do navegador para os arquivos estáticos.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
            "/img/**", "/css/**", "/js/**", "/favicon.ico", "/favicon.png"
        );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF fica no padrão (ativo, token na sessão). O Thymeleaf injeta o
            // campo escondido em todo formulário que usa th:action.
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            // O /login e o /logout são rotas do UserController; os handlers padrão
            // do Spring Security ficam desligados para não disputá-las.
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
