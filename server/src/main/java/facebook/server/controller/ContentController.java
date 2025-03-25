package facebook.server.controller;

import facebook.server.entity.Content;
import facebook.server.repository.ContentRepository;
import facebook.server.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/content")
public class ContentController extends AbstractController<Content, ContentService> {
    @Autowired
    private ContentService contentService;

    @Override
    public ResponseEntity<Content> add(Content newEntry) {
        return new ResponseEntity<>(contentService.save(newEntry), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Content> delete(Long id) {
        try {
            contentService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public ResponseEntity<Content> update(Long id, Content updatedEntry) {
        try {
            return new ResponseEntity<>(contentService.update(id, updatedEntry), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
