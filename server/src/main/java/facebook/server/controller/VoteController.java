package facebook.server.controller;

import facebook.server.entity.Vote;
import facebook.server.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vote")
public class VoteController extends AbstractController<Vote, VoteService> {
    @Autowired
    VoteService voteService;
    @Override
    public ResponseEntity<Vote> add(Vote newEntry) {
        try {
            return ResponseEntity.ok(voteService.save(newEntry));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<Vote> delete(Long id) {
        try {
            voteService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
