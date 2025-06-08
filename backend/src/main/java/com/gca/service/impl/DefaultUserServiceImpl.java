package com.gca.service.impl;

import com.gca.domain.User;
import com.gca.repository.RoleRepository;
import com.gca.repository.UserRepository;
import com.gca.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementación por defecto del servicio de usuarios.
 */
@Service
public class DefaultUserServiceImpl implements UserService, UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultUserServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DefaultUserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    /**
     * Busca un usuario por su email.
     * @param email El email del usuario a buscar.
     * @return El usuario encontrado.
     * @throws UsernameNotFoundException Si no se encuentra el usuario.
     */
    @Override
    public User findByEmail(String email) {
        LOGGER.debug("Buscando usuario por email hash: {}", email != null ? email.hashCode() : "null");
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }

    /**
     * Almacena un nuevo usuario en la base de datos.
     * @param user El usuario a almacenar.
     */
    @Override
    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Codifica la contraseña antes de guardarla
        user.setRoles(roleRepository.getRoleByName("Gestor")); // Solo asigna el rol "Gestor" por defecto
        userRepository.save(user);
        LOGGER.info("Se ha creado un nuevo usuario");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByEmail(username);
    }
}
