package facebook.server.controller;

import facebook.server.dto.UserDTO;
import facebook.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody UserDTO userDTO) {
        if(userDTO == null || userDTO.getEmail() == null || userDTO.getPassword() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(userService.login(userDTO));
    }
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO userDTO) {
        if(userDTO == null || userDTO.getEmail() == null || userDTO.getPassword() == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(userService.register(userDTO));
    }
}

