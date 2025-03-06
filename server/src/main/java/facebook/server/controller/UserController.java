package facebook.server.controller;

import facebook.server.entity.User;
import facebook.server.repository.UserRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController extends AbstractController<User, UserRepository> {
}
