package facebook.server.service;

import facebook.server.entity.User;
import facebook.server.repository.AbstractRepository;
import facebook.server.repository.UserRepository;
import facebook.server.utilities.JWTUtils;
import facebook.server.utilities.UserBuilder;
import facebook.server.utilities.AESUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@Getter
public class UserService extends AbstractService<User, AbstractRepository<User>> {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private StorageS3Service storageS3Service;
    @Autowired
    private AESUtil aesUtil;
    @Autowired
    private HttpServletRequest request;

    public Long getUserIdFromJWT(){
        final String jwtToken = request.getHeader("Authorization")
                .substring(7);
        if (jwtToken.isEmpty()) return null;

        return userRepository.findByEmail(jwtUtils.extractUsername(jwtToken))
                .get().getId();
    }
    public User getUserFromJWT(){
        final String jwtToken = request.getHeader("Authorization")
                .substring(7);
        if (jwtToken.isEmpty()) return null;

        return userRepository.findByEmail(jwtUtils.extractUsername(jwtToken)).get();
    }

    @Transactional
    public void processPayload(String payload) throws Exception {
        String decryptedPayload = aesUtil.decrypt(payload);

        User user = UserBuilder.toUser(decryptedPayload);

        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new Exception("User exists!");
        }

        user = userRepository.save(user);
        if(user.getId() == 0) {
            throw new Exception("User not saved!");
        }

        System.out.println("User saved successfully!");
    }

    @Transactional
    public User savePhoto(MultipartFile photo){
        if (photo.isEmpty()) return null;
        final String jwtToken = request.getHeader("Authorization")
                                        .substring(7);
        if (jwtToken.isEmpty()) return null;
        Long userId = userRepository
                            .findByEmail(jwtUtils.extractUsername(jwtToken))
                            .get().getId();
        Optional<User> optUser = userRepository.findById(userId);
        if (optUser.isEmpty()) return null;
        User user = optUser.get();

        //the encoder puts "/" in the string, so we replace it with "." to avoid path problems
        String imageHashed = passwordEncoder.encode(userId +
                                        user.getUsername()).replace("/", ".");

        storageS3Service.uploadFile(photo, imageHashed);
        user.setUrlPhoto(imageHashed);
        userRepository.save(user);
        user.setPassword("");
        return user;
    }

    public float updateScore(User user, float score){
        user.setScore(user.getScore() + score);
        userRepository.save(user);
        return user.getScore();
    }

}
