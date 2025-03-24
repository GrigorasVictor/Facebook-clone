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
@RequestMapping("/server")
public class ServerController {
    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<String> receivePayload(@RequestBody String payload) {
        if(payload == null || payload.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            userService.processPayload(payload);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            if(e.getMessage().equals("User exists!")) {
                return ResponseEntity.badRequest().body("User exists!");
            }
        }
        return ResponseEntity.ok("Payload received!");
    }
}

