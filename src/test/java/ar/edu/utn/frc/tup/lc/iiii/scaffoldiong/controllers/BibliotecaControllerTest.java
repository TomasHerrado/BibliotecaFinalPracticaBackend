package ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.controllers;

import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.entity.Libro;
import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.entity.Registro;
import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.enums.EstadoLibro;
import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.services.BibliotecaService;
import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.services.LibroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BibliotecaControllerTest {
    @Mock
    private BibliotecaService bibliotecaService;

    @Mock
    private LibroService libroService;

    @InjectMocks
    private BibliotecaController bibliotecaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registrarLibro() {
        Libro libro = new Libro(1L, "123", "Libro 1", "Autor 1", EstadoLibro.DISPONIBLE);

        when(libroService.registrarLibro(any(Libro.class))).thenReturn(libro);

        Libro result = bibliotecaController.registrarLibro(libro);

        assertEquals("Libro 1", result.getTitulo());
        verify(libroService, times(1)).registrarLibro(any(Libro.class));
    }

    @Test
    void actualizarLibro() {
        Libro libro = new Libro(1L, "123", "Libro 1", "Autor 1", EstadoLibro.DISPONIBLE);

        when(libroService.actualizarLibro(any(Libro.class))).thenReturn(libro);

        Libro result = bibliotecaController.actualizarLibro(libro);

        assertEquals("Libro 1", result.getTitulo());
        verify(libroService, times(1)).actualizarLibro(any(Libro.class));
    }

    @Test
    void eliminarLibro() {
        Long id = 1L;

        doNothing().when(libroService).eliminarLibro(id);

        bibliotecaController.eliminarLibro(id);

        verify(libroService, times(1)).eliminarLibro(id);
    }

    @Test
    void verTodosLosLibros() {
        Libro libro1 = new Libro(1L, "123", "Libro 1", "Autor 1", EstadoLibro.DISPONIBLE);
        Libro libro2 = new Libro(2L, "456", "Libro 2", "Autor 2", EstadoLibro.DISPONIBLE);

        when(libroService.obtenerTodosLosLibros()).thenReturn(Arrays.asList(libro1, libro2));

        List<Libro> result = bibliotecaController.verTodosLosLbros();

        assertEquals(2, result.size());
        verify(libroService, times(1)).obtenerTodosLosLibros();
    }

    @Test
    void verLibrosFiltrados() {
        Libro libro = new Libro(1L, "123", "Libro 1", "Autor 1", EstadoLibro.DISPONIBLE);

        when(libroService.obtenerLibrosFiltrados(anyString(), anyString(), any(), anyString()))
                .thenReturn(Collections.singletonList(libro));

        List<Libro> result = bibliotecaController.verLibrosFiltrados("Libro", "Autor", EstadoLibro.DISPONIBLE, "123");

        assertEquals(1, result.size());
        verify(libroService, times(1))
                .obtenerLibrosFiltrados("Libro", "Autor", EstadoLibro.DISPONIBLE, "123");
    }

    @Test
    void alquilarLibros() {
        Registro registro = new Registro();
        registro.setId(1L);
        registro.setTotal(new BigDecimal("100"));

        when(bibliotecaService.alquilarLibros(anyList())).thenReturn(registro);

        Registro result = bibliotecaController.alquilarLibros(Collections.singletonList("123"));

        assertNotNull(result);
        assertEquals(new BigDecimal("100"), result.getTotal());
        verify(bibliotecaService, times(1)).alquilarLibros(anyList());
    }

    @Test
    void devolverLibros() {
        Registro registro = new Registro();
        registro.setId(1L);
        registro.setTotal(new BigDecimal("150"));

        when(bibliotecaService.devolverLibros(anyLong())).thenReturn(registro);

        Registro result = bibliotecaController.devolverLibros(1L);

        assertNotNull(result);
        assertEquals(new BigDecimal("150"), result.getTotal());
        verify(bibliotecaService, times(1)).devolverLibros(1L);
    }

    @Test
    void verTodosLosAlquileres() {
        Registro registro = new Registro();

        when(bibliotecaService.verTodosLosAlquileres()).thenReturn(Collections.singletonList(registro));

        List<Registro> result = bibliotecaController.verTodosLosAlquileres();

        assertEquals(1, result.size());
        verify(bibliotecaService, times(1)).verTodosLosAlquileres();
    }

    @Test
    void informeSemanal() {
        LocalDate fecha = LocalDate.now();
        Registro registro = new Registro();

        when(bibliotecaService.informeSemanal(any())).thenReturn(Collections.singletonList(registro));

        List<Registro> result = bibliotecaController.informeSemanal(fecha);

        assertEquals(1, result.size());
        verify(bibliotecaService, times(1)).informeSemanal(fecha);
    }

    @Test
    void informeLibrosMasAlquilados() {
        Object[] libro = new Object[]{"123", 5L};

        when(bibliotecaService.informeLibrosMasAlquilados()).thenReturn(Collections.singletonList(libro));

        List<Object[]> result = bibliotecaController.informeLibrosMasAlquilados();

        assertEquals(1, result.size());
        verify(bibliotecaService, times(1)).informeLibrosMasAlquilados();
    }
}