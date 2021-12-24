package ua.training.project.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.training.project.entities.Mistake;

@Repository
public interface MistakeRepository extends JpaRepository<Mistake, Long> {

    Page<Mistake> findAllByNameIgnoreCaseContaining(String name, Pageable pageable);

}
