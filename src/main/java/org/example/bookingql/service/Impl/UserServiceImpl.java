package org.example.bookingql.service.Impl;

import org.example.bookingql.dao.UserDAO;
import org.example.bookingql.entity.User;
import org.example.bookingql.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDAO userDAO;

    @Override
    public User findByEmail(String email) {
        return userDAO.findByEmail(email);
    }
}
