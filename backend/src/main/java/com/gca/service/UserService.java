package com.gca.service;

import com.gca.domain.User;

public interface UserService {

    User findByEmail(String name);

    void save(User user);
}
