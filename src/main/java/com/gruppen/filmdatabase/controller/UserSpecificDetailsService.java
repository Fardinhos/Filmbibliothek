package com.gruppen.filmdatabase.controller;

import com.gruppen.filmdatabase.entity.User;
import com.gruppen.filmdatabase.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;

import java.util.Objects;

public class UserSpecificDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository repo;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        final User foundUser = repo.findByEmail(email);
        if (foundUser == null) {
            throw new UsernameNotFoundException(email);
        }

        return new UserSpecificDetails(foundUser);
    }
}
