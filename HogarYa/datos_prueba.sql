-- Script de datos de prueba - HogarYA
-- Ejecutar en phpMyAdmin/MySQL despues de correr la app una vez
-- (para que Hibernate cree las tablas)

-- Provincias
INSERT INTO provincia (nombre) VALUES
('Santa Fe'),
('Cordoba'),
('Buenos Aires');

-- Ciudades
INSERT INTO ciudad (nombre, provincia_id) VALUES
('Santa Fe', 1),
('Rosario', 1),
('Cordoba Capital', 2),
('Villa Maria', 2),
('La Plata', 3);

-- Personas (propietarios e inquilinos)
INSERT INTO persona (nombre, apellido, dni_cuit, telefono, email, domicilio, eliminada, ciudad_id) VALUES
('Bart', 'Simpson', '20-12345678-9', '342-4111111', 'bart@mail.com', 'Evergreen Terrace 742', false, 1),
('Homero', 'Simpson', '27-23456789-4', '342-4222222', 'homero@mail.com', 'Evergreen Terrace 742', false, 1),
('Peter', 'Griffin', '20-34567890-1', '341-4333333', 'peter@mail.com', 'Spooner Street 31', false, 2),
('Rick', 'Sanchez', '27-45678901-6', '341-4444444', 'rick@mail.com', 'Dimension C-137', false, 2),
('Eric', 'Cartman', '20-56789012-3', '351-4555555', 'cartman@mail.com', 'South Park 28', false, 3),
('Morty', 'Smith', '27-67890123-8', '342-4666666', 'morty@mail.com', 'Dimension C-137', false, 1);```
