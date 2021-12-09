package ua.training.project.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.training.project.entities.Course;
import ua.training.project.repositories.CourseRepository;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CoursesRestController {

    private final CourseRepository courseRepository;

    @Autowired
    public CoursesRestController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getAllCourses(){
        return new ResponseEntity<>(courseRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable("id") Long id){
        return new ResponseEntity<>(courseRepository.findById(id).orElseThrow(RuntimeException::new),
                HttpStatus.OK);
    }

    @PostMapping("/courses")
    public ResponseEntity<Course> saveCourse(@RequestBody Course course){
        return new ResponseEntity<>(courseRepository.save(course), HttpStatus.OK);
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<Course> updateCourseById(@PathVariable("id") Long id, @RequestBody Course course){
        course.setId(id);
        return new ResponseEntity<>(courseRepository.save(course), HttpStatus.OK);
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<String> deleteCourseById(@PathVariable("id") Long id){
        courseRepository.delete(courseRepository.getById(id));
        return new ResponseEntity<>("User with id = " + id + " has been successfully deleted", HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleException(Exception ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
