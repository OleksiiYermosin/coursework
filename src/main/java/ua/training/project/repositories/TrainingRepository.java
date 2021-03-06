package ua.training.project.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.training.project.entities.Training;

import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {

    List<Training> findByCourseIdAndUserId(Long courseId, Long userId);

    Page<Training> findByCourseNameContainingAndEventNameContainingAndCourseCategoryNameContainingAndUserIdEquals(String courseName,
                                                                                                            String eventName,
                                                                                                            String courseCategoryName,
                                                                                                            Long userId,
                                                                                                            Pageable pageable);

}
