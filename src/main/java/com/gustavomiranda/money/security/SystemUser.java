package com.gustavomiranda.money.security;

import com.gustavomiranda.money.domain.Usuario;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class SystemUser extends User {
    private static final long serialVersionUID = 1L;

    @Getter
    private Usuario user;

    public SystemUser(Usuario user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getEmail(), user.getSenha(), authorities);
        this.user = user;
    }
}
