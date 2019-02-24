package com.gustavomiranda.money.config;

import com.gustavomiranda.money.config.token.CustomTokenEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.Arrays;

/* Esta classe é responsável pela configuração do servidor de OAuth2. */
@Profile("oauth-security")
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    /* Este injetável gerencia as autenticações. Ele que pega o usuário e senha da aplicação.*/
    @Autowired
    private AuthenticationManager authenticationManager;

    /* Necessário o BCrypt para o Spring Boot 2.X+ */
    @Autowired
    private BCryptPasswordEncoder encoder;

    /* Configurar o cliente, a forma na qual será requisitado a autorização.
    *
    * Podemos colocar o nosso cliente no banco de dados (jdbc) e também em memória.
    *
    * */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

        /* Configuração inMemory, com o cliente angular, senha proposta e o escopo do usuário, ler, escrever e assim por diante.
        * Nos vamos utilizar o password-flow, onde o angular vai receber o usuário e senha, enviará para o servidor e receber o
        * access token.
        *
        * authorizedGrantType escolhe o fluxo de autenticação e o acessTokenValiditySeconds, o tempo do token em segundos. */
        clients.inMemory()
                .withClient("angular")
                .secret(encoder.encode("@ngular0"))
                .scopes("read", "write")
                .authorizedGrantTypes("password", "refresh_token")
                .accessTokenValiditySeconds(1800)
                .refreshTokenValiditySeconds(3600 * 24);
    }

    /* Neste método nós vamos configurar o token-store, onde vamos armazenar o token para que a aplicação consiga buscar o token
    * e depois retornar a string para validarmos se o token ainda está ativo.
    *
    * Aqui nós passamos o nosso authenticationManager, para que seja possível validar a autenticação do usuário. */

    /* ReuseRefreshToken, falso, o token sempre vai dar refresh enquanto o usuário estiver usando a aplicação, se ele passar
    * mais de 24 horas sem utilizar, (tempo definido no método acima no refreshTokenValiditySeconds) ai sim, ele será deslogado. */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

        /* Cereja do bolo.
        * Incrementar alguma informação no token de acesso. */
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));

        endpoints
                .tokenStore(tokenStore())
                .tokenEnhancer(tokenEnhancerChain)
                .reuseRefreshTokens(false)
                .authenticationManager(authenticationManager);
    }

    /* Este método é responsável por incrementar no token de acesso, algum incremento. Como por exemplo, o nome de usuário
    * ou o tanacy-id. :D */
    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new CustomTokenEnhancer();
    }

    /* Aqui, ele decodifica o token para o algorítmo do JWT, onde nós passamos no configure de endpoints o accessTokenConverter. */
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
        accessTokenConverter.setSigningKey("money");
        return accessTokenConverter;
    }

    /* Salva o token em JWT. */
    @Bean
    public TokenStore tokenStore(){
        return new JwtTokenStore(accessTokenConverter());
    }
}
