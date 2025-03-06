package facebook.server.service;

import facebook.server.entity.User;
import facebook.server.utilities.UserBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void testSaveUser() {
        User user = new UserBuilder()
                .withUsername("John Doe")
                .withEmail("john.doe@example.com")
                .withPassword("securepassword")
                .withUrlPhoto("http://example.com/photo.jpg")
                .withRole("USER")
                .withCreatedAt("2023-10-01")
                .build();

        User savedUser = userService.saveUser(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("John Doe");
        assertThat(savedUser.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("securepassword");
        assertThat(savedUser.getUrlPhoto()).isEqualTo("http://example.com/photo.jpg");
        assertThat(savedUser.getRole()).isEqualTo("USER");
        assertThat(savedUser.getCreatedAt()).isEqualTo("2023-10-01");
    }

    @Test
    public void testFindUserById() {
        User user = new UserBuilder()
                .withUsername("Jane Doe")
                .withEmail("jane.doe@example.com")
                .build();

        User savedUser = userService.saveUser(user);
        Optional<User> foundUser = userService.findUserById(savedUser.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("Jane Doe");
        assertThat(foundUser.get().getEmail()).isEqualTo("jane.doe@example.com");
    }

    @Test
    public void testFindAllUsers() {
        User user1 = new UserBuilder().withUsername("User One").withEmail("user.one@example.com").build();
        User user2 = new UserBuilder().withUsername("User Two").withEmail("user.two@example.com").build();

        userService.saveUser(user1);
        userService.saveUser(user2);

        Iterable<User> users = userService.findAllUsers();

        assertThat(users).hasSize(2); //avem 13
    }

    @Test
    public void testDeleteUserById() {
        User user = new UserBuilder().withUsername("User To Delete").withEmail("delete.me@example.com").build();

        User savedUser = userService.saveUser(user);
        userService.deleteUserById(savedUser.getId());

        Optional<User> deletedUser = userService.findUserById(savedUser.getId());

        assertThat(deletedUser).isNotPresent();
    }
}
