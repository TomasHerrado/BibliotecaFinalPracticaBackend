package ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.controllers;

import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.entity.Libro;
import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.entity.Registro;
import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.enums.EstadoLibro;
import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.services.BibliotecaService;
import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.services.LibroService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/biblioteca")
public class BibliotecaController {

    private final BibliotecaService bibliotecaService;
    private final LibroService libroService;

    public BibliotecaController(BibliotecaService bibliotecaService, LibroService libroService) {
        this.bibliotecaService = bibliotecaService;
        this.libroService = libroService;
    }

    // Endpoints para gestión de libros
    @PostMapping("/libros")
    public Libro registrarLibro(@RequestBody Libro libro) {

        return libroService.registrarLibro(libro);
    }

    @PutMapping("/libros")
    public Libro actualizarLibro(@RequestBody Libro libro) {

        return libroService.actualizarLibro(libro);
    }

    @DeleteMapping("/libros/{id}")
    public void eliminarLibro(@PathVariable Long id) {

        libroService.eliminarLibro(id);
    }

    @GetMapping("/libros")
    public List<Libro>verTodosLosLbros(){

        return libroService.obtenerTodosLosLibros();
    }
    @GetMapping("/libros/filtrado")
    public List<Libro> verLibrosFiltrados(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String autor,
            @RequestParam(required = false) EstadoLibro estado,
            @RequestParam(required = false) String isbn
    ) {
        return libroService.obtenerLibrosFiltrados(titulo, autor, estado, isbn);
    }

    // Endpoints para alquiler y devolución
    @GetMapping("/alquilar")
    public List<Registro> verTodosLosAlquileres(){

        return bibliotecaService.verTodosLosAlquileres();
    }
    @GetMapping("/alquilar/filtrado")
    public List<Registro> verAlquileresFiltrados(
            @RequestParam(required = false) String nombreCliente,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) String isbn
    ) {
        return bibliotecaService.verAlquileresFiltrados(nombreCliente, fechaInicio, fechaFin, isbn);
    }

    @PostMapping("/alquilar")
    public Registro alquilarLibros(@RequestBody List<String> isbns) {

        return bibliotecaService.alquilarLibros(isbns);
    }

    @PostMapping("/devolver/{registroId}")
    public Registro devolverLibros(@PathVariable Long registroId) {
        return bibliotecaService.devolverLibros(registroId);
    }

    // Endpoints para informes
    @GetMapping("/informe-semanal")
    public List<Registro> informeSemanal(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio
    ) {
        return bibliotecaService.informeSemanal(fechaInicio);
    }
    @GetMapping("/informe-semanal/filtrado")
    public List<Registro> informeSemanal(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) String nombreCliente,
            @RequestParam(required = false) String isbn
    ) {
        return bibliotecaService.informeSemanalFiltrado(fechaInicio, nombreCliente, isbn);
    }

    @GetMapping("/libros-mas-alquilados")
    public List<Object[]> informeLibrosMasAlquilados() {

        return bibliotecaService.informeLibrosMasAlquilados();
    }
    @GetMapping("/libros-mas-alquilados/filtrado")
    public List<Object[]> informeLibrosMasAlquilados(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) String autor,
            @RequestParam(required = false, defaultValue = "10") int top
    ) {
        return bibliotecaService.informeLibrosMasAlquiladosFiltrado(desde, hasta, autor, top);
    }
}
