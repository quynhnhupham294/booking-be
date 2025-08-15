package org.example.bookingql.service;

import org.example.bookingql.entity.User;


public interface UserService {
    User findByEmail(String email);
}
