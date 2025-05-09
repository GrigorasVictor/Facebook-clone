package facebook.auth.controller;

import facebook.auth.entity.UserAuthentification;
import facebook.auth.service.UserAuthentificationService;
import facebook.auth.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
public class UserController extends AbstractController<UserAuthentification, UserAuthentificationService> {
    @Autowired
    UserAuthentificationService userAuthentificationService;
    @Autowired
    UserDetailsService userDetailsService;

    @PostMapping("/ban/{id}")
    public ResponseEntity banUser(@PathVariable Long id) {
        if(userDetailsService.getUserFromJWT().getRole().equals("USER")){
            return ResponseEntity.status(401).body("Unauthorized");
        }
        userAuthentificationService.banUser(id);
        return ResponseEntity.ok().build();
    }

}
