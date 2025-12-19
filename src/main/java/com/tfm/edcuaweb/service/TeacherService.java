package com.tfm.edcuaweb.service;

import java.util.*;
import java.util.stream.Collectors;

import com.tfm.edcuaweb.dto.*;
import com.tfm.edcuaweb.model.NotificationType;
import com.tfm.edcuaweb.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tfm.edcuaweb.model.Course;
import com.tfm.edcuaweb.model.Role;
import com.tfm.edcuaweb.model.User;
import com.tfm.edcuaweb.repository.CourseRepository;
import com.tfm.edcuaweb.repository.UserRepository;


import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Service @Builder 
@RequiredArgsConstructor 
public class TeacherService {
	
	private final CourseRepository courseRepo;
	private final UserRepository userRepo;

	/* ============== CRUD ================= */
	 @Transactional
	 public CourseResponse createCourse(CourseCreateRequest req, String username) {
		 if (courseRepo.existsByCode(req.getCode())) {
		      throw new IllegalArgumentException("El código de curso ya existe.");
		    }
         // Obtenemos al profesor autenticado por su username (del JWT)
         User teacher = userRepo.findByUsername(username)
                 .orElseThrow(() -> new IllegalArgumentException("Profesor no encontrado: " + username));
         String teacherName = teacher.getName()+" "+teacher.getLastName();

         Course c = Course.builder()
		        .name(req.getName())
		        .description(req.getDescription())
		        .code(req.getCode())
		        .teacher(teacher)
                 .teacherName(teacherName)
		        .build();
         //Guardo y devuelvo la respuesta
		    courseRepo.save(c);
		    return map(c, false);
	 }

	 @Transactional(readOnly = true)
	 public List<CourseResponse> getAllCoursesByTeacher(String username) {
         User teacher = userRepo.findByUsername(username)
                 .orElseThrow(() -> new IllegalArgumentException("Profesor no encontrado: " + username));
         List<Course> courses = courseRepo.findByTeacherId(teacher.getId());
         return courses.stream().map(c -> map(c, false)).collect(Collectors.toList());

	 }

	 @Transactional(readOnly = true)
	 public CourseResponse getCourseDetails(Long id, User teacher, boolean includeStudents) {
         includeStudents = true;
	    Course c = courseRepo.findByIdAndTeacher(id, teacher)
	        .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado o no te pertenece."));
        // Forzar carga de los estudiantes si se pide
         if (includeStudents && c.getStudents() != null) {
             c.getStudents().size(); // fuerza la carga (evita proxy sin inicializar)
         }
         return map(c, includeStudents);
	 }

    @Transactional(readOnly = true)
    public Course getMyCourseById(Long courseId, Long teacherId) {
        return courseRepo.findByIdAndTeacherId(courseId, teacherId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Curso no encontrado o no pertenece al profesor")
                );
    }
    @Transactional(readOnly = true)
    public Course getCourseById(Long courseId, User teacher) {
        return courseRepo.findByIdAndTeacher(courseId, teacher)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Curso no encontrado o no pertenece al profesor")
                );
    }

	 @Transactional
	 public CourseResponse updateMyCourse(Long id, CourseUpdateRequest req, User teacher) {
	    Course c = courseRepo.findByIdAndTeacher(id, teacher)
	        .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado o no te pertenece."));

	    if (req.getName() != null) c.setName(req.getName());
	    if (req.getDescription() != null) c.setDescription(req.getDescription());
	    return map(c, true);
	 }

	 @Transactional
	 public void deleteMyCourse(Long id,User teacher) {
		 Course c = courseRepo.findByIdAndTeacher(id, teacher)
	        .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado o no te pertenece."));
	    courseRepo.delete(c);
	 }

	  /* ====== ALUMNOS ====== */
	 @Transactional
	  public CourseResponse addStudents(Long courseId, AddStudentRequest req) {
	    //Course c = courseRepo.findByIdAndTeacher(courseId, teacher)
         //Buscamos el curso
         Course c = courseRepo.findById(courseId)
	        .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado o no te pertenece."));
         //preparo la lista para añadir los usuarios
	    List<User> toAdd = new ArrayList<>();

	    if (req.getStudentId() != null && !req.getStudentId().isEmpty()) {
	      toAdd.addAll(userRepo.findAllById(req.getStudentId()));
	    } else if (req.getUsernames() != null && !req.getUsernames().isEmpty()) {
	      for (String username : req.getUsernames()) {
	        userRepo.findByUsername(username).ifPresent(toAdd::add);
	      }
	    }
	    //filtra solo ROLE_STUDENT
	    toAdd = toAdd.stream()
	        .filter(u -> u.getRole() == Role.STUDENT)
	        .collect(Collectors.toList());

	   // añadir los estudiantes al curso
         if(c.getStudents() == null){
             c.setStudents(new HashSet<>());
         }
         c.getStudents().addAll(toAdd);
         courseRepo.save(c);

         return map(c, true);
	  }

	  @Transactional
	  public CourseResponse removeStudent(Long courseId, Long studentId, User teacher) {
	    Course c = courseRepo.findByIdAndTeacher(courseId, teacher)
	        .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado o no te pertenece."));

          User student = userRepo.findById(studentId)
                  .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado."));

            if (c.getStudents() != null){
                c.getStudents().removeIf(u -> Objects.equals(u.getId(), studentId));
                //c.getStudents().removeIf(u ->u.getRole() == Role.STUDENT);
            }

            courseRepo.save(c);
	    return map(c, true);
	  }

      @Transactional
      public List<StudentSummary> getStudentsByCourse(Long courseId){
          Course course = courseRepo.findById(courseId)
                  .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado."));
          // Obtenemos los estudiantes
          if (course.getStudents() == null || course.getStudents().isEmpty()) {
              return List.of();
          }
          return course.getStudents().stream()
                  .map(s -> new StudentSummary()).toList();
      }

    public CourseStudentResponse getCourseForStudent(Long courseId, User student){
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado."));

        List<StudentSummary> students = course.getStudents().stream()
                .map(s -> new StudentSummary(
                        s.getId(),
                        s.getUsername(),
                        s.getFullName(),
                        s.getEmail(),
                        s.getEnrollmentNumber(),
                        s.getRole().name()
                ))
                .toList();

        return new CourseStudentResponse(
                course.getId(),
                course.getName(),
                course.getDescription(),
                course.getCode(),
                course.getTeacher().getId(),
                course.getTeacher().getFullName(),
                students // <-- AQUI DEVOLVEMOS LA LISTA
        );
    }


      //================== Metodo para mapear objetos bien estructurados ================
      // sin problemas de carga de JPA ni bucles JSON
      private CourseResponse map(Course course, boolean includeStudents) {
          // Mapea los campos básicos del curso
          CourseResponse response = new CourseResponse();
          response.setId(course.getId()); // usa el nombre real del getter de tu entidad
          response.setName(course.getName());
          response.setDescription(course.getDescription());
          response.setCode(course.getCode());
          response.setCreatedAt(course.getCreatedAt());

          // Información del profesor
          if (course.getTeacher() != null) {
              response.setTeacherId(course.getTeacher().getId()); // ajusta según tu entidad User
              response.setTeacherName(course.getTeacher().getFullName());
              response.setTeacherUsername(course.getTeacher().getUsername());
          }

          // Estudiantes asociados (opcional según 'includeStudents')
          if (includeStudents) {
              if (course.getStudents() != null && !course.getStudents().isEmpty()) {
                  response.setStudents(
                          course.getStudents().stream()
                                  .map(s -> new StudentSummary(
                                          s.getId(),            // ajusta según tu entidad User
                                          s.getUsername(),
                                          s.getFullName(),
                                          s.getEmail(),
                                          s.getEnrollmentNumber(),
                                          s.getRole().name()
                                  ))
                                  .collect(Collectors.toList())
                  );
                  response.setStudentsCount(course.getStudents().size());
              } else {
                  response.setStudents(List.of());
                  response.setStudentsCount(0);
              }
          } else {
              response.setStudents(null);
              response.setStudentsCount(course.getStudents() != null ? course.getStudents().size() : 0);
          }

          return response;
      }


}
	 

