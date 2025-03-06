package facebook.server.service;

import facebook.server.entity.Friend;
import facebook.server.repository.FriendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FriendService {

    private final FriendRepository friendRepository;

    @Autowired
    public FriendService(FriendRepository friendRepository) {
        this.friendRepository = friendRepository;
    }

    public List<Friend> findAllFriends() {
        return friendRepository.findAll();
    }

    public Optional<Friend> findFriendById(Long id) {
        return friendRepository.findById(id);
    }

    public Friend saveFriend(Friend friend) {
        return friendRepository.save(friend);
    }

    public void deleteFriendById(Long id) {
        friendRepository.deleteById(id);
    }
}