package ua.training.project.repositories;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ua.training.project.dto.StudentRatingDTO;
import ua.training.project.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {

    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u WHERE CONCAT(u.name, ' ', u.surname) LIKE %?1% AND u.isStudent=true " +
            "OR CONCAT(u.surname, ' ', u.name) LIKE %?1% AND u.isStudent=true")
    List<User> findByNameOrSurname(String searchString);

}
