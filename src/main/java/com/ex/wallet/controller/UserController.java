package com.ex.wallet.controller;

import com.ex.wallet.dbase.User;
import com.ex.wallet.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User request) {
        User user = userService.createUser(request.getName(), request.getSurname(), request.getPhoneNumber());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

}
