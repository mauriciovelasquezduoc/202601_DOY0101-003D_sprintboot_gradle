package cl.duocuc.alumnos;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTest {

    @Test
    void contextLoads() {
        // Verifica que el contexto de Spring Boot levanta correctamente
    }

    @Test
    void main_ejecutaSinExcepcion() {
        // Cubre la línea main() para JaCoCo
        DemoApplication.main(new String[]{});
    }
}
