-- phpMyAdmin SQL Dump
-- version 4.7.0
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 24-04-2026 a las 17:12:41
-- Versión del servidor: 10.1.25-MariaDB
-- Versión de PHP: 5.6.31

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `db_infrastructure`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `centros_salud`
--

CREATE TABLE `centros_salud` (
  `id` bigint(20) NOT NULL,
  `activo` bit(1) NOT NULL,
  `comuna` varchar(100) NOT NULL,
  `direccion` varchar(200) NOT NULL,
  `nivel_complejidad` varchar(50) DEFAULT NULL,
  `nombre` varchar(150) NOT NULL,
  `region` varchar(100) NOT NULL,
  `tipo` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `centros_salud`
--

INSERT INTO `centros_salud` (`id`, `activo`, `comuna`, `direccion`, `nivel_complejidad`, `nombre`, `region`, `tipo`) VALUES
(1, b'0', 'Antofagasta', 'Av. Salud 456', 'ALTA', 'Hospital Regional RedNorte Actualizado', 'Antofagasta', 'HOSPITAL');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `centro_especialidad`
--

CREATE TABLE `centro_especialidad` (
  `id` bigint(20) NOT NULL,
  `capacidad_diaria` int(11) NOT NULL,
  `centro_id` bigint(20) NOT NULL,
  `especialidad_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `centro_especialidad`
--

INSERT INTO `centro_especialidad` (`id`, `capacidad_diaria`, `centro_id`, `especialidad_id`) VALUES
(1, 20, 1, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `especialidades`
--

CREATE TABLE `especialidades` (
  `id` bigint(20) NOT NULL,
  `nombre` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `especialidades`
--

INSERT INTO `especialidades` (`id`, `nombre`) VALUES
(1, 'Cardiología'),
(2, 'Neurología'),
(3, 'Traumatología');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `centros_salud`
--
ALTER TABLE `centros_salud`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `centro_especialidad`
--
ALTER TABLE `centro_especialidad`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKqu61m1jv2dujctwb9g5x305g2` (`centro_id`),
  ADD KEY `FK6auvisad7h9m551oy6vdkjfd2` (`especialidad_id`);

--
-- Indices de la tabla `especialidades`
--
ALTER TABLE `especialidades`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKkq918o2plf4a6b25osvl96dj7` (`nombre`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `centros_salud`
--
ALTER TABLE `centros_salud`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT de la tabla `centro_especialidad`
--
ALTER TABLE `centro_especialidad`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT de la tabla `especialidades`
--
ALTER TABLE `especialidades`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `centro_especialidad`
--
ALTER TABLE `centro_especialidad`
  ADD CONSTRAINT `FK6auvisad7h9m551oy6vdkjfd2` FOREIGN KEY (`especialidad_id`) REFERENCES `especialidades` (`id`),
  ADD CONSTRAINT `FKqu61m1jv2dujctwb9g5x305g2` FOREIGN KEY (`centro_id`) REFERENCES `centros_salud` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
