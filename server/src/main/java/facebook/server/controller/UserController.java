package facebook.server.controller;

import facebook.server.dto.UserDTO;
import facebook.server.entity.User;
import facebook.server.repository.UserRepository;
import facebook.server.service.StorageS3Service;
import facebook.server.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController extends AbstractController<User, UserRepository> {
    @Autowired
    UserService userService;
    @PostMapping("/photo")
    public ResponseEntity<String> handleFileUpload(HttpServletRequest req, @RequestParam("photo") MultipartFile photo) {
        if (photo.isEmpty()) return new ResponseEntity<>("Photo empty", HttpStatus.BAD_REQUEST);

        //Optional<String> token = JwtService.getToken(req);
        //if (token.isEmpty()) return new ResponseEntity<>("Missing token!", HttpStatus.BAD_REQUEST);
        //Integer userId = JwtService.staticExtractId(token.get());

        Long userId = 3L; // hardcoded for now, need to implement JWT
        Optional<User> optUser = userService.getUserRepository().findById(userId);
        if (optUser.isEmpty()) return new ResponseEntity<>("Missing user!", HttpStatus.BAD_REQUEST);
        User user = optUser.get();

        //the encoder puts "/" in the string, so we replace it with "." to avoid path problems
        String imageHashed = userService.getPasswordEncoder().encode(userId +
                user.getUsername()).
                replace("/", ".");
        userService.getStorageS3Service().uploadFile(photo, imageHashed);

        user.setUrlPhoto(imageHashed);
        userService.getUserRepository().save(user);
        System.out.println(user);
        return new ResponseEntity<>("{ \"msg\" : \"Photo received\", \"id_photo\": \"" + imageHashed + "\" }", HttpStatus.OK);
    }
    @GetMapping("/image/{imageName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String imageName) throws Exception {
        byte[] imageBytes = userService.getStorageS3Service().getImage(imageName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(imageBytes.length);

        return new ResponseEntity<>(
                imageBytes,
                headers, HttpStatus.OK);
    }
    @PostMapping("/auth/login")
    public ResponseEntity<UserDTO> login(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.login(userDTO));
    }
    @PostMapping("/auth/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO userDTO) {
        System.out.println(userDTO);
        return ResponseEntity.ok(userService.register(userDTO));
    }
    //TODO: implement logout
    //TODO: implement routing with JWT (adica sa nu poti accesa anumite rute fara sa fii logat)
}
