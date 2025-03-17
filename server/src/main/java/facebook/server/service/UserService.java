package facebook.server.service;

import facebook.server.dto.UserDTO;
import facebook.server.entity.User;
import facebook.server.repository.AbstractRepository;
import facebook.server.repository.UserRepository;
import facebook.server.utilities.JWTUtils;
import facebook.server.utilities.UserBuilder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

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

    public UserDTO register(UserDTO registrationRequest) {
        UserDTO response = new UserDTO();

        try {
            User user = new UserBuilder()
                    .withUsername(registrationRequest.getUsername())
                    .withEmail(registrationRequest.getEmail())
                    .withPassword(passwordEncoder.encode(registrationRequest.getPassword()))
                    .build();
            System.out.println(user);
            if(userRepository.findByEmail(user.getEmail()).isPresent()) {
                response.setStatusCode(400);
                response.setMessage("User already exists!");
                return response;
            }
            User userResult = userRepository.save(user);
            if(userResult.getId() > 0) {
                response.setUser(userResult);
                response.setMessage("User saved succefully!");
                response.setStatusCode(200);
            }
        }catch (Exception e) {
            response.setStatusCode(500);
            response.setError(e.getMessage());
        }
        return response;
    }

    public UserDTO login(UserDTO loginRequest) {
        UserDTO response = new UserDTO();
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                        loginRequest.getPassword()));
            var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getRole());
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hrs");
            response.setMessage("Successfully logged in");
        }catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public UserDTO refreshToken(UserDTO refreshTokenRequest) {
        UserDTO response = new UserDTO();
        try {
            String email = jwtUtils.extractUsername(refreshTokenRequest.getToken());
            User user = userRepository.findByEmail(email).orElseThrow();
            if(jwtUtils.isTokenValid(refreshTokenRequest.getToken(), user)) {
                var jwt = jwtUtils.generateToken(user);
                response.setStatusCode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshTokenRequest.getToken());
                response.setExpirationTime("24Hrs");
                response.setMessage("Successfully refreshed token!");
            }
            response.setStatusCode(200);
            return response;
        }catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            return response;
        }
    }
}
