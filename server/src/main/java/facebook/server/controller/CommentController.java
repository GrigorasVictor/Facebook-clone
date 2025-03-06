package facebook.server.controller;

import facebook.server.entity.Comment;
import facebook.server.repository.CommentRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comment")
public class CommentController extends AbstractController<Comment, CommentRepository> {
}
