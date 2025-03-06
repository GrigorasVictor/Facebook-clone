package facebook.server.controller;

import facebook.server.entity.Vote;
import facebook.server.repository.VoteRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vote")
public class VoteController extends AbstractController<Vote, VoteRepository> {
}
