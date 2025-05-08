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
        if (vote.getType().equals("UPVOTE")) {
            content.setNrVotes(content.getNrVotes() + 1);

            User contentUser = content.getUser();

            if(content.isTypeContent()) {
                userService.updateScore(contentUser, 2.5f);
            }else{
                userService.updateScore(contentUser, 5f);
            }
        } else if (vote.getType().equals("DOWNVOTE")) {
            content.setNrVotes(content.getNrVotes() - 1);

            User contentUser = content.getUser();
            User voterUser = user;

            if(content.isTypeContent()) {
                userService.updateScore(contentUser, -1.5f);
            }else {
                userService.updateScore(contentUser, -2.5f);
            }
            userService.updateScore(voterUser, -1.5f);
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

        if(vote.getType().equals("UPVOTE")) {
            content.setNrVotes(content.getNrVotes() - 1);
            User contentUser = content.getUser();
            if(content.isTypeContent()) {
                userService.updateScore(contentUser, -2.5f);
            }else{
                userService.updateScore(contentUser, -5f);
            }
        } else if (vote.getType().equals("DOWNVOTE")) {
            content.setNrVotes(content.getNrVotes() + 1);
            User contentUser = content.getUser();
            User voterUser = user;
            if(content.isTypeContent()) {
                userService.updateScore(contentUser, 1.5f);
            }else {
                userService.updateScore(contentUser, 2.5f);
            }
            userService.updateScore(voterUser, 1.5f);
        }
        contentService.getRepository().save(content);
        super.deleteById(id);
    }
}
