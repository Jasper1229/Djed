package jjs.djed.controllers;


import jjs.djed.model.User;
import jjs.djed.services.UserService;
import jjs.djed.web.post.CreateUserRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class UserController {
    private final UserService userService;

    public  UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable UUID id){
         User user = userService.getUser(id);
         if(user == null){
             return ResponseEntity.notFound().build();
         }
         return ResponseEntity.ok(user);
    }

    @PostMapping("/users")
    public ResponseEntity<Object> createUser(@RequestBody CreateUserRequest request) {
        if(userService.usernameExists(request.username())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
        }
        User user = userService.createUser(request.username(), request.password());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getUsers();
        if(users.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(users);
    }
}
