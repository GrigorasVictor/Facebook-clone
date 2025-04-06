package facebook.server.service;

import facebook.server.entity.Content;
import facebook.server.entity.Vote;
import facebook.server.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class VoteService extends AbstractService<Vote, VoteRepository>{
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private ContentService contentService;
    @Autowired
    private UserService userService;

    @Override
    public Vote save(Vote vote) throws Exception {
        if (vote.getType() == null || vote.getUser() == null || vote.getContent() == null) {
            throw new IllegalArgumentException("Vote type, user, or content cannot be null");
        }

        Content content = contentService.getRepository().findById(vote.getContent().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Content not found"));

        if(voteRepository.findByUserIdAndContentId(vote.getUser().getId(), vote.getContent().getId()).isPresent()) {
            throw new IllegalArgumentException("Vote already exists for this user and content");
        }

        if (contentService.getRepository().findById(content.getId()).isEmpty()) {
            throw new IllegalArgumentException("Content not found");
        }
        if (userService.getRepository().findById(vote.getUser().getId()).isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        if (!Objects.equals(userService.getUserIdFromJWT(), vote.getUser().getId())) {
            throw new SecurityException("User ID does not match the JWT token");
        }
        content.setNrVotes(content.getNrVotes() + 1);
        contentService.getRepository().save(content);

        return voteRepository.save(vote);
    }

    @Override
    public void deleteById(Long id) {
        Vote vote = voteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vote not found"));

        Content content = contentService.getRepository().findById(vote.getContent().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Content not found"));

        if(!Objects.equals(userService.getUserIdFromJWT(), vote.getUser().getId())) {
            throw new SecurityException("User ID does not match the JWT token");
        }

        content.setNrVotes(content.getNrVotes() - 1);
        contentService.getRepository().save(content);
        super.deleteById(id);
    }
}
