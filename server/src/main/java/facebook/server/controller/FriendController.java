package facebook.server.controller;

import facebook.server.entity.Friend;
import facebook.server.repository.FriendRepository;
import facebook.server.service.FriendService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/friend")
public class FriendController extends AbstractController<Friend, FriendService> {
}
