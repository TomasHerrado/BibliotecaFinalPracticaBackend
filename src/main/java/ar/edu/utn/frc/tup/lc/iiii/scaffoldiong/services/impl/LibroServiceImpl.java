package ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.services.impl;

import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.entity.Libro;
import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.enums.EstadoLibro;
import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.repositories.LibroRepository;
import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.services.LibroService;
import jakarta.persistence.criteria.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import jakarta.persistence.criteria.Predicate;

import java.util.List;

@Service
public class LibroServiceImpl implements LibroService {
    @Autowired
    private final LibroRepository libroRepository;

    private static final Logger logger = LoggerFactory.getLogger(LibroService.class);
    public LibroServiceImpl(LibroRepository libroRepository) {

        this.libroRepository = libroRepository;
    }

    @Override
    public Libro registrarLibro(Libro libro) {
        //TODO
        /**
         * Completar el metodo de registro
         * el estado inicial del libro debe ser DISPONIBLE
         */
        Libro entity=new Libro();
        entity.setId(libro.getId());
        entity.setIsbn(libro.getIsbn());
        entity.setAutor(libro.getAutor());
        entity.setTitulo(libro.getTitulo());
        entity.setEstado(EstadoLibro.DISPONIBLE);
        return libroRepository.save(entity);
    }

    @Override
    public List<Libro> obtenerTodosLosLibros() {
        //TODO
        /**
         * Completar el metodo
         */
        logger.info("buscar libro");
        return libroRepository.findAll();
    }

    @Override
    public void eliminarLibro(Long id) {
        //TODO
        /**
         * Completar el metodo
         */
        libroRepository.deleteById(id);
    }

    @Override
    public Libro actualizarLibro(Libro libro) {
        //TODO
        /**
         * Completar el metodo
         */
        return libroRepository.findById(libro.getId())
                .map(existing -> {
                    Libro libroConIsbn = libroRepository.findByIsbn(libro.getIsbn());
                    if (libroConIsbn != null && !libroConIsbn.getId().equals(libro.getId())) {
                        throw new IllegalArgumentException("Ya existe otro libro con ese ISBN");
                    }
                    existing.setTitulo(libro.getTitulo());
                    existing.setAutor(libro.getAutor());
                    existing.setIsbn(libro.getIsbn());
                    return libroRepository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("El libro no existe"));
    }

    @Override
    public List<Libro> obtenerLibrosFiltrados(String titulo, String autor, EstadoLibro estado, String isbn) {
        return libroRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (titulo != null && !titulo.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("titulo")), "%" + titulo.toLowerCase() + "%"));
            }

            if (autor != null && !autor.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("autor")), "%" + autor.toLowerCase() + "%"));
            }

            if (estado != null) {
                predicates.add(cb.equal(root.get("estado"), estado));
            }

            if (isbn != null && !isbn.trim().isEmpty()) {
                Expression<String> isbnDbClean = cb.function("REPLACE", String.class, root.get("isbn"), cb.literal("-"), cb.literal(""));
                isbnDbClean = cb.function("REPLACE", String.class, isbnDbClean, cb.literal(" "), cb.literal(""));

                String isbnInputClean = isbn.replaceAll("[-\\s]", "").toLowerCase();

                predicates.add(cb.like(cb.lower(isbnDbClean), "%" + isbnInputClean + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }

}
