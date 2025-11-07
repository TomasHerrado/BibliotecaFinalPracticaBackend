package ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.repositories;

import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.entity.Libro;
import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.enums.EstadoLibro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long>, JpaSpecificationExecutor<Libro> {
    Libro findByIsbn(String isbn);
    List<Libro> findByEstado(EstadoLibro estado);
}
