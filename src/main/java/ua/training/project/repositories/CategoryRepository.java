package ua.training.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.training.project.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
