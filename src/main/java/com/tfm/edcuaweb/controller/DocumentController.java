package com.tfm.edcuaweb.controller;

import com.tfm.edcuaweb.dto.DocumentResponse;
import com.tfm.edcuaweb.model.Course;
import com.tfm.edcuaweb.model.Document;
import com.tfm.edcuaweb.model.NotificationType;
import com.tfm.edcuaweb.model.User;
import com.tfm.edcuaweb.service.DocumentService;

import com.tfm.edcuaweb.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class DocumentController {

    private final DocumentService documentService;
    private final NotificationService notificationService;

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/uploads/{courseId}")
    public ResponseEntity<Document> uploadDoc(
            @PathVariable Long courseId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("username") String username) throws IOException {
        return ResponseEntity.ok(documentService.uploadDoc(courseId,file,username));
    }
    /* Obtener los documentos de un curso*/
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER')")
    @GetMapping("/courses/{courseId}")
    public ResponseEntity<List<DocumentResponse>> getDocumentsByCourse(@PathVariable Long courseId) {
        List<DocumentResponse> docs = documentService.getDocumentsByCourse(courseId)
                .stream().map(d ->new DocumentResponse(
                        d.getId(),
                        d.getName(),
                        d.getFilePath(),
                        d.getVersion(),
                        d.getUploadDate(),
                        d.getType()
                ))
                .toList();
        return ResponseEntity.ok(docs);
    }

    @PreAuthorize("hasRole('TEACHER')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String,String>> deleteDoc(@PathVariable Long id){
        Document doc = documentService.getDocumentsById(id);
        Course course = doc.getCourse();
        User teacher = doc.getTeacher();
        String message = teacher.getFullName()+" ha eliminado el documento "+doc.getName();
        NotificationType type = NotificationType.DOCUMENT_DELETE;
        documentService.deleteDoc(id);
        notificationService.sendToCourse(course,teacher,"Docuemnto eliminado",message,type);

        //return ResponseEntity.ok("Documento eliminado correctamente.");
        return ResponseEntity.ok(Map.of("message", "Documento eliminado"));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PutMapping("{id}")
    public ResponseEntity<Document> updateDoc(@PathVariable Long id, @RequestParam("newName") String newName){
        return ResponseEntity.ok(documentService.updateDoc(id,newName));
    }

    /* Descargar/Previsualizar los documentos */
    @PreAuthorize("hasAnyRole('TEACHER',''STUDENT)")
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable Long id,
            @RequestParam(defaultValue = "inline") String mode
    ) throws IOException {// puede ser "inline" o "attachment"
        Document doc = documentService.getDocumentsById(id);
        Path filePath = Paths.get(doc.getFilePath());
        Resource resource = new UrlResource(filePath.toUri());

        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }
        // 🔹 Detectar tipo MIME (ej: application/pdf, image/png, etc.)
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        // 🔹 Cabecera para mostrar o descargar
        String disposition = mode.equals("attachment") ?
                "attachment" : "inline";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition+"; filename=\"" + doc.getName() + "\"")
                .body(resource);
    }

}
