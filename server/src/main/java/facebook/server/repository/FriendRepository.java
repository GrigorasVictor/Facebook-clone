package facebook.server.repository;

import facebook.server.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendRepository extends AbstractRepository<Friend> {
}