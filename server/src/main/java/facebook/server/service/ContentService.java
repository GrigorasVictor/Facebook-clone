package facebook.server.service;

import facebook.server.entity.Content;
import facebook.server.repository.ContentRepository;
import org.springframework.stereotype.Service;

@Service
public class ContentService extends AbstractService<Content, ContentRepository> {
}
