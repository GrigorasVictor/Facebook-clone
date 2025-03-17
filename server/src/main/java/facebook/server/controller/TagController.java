package facebook.server.controller;

import facebook.server.entity.Tag;
import facebook.server.repository.TagRepository;
import facebook.server.service.TagService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tag")
public class TagController extends AbstractController<Tag, TagService> {
}
