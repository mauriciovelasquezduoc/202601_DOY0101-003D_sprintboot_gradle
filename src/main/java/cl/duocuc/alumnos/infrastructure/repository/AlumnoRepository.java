package cl.duocuc.alumnos.infrastructure.repository;

import cl.duocuc.alumnos.infrastructure.entity.AlumnoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlumnoRepository extends JpaRepository<AlumnoEntity, Long> {
}
