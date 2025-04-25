package io.mountblue.blogApplication.service;

import io.mountblue.blogApplication.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {

    @Override
    UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException;

    void createUser(User user);
}
