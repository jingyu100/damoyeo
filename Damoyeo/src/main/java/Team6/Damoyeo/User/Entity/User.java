package Team6.Damoyeo.User.Entity;

import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.Post.Entity.PostRequest;
import Team6.Damoyeo.calendar.Entity.CalendarEvent;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer userId;

    @Column
    private String password;

    @Column
    private String name;

    @Column
    private String nickname;

    @Column
    private String phone;

    @Column(unique = true)
    private String email;

    @Column
    private String interests;

    @Column
    private String gender;

    @Column
    private LocalDateTime joinDate;

    @Column
    private LocalDateTime quitDate;

    @Column
    private String area;

    @Column
    private String comment;

    @Column
    private String photoUrl;

    @Column
    // 초기값을 1로 설정 하고 탈퇴하면 0으로 할예정임
    @ColumnDefault("1")
    private String status;

    @OneToMany(mappedBy = "user")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @JsonManagedReference // 순환 참조 방지
    private List<CalendarEvent> events;

    @OneToMany(mappedBy = "user")
    private List<PostRequest> requests = new ArrayList<>();

}