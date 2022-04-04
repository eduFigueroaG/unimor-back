package mx.edu.utez.unimor.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
   // @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String photo;
    private boolean status=true;
    @ManyToOne
    private Person person;
    @OneToOne
    private Role role;


    public User(Long id, String password, String email, String photo, boolean status, Person person, Role role) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.photo = photo;
        this.status = status;
        this.person = person;
        this.role = role;
    }
}