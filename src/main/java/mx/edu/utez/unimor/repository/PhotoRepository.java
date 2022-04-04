package mx.edu.utez.unimor.repository;

import mx.edu.utez.unimor.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    List<Photo> findAllByCompany_Id(Long id);
}
