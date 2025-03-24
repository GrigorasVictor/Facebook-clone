package facebook.server.service;

import facebook.server.entity.User;
import facebook.server.repository.AbstractRepository;
import facebook.server.repository.UserRepository;
import facebook.server.utilities.JWTUtils;
import facebook.server.utilities.UserBuilder;
import facebook.server.utilities.AESUtil;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

}
