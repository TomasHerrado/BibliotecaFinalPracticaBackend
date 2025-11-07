package ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.services;

import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.entity.Libro;
import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.enums.EstadoLibro;

import java.util.List;

public interface LibroService {
    public Libro registrarLibro(Libro libro);
    public List<Libro> obtenerTodosLosLibros();
    public void eliminarLibro(Long id);
    public Libro actualizarLibro(Libro libro);
    public List<Libro> obtenerLibrosFiltrados(String titulo, String autor, EstadoLibro estado, String isbn);
}
