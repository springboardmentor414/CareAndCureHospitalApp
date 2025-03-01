package com.cac.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cac.exception.UserNotFoundException;
import com.cac.dto.LoginDetails;
import com.cac.model.ContactForm;
import com.cac.model.UserInfo;
import com.cac.service.UserService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<UserInfo> login(
            @RequestBody LoginDetails loginDetails) throws UserNotFoundException {
                UserInfo userInfo = userService.verifyLoginDetails(loginDetails);

        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }

    @GetMapping("/viewUserInfo/{username}")
    public ResponseEntity<UserInfo> getMethodName(@PathVariable String username) throws UserNotFoundException {
        UserInfo userInfo = userService.getUserByUsername(username);
        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }

    @PostMapping("/addContactFormDetails")
    public ResponseEntity<String> addContactFormDetails(@Valid @RequestBody ContactForm contactForm) {
        userService.addContactForm(contactForm);
        return new ResponseEntity<>("Contact Form Details Added Successfully", HttpStatus.OK);
    }

}
