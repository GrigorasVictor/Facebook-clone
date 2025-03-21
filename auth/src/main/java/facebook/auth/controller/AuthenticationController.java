package facebook.auth.controller;

import facebook.auth.dto.AuthDTO;
import facebook.auth.dto.RegisterDTO;
import facebook.auth.dto.UserDTO;
import facebook.auth.service.UserService;
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
    public ResponseEntity<UserDTO> login(@RequestBody AuthDTO authDTO) {
        if(authDTO == null || authDTO.getEmail() == null || authDTO.getPassword() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(userService.login(authDTO));
    }


    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody RegisterDTO registerDTO) {
        if(registerDTO == null || registerDTO.getEmail() == null
                || registerDTO.getPassword() == null || registerDTO.getUsername() == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            UserDTO userDTO = userService.register(registerDTO);
            userService.sendPayload(userDTO);

            return ResponseEntity.ok(userDTO);

        }catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}

