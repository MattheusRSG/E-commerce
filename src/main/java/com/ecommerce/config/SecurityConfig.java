package com.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;

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

    /**
     * Resolve o token CSRF já no CsrfFilter, em vez de esperar alguém pedir por ele.
     *
     * O padrão do Spring Security 6 é adiar: o token só é criado quando o
     * Thymeleaf encontra um formulário e pede o campo escondido. Nas páginas
     * longas desta loja o primeiro formulário aparece bem depois do buffer de
     * resposta do Tomcat ter sido descarregado — e criar a sessão que guarda o
     * token depois da resposta enviada lança IllegalStateException.
     *
     * Passar null no nome do atributo faz o handler resolver o token na hora.
     * Continua sendo o handler com máscara XOR, que protege contra BREACH.
     *
     * O efeito colateral é que toda visita passa a abrir sessão, inclusive a de
     * quem só está olhando o catálogo.
     */
    private XorCsrfTokenRequestAttributeHandler csrfTokenRequestHandler() {
        XorCsrfTokenRequestAttributeHandler handler = new XorCsrfTokenRequestAttributeHandler();
        handler.setCsrfRequestAttributeName(null);
        return handler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.csrfTokenRequestHandler(csrfTokenRequestHandler()))
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            // O /login e o /logout são rotas do UserController; os handlers padrão
            // do Spring Security ficam desligados para não disputá-las.
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
