package mx.edu.utez.unimor.repository;

import mx.edu.utez.unimor.entity.Company;
import mx.edu.utez.unimor.entity.Fav;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavRepository extends JpaRepository<Fav, Long> {

    boolean existsByUser_IdAndFav_Id(Long user, Long fav);
    boolean deleteByUser_IdAndFav_Id(Long user, Long fav);

    @Query(value = "SELECT * FROM fav WHERE user_id = 2", nativeQuery = true)
    Page<Fav> findAllByUserFav(@Param("id")Long id, Pageable pageable);

}
