package facebook.server.controller;

import facebook.server.dto.UserDTO;
import facebook.server.entity.User;
import facebook.server.service.UserService;
import facebook.server.utilities.DTOBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/user")
public class UserController extends AbstractController<User, UserService> {
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    UserService userService;

    @PostMapping("/photo")
    public ResponseEntity uploadUserPhoto(@RequestPart("photo") MultipartFile photo) {
        try {
            userService.savePhoto(photo);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        try{
            return new ResponseEntity<>(userService.getUserFromJWT(), HttpStatus.OK);
        }catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //TODO: user-ul normal n-are voie sa adauge sau sa stearga useri, update-ul numa daca este pe el insusi(din token)
}
