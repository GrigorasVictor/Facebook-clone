package facebook.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "content")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false)
    @JsonProperty("title")
    private String title;

    @Column(name = "text", columnDefinition = "TEXT")
    @JsonProperty("text")
    private String text;

    @Column(name = "status", nullable = false)
    @JsonProperty("status")
    private String status;

    @Column(name = "type", nullable = false)
    @JsonProperty("type")
    private boolean typeContent;

    @Column(name = "nr_votes")
    @JsonProperty("nr_votes")
    private Integer nrVotes;

    @Column(name = "nr_comments")
    @JsonProperty("nr_comments")
    private Integer nrComments;

    @Column(name = "url_photo")
    @JsonProperty("url_photo")
    private String urlPhoto;

    @ManyToMany(mappedBy = "contents")
    @JsonIgnoreProperties("contents")
    private List<Tag> tags;

    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
