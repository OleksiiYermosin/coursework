package ua.training.project.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ua.training.project.entities.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {

    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u WHERE CONCAT(u.name, ' ', u.surname) LIKE %?1% AND u.isStudent=true " +
            "OR CONCAT(u.surname, ' ', u.name) LIKE %?1% AND u.isStudent=true")
    Page<User> findByNameOrSurname(String searchString, Pageable pageable);

}
