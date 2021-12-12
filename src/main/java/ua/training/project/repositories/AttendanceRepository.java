package ua.training.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.training.project.entities.Attendance;
import ua.training.project.utils.AttendanceKey;

public interface AttendanceRepository extends JpaRepository<Attendance, AttendanceKey> {
}
