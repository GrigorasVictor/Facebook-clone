package facebook.server.service;

import facebook.server.entity.Friend;
import facebook.server.repository.FriendRepository;
import org.springframework.stereotype.Service;

@Service
public class FriendService extends AbstractService<Friend, FriendRepository> {
}
