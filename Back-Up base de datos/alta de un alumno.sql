INSERT INTO alumnos (
    nombre, 
    apellido, 
    dni, 
    telefono_padre, 
    hora_inicio_taller, 
    turno_comedor, 
    nota, 
    pide_calentito, 
    password, 
    telefono_alumno
) 
VALUES (
    'Mariana', 
    'Emon', 
    '88888888', 
    '123456', 
    '07:00', 
    '12:00', 
    10, 
    1,     
    NULL,  -- Para que el sistema le pida crear el PIN
    '654321'
);

-- Verificamos que ahora sí entró
SELECT * FROM alumnos WHERE dni = '99999999';