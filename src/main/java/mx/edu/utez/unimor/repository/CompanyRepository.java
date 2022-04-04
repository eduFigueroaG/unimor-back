package mx.edu.utez.unimor.repository;

import mx.edu.utez.unimor.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query(value = "SELECT * FROM company WHERE status =true", nativeQuery = true)
    Page<Company> findAllPaginated(Pageable pageable);

    @Query(value = "SELECT * FROM company", nativeQuery = true)
    Page<Company> findAllPaginatedNoStatus(Pageable pageable);

    List<Company> findAllByStatus(boolean status);


    boolean existsByName(String name);
}
