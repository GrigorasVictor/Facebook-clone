package facebook.server.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", nullable = false)
    @JsonProperty("username")
    private String username;

    @Column(name = "email", nullable = false)
    @JsonProperty("email")
    private String email;

    @Column(name = "password", nullable = false)
    @JsonProperty("password")
    private String password;

    @Column(name = "url_photo", nullable = false)
    @JsonProperty("url_photo")
    private String urlPhoto;

    @Column(name = "role", nullable = false)
    @JsonProperty("role")
    private String role;

    @Column(name = "created_at", nullable = false)
    @JsonProperty("created_at")
    private String createdAt;
}
