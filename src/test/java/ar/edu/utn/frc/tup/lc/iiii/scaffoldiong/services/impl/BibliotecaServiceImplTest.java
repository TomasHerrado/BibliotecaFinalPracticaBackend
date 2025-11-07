package ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.services.impl;

import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.Cliente.RestClient;
import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.entity.Libro;
import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.entity.Persona;
import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.entity.Registro;
import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.enums.EstadoLibro;
import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.repositories.LibroRepository;
import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.repositories.RegistroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BibliotecaServiceImplTest {
    @Mock
    private RegistroRepository registroRepository;

    @Mock
    private LibroRepository libroRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RestClient restClient;

    @InjectMocks
    private BibliotecaServiceImpl bibliotecaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void alquilarLibrosExitoso() {
        // Configuración
        String isbn = "123";
        Libro libro = new Libro(1L, isbn, "Libro 1", "Autor 1", EstadoLibro.DISPONIBLE);
        Persona persona = new Persona(1L, "Juan Perez", "Calle 123");

        when(libroRepository.findByIsbn(isbn)).thenReturn(libro);
        when(restClient.getPersona()).thenReturn(Collections.singletonList(persona));
        when(registroRepository.save(any(Registro.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Ejecución
        Registro result = bibliotecaService.alquilarLibros(Collections.singletonList(isbn));

        // Verificación
        assertNotNull(result);
        assertEquals(EstadoLibro.RESERVADO, libro.getEstado());
        verify(libroRepository, times(1)).save(libro);
        verify(registroRepository, times(1)).save(any(Registro.class));
    }

    @Test
    void alquilarLibrosNoDisponibles() {
        String isbn = "123";
        Libro libro = new Libro(1L, isbn, "Libro 1", "Autor 1", EstadoLibro.RESERVADO);

        when(libroRepository.findByIsbn(isbn)).thenReturn(libro);

        assertThrows(IllegalArgumentException.class, () ->
                bibliotecaService.alquilarLibros(Collections.singletonList(isbn)));
    }

    @Test
    void devolverLibrosExitoso() {
        Long registroId = 1L;
        Libro libro = new Libro(1L, "123", "Libro 1", "Autor 1", EstadoLibro.RESERVADO);
        Registro registro = new Registro();
        registro.setId(registroId);
        registro.setLibrosReservados(Collections.singletonList(libro));
        registro.setFechaReserva(LocalDate.now().minusDays(3));

        when(registroRepository.findById(registroId)).thenReturn(Optional.of(registro));
        when(registroRepository.save(any(Registro.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Registro result = bibliotecaService.devolverLibros(registroId);

        assertNotNull(result.getFechaDevolucion());
        assertEquals(EstadoLibro.DISPONIBLE, libro.getEstado());
        assertEquals(new BigDecimal("150"), result.getTotal());
        verify(registroRepository, times(1)).save(registro);
    }

    @Test
    void calcularCostoAlquilerHasta2Dias() {
        LocalDate inicio = LocalDate.now();
        LocalDate fin = inicio.plusDays(2);

        BigDecimal result = bibliotecaService.calcularCostoAlquiler(inicio, fin, 2);

        assertEquals(new BigDecimal("200"), result);
    }

    @Test
    void calcularCostoAlquiler3a5Dias() {
        LocalDate inicio = LocalDate.now();
        LocalDate fin = inicio.plusDays(4);

        BigDecimal result = bibliotecaService.calcularCostoAlquiler(inicio, fin, 1);

        assertEquals(new BigDecimal("150"), result);
    }

    @Test
    void calcularCostoAlquilerMasDe5Dias() {
        LocalDate inicio = LocalDate.now();
        LocalDate fin = inicio.plusDays(7);

        BigDecimal result = bibliotecaService.calcularCostoAlquiler(inicio, fin, 1);

        assertEquals(new BigDecimal("210"), result); // 150 + (2*30)
    }

    @Test
    void verTodosLosAlquileres() {
        Registro registro1 = new Registro();
        Registro registro2 = new Registro();

        when(registroRepository.findAll()).thenReturn(Arrays.asList(registro1, registro2));

        List<Registro> result = bibliotecaService.verTodosLosAlquileres();

        assertEquals(2, result.size());
        verify(registroRepository, times(1)).findAll();
    }

    @Test
    void informeSemanal() {
        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin = fechaInicio.plusDays(6);
        Registro registro = new Registro();

        when(registroRepository.obtenerRegistrosSemana(fechaInicio, fechaFin))
                .thenReturn(Collections.singletonList(registro));

        List<Registro> result = bibliotecaService.informeSemanal(fechaInicio);

        assertEquals(1, result.size());
        verify(registroRepository, times(1)).obtenerRegistrosSemana(fechaInicio, fechaFin);
    }

    @Test
    void informeLibrosMasAlquilados() {
        Object[] libro1 = new Object[]{"123", 5L};
        Object[] libro2 = new Object[]{"456", 3L};

        when(registroRepository.obtenerLibrosMasAlquilados())
                .thenReturn(Arrays.asList(libro1, libro2));

        List<Object[]> result = bibliotecaService.informeLibrosMasAlquilados();

        assertEquals(2, result.size());
        verify(registroRepository, times(1)).obtenerLibrosMasAlquilados();
    }

    @Test
    void verAlquileresFiltrados() {
        Registro registro = new Registro();

        when(registroRepository.findAll(ArgumentMatchers.<Specification<Registro>>any())).thenReturn(Collections.singletonList(registro));

        List<Registro> result = bibliotecaService.verAlquileresFiltrados("Juan",
                LocalDate.now().minusDays(7), LocalDate.now(), "123");

        assertEquals(1, result.size());
    }
}