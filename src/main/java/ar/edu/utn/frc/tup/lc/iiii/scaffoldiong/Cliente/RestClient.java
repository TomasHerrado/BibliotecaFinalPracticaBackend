package ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.Cliente;

import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.entity.Persona;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RestClient {
    private final RestTemplate restTemplate;

//    public List<Persona> getPersona() {
//        String url = "https://run.mocky.io/v3/91f86e81-98d4-44d9-9ca9-b0b5ac72c78d";
//        try {
//            ResponseEntity<List<Persona>> response = restTemplate.exchange(
//                    url,
//                    HttpMethod.GET,
//                    null,
//                    new ParameterizedTypeReference<List<Persona>>() {
//                    });
//            return response.getBody();
//        } catch (RestClientException ex) {
//            throw new IllegalStateException("Error al comunicarse con la API externa de personas", ex);
//        }
//    }
    public List<Persona> getPersona() {
        String url = "http://localhost:8081/api/personas";
        try {
            ResponseEntity<List<Persona>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Persona>>() {
                    });
            return response.getBody();
        } catch (RestClientException ex) {
            throw new IllegalStateException("Error al comunicarse con el servicio de personas", ex);
        }
    }

    public Persona getPersonaAleatoria() {
        String url = "http://localhost:8081/api/personas/aleatorio";
        try {
            return restTemplate.getForObject(url, Persona.class);
        } catch (RestClientException ex) {
            throw new IllegalStateException("Error al comunicarse con el servicio de personas", ex);
        }
    }
}
