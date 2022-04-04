package mx.edu.utez.unimor.repository;

import mx.edu.utez.unimor.entity.Comment;
import mx.edu.utez.unimor.entity.Company;
import mx.edu.utez.unimor.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Comment findByCompanyAndUser(Company company, User user);
}
