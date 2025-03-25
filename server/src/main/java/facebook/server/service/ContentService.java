package facebook.server.service;

import facebook.server.entity.Content;
import facebook.server.entity.User;
import facebook.server.repository.ContentRepository;
import facebook.server.utilities.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ContentService extends AbstractService<Content, ContentRepository> {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private StorageS3Service storageS3Service;

    @Override
    public Content save(Content entity) {
        final String authHeader = request.getHeader("Authorization");
        final String jwtToken = authHeader.substring(7);

        User user = userService.getUserRepository()
                .findByEmail(jwtUtils.extractUsername(jwtToken))
                .orElseThrow(() -> new RuntimeException("User not found"));

        entity.setUser(user);
        entity.setVotes(new ArrayList<>());
        entity.setNrComments(0);
        entity.setNrVotes(0);
        entity.setTags(new ArrayList<>());

        return repository.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        Content content = repository.findById(id).get();

        if (content.getUser().getEmail().equals(jwtUtils
                .extractUsername(request.getHeader("Authorization").substring(7)))) {
            repository.deleteById(id);
            return;
        }

        throw new RuntimeException("You are not allowed to delete this content");
    }

    @Override
    public Content update(Long id, Content entity) {
        if(entity.getUser() == null){
            throw new RuntimeException("You are not allowed to update this content");
        }

        if (!entity.getUser().getEmail().equals(jwtUtils
                .extractUsername(request.getHeader("Authorization").substring(7)))) {
            throw new RuntimeException("You are not allowed to update this content");
        }
        if(repository.findById(id).isEmpty())
            throw new RuntimeException("Content not found");


        Content target = repository.findById(id).get();
        target.setText(entity.getText());
        target.setTitle(entity.getTitle());
        target.setTags(entity.getTags());

        return repository.save(target);
    }
}
