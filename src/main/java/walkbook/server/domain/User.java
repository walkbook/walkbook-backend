package walkbook.server.domain;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import walkbook.server.enums.Gender;

@Entity
@Table (name = "user")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @JsonIgnore
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "username", length = 20, unique = true)
    private String username;

    @JsonIgnore
    @Column(name = "password", length = 20)
    private String password;

    @Column(name = "nickname", length = 20)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 6)
    private Gender gender;

    @Column(name = "age")
    private int age;

    @Column(name = "location")
    private String location;

    @Column(name = "introduction")
    private String introduction;
}
