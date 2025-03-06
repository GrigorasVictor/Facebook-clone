package facebook.server.controller;

import facebook.server.entity.User;
import facebook.server.repository.UserRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController extends AbstractController<User, UserRepository> {
    //TODO: implement login
    //TODO: implement signup
    //TODO: implement logout
    //TODO: implement routing with JWT (adica sa nu poti accesa anumite rute fara sa fii logat)
}
