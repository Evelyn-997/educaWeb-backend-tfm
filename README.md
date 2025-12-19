# EducaWeb – Backend (TFM) 

## Descripción ## 
Frontend de la plataforma web educativa EducaWeb, desarrollado en Angular
como parte del Trabajo Fin de Máster.

---

## Arquitectura general del proyecto

| Componente          | Tecnología utilizada                         |
| ------------------- | -------------------------------------------- |
| Backend             | Java + Spring Boot + Spring Security         |
| Frontend            | Angular + Bootstrap                          |
| Base de datos       | MySQL                                        |
| Autenticación       | JWT (JSON Web Tokens)                        |
| Gestión de archivos | Almacenamiento local                         |
| Notificaciones      | WebSockets                                   |
| Testing             | JUnit + Postman (API)                        |
| Despliegue          | GitHub                                       |

> Este repositorio corresponde únicamente al **Backend** del sistema.

---

## Tecnologías utilizadas (Backend)
- Java 17
- Spring Boot
- Spring Security
- JWT
- JPA / Hibernate
- MySQL

## Funcionalidades principales
- Autenticación y autorización por roles
- Gestión de usuarios, cursos y documentos
- Control de acceso con JWT
- Envío de notificaciones
- API REST segura

## Configuración
Las variables sensibles no se incluyen en el repositorio.

## Ejecución
```bash
mvn clean install
mvn spring-boot:run
```

### Autora
Evelyn Ynachaliquin Gómez
