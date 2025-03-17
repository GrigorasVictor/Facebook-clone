package facebook.server.controller;

import facebook.server.entity.Content;
import facebook.server.repository.ContentRepository;
import facebook.server.service.ContentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/content")
public class ContentController extends AbstractController<Content, ContentService> {
}
