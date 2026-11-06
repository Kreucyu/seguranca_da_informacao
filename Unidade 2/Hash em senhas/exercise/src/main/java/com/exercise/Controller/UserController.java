package com.exercise.Controller;

import com.exercise.DTO.UserDTO;
import com.exercise.DTO.ResponseDTO;
import com.exercise.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    private ResponseEntity<ResponseDTO> register(@Valid @RequestBody UserDTO registerUserDTO) {
        return ResponseEntity.ok().body(userService.register(registerUserDTO));
    }

    @PostMapping("/login")
    private ResponseEntity<ResponseDTO> login(@Valid @RequestBody UserDTO loginUserDTO) {
        return ResponseEntity.ok().body(userService.login(loginUserDTO));
    }

}
