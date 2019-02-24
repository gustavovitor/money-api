package com.gustavomiranda.money.config;

import com.gustavomiranda.money.security.AppUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;


/* Classe de configurações para os fluxos de OAuth2. */
@Profile("oauth-security")
@Configuration
@EnableWebSecurity
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    /* Necessário para o Spring Boot 2.X, ele tem por requisito o encrypt das senhas. */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private AppUserDetailsService userDetailsService;

    /* Este método é injetável no OAuth2, porém no security basic ele é sobrescrito.
    * Basicamente, ele gerencia a forma na qual o usuário vai logar, no nosso caso, ele está buscando um usuário
    * da memória e não do banco de dados.*/
    @Autowired
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        BCryptPasswordEncoder encoder = passwordEncoder();
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    /* Este método para o OAuth2 é público e não protected, aqui é onde configuramos as permissões do nosso HTTP.
    * No que está configurado no momento, dizemos que nosso HTTP, qualquer requisição precisa estar autenticado, e
    * não vamos guardar sessões no servidor.*/
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .authenticated()
        .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
                .csrf().disable();
    }

    /* Aqui é também a configuração para não guardar sessões para o OAuth2. */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.stateless(true);
    }

    /* Para que funcione corretamente as permissões no OAuth2, é necessário a implementação dessa bean. */
    @Bean
    public MethodSecurityExpressionHandler createExpressionHandler(){
        return new OAuth2MethodSecurityExpressionHandler();
    }
}
