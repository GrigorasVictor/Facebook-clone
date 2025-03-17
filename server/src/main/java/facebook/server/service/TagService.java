package facebook.server.service;

import facebook.server.entity.Tag;
import facebook.server.repository.TagRepository;
import org.springframework.stereotype.Service;

@Service
public class TagService extends AbstractService<Tag, TagRepository> {
}
