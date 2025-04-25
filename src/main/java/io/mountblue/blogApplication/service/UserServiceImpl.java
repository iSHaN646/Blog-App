package io.mountblue.blogApplication.service;

import io.mountblue.blogApplication.entity.User;
import io.mountblue.blogApplication.entity.UserInfo;
import io.mountblue.blogApplication.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    private UserRepository userRepository;


    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(userName);
        if(user==null){
            throw new UsernameNotFoundException("this user does not exist");
        }

        return new UserInfo(user);
    }

    @Override
    public void createUser(User user) {
        userRepository.save(user);
    }
}
