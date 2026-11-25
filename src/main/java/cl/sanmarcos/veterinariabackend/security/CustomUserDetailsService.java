package cl.sanmarcos.veterinariabackend.security;

import cl.sanmarcos.veterinariabackend.model.Usuario;
import cl.sanmarcos.veterinariabackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        List<SimpleGrantedAuthority> authorities;
        if (usuario.getRol() != null) {
            String rolNombre = usuario.getRol().name().trim().toUpperCase();
            String authority = rolNombre.startsWith("ROLE_") ? rolNombre : "ROLE_" + rolNombre;
            authorities = Collections.singletonList(new SimpleGrantedAuthority(authority));
        } else {
            authorities = Collections.emptyList();
        }

        return new User(
                usuario.getEmail(),
                usuario.getPassword(),
                authorities
        );
    }
}