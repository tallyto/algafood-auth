package com.tallyto.algafood.auth.core;

import com.tallyto.algafood.auth.domain.model.Usuario;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Collections;

@Getter
@EqualsAndHashCode(callSuper = false)
public class AuthUser extends User {

    private final Long userId;
    private final String fullName;

    public AuthUser(Usuario usuario, Collection<? extends GrantedAuthority> authorities) {
        super(usuario.getEmail(), usuario.getSenha(), authorities);
        this.fullName = usuario.getNome();
        this.userId = usuario.getId();
    }
}
