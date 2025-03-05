package facebook.server.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "post_tag_handler")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostTagHandler {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_tag_handler_id")
    private Long id;

    //?
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    //?
    @ManyToOne
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;
}
