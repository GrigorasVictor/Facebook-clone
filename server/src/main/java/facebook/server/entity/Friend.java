package facebook.server.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "friend")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id_1", insertable = false, updatable = false)
    @JsonProperty("user_1")
    private User user1;

    @ManyToOne
    @JoinColumn(name = "user_id_2", insertable = false, updatable = false)
    @JsonProperty("user_2")
    private User user2;

    @Column(name = "status1") // it should be nullable = false, but we need to refactor the entire database
    @JsonProperty("status1")
    private boolean status;

    @Column(name = "status2") // it should be nullable = false, but we need to refactor the entire database
    @JsonProperty("status2")
    private boolean status2;
}
