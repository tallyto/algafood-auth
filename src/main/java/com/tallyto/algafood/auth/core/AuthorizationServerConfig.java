package com.tallyto.algafood.auth.core;

import com.tallyto.algafood.auth.core.properties.JwtKeyStoreProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.util.Arrays;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    JwtKeyStoreProperties jwtKeyStoreProperties;


    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("algafood-web")
                    .secret(passwordEncoder.encode("web123"))
                    .authorizedGrantTypes("password", "refresh_token")
                    .scopes("write", "read")
                    .accessTokenValiditySeconds(6 * 60 * 60) // 6 horas de validade do token de acesso. (em segundos)
                    .refreshTokenValiditySeconds(12 * 60 * 60) // 12 horas de validade do token de refresh. (em segundos)
                .and()
                    .withClient("faturamento")
                    .secret(passwordEncoder.encode("faturamento123"))
                    .authorizedGrantTypes("client_credentials")
                    .scopes("write", "read")
                .and()
                    .withClient("foodanalytics")
                    .secret(passwordEncoder.encode("food123"))
                    .authorizedGrantTypes("authorization_code")
                    .scopes("write", "read")
                    .redirectUris("http://localhost:4000")
                // http://localhost:3002/oauth/authorize?response_type=code&client_id=foodanalytics&state=abc&redirect_uri=http://localhost:4000 -> authorization code
                // http://localhost:3002/oauth/authorize?response_type=code&client_id=foodanalytics&redirect_uri=http://localhost:4000&code_challenge=teste123&code_challenge_method=plain -> authorization code PKCE plain
                // http://localhost:3002/oauth/authorize?response_type=code&client_id=foodanalytics&redirect_uri=http://localhost:4000&code_challenge=8UvrgwTxumWIvZsMqsmsYGWp1GlBc6uzBWKOtPgOrvo&code_challenge_method=s256 -> authorization code sha256
                .and()
                    .withClient("webadmin")
                    .authorizedGrantTypes("implicit")
                    .scopes("write","read")
                    .redirectUris("http://localhost:4000")
                // http://localhost:3002/oauth/authorize?response_type=token&client_id=webadmin&state=abc&redirect_uri=http://localhost:4000
                .and()
                    .withClient("admin")
                    .secret(passwordEncoder.encode("admin"));

    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        var accessTokenConverter = new JwtAccessTokenConverter();

        /*
         * Cria tokens JWT com assinatura HMACSHA256
         * accessTokenConverter.setSigningKey("sua_chave_aqui");
         */

        // Gera tokens JWT com assinatura SHA256

        var jksResource = new ClassPathResource(jwtKeyStoreProperties.getPath());
        var keyStorePassword = jwtKeyStoreProperties.getPassword();
        var keyPairAlias = jwtKeyStoreProperties.getKeypairAlias();

        var keyStoreKeyFactory = new KeyStoreKeyFactory(jksResource, keyStorePassword.toCharArray());
        var keyPair = keyStoreKeyFactory.getKeyPair(keyPairAlias);

        accessTokenConverter.setKeyPair(keyPair);

        return accessTokenConverter;
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        var enhancerChain = new TokenEnhancerChain();
        enhancerChain.setTokenEnhancers(Arrays.asList(new JwtCustomClaimsTokenEnhancer(), jwtAccessTokenConverter()));
        endpoints
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
                .reuseRefreshTokens(false) // para que o token de refresh seja gerado apenas uma vez (não pode ser usado mais de uma vez)
                .accessTokenConverter(jwtAccessTokenConverter())
                .tokenEnhancer(enhancerChain)
                .approvalStore(approvalStore(endpoints.getTokenStore()))
                .tokenGranter(tokenGranter(endpoints)); // para que o token de refresh seja gerado apenas uma vez (não pode ser usado mais de uma vez)
    }

    private ApprovalStore approvalStore(TokenStore tokenStore) {
        var approvalStore = new TokenApprovalStore();
        approvalStore.setTokenStore(tokenStore);
        return approvalStore;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients();
    }

    private TokenGranter tokenGranter(AuthorizationServerEndpointsConfigurer endpoints) {
        var pkceAuthorizationCodeTokenGranter = new PkceAuthorizationCodeTokenGranter(endpoints.getTokenServices(),
                endpoints.getAuthorizationCodeServices(), endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory());

        var granters = Arrays.asList(
                pkceAuthorizationCodeTokenGranter, endpoints.getTokenGranter());

        return new CompositeTokenGranter(granters);
    }
}
