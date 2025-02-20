-- MySQL dump 10.13  Distrib 8.0.27, for Win64 (x86_64)
--
-- Host: localhost    Database: timesheet
-- ------------------------------------------------------
-- Server version	8.0.27

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee` (
  `uuid` varchar(36) NOT NULL DEFAULT (uuid()),
  `uuid_user` varchar(36) NOT NULL,
  `name` varchar(128) NOT NULL,
  `surname` varchar(128) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uuid_user_UNIQUE` (`uuid_user`),
  CONSTRAINT `fk_uuid_user_employee` FOREIGN KEY (`uuid_user`) REFERENCES `user` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee`
--

LOCK TABLES `employee` WRITE;
/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
INSERT INTO `employee` VALUES ('aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa','254cbb93-43ae-4bc6-ac3b-dbc8378cedc3','Mario','Rossi','2025-02-18 18:22:37','2025-02-18 18:22:37',NULL),('bbbbbbb2-bbbb-bbbb-bbbb-bbbbbbbbbbbb','1a206e11-4d1e-4341-bf5c-f99fbe17f7a8','Luigi','Verdi','2025-02-18 18:22:37','2025-02-18 18:22:37',NULL);
/*!40000 ALTER TABLE `employee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `presence`
--

DROP TABLE IF EXISTS `presence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `presence` (
  `uuid` varchar(36) NOT NULL DEFAULT (uuid()),
  `uuid_timesheet` varchar(36) NOT NULL,
  `work_day` date NOT NULL,
  `entry_time` time NOT NULL,
  `exit_time` time NOT NULL,
  `description` varchar(1024) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`uuid`),
  KEY `fk_uuid_timesheet_presence_idx` (`uuid_timesheet`),
  CONSTRAINT `fk_uuid_timesheet_presence` FOREIGN KEY (`uuid_timesheet`) REFERENCES `timesheet` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `presence`
--

LOCK TABLES `presence` WRITE;
/*!40000 ALTER TABLE `presence` DISABLE KEYS */;
INSERT INTO `presence` VALUES ('ppppppp1-0001-0001-0001-000000000001','ttttttt1-0001-0001-0001-000000000001','2024-01-01','08:00:00','17:00:00','Lavoro in ufficio','2025-02-18 18:22:37','2025-02-18 18:22:37',NULL),('ppppppp1-0001-0001-0001-000000000005','ttttttt2-0001-0001-0001-000000000003','2024-01-01','08:00:00','17:00:00','Lavoro in ufficio','2025-02-18 18:22:37','2025-02-18 18:22:37',NULL),('ppppppp1-0002-0001-0001-000000000002','ttttttt1-0001-0001-0001-000000000001','2024-01-02','09:00:00','18:00:00','Lavoro da remoto','2025-02-18 18:22:37','2025-02-18 18:22:37',NULL),('ppppppp1-0002-0001-0001-000000000006','ttttttt2-0001-0001-0001-000000000003','2024-01-02','09:00:00','18:00:00','Lavoro da remoto','2025-02-18 18:22:37','2025-02-18 18:22:37',NULL),('ppppppp2-0001-0001-0001-000000000003','ttttttt1-0002-0001-0001-000000000002','2024-02-01','08:30:00','17:30:00','Lavoro in sede','2025-02-18 18:22:37','2025-02-18 18:22:37',NULL),('ppppppp2-0001-0001-0001-000000000007','ttttttt2-0002-0001-0001-000000000004','2024-02-01','08:30:00','17:30:00','Lavoro in sede','2025-02-18 18:22:37','2025-02-18 18:22:37',NULL),('ppppppp2-0002-0001-0001-000000000004','ttttttt1-0002-0001-0001-000000000002','2024-02-02','09:00:00','18:00:00','Meeting esterno','2025-02-18 18:22:37','2025-02-18 18:22:37',NULL),('ppppppp2-0002-0001-0001-000000000008','ttttttt2-0002-0001-0001-000000000004','2024-02-02','09:00:00','18:00:00','Meeting esterno','2025-02-18 18:22:37','2025-02-18 18:22:37',NULL);
/*!40000 ALTER TABLE `presence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `timesheet`
--

DROP TABLE IF EXISTS `timesheet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `timesheet` (
  `uuid` varchar(36) NOT NULL DEFAULT (uuid()),
  `uuid_employee` varchar(36) NOT NULL,
  `month` int NOT NULL,
  `year` int NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uuid_employee_month_year` (`uuid_employee`,`month`,`year`),
  KEY `uuid_employee_timesheet_idx` (`uuid_employee`),
  CONSTRAINT `fk_employee_timesheet` FOREIGN KEY (`uuid_employee`) REFERENCES `employee` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `timesheet`
--

LOCK TABLES `timesheet` WRITE;
/*!40000 ALTER TABLE `timesheet` DISABLE KEYS */;
INSERT INTO `timesheet` VALUES ('ttttttt1-0001-0001-0001-000000000001','aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa',1,2024,'2025-02-18 18:22:37','2025-02-18 18:22:37',NULL),('ttttttt1-0002-0001-0001-000000000002','aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa',2,2024,'2025-02-18 18:22:37','2025-02-18 18:22:37',NULL),('ttttttt2-0001-0001-0001-000000000003','bbbbbbb2-bbbb-bbbb-bbbb-bbbbbbbbbbbb',1,2024,'2025-02-18 18:22:37','2025-02-18 18:22:37',NULL),('ttttttt2-0002-0001-0001-000000000004','bbbbbbb2-bbbb-bbbb-bbbb-bbbbbbbbbbbb',2,2024,'2025-02-18 18:22:37','2025-02-18 18:22:37',NULL);
/*!40000 ALTER TABLE `timesheet` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `uuid` varchar(36) NOT NULL DEFAULT (uuid()),
  `email` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('1a206e11-4d1e-4341-bf5c-f99fbe17f7a8','user2@example.com','password2','2025-02-18 18:22:37','2025-02-19 12:49:04',NULL),('254cbb93-43ae-4bc6-ac3b-dbc8378cedc3','user1@example.com','password1','2025-02-18 18:22:37','2025-02-19 12:48:41',NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-02-20  2:07:48
