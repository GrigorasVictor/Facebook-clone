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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty("id")
    @Column(name = "content_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties("password")
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

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("content")
    private List<Vote> votes;

    @JsonIgnoreProperties("contents")
    @ManyToMany(mappedBy = "contents")
    private List<Tag> tags;

    private String urlPhoto;

    @JsonProperty("nr_comments")
    @Column(name = "nr_comments", nullable = false)
    private int nrComments;

    @JsonProperty("nr_votes")
    @Column(name = "nr_votes", nullable = false)
    private int nrVotes;

    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "content_id", nullable = true, updatable = false)
    @JsonProperty("parent_id")
    private Content parentContent;

    @Override
    public String toString() {
        return "Content{" +
                "id=" + id +
                ", user=" + user +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", status='" + status + '\'' +
                ", typeContent=" + typeContent +
                ", votes=" + votes +
                ", tags=" + tags +
                ", urlPhoto='" + urlPhoto + '\'' +
                ", nrComments=" + nrComments +
                ", nrVotes=" + nrVotes +
                ", createdAt=" + createdAt +
                '}';
    }
}