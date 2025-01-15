-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Jan 15, 2025 at 04:33 PM
-- Server version: 8.0.30
-- PHP Version: 8.3.7

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `payroll_system`
--

-- --------------------------------------------------------

--
-- Table structure for table `attendance`
--

CREATE TABLE `attendance` (
  `id` int NOT NULL,
  `employee_id` int NOT NULL,
  `date` date NOT NULL,
  `status` enum('hadir','cuti','absen') NOT NULL COMMENT 'Status kehadiran',
  `hours_worked` decimal(5,2) DEFAULT NULL COMMENT 'Jam kerja untuk freelance',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `attendance`
--

INSERT INTO `attendance` (`id`, `employee_id`, `date`, `status`, `hours_worked`, `created_at`) VALUES
(1, 2, '2025-01-01', 'hadir', NULL, '2025-01-12 14:35:06'),
(2, 3, '2025-01-01', 'hadir', 8.00, '2025-01-12 14:35:06'),
(3, 4, '2025-01-01', 'cuti', NULL, '2025-01-12 14:35:06'),
(4, 2, '2025-01-13', 'hadir', NULL, '2025-01-13 06:59:08'),
(5, 2, '2025-01-17', 'cuti', NULL, '2025-01-13 07:04:20'),
(6, 2, '2025-01-16', 'cuti', NULL, '2025-01-13 07:06:38'),
(7, 2, '2025-01-15', 'cuti', NULL, '2025-01-13 07:30:33'),
(8, 2, '2025-01-13', 'cuti', NULL, '2025-01-13 07:48:24'),
(12, 2, '2025-02-04', 'cuti', NULL, '2025-01-14 18:15:10'),
(13, 2, '2025-01-21', 'cuti', NULL, '2025-01-14 18:18:51');

-- --------------------------------------------------------

--
-- Table structure for table `employees`
--

CREATE TABLE `employees` (
  `id` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `status` enum('tetap','freelance') NOT NULL COMMENT 'Status kerja',
  `base_salary` decimal(10,2) DEFAULT NULL COMMENT 'Gaji pokok untuk tetap',
  `hourly_rate` decimal(10,2) DEFAULT NULL COMMENT 'Tarif per jam untuk freelance',
  `leave_balance` int DEFAULT '0' COMMENT 'Sisa cuti',
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL COMMENT 'Hashed password untuk login',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `employees`
--

INSERT INTO `employees` (`id`, `name`, `status`, `base_salary`, `hourly_rate`, `leave_balance`, `username`, `password`, `created_at`) VALUES
(1, 'Andre', 'tetap', 5000000.00, NULL, 12, 'hrd', 'password', '2025-01-12 14:35:06'),
(2, 'Ivanka', 'tetap', 5000000.00, NULL, 6, 'ivanka', 'password', '2025-01-12 14:35:06'),
(3, 'Deni', 'freelance', NULL, 50000.00, NULL, 'deni', 'password', '2025-01-12 14:35:06'),
(4, 'Ichsan', 'tetap', 4000000.00, NULL, 10, 'ichsan', 'password', '2025-01-12 14:35:06'),
(5, 'Samsul', 'tetap', 5500000.00, NULL, 12, 'samsul', 'password', '2025-01-14 09:08:42'),
(9, 'jamal', 'tetap', 400000.00, NULL, 10, 'jamal', '123', '2025-01-14 09:26:58'),
(10, 'test', 'tetap', 1500000.00, NULL, 12, 'test', '123', '2025-01-14 18:19:39');

-- --------------------------------------------------------

--
-- Table structure for table `roles`
--

CREATE TABLE `roles` (
  `id` int NOT NULL,
  `role_name` enum('Karyawan','HRD') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Peran pengguna',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `roles`
--

INSERT INTO `roles` (`id`, `role_name`, `created_at`) VALUES
(1, 'Karyawan', '2025-01-12 14:35:06'),
(2, 'HRD', '2025-01-12 14:35:06');

-- --------------------------------------------------------

--
-- Table structure for table `salaries`
--

CREATE TABLE `salaries` (
  `id` int NOT NULL,
  `employee_id` int NOT NULL,
  `month` date NOT NULL COMMENT 'Bulan penggajian',
  `total_salary` decimal(10,2) NOT NULL COMMENT 'Total gaji',
  `deductions` decimal(10,2) DEFAULT '0.00' COMMENT 'Pemotongan gaji',
  `notes` text COMMENT 'Catatan tambahan',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `salaries`
--

INSERT INTO `salaries` (`id`, `employee_id`, `month`, `total_salary`, `deductions`, `notes`, `created_at`) VALUES
(1, 1, '2025-01-01', 5000000.00, 0.00, 'Full attendance', '2025-01-12 14:35:06'),
(2, 2, '2025-01-01', 4000000.00, 100000.00, 'One day cut deducted\r\n', '2025-01-12 14:35:06'),
(3, 3, '2025-01-01', 400000.00, 100000.00, 'Freelance hours: 8', '2025-01-12 14:35:06'),
(4, 9, '2025-01-01', 400000.00, 0.00, 'Initial salary', '2025-01-14 09:26:58'),
(5, 10, '2025-01-01', 1500000.00, 0.00, 'Initial salary', '2025-01-14 18:19:39');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL COMMENT 'Hashed password',
  `role_id` int NOT NULL,
  `employee_id` int DEFAULT NULL COMMENT 'Relasi ke tabel employees',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `role_id`, `employee_id`, `created_at`) VALUES
(1, 'hrd', 'password', 2, 1, '2025-01-12 14:35:06'),
(2, 'user1', 'password', 1, 2, '2025-01-12 14:35:06'),
(3, 'jane_smith', 'password', 1, 3, '2025-01-12 14:35:06'),
(4, 'mark_taylor', 'password', 1, 4, '2025-01-12 14:35:06'),
(8, 'jamal', '123', 1, 9, '2025-01-14 09:26:58'),
(9, 'test', '123', 1, 10, '2025-01-14 18:19:39');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `attendance`
--
ALTER TABLE `attendance`
  ADD PRIMARY KEY (`id`),
  ADD KEY `employee_id` (`employee_id`);

--
-- Indexes for table `employees`
--
ALTER TABLE `employees`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indexes for table `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `salaries`
--
ALTER TABLE `salaries`
  ADD PRIMARY KEY (`id`),
  ADD KEY `employee_id` (`employee_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `employee_id` (`employee_id`),
  ADD KEY `role_id` (`role_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `attendance`
--
ALTER TABLE `attendance`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT for table `employees`
--
ALTER TABLE `employees`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `roles`
--
ALTER TABLE `roles`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `salaries`
--
ALTER TABLE `salaries`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `attendance`
--
ALTER TABLE `attendance`
  ADD CONSTRAINT `attendance_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `salaries`
--
ALTER TABLE `salaries`
  ADD CONSTRAINT `salaries_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `users_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `users_ibfk_2` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
