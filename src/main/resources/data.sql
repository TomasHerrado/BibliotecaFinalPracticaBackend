-- Vaciar tablas si existen (en orden inverso a las relaciones)
DELETE FROM registros_libros_reservados;
DELETE FROM registros;
DELETE FROM libros;
DELETE FROM personas;

-- Insertar libros
INSERT INTO libros (id, isbn, titulo, autor, estado) VALUES
                                                         (1, '9788478291199', 'El Quijote', 'Miguel de Cervantes', 'DISPONIBLE'),
                                                         (2, '9788433966926', 'La metamorfosis', 'Franz Kafka', 'DISPONIBLE'),
                                                         (3, '9788499089515', 'Cien años de soledad', 'Gabriel García Márquez', 'DISPONIBLE'),
                                                         (4, '9788432220937', 'La casa de los espíritus', 'Isabel Allende', 'DISPONIBLE'),
                                                         (5, '9788420633114', 'La sombra del viento', 'Carlos Ruiz Zafón', 'DISPONIBLE'),
                                                         (6, '9788446024521', 'Harry Potter y la piedra filosofal', 'J.K. Rowling', 'DISPONIBLE'),
                                                         (7, '9788498382662', 'El Señor de los Anillos', 'J.R.R. Tolkien', 'DISPONIBLE'),
                                                         (8, '9788497593793', 'Orgullo y prejuicio', 'Jane Austen', 'DISPONIBLE'),
                                                         (9, '9788490626887', 'Crimen y castigo', 'Fiódor Dostoyevski', 'DISPONIBLE'),
                                                         (10, '9788408062783', 'El código Da Vinci', 'Dan Brown', 'DISPONIBLE');

-- Insertar personas
INSERT INTO personas (id, nombre, domicilio) VALUES
                                                 (1, 'Juan Pérez', 'Av. Siempreviva 123'),
                                                 (2, 'María González', 'Calle Falsa 123'),
                                                 (3, 'Carlos Rodríguez', 'Plaza Principal 456'),
                                                 (4, 'Ana Martínez', 'Av. Libertador 789'),
                                                 (5, 'Luis López', 'Paseo de la Reforma 101');

-- Insertar registros de alquiler (historial)
INSERT INTO registros (id, cliente_id, nombre_cliente, fecha_reserva, fecha_devolucion, total_alquiler) VALUES
                                                                                                            (1, 1, 'Juan Pérez', '2025-03-15', '2025-03-17', 200.00),
                                                                                                            (2, 2, 'María González', '2025-03-20', '2025-03-25', 300.00),
                                                                                                            (3, 3, 'Carlos Rodríguez', '2025-04-01', '2025-04-10', 450.00);

-- Relacionar libros con registros
INSERT INTO registros_libros_reservados (registro_id, libros_reservados_id) VALUES
                                                                                (1, 1),
                                                                                (1, 2),
                                                                                (2, 3),
                                                                                (2, 4),
                                                                                (3, 5),
                                                                                (3, 6),
                                                                                (3, 7);

-- Actualizar secuencias de ids (si es necesario en MySQL)
ALTER TABLE libros AUTO_INCREMENT = 11;
ALTER TABLE personas AUTO_INCREMENT = 6;
ALTER TABLE registros AUTO_INCREMENT = 4;
