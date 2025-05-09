package facebook.server.service;

import facebook.server.entity.User;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpHeaders;

@Service
public class ServerService {
    Logger logger = LoggerFactory.getLogger(ServerService.class);

    @Autowired
    UserService userService;

    @Transactional
    public void sendBanPayload(Long id) throws Exception {
        User user = userService.getUserFromJWT();

        if(user.getRole().equals("USER")){
            logger.error("Unauthorized access attempt by user with ID: {}", user.getId());
            throw new Exception("Unauthorized access attempt by user with ID: " + user.getId());
        }

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/user/ban/" + id;

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "Bearer " + userService.getJWT());

        HttpEntity<String> entity = new HttpEntity<>("", headers);

        try {
            restTemplate.postForObject(url, entity, String.class);
        } catch (Exception e) {
            userService.unbanUser(id);
            logger.error("An error occurred while sending the ban payload", e);
            throw new Exception("An error occurred while sending the ban payload: " + e.getMessage());
        }
    }


}
