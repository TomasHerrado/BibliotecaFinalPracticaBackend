package ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "registros")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Registro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long clienteId;
    private String nombreCliente;

    @Column(name = "fecha_reserva")
    private LocalDate fechaReserva;

    @Column(name = "fecha_devolucion")
    private LocalDate fechaDevolucion;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Libro> librosReservados;

    @Column(name = "total_alquiler")
    private BigDecimal total;
}
