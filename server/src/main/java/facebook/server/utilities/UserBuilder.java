package facebook.server.utilities;

import facebook.server.entity.User;

import java.time.LocalDateTime;

public class UserBuilder {
    private final User user;

    public UserBuilder() {
        user = new User();
        user.setUsername("Default User");
        user.setEmail("default@example.com");
        user.setPassword("securepassword");
        user.setUrlPhoto("-");
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now().toString());
    }

    public UserBuilder withUsername(String username) {
        user.setUsername(username);
        return this;
    }

    public UserBuilder withEmail(String email) {
        user.setEmail(email);
        return this;
    }

    public UserBuilder withPassword(String password) {
        user.setPassword(password);
        return this;
    }

    public UserBuilder withUrlPhoto(String urlPhoto) {
        user.setUrlPhoto(urlPhoto);
        return this;
    }

    public UserBuilder withRole(String role) {
        user.setRole(role);
        return this;
    }
    public User build() {
        return user;
    }
}
