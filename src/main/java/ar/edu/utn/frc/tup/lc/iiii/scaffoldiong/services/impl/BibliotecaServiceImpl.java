package ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.services.impl;

import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.Cliente.RestClient;
import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.entity.Libro;
import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.entity.Persona;
import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.entity.Registro;
import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.enums.EstadoLibro;
import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.repositories.LibroRepository;
import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.repositories.RegistroRepository;
import ar.edu.utn.frc.tup.lc.iiii.scaffoldiong.services.BibliotecaService;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Map;

@Service
@Transactional
public class BibliotecaServiceImpl implements BibliotecaService{

    private final RegistroRepository registroRepository;
    private final LibroRepository libroRepository;
    private final RestTemplate restTemplate;
    private final RestClient restClient;
    public BibliotecaServiceImpl(LibroRepository libroRepository, RegistroRepository registroRepository, RestTemplate restTemplate, RestClient restClient) {
        this.registroRepository = registroRepository;
        this.libroRepository = libroRepository;
        this.restTemplate = restTemplate;
        this.restClient = restClient;
    }

    @Override
    public Registro alquilarLibros(List<String> isbns) {
        //TODO
        /**
         * Completar el metodo de alquiler
         * Se debe buscar la lista de libros por su codigo de isbn,
         * validar que los libros a alquilar tengan estado DISPONIBLE sino arrojar una exception
         * ya que solo se pueden alquilar libros que esten en dicho estado
         * throw new IllegalStateException("Uno o más libros ya están reservados.")
         * Recuperar un cliente desde la api externa /api/personas/aleatorio y guardar la reserva
         */
        // Buscar libros por sus ISBNs
        List<Libro> libros=isbns.stream()
                .map(isbn -> libroRepository.findByIsbn(isbn))
                .filter(libro -> libro!=null)
                .toList();

        // Validar que todos los libros estén disponibles
        boolean todosDisponibles = libros.stream()
                .allMatch(libro -> EstadoLibro.DISPONIBLE.equals(libro.getEstado()));
        if(!todosDisponibles){
            throw new IllegalArgumentException("Uno o mas libros ya estan reservados");

        }
        // Obtener un cliente aleatorio desde la API externa
        List<Persona> personas = restClient.getPersona();
        if (personas == null || personas.isEmpty()) {
            throw new IllegalStateException("No se pudo obtener un cliente desde la API externa.");
        }
        Persona cliente = personas.get(0); // Tomamos el primero

        // Cambiar el estado de los libros a RESERVADO
        libros.forEach(libro -> {
            libro.setEstado(EstadoLibro.RESERVADO);
            libroRepository.save(libro);
        });
        // Crear y guardar el registro de alquiler
        Registro registro = new Registro();
        registro.setClienteId(cliente.getId());
        registro.setNombreCliente(cliente.getNombre());
        registro.setFechaReserva(LocalDate.now());
        registro.setLibrosReservados(libros);
        registro.setTotal(BigDecimal.ZERO);

        return registroRepository.save(registro);
    }

    @Override
    public Registro devolverLibros(Long registroId) {
        //TODO
        /**
         * Completar el metodo de devolucion
         * Se debe buscar la reserva por su id,
         * actualizar la fecha de devolucion y calcular el importe a facturar,
         * actualizar el estado de los libros a DISPONIBLE
         * y guardar el registro con los datos actualizados
         */
        // Buscar el registro por su ID
        Registro registro= registroRepository.findById(registroId)
                .orElseThrow(()-> new IllegalArgumentException("Registro no encontrado con ID: "+ registroId));
        // Establecer la fecha de devolución
        LocalDate fechaDevolucion = LocalDate.now();
        registro.setFechaDevolucion(fechaDevolucion);

        //Clacular el importe a facturar
        int cantidadLibros = registro.getLibrosReservados().size();
        BigDecimal costoAlquiler= calcularCostoAlquiler(registro.getFechaReserva(),fechaDevolucion,cantidadLibros);
        registro.setTotal(costoAlquiler);

        // Actualizar el estado de los libros a DISPONIBLE
        registro.getLibrosReservados().forEach(libro -> {
            libro.setEstado(EstadoLibro.DISPONIBLE);
            libroRepository.save(libro);
        });
        // Guardar los cambios en el registro
        return registroRepository.save(registro);
    }
    public BigDecimal calcularCostoAlquiler(LocalDate inicio, LocalDate fin, int cantidadLibros) {
        //TODO
        /**
         * Completar el metodo de calculo
         * se calcula el importe a pagar por libro en funcion de la cantidad de dias,
         * es la diferencia entre el alquiler y la devolucion, respetando la siguiente tabla:
         * hasta 2 dias se debe pagar $100 por libro
         * desde 3 dias y hasta 5 dias se debe pagar $150 por libro
         * más de 5 dias se debe pagar $150 por libro + $30 por cada día extra
         */
        // Calcular la diferencia de días
        long diasAlquiler= ChronoUnit.DAYS.between(inicio, fin);

        // Determinar el costo por libro según la tabla de precios
        BigDecimal costoPorLibro;

        if (diasAlquiler<= 2){
            //Hasta 2 dias: $100 por libro
            costoPorLibro=new BigDecimal("100");
        } else if (diasAlquiler<=5) {
            //entre 3 y 5 dias: $150 por libro
            costoPorLibro=new BigDecimal("150");
        }else {
            //mas de 5 dias:$150 por libro + $30 por cada dia extra
            long diasExtras= diasAlquiler-5;
            BigDecimal costoBase= new BigDecimal("150");
            BigDecimal costoDiasExtra=new BigDecimal("30").multiply(new BigDecimal(diasExtras));
            costoPorLibro=costoBase.add(costoDiasExtra);
        }
        // Multiplicar por la cantidad de libros
        return costoPorLibro.multiply(new BigDecimal(cantidadLibros));
    }

    @Override
    public List<Registro> verTodosLosAlquileres() {
        return registroRepository.findAll();
    }

    @Override
    public List<Registro> informeSemanal(LocalDate fechaInicio) {
        //TODO
        /**
         * Completar el metodo de reporte semanal
         * se debe retornar la lista de registros de la semana tomando como referencia
         * la fecha de inicio para la busqueda
         */
        // Calcular la fecha final (una semana después de la fecha de inicio)
        LocalDate fechaFin = fechaInicio.plusDays(6);

        // Obtener los registros de la semana utilizando el método del repositorio
        return registroRepository.obtenerRegistrosSemana(fechaInicio, fechaFin);
    }

    @Override
    public List<Object[]> informeLibrosMasAlquilados() {
        //TODO
        /**
         * Completar el metodo de reporte de libros mas alquilados
         * se debe retornar la lista de libros mas alquilados
         */
        // Utilizar el método del repositorio para obtener los libros más alquilados
        return registroRepository.obtenerLibrosMasAlquilados();
    }

    @Override
    public List<Registro> verAlquileresFiltrados(String nombreCliente, LocalDate fechaInicio, LocalDate fechaFin, String isbn) {
        return registroRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nombreCliente != null && !nombreCliente.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("nombreCliente")), "%" + nombreCliente.toLowerCase() + "%"));
            }

            if (fechaInicio != null && fechaFin != null) {
                predicates.add(cb.between(root.get("fechaReserva"), fechaInicio, fechaFin));
            }

            if (isbn != null && !isbn.trim().isEmpty()) {
                Join<Object, Object> joinLibros = root.join("librosReservados");

                Expression<String> isbnDbClean = cb.function("REPLACE", String.class, joinLibros.get("isbn"), cb.literal("-"), cb.literal(""));
                isbnDbClean = cb.function("REPLACE", String.class, isbnDbClean, cb.literal(" "), cb.literal(""));

                String isbnInputClean = isbn.replaceAll("[-\\s]", "").toLowerCase();

                predicates.add(cb.like(cb.lower(isbnDbClean), "%" + isbnInputClean + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }


    @Override
    public List<Registro> informeSemanalFiltrado(LocalDate fechaInicio, String nombreCliente, String isbn) {
        LocalDate fechaFin = fechaInicio.plusDays(6);
        return verAlquileresFiltrados(nombreCliente, fechaInicio, fechaFin, isbn);
    }

    @Override
    public List<Object[]> informeLibrosMasAlquiladosFiltrado(LocalDate desde, LocalDate hasta, String autor, int top) {
        List<Registro> registros = registroRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (desde != null && hasta != null) {
                predicates.add(cb.between(root.get("fechaReserva"), desde, hasta));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        });

        Map<String, Long> conteoPorIsbn = new HashMap<>();
        Map<String, String> isbnToAutor = new HashMap<>();

        for (Registro r : registros) {
            for (Libro libro : r.getLibrosReservados()) {
                if (autor == null || libro.getAutor().equalsIgnoreCase(autor)) {
                    conteoPorIsbn.merge(libro.getIsbn(), 1L, Long::sum);
                    isbnToAutor.putIfAbsent(libro.getIsbn(), libro.getAutor());
                }
            }
        }

        return conteoPorIsbn.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(top)
                .map(entry -> new Object[]{entry.getKey(), isbnToAutor.get(entry.getKey()), entry.getValue()})
                .toList();
    }
}
