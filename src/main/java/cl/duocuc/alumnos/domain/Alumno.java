package cl.duocuc.alumnos.domain;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Alumno {
    private Long id;
    private String nombre;
    private String apellido;
}
