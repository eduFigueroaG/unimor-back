package mx.edu.utez.unimor.util;

import lombok.AllArgsConstructor;
import mx.edu.utez.unimor.entity.Category;
import mx.edu.utez.unimor.entity.Person;
import mx.edu.utez.unimor.entity.Role;
import mx.edu.utez.unimor.entity.User;
import mx.edu.utez.unimor.repository.CategoryRepository;
import mx.edu.utez.unimor.repository.PersonRepository;
import mx.edu.utez.unimor.repository.RoleRepository;
import mx.edu.utez.unimor.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component
@Transactional
@AllArgsConstructor
public class InitialData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        if (!roleRepository.existsById(1L)){
            Category category = new Category();
            category.setName("Comida");
            categoryRepository.save(category);
            Role role = new Role(1L, "Admin", true);
            Role roleUser =  new Role(2L, "User", true);
            Person person = new Person(1L, "Eduardo", "Figueroa", "Garcia");
            person = personRepository.save(person);
            role = roleRepository.save(role);
            roleRepository.save(roleUser);
            User user = new User(1L, passwordEncoder.encode("root123"),"eduardofigueroa@utez.edu.mx",
                    "", true, person, role);
            userRepository.save(user);
        }
    }
}
