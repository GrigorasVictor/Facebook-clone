package facebook.server.service;

import facebook.server.entity.Vote;
import facebook.server.repository.VoteRepository;
import org.springframework.stereotype.Service;

@Service
public class VoteService extends AbstractService<Vote, VoteRepository>{
}
