-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: localhost    Database: medicine_dealer
-- ------------------------------------------------------
-- Server version	8.0.13

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
-- Dumping data for table `associati`
--

LOCK TABLES `associati` WRITE;
/*!40000 ALTER TABLE `associati` DISABLE KEYS */;
INSERT INTO `associati` VALUES (1,1,'Test1'),(3,1,'Test2');
/*!40000 ALTER TABLE `associati` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `assunzioni`
--

LOCK TABLES `assunzioni` WRITE;
/*!40000 ALTER TABLE `assunzioni` DISABLE KEYS */;
/*!40000 ALTER TABLE `assunzioni` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `eventi`
--

LOCK TABLES `eventi` WRITE;
/*!40000 ALTER TABLE `eventi` DISABLE KEYS */;
INSERT INTO `eventi` VALUES (1,'2020-05-09','10:00:00',1,045359027,NULL),(2,'2020-05-09','10:00:00',1,045359027,NULL),(3,'2021-05-09','10:00:00',1,045359027,NULL),(4,'2021-05-09','10:00:00',2,045359027,NULL);
/*!40000 ALTER TABLE `eventi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `farmaci`
--

LOCK TABLES `farmaci` WRITE;
/*!40000 ALTER TABLE `farmaci` DISABLE KEYS */;
INSERT INTO `farmaci` VALUES (045359027,'PER DOSE UNITARIA PVC/ACLAR/AL');
/*!40000 ALTER TABLE `farmaci` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `porta_medicine`
--

LOCK TABLES `porta_medicine` WRITE;
/*!40000 ALTER TABLE `porta_medicine` DISABLE KEYS */;
INSERT INTO `porta_medicine` VALUES (1,'c260c28d4d5244c69409c46f1533858e','asddrwv234f23f'),(2,'c260c28d4d5244er9409c46f1533858e','sdfsdfsdfsd'),(3,'c260c28d4re244er9409c46f1533858e','fdsvdsv');
/*!40000 ALTER TABLE `porta_medicine` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `utenti`
--

LOCK TABLES `utenti` WRITE;
/*!40000 ALTER TABLE `utenti` DISABLE KEYS */;
INSERT INTO `utenti` VALUES (1,'aa@gmail.com','$2a$10$gu4N3zcR2d3/O85yif0JGuqNlLOjMxFCrhzFlRMJDsvqEmmRzZga6','a','a','2020-05-05');
/*!40000 ALTER TABLE `utenti` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'medicine_dealer'
--

--
-- Dumping routines for database 'medicine_dealer'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-05-12 12:11:30
