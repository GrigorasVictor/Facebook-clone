package facebook.server.controller;

import facebook.server.entity.Content;
import facebook.server.service.ContentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
@RequestMapping("/content")
public class ContentController extends AbstractController<Content, ContentService> {
    @Autowired
    private ContentService contentService;
    @Autowired
    private HttpServletRequest request;

    @Override
    public ResponseEntity<Content> add(Content newEntry) { // era mai usor daca il luam ca parammetru de functie
                                                           //dar nu mai mergea sa dau Override la functie

        MultipartFile photo = ((MultipartHttpServletRequest) request).getFile("photo");
        return photo != null ?  new ResponseEntity<>(contentService.save(newEntry, photo), HttpStatus.OK) :
                                new ResponseEntity<>(contentService.save(newEntry), HttpStatus.OK);
    } // trebuie sa vedem cum testam asta cu form-data


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
