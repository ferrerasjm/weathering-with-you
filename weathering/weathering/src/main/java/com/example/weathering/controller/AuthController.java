package com.example.weathering.controller;

import com.example.weathering.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public Map<String, String> signup(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String confirm = body.get("confirm");

        Map<String, String> res = new HashMap<>();
        if (!password.equals(confirm)) {
            res.put("message", "Passwords do not match!");
            return res;
        }

        String result = userService.registerUser(username, password);
        if (result.equals("Success")) {
            res.put("message", "Account Successfully Created! Try Logging In.");
        } else {
            res.put("message", result);
        }
        return res;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        Map<String, String> res = new HashMap<>();
        if (userService.login(username, password)) {
            res.put("message", "Login successful");
        } else {
            res.put("message", "Invalid username or password");
        }
        return res;
    }

    @GetMapping("/users")
    public Object getAllUsers() {
        return userService.getAllUsers();
    }


    @PutMapping("/update")
    public Map<String, String> updatePassword(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String newPassword = body.get("newPassword");

        String message = userService.updatePassword(username, newPassword);
        return Map.of("message", message);
    }


    @DeleteMapping("/delete/{username}")
    public Map<String, String> deleteUser(@PathVariable String username) {
        String message = userService.deleteUser(username);
        return Map.of("message", message);
    }


}
