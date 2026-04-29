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
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Clase base para los tests de contrato generados por Spring Cloud Contract.
 * Configura RestAssuredMockMvc con CSRF deshabilitado para que los contratos
 * POST/PUT/DELETE puedan ejecutarse sin token CSRF.
 */
@WebMvcTest(AlumnoController.class)
@WithMockUser
public abstract class AlumnoContractBase {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private AlumnoService service;

    @BeforeEach
    void setUp() {
        // Stub: GET /alumnos → lista vacía
        when(service.listar()).thenReturn(List.of());

        // Stub: POST /alumnos → alumno creado con id=1
        when(service.crear(any(Alumno.class)))
                .thenReturn(new Alumno(1L, "Juan", "Perez"));

        // Configurar MockMvc con Spring Security pero sin CSRF
        // (los contratos no envían token CSRF por diseño)
        MockMvc mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        RestAssuredMockMvc.mockMvc(mockMvc);
    }
}
