package cl.duocuc.alumnos.application;

import cl.duocuc.alumnos.domain.Alumno;
import cl.duocuc.alumnos.infrastructure.mapper.AlumnoMapper;
import cl.duocuc.alumnos.infrastructure.repository.AlumnoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlumnoService {

    private final AlumnoRepository repo;

    public AlumnoService(AlumnoRepository repo) {
        this.repo = repo;
    }

    public List<Alumno> listar() {
        return repo.findAll().stream().map(AlumnoMapper::toDomain).collect(Collectors.toList());
    }

    public Alumno crear(Alumno a) {
        return AlumnoMapper.toDomain(repo.save(AlumnoMapper.toEntity(a)));
    }

    public Alumno actualizar(Long id, Alumno a) {
        a.setId(id);
        return crear(a);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}
