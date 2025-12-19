package com.tfm.edcuaweb.service;

import com.tfm.edcuaweb.event.NotificationEvent;
import com.tfm.edcuaweb.event.NotificationEventPublisher;
import com.tfm.edcuaweb.model.Course;
import com.tfm.edcuaweb.model.Document;
import com.tfm.edcuaweb.model.NotificationType;
import com.tfm.edcuaweb.model.User;
import com.tfm.edcuaweb.repository.CourseRepository;
import com.tfm.edcuaweb.repository.DocumentRepository;
import com.tfm.edcuaweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepo;
    private final CourseRepository courseRepo;
    private final UserRepository userRepo;
    private final NotificationService notifyService;
    private final NotificationEventPublisher eventPublisher;

    private final Path baseUploadPath = Paths.get("./uploads");

    /* Subida de ficheros  */
    public Document uploadDoc(Long courseId, MultipartFile file,String username) throws IOException {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        User teacher = userRepo.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("El usuario no existe"));
        //Guardamos los ficheros en una subcarpeta de cada curso
        String courseFolderName = course.getName().replaceAll("[^a-zA-Z0-9-_]", "_");
        Path coursePath = baseUploadPath.resolve(courseFolderName);

        if (!Files.exists(coursePath)) {
            Files.createDirectories(coursePath);
        }

        //Guardamos el archivo
        String fileName = file.getOriginalFilename();
        int versionNumber = documentRepo.countByNameAndCourse_Id(fileName, courseId)+1;
        String version = "v_"+versionNumber;
        String type = file.getContentType(); //Extraer la extension

        Path destination = coursePath.resolve(version+"."+fileName);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        // Guardamos el documento en la Base de Datos
        Document document =  Document.builder()
                .name(fileName)
                .filePath(destination.toString())
                .version(version)
                .type(type)
                .uploadDate(LocalDate.now())
                .course(course)
                .teacher(teacher)
                .build();

        notifyService.sendToCourse(
                course,
                teacher,
                "Nuevo documento en " + course.getName(),
                "Se ha subido el archivo: " + fileName,
                NotificationType.DOCUMENT_UPLOAD
        );
        return documentRepo.save(document);
    }

    /* Listar los documentos  */
    public List<Document> getDocumentsByCourse(Long courseId) {
        return documentRepo.findByCourseId(courseId);
    }

    /* Borrar doucmentos en funcion del ID*/
    public void deleteDoc(Long id){
        Document document = documentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("El documento no existe"));
        // Borrar fichero físico
        if (document.getFilePath() != null) {
            try {
                Path filePath = Paths.get(document.getFilePath());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                throw new RuntimeException("Error al eliminar el archivo físico", e);
            }
        }
        //Borrar registro de BD
        documentRepo.deleteById(id);
    }

    /* Actualizar Documentos */
    public Document updateDoc(Long id, String newName) {

        Document doc = documentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("El documento no existe"));
        String originName = doc.getName();
        doc.setName(newName);

        //Notificamos a los estudiantes del curso
        Course course = doc.getCourse();
        User teacher = doc.getTeacher();
        for(User student: course.getStudents()){
            eventPublisher.publish(new NotificationEvent(
                    teacher,
                    student,
                    "Documento Modificado",
                    teacher.getFullName()+" ha modificado el documento "+ originName +" - Nuevo nombre: "+newName,
                    NotificationType.DOCUMENT_UPLOAD
            ));
        }
        return documentRepo.save(doc);
    }
    /* Obtener los documentoas en funcion del ID del curso */
    public Document getDocumentsById(Long id) {
        return documentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("El documento no existe"));
    }

    /*Filtrar documentos */
    public List<Document> filterDocuments(Long courseId, String type, String version, LocalDateTime from, LocalDateTime to) {
        if(type != null && !type.isEmpty()){
            return documentRepo.findByCourse_IdAndTypeContainingIgnoreCase(courseId, type);
        } else if (version != null && !version.isEmpty()) {
            return documentRepo.findByCourse_IdAndVersionContainingIgnoreCase(courseId, version);
        }else if(from != null && to != null) {
            return documentRepo.findByCourseAndDateRange(courseId, from, to);
        }else {
            return documentRepo.findByCourseId(courseId);
        }
    }

}
