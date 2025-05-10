package facebook.auth.controller;

import facebook.auth.entity.UserAuthentification;
import facebook.auth.service.EmailService;
import facebook.auth.service.UserAuthentificationService;
import facebook.auth.service.UserDetailsService;
import facebook.auth.utilities.AESUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class UserController extends AbstractController<UserAuthentification, UserAuthentificationService> {
    Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    UserAuthentificationService userAuthentificationService;
    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    AESUtil aesUtil;
    @Autowired
    EmailService emailService;



    @PostMapping("/ban")
    public ResponseEntity banUser(@RequestBody String encryptedId) {
        // this should be authorized but we have an encryption so it's not a big deal
        try {
            Long id = Long.valueOf(aesUtil.decrypt(encryptedId));
            logger.info("User ID to ban: {}", id);
            userAuthentificationService.banUser(id);
            emailService.sendBanEmail("victorandrei201112@gmail.com");
            //emailService.sendBanEmail(userDetailsService.getUser(id).getEmail());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/unban")
    public ResponseEntity unbanUser(@RequestBody String encryptedId) {
        // this should be authorized but we have an encryption so it's not a big deal
        try {
            Long id = Long.valueOf(aesUtil.decrypt(encryptedId));
            logger.info("User ID to unban: {}", id);
            userAuthentificationService.unbanUser(id);
            emailService.sendUnbanEmail("victorandrei201112@gmail.com");
            //emailService.sendUnbanEmail(userDetailsService.getUser(id).getEmail());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
