package com.gca.service;

import com.gca.domain.User;

/**
 * Interfaz que define las operaciones del servicio de usuario.
 */
public interface UserService {

    /**
     * Busca un usuario por su email.
     *
     * @param name el email de usuario a buscar
     * @return el usuario encontrado, o null si no existe
     */
    User findByEmail(String name);

    /**
     * Almacena un usuario en la base de datos.
     *
     * @param user el usuario a guardar
     */
    void save(User user);
}
