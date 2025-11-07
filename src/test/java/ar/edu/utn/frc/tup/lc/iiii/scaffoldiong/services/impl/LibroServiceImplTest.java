package ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.services.impl;

import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.entity.Libro;
import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.enums.EstadoLibro;
import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.repositories.LibroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LibroServiceImplTest {
    @Mock
    private LibroRepository libroRepository;

    @InjectMocks
    private LibroServiceImpl libroService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registrarLibro() {
        // Libro de entrada (sin estado)
        Libro libroInput = new Libro();
        libroInput.setIsbn("123");
        libroInput.setTitulo("Titulo");
        libroInput.setAutor("Autor");

        // Configura el mock para devolver el libro con el estado establecido
        when(libroRepository.save(any(Libro.class))).thenAnswer(invocation -> {
            Libro libroGuardado = invocation.getArgument(0);
            // Asegura que el estado se estableció correctamente
            assertEquals(EstadoLibro.DISPONIBLE, libroGuardado.getEstado());
            return libroGuardado; // Devuelve el mismo libro que recibe
        });

        Libro result = libroService.registrarLibro(libroInput);

        assertNotNull(result);
        assertEquals("123", result.getIsbn());
        assertEquals("Titulo", result.getTitulo());
        assertEquals("Autor", result.getAutor());
        assertEquals(EstadoLibro.DISPONIBLE, result.getEstado());
        verify(libroRepository, times(1)).save(any(Libro.class));
    }

    @Test
    void obtenerTodosLosLibros() {
        Libro libro1 = new Libro(1L, "123", "Libro 1", "Autor 1", EstadoLibro.DISPONIBLE);
        Libro libro2 = new Libro(2L, "456", "Libro 2", "Autor 2", EstadoLibro.DISPONIBLE);

        when(libroRepository.findAll()).thenReturn(Arrays.asList(libro1, libro2));

        List<Libro> result = libroService.obtenerTodosLosLibros();

        assertEquals(2, result.size());
        verify(libroRepository, times(1)).findAll();
    }

    @Test
    void eliminarLibro() {
        Long id = 1L;

        doNothing().when(libroRepository).deleteById(id);

        libroService.eliminarLibro(id);

        verify(libroRepository, times(1)).deleteById(id);
    }

    @Test
    void actualizarLibroExitoso() {
        Long id = 1L;
        Libro libroExistente = new Libro(id, "123", "Titulo Viejo", "Autor Viejo", EstadoLibro.DISPONIBLE);
        Libro libroActualizado = new Libro(id, "123", "Titulo Nuevo", "Autor Nuevo", EstadoLibro.DISPONIBLE);

        when(libroRepository.findById(id)).thenReturn(Optional.of(libroExistente));
        when(libroRepository.findByIsbn("123")).thenReturn(libroExistente);
        when(libroRepository.save(any(Libro.class))).thenReturn(libroActualizado);

        Libro result = libroService.actualizarLibro(libroActualizado);

        assertEquals("Titulo Nuevo", result.getTitulo());
        assertEquals("Autor Nuevo", result.getAutor());
        verify(libroRepository, times(1)).save(any(Libro.class));
    }

    @Test
    void actualizarLibroConIsbnExistente() {
        Long id = 1L;
        Libro libroExistente = new Libro(id, "123", "Titulo", "Autor", EstadoLibro.DISPONIBLE);
        Libro otroLibro = new Libro(2L, "123", "Otro Titulo", "Otro Autor", EstadoLibro.DISPONIBLE);
        Libro libroActualizado = new Libro(id, "123", "Titulo Nuevo", "Autor Nuevo", EstadoLibro.DISPONIBLE);

        when(libroRepository.findById(id)).thenReturn(Optional.of(libroExistente));
        when(libroRepository.findByIsbn("123")).thenReturn(otroLibro);

        assertThrows(IllegalArgumentException.class, () -> libroService.actualizarLibro(libroActualizado));
    }

    @Test
    void obtenerLibrosFiltrados() {
        Libro libro1 = new Libro(1L, "123", "Libro 1", "Autor 1", EstadoLibro.DISPONIBLE);
        Libro libro2 = new Libro(2L, "456", "Libro 2", "Autor 2", EstadoLibro.RESERVADO);

        when(libroRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class))).thenReturn(Arrays.asList(libro1));

        List<Libro> result = libroService.obtenerLibrosFiltrados("Libro", null, EstadoLibro.DISPONIBLE, null);

        assertEquals(1, result.size());
        assertEquals("Libro 1", result.get(0).getTitulo());
    }
}