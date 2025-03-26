package facebook.server.controller;

import facebook.server.dto.UserDTO;
import facebook.server.entity.User;
import facebook.server.service.UserService;
import facebook.server.utilities.DTOBuilder;
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
public class UserController extends AbstractController<User, UserService> {
    @Autowired
    UserService userService;
    @PostMapping("/photo")
    public ResponseEntity<UserDTO> handleFileUpload(@RequestPart("photo") MultipartFile photo) {
        User user = userService.savePhoto(photo);
        DTOBuilder dtoBuilder = new DTOBuilder();

        if(user == null) {
            dtoBuilder.withError("404")
                    .withMessage("Photo not saved")
                    .build();
        }else{
            dtoBuilder.withError("200")
                    .withUser(user)
                    .withMessage("Photo saved")
                    .build();
        }
        return new ResponseEntity<>(dtoBuilder.build(), HttpStatus.OK);
    }
    @GetMapping("/photo/{imageName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String imageName){
        try{
            byte[] imageBytes = userService.getStorageS3Service().getImage(imageName);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(imageBytes.length);

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    //TODO: user-ul normal n-are voie sa adauge sau sa stearga useri, update-ul numa daca este pe el insusi(din token)
}
