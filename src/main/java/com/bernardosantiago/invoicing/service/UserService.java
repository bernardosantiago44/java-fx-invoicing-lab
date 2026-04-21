package com.bernardosantiago.invoicing.service;

import com.bernardosantiago.invoicing.model.User;

public class UserService {

    public void save(User user) {
        System.out.println("Saving user: " + user.getName());
    }
}