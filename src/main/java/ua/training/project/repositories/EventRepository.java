package ua.training.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.training.project.entities.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

}
