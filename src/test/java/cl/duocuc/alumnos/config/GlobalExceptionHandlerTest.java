package cl.duocuc.alumnos.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handle_retorna500ConMensaje() {
        Exception ex = new Exception("Error interno de prueba");

        ResponseEntity<String> response = handler.handle(ex);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Error interno de prueba", response.getBody());
    }

    @Test
    void handle_conMensajeNulo() {
        Exception ex = new Exception((String) null);

        ResponseEntity<String> response = handler.handle(ex);

        assertEquals(500, response.getStatusCode().value());
        assertNull(response.getBody());
    }
}
