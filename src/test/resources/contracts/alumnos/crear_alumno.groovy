import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "POST /alumnos crea un alumno y retorna el objeto con id"

    request {
        method POST()
        url '/alumnos'
        headers {
            contentType applicationJson()
        }
        body([
            nombre  : "Juan",
            apellido: "Perez"
        ])
    }

    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        body([
            id      : $(producer(regex('[0-9]+')), consumer(1)),
            nombre  : "Juan",
            apellido: "Perez"
        ])
    }
}
