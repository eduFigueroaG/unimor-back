package mx.edu.utez.unimor.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String lastname;
    private String surname;

    public Person(Long id, String name, String lastname, String surname) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.surname = surname;
    }
}
