package facebook.server.service;

import facebook.server.entity.Friend;
import facebook.server.repository.FriendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FriendService extends AbstractService<Friend, FriendRepository> {
    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private UserService userService;

    @Override
    public Friend save(Friend entity) throws Exception {
        if(entity.getUser1() == null || entity.getUser2() == null) {
            throw new IllegalArgumentException("Friendship user1 or user2 cannot be null");
        }

        if(userService.getRepository().findById(entity.getUser1().getId()).isEmpty()) {
            throw new IllegalArgumentException("User1 not found");
        }
        if(userService.getRepository().findById(entity.getUser2().getId()).isEmpty()) {
            throw new IllegalArgumentException("User2 not found");
        }

        // Check if the friendship already exists

        if(!Objects.equals(userService.getUserIdFromJWT(), entity.getUser1().getId()) &&
                !Objects.equals(userService.getUserIdFromJWT(), entity.getUser2().getId())) {
            throw new SecurityException("User ID does not match the JWT token");
        }

        return friendRepository.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        super.deleteById(id);
    }
}
