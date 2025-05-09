package facebook.server.service;

import facebook.server.entity.Content;
import facebook.server.entity.User;
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
        if (vote.getType() == null  || vote.getContent() == null) {
            throw new IllegalArgumentException("Vote type or content cannot be null");
        }

        Content content = contentService.getRepository().findById(vote.getContent().getId()).get();
        User user = userService.getUserFromJWT();
        if (content.getId() == null) {
            throw new IllegalArgumentException("Content ID cannot be null");
        }
        if(voteRepository.findByUserIdAndContentId(user.getId(), vote.getContent().getId()).isPresent()) {
            throw new IllegalArgumentException("Vote already exists for this user and content");
        }
        if(content.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("User cannot vote on their own content");
        }

        vote.setUser(user);
        content.setNrVotes(content.getNrVotes() + 1);

        if (vote.getType().equals("UPVOTE")) {

            User contentUser = content.getUser();
            float score = content.isTypeContent() ? 2.5f : 5f;
            userService.updateScore(contentUser, score);

        } else if (vote.getType().equals("DOWNVOTE")) {
            User contentUser = content.getUser();

            userService.updateScore(user, -1.5f); // voter score
            float score = content.isTypeContent() ? -1.5f : -2.5f;
            userService.updateScore(contentUser, score);
        }

        content.addVote(vote);
        vote.setContent(content);
        contentService.getRepository().save(content); //cred ca salveaza de 2 ori
        return vote;
    }

    @Override
    public void deleteById(Long id) {
        Vote vote = voteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vote not found"));

        Content content = contentService.getRepository().findById(vote.getContent().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Content not found"));

        User user = userService.getUserFromJWT();
        if(!Objects.equals(user.getId(), vote.getUser().getId())) {
            throw new SecurityException("User ID does not match the JWT token");
        }

        if(content.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("User cannot vote on their own content");
        }

        content.setNrVotes(content.getNrVotes() - 1);
        if(vote.getType().equals("UPVOTE")) {

            User contentUser = content.getUser();
            float score = content.isTypeContent() ? -2.5f : -5f;
            userService.updateScore(contentUser, score);

        } else if (vote.getType().equals("DOWNVOTE")) {

            User contentUser = content.getUser();
            float score = content.isTypeContent() ? 1.5f : 2.5f;
            userService.updateScore(contentUser, score);
        }
        contentService.getRepository().save(content);
        super.deleteById(id);
    }
}
