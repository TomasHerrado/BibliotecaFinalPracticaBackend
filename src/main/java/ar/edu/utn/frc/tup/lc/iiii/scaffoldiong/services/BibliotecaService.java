package ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.services;

import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.entity.Libro;
import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.entity.Registro;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface BibliotecaService {
    public Registro alquilarLibros(List<String> isbns);
    public Registro devolverLibros(Long registroId);
    public List<Registro>verTodosLosAlquileres();
    public List<Registro> informeSemanal(LocalDate fechaInicio);
    public List<Object[]> informeLibrosMasAlquilados();
    public List<Registro> verAlquileresFiltrados(String nombreCliente, LocalDate fechaInicio, LocalDate fechaFin, String isbn);
    public List<Registro> informeSemanalFiltrado(LocalDate fechaInicio, String nombreCliente, String isbn);
    public List<Object[]> informeLibrosMasAlquiladosFiltrado(LocalDate desde, LocalDate hasta, String autor, int top);
}
