package ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.entity;


import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.enums.EstadoLibro;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "libros")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String isbn;
    private String titulo;
    private String autor;

    @Enumerated(EnumType.STRING)
    private EstadoLibro estado;
}
