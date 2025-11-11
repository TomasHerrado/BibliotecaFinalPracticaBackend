package ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.Cliente;

import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.entity.Persona;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${personas.service.url}")
    private String personasServiceUrl;

    public List<Persona> getPersona() {
        String url = personasServiceUrl + "/api/personas";
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
        String url = personasServiceUrl + "/api/personas/aleatorio";
        try {
            return restTemplate.getForObject(url, Persona.class);
        } catch (RestClientException ex) {
            throw new IllegalStateException("Error al comunicarse con el servicio de personas", ex);
        }
    }
}
