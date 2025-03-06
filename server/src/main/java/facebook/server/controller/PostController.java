package facebook.server.controller;


import facebook.server.entity.Post;
import facebook.server.repository.PostRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/post")
public class PostController extends AbstractController<Post, PostRepository> {
}
