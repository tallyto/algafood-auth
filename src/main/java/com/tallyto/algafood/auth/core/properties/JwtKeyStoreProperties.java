package com.tallyto.algafood.auth.core.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
@Component
@ConfigurationProperties("algafood.jwt.keystore") // prefixo do arquivo de configuração
@Getter
@Setter
public class JwtKeyStoreProperties {

    @NotBlank
    private String path;


    @NotBlank
    private String password;


    @NotBlank
    private String keypairAlias;


}
