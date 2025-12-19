package com.tfm.edcuaweb.model;

public enum NotificationType {
    MANUAL,            // Enviada manualmente por el docente
    DOCUMENT_UPLOAD,   // Nuevo documento subido
    DOCUMENT_DELETE,  // Documento eliminado
    NEW_TASK,          // Nueva tarea o cuestionario
    COURSE_UPDATE,       // Mensaje directo
    COURSE_NEWS,
    COURSE_DELETE,
    GRADE_UPDATE,
    SYSTEM_ALERT       // Notificación del sistema o admin
}
