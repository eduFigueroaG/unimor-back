package mx.edu.utez.unimor.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Company implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String phone;
    private String altitude;
    private String latitude;
    private boolean status = true;
    @OneToOne
    private Category category;
    @OneToMany(mappedBy = "company")
    private List<Photo> photos;
    @OneToMany(mappedBy = "company")
    private List<Comment> comments;
}
