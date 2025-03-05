package facebook.server.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "post")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title")
    @JsonProperty("title")
    private String title;

    @Column(name = "text")
    @JsonProperty("text")
    private String text;

    @Column(name = "status")
    @JsonProperty("status")
    private String status;

    @Column(name = "nr_votes")
    @JsonProperty("nr_votes")
    private Integer nrVotes;

    @Column(name = "created_at")
    @JsonProperty("created_at")
    private String createdAt;
}
