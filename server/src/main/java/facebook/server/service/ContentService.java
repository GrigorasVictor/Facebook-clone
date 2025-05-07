package facebook.server.service;

import facebook.server.dto.ContentDTO;
import facebook.server.entity.Content;
import facebook.server.entity.Tag;
import facebook.server.entity.User;
import facebook.server.repository.ContentRepository;
import facebook.server.utilities.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TagService tagService;

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

        //saving the ph
        return repository.save(entity);
    }

    public Content save(ContentDTO entityDTO, MultipartFile photo) throws Exception {
        User user = userService.getUserFromJWT();

        if (photo.isEmpty()) {
            throw new RuntimeException("Photo is empty");
        }

        // Extract content from DTO
        Content entity = entityDTO.getContent();

        // Set required fields
        entity.setUser(user);
        entity.setVotes(new ArrayList<>());
        entity.setNrComments(0);
        entity.setNrVotes(0);
        entity.setCreatedAt(LocalDateTime.now());

        // Initialize tags if null
        if (entity.getTags() == null) {
            entity.setTags(new ArrayList<>());
        }

        // Generate unique image name and upload photo
        String imageHashed = passwordEncoder.encode(System.currentTimeMillis() +
                user.getUsername()).replace("/", ".");
        String url = storageS3Service.uploadFile(photo, imageHashed);
        entity.setUrlPhoto(url);

        try {
            // Save content first
            Content savedContent = repository.save(entity);

            // Process all tags from the DTO
            for (Tag tagItem : entityDTO.getTag()) {
                if (tagItem.getName() != null && !tagItem.getName().isEmpty()) {
                    Tag tag = tagService.findByNameOrCreate(tagItem.getName());

                    // Add bidirectional relationship
                    if (tag.getContents() == null) {
                        tag.setContents(new ArrayList<>());
                    }
                    tag.getContents().add(savedContent);

                    if (!savedContent.getTags().contains(tag)) {
                        savedContent.getTags().add(tag);
                    }
                }
            }

            // Save again to update tags
            savedContent = repository.save(savedContent);
            return savedContent;
        } catch (Exception e) {
            // Delete uploaded file if saving fails
            storageS3Service.deleteFile(imageHashed);
            throw new RuntimeException("Error while saving content with file: " + e.getMessage());
        }
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

    public List<Content> getAllLimited() throws IOException {
        Pageable pageable = PageRequest.of(0, 5);
        return repository.findAll(pageable).getContent();
    }
}
