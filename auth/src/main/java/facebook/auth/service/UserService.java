package facebook.auth.service;

import facebook.auth.dto.AuthDTO;
import facebook.auth.dto.RegisterDTO;
import facebook.auth.dto.UserDTO;
import facebook.auth.entity.User;
import facebook.auth.entity.UserAuthentification;
import facebook.auth.repository.UserAuthentificationRepository;
import facebook.auth.utilities.AESUtil;
import facebook.auth.utilities.DTOBuilder;
import facebook.auth.utilities.JWTUtils;
import facebook.auth.utilities.UserAuthentificationBuilder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Getter
public class UserService{
    @Autowired
    private UserAuthentificationRepository userAuthentificationRepository;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AESUtil aesUtil;

    public UserDTO register(RegisterDTO registerDTO) {
        DTOBuilder response = new DTOBuilder();

        try {
            UserAuthentification user = new UserAuthentificationBuilder()
                    .withUsername(registerDTO.getUsername())
                    .withEmail(registerDTO.getEmail())
                    .withPassword(passwordEncoder.encode(registerDTO.getPassword()))
                    .build();

            System.out.println(user);

            if(userAuthentificationRepository.findByEmail(user.getEmail()).isPresent()) {
                response.withStatusCode(400)
                        .withMessage("User already exists with this email!");

                return response.build();
            }

            UserAuthentification userResult = userAuthentificationRepository.save(user);
            if(userResult.getId() > 0) {
                response.withUser(userResult.toUser())
                        .withMessage("User saved succefully!")
                        .withStatusCode(200);

                return response.build();
            }
        }catch (Exception e) {
            response.withStatusCode(500)
                    .withError(e.getMessage());
        }
        return response.build();
    }

    public UserDTO login(AuthDTO authDTO) {
        DTOBuilder response = new DTOBuilder();

        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(),
                                                                        authDTO.getPassword()));

            var user = userAuthentificationRepository.findByEmail(authDTO.getEmail())
                                                                .orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            response.withStatusCode(200)
                    .withToken(jwt)
                    .withRefreshToken(refreshToken)
                    .withExpirationTime("24Hrs")
                    .withMessage("Successfully logged in");
        }catch (Exception e) {
            response.withStatusCode(500)
                    .withMessage(e.getMessage());
        }
        return response.build();
    }

    public UserDTO refreshToken(UserDTO refreshTokenRequest) { // probabil nu-l folosim
        DTOBuilder response = new DTOBuilder();
        try {
            String email = jwtUtils.extractUsername(refreshTokenRequest.getToken());
            UserAuthentification user = userAuthentificationRepository
                    .findByEmail(email).orElseThrow();

            if (jwtUtils.isTokenValid(refreshTokenRequest.getToken(), user)) {
                var jwt = jwtUtils.generateToken(user);
                response.withStatusCode(200)
                        .withToken(jwt)
                        .withRefreshToken(refreshTokenRequest.getToken())
                        .withExpirationTime("24Hrs")
                        .withMessage("Successfully refreshed token!")
                        .withStatusCode(200);
            }
        } catch (Exception e) {
            response.withStatusCode(500)
                    .withMessage(e.getMessage());

        }
        return response.build();
    }
}

