package cl.duocuc.alumnos.contract;

import cl.duocuc.alumnos.application.AlumnoService;
import cl.duocuc.alumnos.domain.Alumno;
import cl.duocuc.alumnos.infrastructure.controller.AlumnoController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Clase base para los tests de contrato generados por Spring Cloud Contract.
 * Configura el contexto MockMvc con los mocks necesarios para que los contratos
 * puedan verificarse sin levantar el servidor completo.
 */
@WebMvcTest(AlumnoController.class)
@WithMockUser
public abstract class AlumnoContractBase {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlumnoService service;

    @BeforeEach
    void setUp() {
        // Stub: GET /alumnos → lista vacía
        when(service.listar()).thenReturn(List.of());

        // Stub: POST /alumnos → alumno creado con id=1
        when(service.crear(any(Alumno.class)))
                .thenReturn(new Alumno(1L, "Juan", "Perez"));

        RestAssuredMockMvc.mockMvc(mockMvc);
    }
}
