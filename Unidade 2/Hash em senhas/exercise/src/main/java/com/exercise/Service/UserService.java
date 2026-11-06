package com.exercise.Service;

import com.exercise.DTO.UserDTO;
import com.exercise.DTO.ResponseDTO;
import com.exercise.Entity.User;
import com.exercise.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseDTO register(UserDTO registerUserDTO) {
        User user = new User();
        user.setLogin(registerUserDTO.login());
        user.setPassword(passwordEncoder.encode(registerUserDTO.password()));
        user.setCreated_at(LocalDateTime.now());

        if (userRepository.findByLogin(registerUserDTO.login()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Login já cadastrado");
        }

        userRepository.save(user);
        return new ResponseDTO("Usuário registrado com sucesso!");
    }

    public ResponseDTO login (UserDTO loginUserDTO) {
        User user = userRepository.findByLogin(loginUserDTO.login());

        if(user == null || !passwordEncoder.matches(loginUserDTO.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
        }

        return new ResponseDTO("Login realizado com sucesso!");
    }
}
