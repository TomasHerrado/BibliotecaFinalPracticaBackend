package ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.repositories;

import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.entity.Registro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RegistroRepository extends JpaRepository<Registro, Long>, JpaSpecificationExecutor<Registro> {
    @Query("SELECT r FROM Registro r WHERE r.fechaReserva BETWEEN :inicio AND :fin")
    List<Registro> obtenerRegistrosSemana(LocalDate inicio, LocalDate fin);

    @Query("SELECT l.isbn, COUNT(l) as total FROM Registro r JOIN r.librosReservados l GROUP BY l.isbn ORDER BY total DESC")
    List<Object[]> obtenerLibrosMasAlquilados();
}
