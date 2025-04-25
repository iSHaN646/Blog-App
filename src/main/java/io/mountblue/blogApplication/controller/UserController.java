package io.mountblue.blogApplication.controller;

import io.mountblue.blogApplication.entity.User;
import io.mountblue.blogApplication.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;

@Controller
public class UserController {

    private UserService userService;
    private BCryptPasswordEncoder passwordEncoder;

    public UserController(UserService userService,BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder=passwordEncoder;
    }

    @GetMapping("/signup")
    public String showSignup(Model model){
        model.addAttribute("user",new User());
        return "Signup";
    }

    @PostMapping("/adduser")
    public String addUser(@ModelAttribute("user") User user){
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.createUser(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm(){
        return "Login";
    }
}
