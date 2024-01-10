package com.tallyto.algafood.auth.core;

import com.tallyto.algafood.auth.domain.model.Usuario;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

@Getter
@EqualsAndHashCode(callSuper = false)
public class AuthUser extends User {

    private final Long userId;
    private final String fullName;

    public AuthUser(Usuario usuario) {
        super(usuario.getEmail(), usuario.getSenha(), Collections.emptyList());
        this.fullName = usuario.getNome();
        this.userId = usuario.getId();
    }
}
