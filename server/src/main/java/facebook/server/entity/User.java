package facebook.server.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "user")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
