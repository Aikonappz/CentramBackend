-- MySQL dump 10.13  Distrib 5.7.36, for Linux (x86_64)
--
-- Host: localhost    Database: centram
-- ------------------------------------------------------
-- Server version	5.7.36-0ubuntu0.18.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `action`
--

DROP TABLE IF EXISTS `action`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `action` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_name_idx` (`name`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `action`
--

LOCK TABLES `action` WRITE;
/*!40000 ALTER TABLE `action` DISABLE KEYS */;
INSERT INTO `action` VALUES (1,'READ'),(2,'WRITE'),(3,'DELETE'),(4,'SEARCH');
/*!40000 ALTER TABLE `action` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `activity_log`
--

DROP TABLE IF EXISTS `activity_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `activity_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `activity_date_time` datetime DEFAULT NULL,
  `activity_type` int(11) DEFAULT NULL,
  `organisation_id` decimal(19,2) DEFAULT NULL,
  `user_id` decimal(19,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id_idx` (`user_id`),
  KEY `organisation_id_idx` (`organisation_id`),
  KEY `activity_type_idx` (`activity_type`),
  KEY `user_id_activity_type_organisation_id_idx` (`user_id`,`activity_type`,`organisation_id`),
  KEY `user_id_activity_type_idx` (`user_id`,`activity_type`),
  KEY `user_id_organisation_id_idx` (`user_id`,`organisation_id`),
  KEY `activity_type_organisation_id_idx` (`activity_type`,`organisation_id`)
) ENGINE=MyISAM AUTO_INCREMENT=305 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity_log`
--

LOCK TABLES `activity_log` WRITE;
/*!40000 ALTER TABLE `activity_log` DISABLE KEYS */;
INSERT INTO `activity_log` VALUES (1,'2021-11-01 16:34:48',0,NULL,1.00),(2,'2021-11-01 16:35:54',1,NULL,1.00),(3,'2021-11-01 18:08:59',2,NULL,1.00),(4,'2021-11-01 18:09:31',2,NULL,1.00),(5,'2021-11-01 18:09:49',2,NULL,1.00),(6,'2021-11-01 18:10:01',2,NULL,1.00),(7,'2021-11-01 18:10:55',2,NULL,1.00),(8,'2021-11-01 18:46:23',2,NULL,1.00),(9,'2021-11-01 18:49:20',2,NULL,1.00),(10,'2021-11-01 18:51:12',3,NULL,1.00),(11,'2021-11-02 08:34:25',0,NULL,1.00),(12,'2021-11-02 08:36:05',2,NULL,1.00),(13,'2021-11-02 08:37:25',3,NULL,1.00),(14,'2021-11-02 08:37:39',0,NULL,1.00),(15,'2021-11-02 08:46:57',0,NULL,1.00),(16,'2021-11-02 08:46:59',0,NULL,1.00),(17,'2021-11-02 08:48:47',0,NULL,1.00),(18,'2021-11-02 09:44:03',8,NULL,1.00),(19,'2021-11-02 09:45:52',0,NULL,2.00),(20,'2021-11-02 09:55:20',9,NULL,1.00),(21,'2021-11-02 09:57:11',9,NULL,1.00),(22,'2021-11-02 09:58:00',9,NULL,1.00),(23,'2021-11-02 10:00:44',3,NULL,1.00),(24,'2021-11-02 10:02:02',9,NULL,1.00),(25,'2021-11-02 10:02:20',9,NULL,1.00),(26,'2021-11-02 10:14:16',0,NULL,1.00),(27,'2021-11-02 10:15:03',9,NULL,1.00),(28,'2021-11-02 10:15:32',9,NULL,1.00),(29,'2021-11-02 10:15:48',9,NULL,1.00),(30,'2021-11-02 10:15:50',9,NULL,1.00),(31,'2021-11-02 10:16:07',9,NULL,1.00),(32,'2021-11-02 10:16:08',9,NULL,1.00),(33,'2021-11-02 10:37:10',0,NULL,1.00),(34,'2021-11-03 15:32:51',0,NULL,1.00),(35,'2021-11-03 15:42:28',0,NULL,1.00),(36,'2021-11-03 15:44:42',1,NULL,1.00),(37,'2021-11-03 15:57:23',2,NULL,1.00),(38,'2021-11-03 15:58:36',2,NULL,1.00),(39,'2021-11-03 16:03:20',2,NULL,1.00),(40,'2021-11-03 16:13:52',2,NULL,1.00),(41,'2021-11-03 16:16:19',2,NULL,1.00),(42,'2021-11-03 16:17:19',3,NULL,1.00),(43,'2021-11-03 16:19:01',0,NULL,1.00),(44,'2021-11-04 10:25:58',0,NULL,1.00),(45,'2021-11-04 10:26:59',8,NULL,1.00),(46,'2021-11-04 10:30:17',8,NULL,1.00),(47,'2021-11-04 10:33:14',8,NULL,1.00),(48,'2021-11-04 10:33:42',8,NULL,1.00),(49,'2021-11-04 10:38:17',8,NULL,1.00),(50,'2021-11-04 10:38:49',8,NULL,1.00),(51,'2021-11-04 10:42:49',8,NULL,1.00),(52,'2021-11-04 10:44:27',8,NULL,1.00),(53,'2021-11-04 10:45:46',9,NULL,1.00),(54,'2021-11-04 10:46:53',9,NULL,1.00),(55,'2021-11-04 11:32:38',0,NULL,1.00),(56,'2021-11-04 11:32:46',0,NULL,1.00),(57,'2021-11-04 12:27:11',0,NULL,1.00),(58,'2021-11-04 12:27:14',0,NULL,1.00),(59,'2021-11-04 12:35:10',8,NULL,1.00),(60,'2021-11-04 12:36:08',8,NULL,1.00),(61,'2021-11-04 12:38:17',8,NULL,1.00),(62,'2021-11-04 12:45:18',8,NULL,1.00),(63,'2021-11-04 12:45:20',5,NULL,1.00),(64,'2021-11-04 13:05:16',6,NULL,1.00),(65,'2021-11-04 13:07:36',6,NULL,1.00),(66,'2021-11-04 13:46:03',6,NULL,1.00),(67,'2021-11-04 13:46:23',6,NULL,1.00),(68,'2021-11-07 06:33:34',2,NULL,1.00),(69,'2021-11-07 08:02:57',2,NULL,1.00),(70,'2021-11-07 11:42:56',3,NULL,1.00),(71,'2021-11-07 14:19:16',0,NULL,1.00),(72,'2021-11-07 14:19:30',0,NULL,1.00),(73,'2021-11-07 14:20:30',0,NULL,1.00),(74,'2021-11-07 14:27:20',0,NULL,1.00),(75,'2021-11-07 14:29:30',0,NULL,1.00),(76,'2021-11-07 14:31:15',0,NULL,1.00),(77,'2021-11-07 14:40:06',0,NULL,1.00),(78,'2021-11-07 14:44:07',0,NULL,1.00),(79,'2021-11-07 14:46:15',0,NULL,1.00),(80,'2021-11-07 14:48:34',0,NULL,1.00),(81,'2021-11-07 14:50:14',0,NULL,1.00),(82,'2021-11-07 15:04:07',0,NULL,1.00),(83,'2021-11-07 15:04:22',1,NULL,1.00),(84,'2021-11-07 15:05:15',0,NULL,1.00),(85,'2021-11-07 15:05:24',0,NULL,1.00),(86,'2021-11-07 15:05:38',1,NULL,1.00),(87,'2021-11-07 15:06:15',0,NULL,1.00),(88,'2021-11-07 15:06:30',1,NULL,1.00),(89,'2021-11-07 16:44:14',0,NULL,1.00),(90,'2021-11-07 16:44:28',0,NULL,1.00),(91,'2021-11-08 05:44:24',0,NULL,1.00),(92,'2021-11-08 07:09:09',1,NULL,1.00),(93,'2021-11-08 07:25:06',0,NULL,1.00),(94,'2021-11-08 07:25:50',1,NULL,1.00),(95,'2021-11-08 07:26:26',0,NULL,1.00),(96,'2021-11-08 07:26:51',1,NULL,1.00),(97,'2021-11-08 07:29:15',0,NULL,1.00),(98,'2021-11-08 07:32:22',1,NULL,1.00),(99,'2021-11-08 07:32:40',0,NULL,1.00),(100,'2021-11-08 07:32:47',1,NULL,1.00),(101,'2021-11-08 07:35:21',0,NULL,1.00),(102,'2021-11-08 07:35:27',1,NULL,1.00),(103,'2021-11-08 07:46:21',0,NULL,1.00),(104,'2021-11-08 08:10:51',0,NULL,1.00),(105,'2021-11-08 08:11:39',1,NULL,1.00),(106,'2021-11-08 08:12:26',0,NULL,1.00),(107,'2021-11-08 08:12:31',1,NULL,1.00),(108,'2021-11-08 08:14:50',0,NULL,1.00),(109,'2021-11-08 08:57:21',1,NULL,1.00),(110,'2021-11-08 08:58:52',0,NULL,1.00),(111,'2021-11-08 08:59:00',1,NULL,1.00),(112,'2021-11-08 09:01:18',0,NULL,1.00),(113,'2021-11-08 09:01:23',1,NULL,1.00),(114,'2021-11-08 11:51:52',0,NULL,1.00),(115,'2021-11-08 11:52:36',1,NULL,1.00),(116,'2021-11-08 12:00:14',0,NULL,1.00),(117,'2021-11-08 12:05:43',0,NULL,1.00),(118,'2021-11-08 12:08:33',1,NULL,1.00),(119,'2021-11-08 12:08:38',0,NULL,1.00),(120,'2021-11-08 12:10:02',1,NULL,1.00),(121,'2021-11-08 14:07:32',0,NULL,1.00),(122,'2021-11-09 12:45:11',9,NULL,1.00),(123,'2021-11-09 12:45:20',9,NULL,1.00),(124,'2021-11-09 12:56:04',9,NULL,1.00),(125,'2021-11-09 12:56:41',9,NULL,1.00),(126,'2021-11-09 12:57:54',9,NULL,1.00),(127,'2021-11-09 12:59:36',9,NULL,1.00),(128,'2021-11-09 13:00:33',9,NULL,1.00),(129,'2021-11-09 13:04:13',9,NULL,1.00),(130,'2021-11-09 13:04:21',9,NULL,1.00),(131,'2021-11-09 13:05:45',9,NULL,1.00),(132,'2021-11-09 13:05:49',9,NULL,1.00),(133,'2021-11-09 13:06:42',9,NULL,1.00),(134,'2021-11-09 13:12:43',9,NULL,1.00),(135,'2021-11-09 13:18:50',9,NULL,1.00),(136,'2021-11-09 13:18:53',9,NULL,1.00),(137,'2021-11-09 13:19:49',9,NULL,1.00),(138,'2021-11-09 13:19:53',9,NULL,1.00),(139,'2021-11-09 13:21:44',9,NULL,1.00),(140,'2021-11-09 13:21:48',9,NULL,1.00),(141,'2021-11-09 13:28:55',9,NULL,1.00),(142,'2021-11-09 13:29:00',9,NULL,1.00),(143,'2021-11-09 13:35:09',9,NULL,1.00),(144,'2021-11-09 13:35:15',9,NULL,1.00),(145,'2021-11-09 14:27:26',9,NULL,1.00),(146,'2021-11-09 14:27:29',9,NULL,1.00),(147,'2021-11-10 03:45:47',9,NULL,1.00),(148,'2021-11-10 03:45:54',9,NULL,1.00),(149,'2021-11-10 03:47:00',9,NULL,1.00),(150,'2021-11-10 03:47:04',9,NULL,1.00),(151,'2021-11-10 03:47:07',9,NULL,1.00),(152,'2021-11-10 03:47:10',9,NULL,1.00),(153,'2021-11-10 03:47:17',9,NULL,1.00),(154,'2021-11-10 03:47:20',9,NULL,1.00),(155,'2021-11-10 05:01:33',9,NULL,1.00),(156,'2021-11-10 05:46:17',9,NULL,1.00),(157,'2021-11-10 05:46:22',9,NULL,1.00),(158,'2021-11-10 11:48:02',1,NULL,1.00),(159,'2021-11-10 11:48:07',0,NULL,1.00),(160,'2021-11-10 15:16:15',9,NULL,1.00),(161,'2021-11-10 15:16:26',9,NULL,1.00),(162,'2021-11-10 16:44:36',9,NULL,1.00),(163,'2021-11-10 16:44:54',9,NULL,1.00),(164,'2021-11-10 16:51:11',0,NULL,1.00),(165,'2021-11-10 16:51:11',0,NULL,1.00),(166,'2021-11-10 17:47:20',0,NULL,1.00),(167,'2021-11-10 18:35:48',9,NULL,1.00),(168,'2021-11-10 18:35:56',9,NULL,1.00),(169,'2021-11-10 18:47:36',9,NULL,1.00),(170,'2021-11-10 18:47:39',9,NULL,1.00),(171,'2021-11-11 05:07:30',9,NULL,1.00),(172,'2021-11-11 05:21:58',9,NULL,1.00),(173,'2021-11-11 05:22:20',9,NULL,1.00),(174,'2021-11-11 05:22:31',9,NULL,1.00),(175,'2021-11-11 05:22:38',9,NULL,1.00),(176,'2021-11-11 05:23:39',9,NULL,1.00),(177,'2021-11-11 05:23:51',9,NULL,1.00),(178,'2021-11-11 05:26:21',9,NULL,1.00),(179,'2021-11-11 05:27:41',9,NULL,1.00),(180,'2021-11-11 06:39:11',9,NULL,1.00),(181,'2021-11-11 06:41:01',9,NULL,1.00),(182,'2021-11-11 06:41:14',9,NULL,1.00),(183,'2021-11-11 06:41:26',9,NULL,1.00),(184,'2021-11-11 06:44:58',9,NULL,1.00),(185,'2021-11-11 06:45:11',9,NULL,1.00),(186,'2021-11-11 06:46:12',9,NULL,1.00),(187,'2021-11-11 06:46:31',9,NULL,1.00),(188,'2021-11-11 07:10:38',9,NULL,1.00),(189,'2021-11-11 07:14:14',9,NULL,1.00),(190,'2021-11-11 07:15:33',9,NULL,1.00),(191,'2021-11-11 07:16:01',9,NULL,1.00),(192,'2021-11-11 07:16:12',9,NULL,1.00),(193,'2021-11-11 07:16:22',9,NULL,1.00),(194,'2021-11-11 07:16:58',9,NULL,1.00),(195,'2021-11-11 08:41:14',8,NULL,1.00),(196,'2021-11-11 10:23:05',9,NULL,1.00),(197,'2021-11-11 10:24:12',9,NULL,1.00),(198,'2021-11-11 10:24:18',9,NULL,1.00),(199,'2021-11-11 10:24:23',9,NULL,1.00),(200,'2021-11-11 10:24:32',9,NULL,1.00),(201,'2021-11-11 10:24:36',9,NULL,1.00),(202,'2021-11-11 10:24:41',9,NULL,1.00),(203,'2021-11-11 10:25:21',8,NULL,1.00),(204,'2021-11-11 10:26:04',8,NULL,1.00),(205,'2021-11-11 10:26:16',9,NULL,1.00),(206,'2021-11-11 10:26:24',9,NULL,1.00),(207,'2021-11-11 10:26:32',9,NULL,1.00),(208,'2021-11-11 10:28:58',9,NULL,1.00),(209,'2021-11-11 10:29:05',9,NULL,1.00),(210,'2021-11-11 11:19:55',3,NULL,1.00),(211,'2021-11-11 11:20:06',3,NULL,1.00),(212,'2021-11-11 11:20:23',3,NULL,1.00),(213,'2021-11-11 11:24:59',3,NULL,1.00),(214,'2021-11-11 11:30:41',3,NULL,1.00),(215,'2021-11-11 11:33:11',3,NULL,1.00),(216,'2021-11-11 11:34:38',3,NULL,1.00),(217,'2021-11-11 11:34:46',1,NULL,1.00),(218,'2021-11-11 11:41:22',0,NULL,1.00),(219,'2021-11-11 11:41:42',9,NULL,1.00),(220,'2021-11-11 11:41:45',9,NULL,1.00),(221,'2021-11-11 11:41:50',9,NULL,1.00),(222,'2021-11-11 11:41:56',9,NULL,1.00),(223,'2021-11-11 11:45:30',1,NULL,1.00),(224,'2021-11-11 11:57:26',0,NULL,1.00),(225,'2021-11-11 11:58:34',0,NULL,1.00),(226,'2021-11-11 11:58:46',0,NULL,1.00),(227,'2021-11-11 12:08:00',0,NULL,1.00),(228,'2021-11-11 12:10:52',0,NULL,1.00),(229,'2021-11-11 12:12:31',0,1.00,44.00),(230,'2021-11-11 12:13:11',0,1.00,44.00),(231,'2021-11-11 12:25:58',1,NULL,1.00),(232,'2021-11-11 12:28:52',0,NULL,1.00),(233,'2021-11-11 12:30:34',1,NULL,1.00),(234,'2021-11-11 12:30:59',0,NULL,1.00),(235,'2021-11-11 12:31:12',0,NULL,1.00),(236,'2021-11-11 12:31:22',0,NULL,1.00),(237,'2021-11-11 12:31:40',0,NULL,1.00),(238,'2021-11-11 12:31:49',0,NULL,1.00),(239,'2021-11-11 13:08:05',0,NULL,1.00),(240,'2021-11-11 13:08:44',1,NULL,1.00),(241,'2021-11-11 13:10:20',0,NULL,1.00),(242,'2021-11-11 13:10:37',0,NULL,1.00),(243,'2021-11-11 13:11:30',1,NULL,1.00),(244,'2021-11-11 13:17:09',0,NULL,1.00),(245,'2021-11-11 13:17:42',3,NULL,1.00),(246,'2021-11-11 15:59:21',6,NULL,1.00),(247,'2021-11-11 15:59:29',6,NULL,1.00),(248,'2021-11-12 09:09:16',5,NULL,1.00),(249,'2021-11-12 09:56:50',5,NULL,1.00),(250,'2021-11-12 10:00:35',5,NULL,1.00),(251,'2021-11-12 10:05:12',6,NULL,1.00),(252,'2021-11-12 10:06:51',5,NULL,1.00),(253,'2021-11-12 10:11:13',5,NULL,1.00),(254,'2021-11-12 10:12:55',5,NULL,1.00),(255,'2021-11-12 10:14:26',5,NULL,1.00),(256,'2021-11-12 10:14:27',8,NULL,1.00),(257,'2021-11-12 10:16:12',5,NULL,1.00),(258,'2021-11-12 10:16:13',8,NULL,1.00),(259,'2021-11-12 10:31:03',6,NULL,1.00),(260,'2021-11-12 11:05:14',6,NULL,1.00),(261,'2021-11-12 11:05:46',6,NULL,1.00),(262,'2021-11-12 11:06:05',6,NULL,1.00),(263,'2021-11-12 11:10:35',6,NULL,1.00),(264,'2021-11-12 11:11:35',6,NULL,1.00),(265,'2021-11-12 11:13:29',6,NULL,1.00),(266,'2021-11-12 11:13:44',6,NULL,1.00),(267,'2021-11-12 11:14:40',6,NULL,1.00),(268,'2021-11-12 11:15:17',6,NULL,1.00),(269,'2021-11-12 11:24:19',6,NULL,1.00),(270,'2021-11-12 11:25:39',6,NULL,1.00),(271,'2021-11-12 11:26:29',6,NULL,1.00),(272,'2021-11-12 11:27:30',6,NULL,1.00),(273,'2021-11-12 11:28:40',6,NULL,1.00),(274,'2021-11-12 11:32:03',6,NULL,1.00),(275,'2021-11-12 11:33:35',6,NULL,1.00),(276,'2021-11-12 11:37:08',6,NULL,1.00),(277,'2021-11-12 11:37:44',6,NULL,1.00),(278,'2021-11-12 11:38:50',6,NULL,1.00),(279,'2021-11-12 11:39:44',6,NULL,1.00),(280,'2021-11-12 12:10:47',6,NULL,1.00),(281,'2021-11-12 12:18:32',6,NULL,1.00),(282,'2021-11-12 12:20:39',6,NULL,1.00),(283,'2021-11-12 12:21:24',6,NULL,1.00),(284,'2021-11-12 12:24:05',6,NULL,1.00),(285,'2021-11-12 12:25:03',6,NULL,1.00),(286,'2021-11-12 12:45:52',6,NULL,1.00),(287,'2021-11-12 12:47:01',6,NULL,1.00),(288,'2021-11-12 12:48:34',6,NULL,1.00),(289,'2021-11-12 12:49:35',6,NULL,1.00),(290,'2021-11-12 12:50:55',6,NULL,1.00),(291,'2021-11-12 12:52:49',6,NULL,1.00),(292,'2021-11-12 14:38:00',6,NULL,1.00),(293,'2021-11-12 15:36:31',6,NULL,1.00),(294,'2021-11-12 15:37:19',6,NULL,1.00),(295,'2021-11-12 15:49:14',6,NULL,1.00),(296,'2021-11-12 15:54:59',6,NULL,1.00),(297,'2021-11-12 15:58:18',6,NULL,1.00),(298,'2021-11-12 16:00:12',6,NULL,1.00),(299,'2021-11-12 16:17:37',6,NULL,1.00),(300,'2021-11-12 16:21:44',6,NULL,1.00),(301,'2021-11-12 16:22:21',6,NULL,1.00),(302,'2021-11-12 16:22:29',6,NULL,1.00),(303,'2021-11-12 16:22:32',6,NULL,1.00),(304,'2021-11-12 16:22:53',6,NULL,1.00);
/*!40000 ALTER TABLE `activity_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `app_configuration`
--

DROP TABLE IF EXISTS `app_configuration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `app_configuration` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` decimal(19,2) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `modified_by` decimal(19,2) DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `configuration_key` varchar(255) NOT NULL,
  `configuration_properties` text NOT NULL,
  `configuration_value` text NOT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_o8qvtwwq08bnpfd9a4gxdnqj1` (`configuration_key`),
  UNIQUE KEY `config_key_idx` (`configuration_key`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `app_configuration`
--

LOCK TABLES `app_configuration` WRITE;
/*!40000 ALTER TABLE `app_configuration` DISABLE KEYS */;
INSERT INTO `app_configuration` VALUES (1,1.00,'2020-08-12 07:35:41',1.00,'2020-08-12 07:35:41',0,'BASE_EMAIL_TEMPLATE','','<!doctype html><html><head><meta content=\"width=device-width\" name=\"viewport\"/><meta content=\"text/html; charset=UTF-8\" http-equiv=\"Content-Type\"/><title>Simple Transactional Email</title><style>\n      /* -------------------------------------\n          GLOBAL RESETS\n      ------------------------------------- */\n\n      /*All the styling goes here*/\n\n      img {\n        border: none;\n        -ms-interpolation-mode: bicubic;\n        max-width: 100%;\n      }\n\n      body {\n        background-color: #f6f6f6;\n        font-family: sans-serif;\n        -webkit-font-smoothing: antialiased;\n        font-size: 14px;\n        line-height: 1.4;\n        margin: 0;\n        padding: 0;\n        -ms-text-size-adjust: 100%;\n        -webkit-text-size-adjust: 100%;\n      }\n\n      table {\n        border-collapse: separate;\n        mso-table-lspace: 0pt;\n        mso-table-rspace: 0pt;\n        width: 100%; }\n        table td {\n          font-family: sans-serif;\n          font-size: 14px;\n          vertical-align: top;\n      }\n\n      /* -------------------------------------\n          BODY & CONTAINER\n      ------------------------------------- */\n\n      .body {\n        background-color: #f6f6f6;\n        width: 100%;\n      }\n\n      /* Set a max-width, and make it display as block so it will automatically stretch to that width, but will also shrink down on a phone or something */\n      .container {\n        display: block;\n        margin: 0 auto !important;\n        /* makes it centered */\n        max-width: 580px;\n        padding: 10px;\n        width: 580px;\n      }\n\n      /* This should also be a block element, so that it will fill 100% of the .container */\n      .content {\n        box-sizing: border-box;\n        display: block;\n        margin: 0 auto;\n        max-width: 580px;\n        padding: 10px;\n      }\n\n      /* -------------------------------------\n          HEADER, FOOTER, MAIN\n      ------------------------------------- */\n      .main {\n        background: #ffffff;\n        border-radius: 3px;\n        width: 100%;\n      }\n\n      .wrapper {\n        box-sizing: border-box;\n        padding: 20px;\n      }\n\n      .content-block {\n        padding-bottom: 10px;\n        padding-top: 10px;\n      }\n\n      .footer {\n        clear: both;\n        margin-top: 10px;\n        text-align: center;\n        width: 100%;\n      }\n        .footer td,\n        .footer p,\n        .footer span,\n        .footer a {\n          color: #999999;\n          font-size: 12px;\n          text-align: center;\n      }\n\n      /* -------------------------------------\n          TYPOGRAPHY\n      ------------------------------------- */\n      h1,\n      h2,\n      h3,\n      h4 {\n        color: #000000;\n        font-family: sans-serif;\n        font-weight: 400;\n        line-height: 1.4;\n        margin: 0;\n        margin-bottom: 30px;\n      }\n\n      h1 {\n        font-size: 35px;\n        font-weight: 300;\n        text-align: center;\n        text-transform: capitalize;\n      }\n\n      p,\n      ul,\n      ol {\n        font-family: sans-serif;\n        font-size: 14px;\n        font-weight: normal;\n        margin: 0;\n        margin-bottom: 15px;\n      }\n        p li,\n        ul li,\n        ol li {\n          list-style-position: inside;\n          margin-left: 5px;\n      }\n\n      a {\n        color: #3498db;\n        text-decoration: underline;\n      }\n\n      /* -------------------------------------\n          BUTTONS\n      ------------------------------------- */\n      .btn {\n        box-sizing: border-box;\n        width: 100%; }\n        .btn > tbody > tr > td {\n          padding-bottom: 15px; }\n        .btn table {\n          width: auto;\n      }\n        .btn table td {\n          background-color: #ffffff;\n          border-radius: 5px;\n          text-align: center;\n      }\n        .btn a {\n          background-color: #ffffff;\n          border: solid 1px #3498db;\n          border-radius: 5px;\n          box-sizing: border-box;\n          color: #3498db;\n          cursor: pointer;\n          display: inline-block;\n          font-size: 14px;\n          font-weight: bold;\n          margin: 0;\n          padding: 12px 25px;\n          text-decoration: none;\n          text-transform: capitalize;\n      }\n\n      .btn-primary table td {\n        background-color: #3498db;\n      }\n\n      .btn-primary a {\n        background-color: #3498db;\n        border-color: #3498db;\n        color: #ffffff;\n      }\n\n      /* -------------------------------------\n          OTHER STYLES THAT MIGHT BE USEFUL\n      ------------------------------------- */\n      .last {\n        margin-bottom: 0;\n      }\n\n      .first {\n        margin-top: 0;\n      }\n\n      .align-center {\n        text-align: center;\n      }\n\n      .align-right {\n        text-align: right;\n      }\n\n      .align-left {\n        text-align: left;\n      }\n\n      .clear {\n        clear: both;\n      }\n\n      .mt0 {\n        margin-top: 0;\n      }\n\n      .mb0 {\n        margin-bottom: 0;\n      }\n\n      .preheader {\n        color: transparent;\n        display: none;\n        height: 0;\n        max-height: 0;\n        max-width: 0;\n        opacity: 0;\n        overflow: hidden;\n        mso-hide: all;\n        visibility: hidden;\n        width: 0;\n      }\n\n      .powered-by a {\n        text-decoration: none;\n      }\n\n      hr {\n        border: 0;\n        border-bottom: 1px solid #f6f6f6;\n        margin: 20px 0;\n      }\n\n      /* -------------------------------------\n          RESPONSIVE AND MOBILE FRIENDLY STYLES\n      ------------------------------------- */\n      @media only screen and (max-width: 620px) {\n        table[class=body] h1 {\n          font-size: 28px !important;\n          margin-bottom: 10px !important;\n        }\n        table[class=body] p,\n        table[class=body] ul,\n        table[class=body] ol,\n        table[class=body] td,\n        table[class=body] span,\n        table[class=body] a {\n          font-size: 16px !important;\n        }\n        table[class=body] .wrapper,\n        table[class=body] .article {\n          padding: 10px !important;\n        }\n        table[class=body] .content {\n          padding: 0 !important;\n        }\n        table[class=body] .container {\n          padding: 0 !important;\n          width: 100% !important;\n        }\n        table[class=body] .main {\n          border-left-width: 0 !important;\n          border-radius: 0 !important;\n          border-right-width: 0 !important;\n        }\n        table[class=body] .btn table {\n          width: 100% !important;\n        }\n        table[class=body] .btn a {\n          width: 100% !important;\n        }\n        table[class=body] .img-responsive {\n          height: auto !important;\n          max-width: 100% !important;\n          width: auto !important;\n        }\n      }\n\n      /* -------------------------------------\n          PRESERVE THESE STYLES IN THE HEAD\n      ------------------------------------- */\n      @media all {\n        .ExternalClass {\n          width: 100%;\n        }\n        .ExternalClass,\n        .ExternalClass p,\n        .ExternalClass span,\n        .ExternalClass font,\n        .ExternalClass td,\n        .ExternalClass div {\n          line-height: 100%;\n        }\n        .apple-link a {\n          color: inherit !important;\n          font-family: inherit !important;\n          font-size: inherit !important;\n          font-weight: inherit !important;\n          line-height: inherit !important;\n          text-decoration: none !important;\n        }\n        #MessageViewBody a {\n          color: inherit;\n          text-decoration: none;\n          font-size: inherit;\n          font-family: inherit;\n          font-weight: inherit;\n          line-height: inherit;\n        }\n        .btn-primary table td:hover {\n          background-color: #34495e !important;\n        }\n        .btn-primary a:hover {\n          background-color: #34495e !important;\n          border-color: #34495e !important;\n        }\n      }\n\n\n    </style></head><body class=\"\"><!--<span class=\"preheader\">This is preheader text. Some clients will show this text as a preview.</span>--><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"body\" role=\"presentation\"><tr><td>&nbsp;</td><td class=\"container\"><div class=\"content\"><table class=\"main\" role=\"presentation\"><tr><td class=\"wrapper\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\"><tr><td><p>Hi [[${recipient_name}]],</p><p>[[${mail_body}]]</p><p>Thanks,</br>\n                                            Team Retailer</p></td></tr></table></td></tr></table><div class=\"footer\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\"><tr><td class=\"content-block powered-by\">\n                                Powered by <a href=\"http://www.retailer.net.in/\">Retailer Inc.</a>.\n                            </td></tr></table></div></div></td><td>&nbsp;</td></tr></table></body></html>',1),(2,1.00,'2020-08-12 07:35:41',1.00,'2020-08-12 07:35:41',0,'FORGOT_PASSWORD_EMAIL_TEMPLATE','{\"mailSubject\":\"Here\'s how to reset your password.\"}','<div><p>Did you forget your password?</p><table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"btn btn-primary\"><tbody><tr><td align=\"left\"><table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tbody><tr><td><a th:href=\"@{${reset_link}}\" target=\"_blank\">Reset Password</a></td></tr></tbody></table></td></tr></tbody></table><p>This link will expire in 24 hours and can be used only once.</p><p>If you don\'t want to change your password or didn\'t request this, please ignore and delete this message.</p></div>',1),(3,1.00,'2020-08-12 07:35:41',1.00,'2020-08-12 07:35:41',0,'RESET_PASSWORD_EMAIL_TEMPLATE','{\"mailSubject\":\"Your password has been reset.\"}','<div><p>Password reset was successful!</p><table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"btn btn-primary\"><tbody><tr><td align=\"left\"><table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tbody><tr><td><a th:href=\"@{${signin_link}}\" target=\"_blank\">Sign In</a></td></tr></tbody></table></td></tr></tbody></table></div>',1),(4,1.00,'2020-08-12 07:35:41',1.00,'2020-08-12 07:35:41',0,'DEMO_REQUEST_EMAIL_TEMPLATE','{\"mailSubject\":\"We got your request, will connect you soon.\"}','<div><p>We got your request? Our representative will contact you soon.</p><p>If you didn\'t request this, please ignore and delete this message.</p></div>',1),(5,1.00,'2020-08-12 07:35:41',1.00,'2020-08-12 07:35:41',0,'APP_DEMO_REQUEST_EMAIL_TEMPLATE','{\"mailSubject\":\"We got a new demo request, need quick action .\"}','<div><p>We got a new Demo request? Need quick action on the below details.</p><p><b>Name : </b>[[${requester_name}]]</p><p><b>Conatact Email : </b>[[${requester_email}]]</p><p><b>Conatact Phone : </b>[[${requester_phone}]]</p></div>',1),(6,1.00,'2020-08-12 07:35:41',1.00,'2020-08-12 07:35:41',0,'WELCOME_ONBOARD_EMAIL_TEMPLATE','{\"mailSubject\":\"Welcome to Centram.\"}','<div><p>Welcome to <a th:href=\"@{${login_link}}\" target=\"_blank\"><b>Reatiler</b></a>.</p><p>Credentials are below</p><p>Username : <b>[[${user_name}]]</b></p><p>Password : <b>[[${password}]]</b></p><table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"btn btn-primary\"><tbody><tr><td align=\"left\"><table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tbody><tr><td><a th:href=\"@{${login_link}}\" target=\"_blank\">Sign In</a></td></tr></tbody></table></td></tr></tbody></table></div>',1);
/*!40000 ALTER TABLE `app_configuration` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `app_configuration_aud`
--

DROP TABLE IF EXISTS `app_configuration_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `app_configuration_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `configuration_key` varchar(255) NOT NULL,
  `configuration_properties` text NOT NULL,
  `configuration_value` text NOT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FKachcdrr69ddy2rht4br7yv78c` (`rev`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `app_configuration_aud`
--

LOCK TABLES `app_configuration_aud` WRITE;
/*!40000 ALTER TABLE `app_configuration_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `app_configuration_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `department`
--

DROP TABLE IF EXISTS `department`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `department` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` decimal(19,2) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `modified_by` decimal(19,2) DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `department_name_idx` (`name`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `department`
--

LOCK TABLES `department` WRITE;
/*!40000 ALTER TABLE `department` DISABLE KEYS */;
INSERT INTO `department` VALUES (1,1.00,'2021-11-02 09:58:00',1.00,'2021-11-02 09:58:00',0,'HR',1),(2,1.00,'2021-11-02 09:58:00',1.00,'2021-11-02 09:58:00',0,'IT',1);
/*!40000 ALTER TABLE `department` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `department_aud`
--

DROP TABLE IF EXISTS `department_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `department_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FKdrxjxvx2qlyxtsq8teb2fgqy8` (`rev`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `department_aud`
--

LOCK TABLES `department_aud` WRITE;
/*!40000 ALTER TABLE `department_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `department_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `form_template`
--

DROP TABLE IF EXISTS `form_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `form_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` decimal(19,2) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `modified_by` decimal(19,2) DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `form_type` varchar(255) NOT NULL,
  `group_ignore_fields` longtext NOT NULL,
  `status` int(11) DEFAULT NULL,
  `template` longtext NOT NULL,
  `template_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `form_template`
--

LOCK TABLES `form_template` WRITE;
/*!40000 ALTER TABLE `form_template` DISABLE KEYS */;
/*!40000 ALTER TABLE `form_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `form_template_aud`
--

DROP TABLE IF EXISTS `form_template_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `form_template_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `form_type` varchar(255) DEFAULT NULL,
  `group_ignore_fields` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `template` longtext,
  `template_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK3f58fmgrmpy72jrbl2mfqx7pk` (`rev`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `form_template_aud`
--

LOCK TABLES `form_template_aud` WRITE;
/*!40000 ALTER TABLE `form_template_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `form_template_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `location`
--

DROP TABLE IF EXISTS `location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `location` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` decimal(19,2) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `modified_by` decimal(19,2) DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `location_name_idx` (`name`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `location`
--

LOCK TABLES `location` WRITE;
/*!40000 ALTER TABLE `location` DISABLE KEYS */;
INSERT INTO `location` VALUES (1,1.00,'2021-11-02 09:58:00',1.00,'2021-11-02 09:58:00',0,'BANGALORE',1),(2,1.00,'2021-11-02 09:58:00',1.00,'2021-11-02 09:58:00',0,'KOLKATA',1);
/*!40000 ALTER TABLE `location` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `location_aud`
--

DROP TABLE IF EXISTS `location_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `location_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FKpg6igcpxahva6ks655c56sqst` (`rev`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `location_aud`
--

LOCK TABLES `location_aud` WRITE;
/*!40000 ALTER TABLE `location_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `location_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `media_file`
--

DROP TABLE IF EXISTS `media_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `media_file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` longblob NOT NULL,
  `entity_id` bigint(20) DEFAULT NULL,
  `entity_type` int(11) DEFAULT NULL,
  `file_name` varchar(255) NOT NULL,
  `file_type` varchar(255) NOT NULL,
  `media_type` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `media_srch_idx` (`entity_type`,`media_type`,`entity_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `media_file`
--

LOCK TABLES `media_file` WRITE;
/*!40000 ALTER TABLE `media_file` DISABLE KEYS */;
/*!40000 ALTER TABLE `media_file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `module`
--

DROP TABLE IF EXISTS `module`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `module` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `module_name_idx` (`name`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `module`
--

LOCK TABLES `module` WRITE;
/*!40000 ALTER TABLE `module` DISABLE KEYS */;
INSERT INTO `module` VALUES (1,'ONBOARDING',1),(2,'USER',1),(3,'ASSET',1);
/*!40000 ALTER TABLE `module` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `organisation`
--

DROP TABLE IF EXISTS `organisation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organisation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` decimal(19,2) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `modified_by` decimal(19,2) DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `status` int(11) DEFAULT NULL,
  `add1` varchar(255) DEFAULT NULL,
  `add2` varchar(255) NOT NULL,
  `city` varchar(255) DEFAULT NULL,
  `gstin` varchar(255) DEFAULT NULL,
  `license_end` datetime DEFAULT NULL,
  `license_start` datetime DEFAULT NULL,
  `license_type` int(11) DEFAULT NULL,
  `pan` varchar(255) DEFAULT NULL,
  `pincode` varchar(255) DEFAULT NULL,
  `tan` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `org_name_idx` (`name`)
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `organisation`
--

LOCK TABLES `organisation` WRITE;
/*!40000 ALTER TABLE `organisation` DISABLE KEYS */;
INSERT INTO `organisation` VALUES (1,NULL,NULL,1.00,'2021-11-12 16:22:53',44,'L&J Technology Solution',1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z2','2021-11-30 00:00:00','2021-11-18 00:00:00',1,'DEBPS9954W','700034','SWER78161Q');
/*!40000 ALTER TABLE `organisation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `organisation_aud`
--

DROP TABLE IF EXISTS `organisation_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organisation_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `add1` varchar(255) DEFAULT NULL,
  `add2` varchar(255) NOT NULL,
  `city` varchar(255) DEFAULT NULL,
  `gstin` varchar(255) DEFAULT NULL,
  `license_end` datetime DEFAULT NULL,
  `license_start` datetime DEFAULT NULL,
  `license_type` int(11) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `pan` varchar(255) DEFAULT NULL,
  `pincode` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `tan` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FKfb6767p3ssyday3r8vj0dh5o8` (`rev`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `organisation_aud`
--

LOCK TABLES `organisation_aud` WRITE;
/*!40000 ALTER TABLE `organisation_aud` DISABLE KEYS */;
INSERT INTO `organisation_aud` VALUES (1,13,0,'507, B.C Road','Behala','Kolkata','6576243-9328493284-09404234','2021-11-04 12:45:10','2021-11-04 12:45:10',0,'L&J Software Solution','DEBPS9954D','700034',1,'HYTT907871'),(1,14,1,'507, B.C Road','Behala','Kolkata','6576243-9328493284-09404234','2021-11-04 13:05:16','2021-11-04 06:57:34',0,'L&J Software Solution123','DEBPS9954D','700034',1,'HYTT907871'),(1,15,1,'507, B.C Road','Behala','Kolkata','6576243-9328493284-09404234','2021-11-04 13:07:36','2021-11-04 06:57:34',0,'L&J Software Solution','DEBPS9954D','700034',1,'HYTT907871'),(2,51,0,'Test1','Test2','Test3','37AADCS0472N1Z1','2021-11-12 09:09:15','2021-11-12 09:09:15',1,'Test','DEBPS9962D','Test4',1,'DEYU13455E'),(3,52,0,'Test1','Test2','Test3','37AADCS0472N1Z1','2021-11-12 09:56:50','2021-11-12 09:56:50',2,'Test','DEBPS9962D','Test4',1,'DESS11111I'),(4,53,0,'Test1','Test2','Test3','37AADCS0472N1Z1','2021-11-12 10:00:35','2021-11-12 10:00:35',2,'Test','DEBPS9962D','Test4',1,'ASDS11212D'),(5,54,0,'Test1','Test2','Test3','37AADCS0472N1Z1','2021-11-12 10:05:12','2021-11-12 10:05:12',2,'Test','DEBPS9962D','Test4',1,'DEWW78171J'),(6,55,0,'Test1','Test2','Test3','37AADCS0472N1Z1','2021-11-12 10:06:51','2021-11-12 10:06:51',2,'Test','DEBPS9962D','Test4',1,'DEEW11121S'),(7,56,0,'Test1','Test2','Kolkata','37AADCS0472N1Z1','2021-11-12 10:11:13','2021-11-12 10:11:13',2,'Sumit Samaddar','DEBPS9962D','700034',1,'DEEE12345L'),(8,57,0,'Test1','Test2','Test3','37AADCS0472N1Z1','2021-11-12 10:12:55','2021-11-12 10:12:55',2,'Test','DEBPS9962D','Test4',1,'ASAS11111K'),(9,58,0,'Test1','Test2','Test3','37AADCS0472N1Z1','2021-11-12 10:14:26','2021-11-12 10:14:26',2,'Test','DEBPS9962D','Test4',1,'ASAS11111P'),(10,60,0,'Test1','Test2','Test3','37AADCS0472N1Z1','2021-11-12 10:16:12','2021-11-12 10:16:12',0,'Test','DEBPS9962D','Test4',1,'SDSD11111L'),(1,62,1,'507, B.C Road','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 10:31:03','2021-11-04 06:57:34',0,'L&J Software Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,63,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 11:05:14',NULL,2,'L&J Technology Solution','DEBPS9954D','700034',0,'SWER78161J'),(1,64,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 11:05:46',NULL,2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,65,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 11:06:05',NULL,2,'L&J Technology Solution','DEBPS9954D','700034',0,'SWER78161J'),(1,66,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 11:10:35',NULL,2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,67,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 11:11:35',NULL,2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,68,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 11:13:29','2021-11-11 13:00:00',2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,69,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 11:13:44','2021-11-11 13:00:00',2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,70,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 11:14:40','2021-11-10 13:00:00',2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,71,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 11:15:17','2021-11-14 13:00:00',2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,72,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 11:24:19','2021-11-13 13:00:00',2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,73,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 11:25:39','2021-11-11 13:00:00',2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,74,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 11:26:29','2021-11-08 13:00:00',2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,75,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 11:27:30','2021-11-09 13:00:00',2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,76,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 11:28:40','2021-11-27 13:00:00',2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,77,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 11:32:03','2021-11-14 13:00:00',2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,78,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 11:33:35','2021-11-23 13:00:00',2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,79,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 11:37:08','2021-11-23 13:00:00',2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,80,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 11:37:44','2021-11-24 13:00:00',2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,81,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 11:38:50','2021-11-26 13:00:00',2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,82,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 11:39:44','2021-11-24 13:00:00',2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,83,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 12:10:47','2021-11-12 13:00:00',2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,84,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 12:18:32',NULL,2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,85,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 12:20:39','2021-11-12 13:00:00',2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,86,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 12:21:24','2021-11-15 13:00:00',2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,87,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 12:24:05',NULL,2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,88,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 12:25:03',NULL,2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,89,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 12:45:52',NULL,2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,90,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 12:47:01',NULL,2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,91,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 12:48:34','2021-11-09 13:00:00',2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,92,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 12:49:35',NULL,2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,93,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 12:50:55',NULL,2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,94,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 12:52:49','2021-11-09 13:00:00',2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,95,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-12 14:38:00','2021-11-09 18:30:00',2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,96,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-12-12 00:00:00','2021-11-24 00:00:00',2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,97,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-12-11 00:00:00','2021-11-14 00:00:00',2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,98,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-28 00:00:00','2021-11-24 00:00:00',2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,99,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z1','2021-11-28 00:00:00','2021-11-24 00:00:00',2,'L&J Technology Solution','DEBPS9954D','700034',1,'SWER78161J'),(1,100,1,'507, B.C Road, Kolkata1','Behala1','Kolkata1','37AADCS0472N1Z2','2021-11-30 00:00:00','2021-11-18 00:00:00',1,'L&J Technology Solution1','DEBPS9954W','7000341',1,'SWER78161Q'),(1,101,1,'507, B.C Road, Kolkata','Behala','Kolkata','37AADCS0472N1Z2','2021-11-30 00:00:00','2021-11-18 00:00:00',1,'L&J Technology Solution','DEBPS9954W','700034',1,'SWER78161Q');
/*!40000 ALTER TABLE `organisation_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permission`
--

DROP TABLE IF EXISTS `permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `action_id` bigint(20) DEFAULT NULL,
  `module_id` bigint(20) DEFAULT NULL,
  `role_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `perm_role_idx` (`role_id`),
  KEY `FK31ek6djuqpqruvgku1ghsqja` (`action_id`),
  KEY `FKtnix0mh61fpm4o7cb7n3a5uj7` (`module_id`)
) ENGINE=MyISAM AUTO_INCREMENT=33 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permission`
--

LOCK TABLES `permission` WRITE;
/*!40000 ALTER TABLE `permission` DISABLE KEYS */;
INSERT INTO `permission` VALUES (1,1,1,1),(2,2,1,1),(3,3,1,1),(4,4,1,1),(5,1,2,1),(6,2,2,1),(7,3,2,1),(8,4,2,1),(9,1,3,1),(10,2,3,1),(11,3,3,1),(12,4,3,1),(13,1,1,1),(14,2,1,1),(15,3,1,1),(16,4,1,1),(17,1,2,1),(18,2,2,1),(19,3,2,1),(20,4,2,1),(21,1,3,1),(22,2,3,1),(23,3,3,1),(24,4,3,1),(25,1,2,3),(26,2,2,3),(27,3,2,3),(28,4,2,3),(29,1,3,3),(30,2,3,3),(31,3,3,3),(32,4,3,3);
/*!40000 ALTER TABLE `permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `revinfo`
--

DROP TABLE IF EXISTS `revinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `revinfo` (
  `rev` int(11) NOT NULL AUTO_INCREMENT,
  `revtstmp` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`)
) ENGINE=MyISAM AUTO_INCREMENT=102 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `revinfo`
--

LOCK TABLES `revinfo` WRITE;
/*!40000 ALTER TABLE `revinfo` DISABLE KEYS */;
INSERT INTO `revinfo` VALUES (1,1635846243259),(2,1635847080055),(3,1636021994457),(4,1636022022067),(5,1636022296677),(6,1636022329275),(7,1636022569498),(8,1636022667043),(9,1636022812724),(10,1636029310443),(11,1636029367558),(12,1636029497052),(13,1636029922926),(14,1636031115985),(15,1636031255926),(16,1636607252415),(17,1636608118249),(18,1636608139689),(19,1636608151084),(20,1636608158160),(21,1636608219078),(22,1636608230953),(23,1636608380814),(24,1636608461338),(25,1636612751281),(26,1636612861361),(27,1636612874234),(28,1636612885509),(29,1636613098506),(30,1636613110930),(31,1636613171858),(32,1636613190853),(33,1636614853896),(34,1636614932970),(35,1636614961216),(36,1636614972445),(37,1636614982192),(38,1636620073953),(39,1636626185448),(40,1636626251664),(41,1636626262558),(42,1636626280681),(43,1636626320560),(44,1636626364048),(45,1636626383950),(46,1636626391572),(47,1636626538424),(48,1636626545151),(49,1636630910206),(50,1636630915521),(51,1636708155770),(52,1636711010197),(53,1636711235035),(54,1636711511689),(55,1636711611029),(56,1636711873398),(57,1636711974970),(58,1636712066279),(59,1636712067457),(60,1636712172446),(61,1636712172615),(62,1636713063500),(63,1636715113951),(64,1636715145858),(65,1636715164916),(66,1636715435351),(67,1636715494703),(68,1636715608731),(69,1636715624155),(70,1636715679601),(71,1636715716804),(72,1636716258858),(73,1636716339154),(74,1636716388879),(75,1636716449671),(76,1636716520086),(77,1636716722705),(78,1636716815093),(79,1636717027803),(80,1636717064050),(81,1636717129909),(82,1636717184428),(83,1636719047365),(84,1636719512540),(85,1636719638627),(86,1636719684228),(87,1636719844714),(88,1636719902567),(89,1636721151959),(90,1636721220684),(91,1636721314043),(92,1636721375046),(93,1636721454609),(94,1636721569385),(95,1636727879711),(96,1636731391801),(97,1636731439227),(98,1636732499367),(99,1636733856936),(100,1636734140792),(101,1636734172821);
/*!40000 ALTER TABLE `revinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_name_idx` (`name`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'APP_ADMIN',1),(2,'BUISNESS_LEAD',1),(3,'ORG_ADMIN',1);
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` decimal(19,2) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `modified_by` decimal(19,2) DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `roles` text NOT NULL,
  `status` int(11) DEFAULT NULL,
  `organisation_id` bigint(20) DEFAULT NULL,
  `contact_no` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `employee_id` varchar(255) DEFAULT NULL,
  `project_code` varchar(255) DEFAULT NULL,
  `department_id` bigint(20) DEFAULT NULL,
  `location_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email_indx` (`email`),
  KEY `org_id_idx` (`organisation_id`),
  KEY `FKgkh2fko1e4ydv1y6vtrwdc6my` (`department_id`),
  KEY `FKneyhvoj17hax43m8dq3u7gbic` (`location_id`)
) ENGINE=MyISAM AUTO_INCREMENT=48 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,NULL,NULL,1.00,'2021-11-11 10:29:05',25,'Sumit','Samaddar','$2a$10$e/MDm2bmCHRD8.UNQlw8uurG3luAEOrYXk9c/CEPdj922e3N.B4uq','1',1,NULL,'+919883318811','babisumit@gmail.com','98798','6756677',2,2),(2,NULL,NULL,1.00,'2021-11-11 06:39:11',5,'Ramesh','Prabhu','$2a$10$q.NTOBIVt70XJWX9fEla4.PnR./a/tL9Q1EMLjgP0Lk9CdBs9jVf2','2',1,NULL,'+919999999999','ramesh.prabhu@gmail.com',NULL,NULL,NULL,NULL),(14,NULL,NULL,1.00,'2021-11-11 05:27:41',1,'Amit','Samaddar','$2a$10$DaCD2w9FjcwYILoesDYYq.09jKVGD6wckvfQncSL/8fjp2tShIRJK','1,2',1,NULL,'+918888888888','sumit4con@gmail.com','677221','PC00012',1,2);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_aud`
--

DROP TABLE IF EXISTS `user_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `roles` text,
  `status` int(11) DEFAULT NULL,
  `organisation_id` bigint(20) DEFAULT NULL,
  `contact_no` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `employee_id` varchar(255) DEFAULT NULL,
  `project_code` varchar(255) DEFAULT NULL,
  `department_id` bigint(20) DEFAULT NULL,
  `location_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK89ntto9kobwahrwxbne2nqcnr` (`rev`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_aud`
--

LOCK TABLES `user_aud` WRITE;
/*!40000 ALTER TABLE `user_aud` DISABLE KEYS */;
INSERT INTO `user_aud` VALUES (2,1,0,'Ramesh','Prabhu','$2a$10$q.NTOBIVt70XJWX9fEla4.PnR./a/tL9Q1EMLjgP0Lk9CdBs9jVf2','2',1,NULL,'','',NULL,NULL,NULL,NULL),(2,2,1,'Ramesh','Prabhu','$2a$10$q.NTOBIVt70XJWX9fEla4.PnR./a/tL9Q1EMLjgP0Lk9CdBs9jVf2','2',1,NULL,'','',NULL,NULL,NULL,NULL),(9,7,0,'Amit','Samaddar','$2a$10$yTYBSMVT2illDs2SXpRumeBj73TVv5jcC4OqlIHSPJfhhmEIaZn6q','2',1,NULL,'+91 8888888888','sumit4con@gmail.com','677221','PC0001',NULL,NULL),(10,8,0,'Amit','Samaddar','$2a$10$sp3Bsig6vDnYmxLVdZMMjuDwmCcehc9f5gRra//Ca./GZYVo/w17G','2',1,NULL,'+91 8888888888','sumit4con@gmail.com','677221','PC0001',NULL,NULL),(10,9,1,'Amit','Samaddar','$2a$10$feIIOoz5IhcfLVrv6fbL0.t.RX0c1pzxYI35grxxVplb2IwLKJzsu','2',1,NULL,'+91 8888888888','sumit4con@gmail.com','677221','PC00012',NULL,NULL),(11,10,0,'Amit','Samaddar','$2a$10$aVvZYBco0orvejc4RwWoJOXjT1MtLol9MX1hQUf9.HnvPi4KE49Cy','2',1,NULL,'+91 8888888888','sumit4con@gmail.com','677221','PC00012',NULL,NULL),(12,11,0,'Amit','Samaddar','$2a$10$juDizX7JnjluRw0BdKe8N.QhZiF9FlOWrgjDHc29/.mRNLyjm0gU6','2',1,NULL,'+91 8888888888','sumit4con@gmail.com','677221','PC00012',NULL,NULL),(13,12,0,'Amit','Samaddar','$2a$10$CEr5M2ZVaPsZz/xPNHUT8.oH2bbc2DmC4ebDzHgwB.TcePakJLZgW','2',1,NULL,'+91 8888888888','sumit4con@gmail.com','677221','PC00012',1,2),(14,13,0,'Amit','Samaddar','$2a$10$DaCD2w9FjcwYILoesDYYq.09jKVGD6wckvfQncSL/8fjp2tShIRJK','2',1,1,'+91 8888888888','sumit4con@gmail.com','677221','PC00012',1,2),(1,16,1,'Sumit','Samaddar','$2a$10$T4q/AFrwX2Rv692hJzvlRuJE8b.cTjsJ3oHWaSDEbwzrZc88Sq3HG','1',0,NULL,'+919883318811','babisumit@gmail.com',NULL,NULL,NULL,NULL),(1,17,1,'Sumit','Samaddar','$2a$10$T4q/AFrwX2Rv692hJzvlRuJE8b.cTjsJ3oHWaSDEbwzrZc88Sq3HG','1,2',1,NULL,'+919883318811','babisumit@gmail.com',NULL,NULL,NULL,NULL),(1,18,1,'Sumit','Samaddar','$2a$10$T4q/AFrwX2Rv692hJzvlRuJE8b.cTjsJ3oHWaSDEbwzrZc88Sq3HG','1,2',0,NULL,'+919883318811','babisumit@gmail.com','98798','6756677',2,2),(1,19,1,'Sumit','Samaddar','$2a$10$T4q/AFrwX2Rv692hJzvlRuJE8b.cTjsJ3oHWaSDEbwzrZc88Sq3HG','1,2',1,NULL,'+919883318811','babisumit@gmail.com','98798','6756677',2,2),(1,20,1,'Sumit','Samaddar','$2a$10$T4q/AFrwX2Rv692hJzvlRuJE8b.cTjsJ3oHWaSDEbwzrZc88Sq3HG','1,2',0,NULL,'+919883318811','babisumit@gmail.com','98798','6756677',2,2),(1,21,1,'Sumit','Samaddar','$2a$10$T4q/AFrwX2Rv692hJzvlRuJE8b.cTjsJ3oHWaSDEbwzrZc88Sq3HG','1,2',1,NULL,'+919883318811','babisumit@gmail.com','98798','6756677',2,2),(1,22,1,'Sumit','Samaddar','$2a$10$T4q/AFrwX2Rv692hJzvlRuJE8b.cTjsJ3oHWaSDEbwzrZc88Sq3HG','1,2',0,NULL,'+919883318811','babisumit@gmail.com','98798','6756677',2,2),(2,23,1,'Ramesh','Prabhu','$2a$10$q.NTOBIVt70XJWX9fEla4.PnR./a/tL9Q1EMLjgP0Lk9CdBs9jVf2','2',0,NULL,'+919999999999','ramesh.prabhu@gmail.com',NULL,NULL,NULL,NULL),(14,24,1,'Amit','Samaddar','$2a$10$DaCD2w9FjcwYILoesDYYq.09jKVGD6wckvfQncSL/8fjp2tShIRJK','1,2',1,NULL,'+918888888888','sumit4con@gmail.com','677221','PC00012',1,2),(2,25,1,'Ramesh','Prabhu','$2a$10$q.NTOBIVt70XJWX9fEla4.PnR./a/tL9Q1EMLjgP0Lk9CdBs9jVf2','2',1,NULL,'+919999999999','ramesh.prabhu@gmail.com',NULL,NULL,NULL,NULL),(1,26,1,'Sumit','Samaddar','$2a$10$T4q/AFrwX2Rv692hJzvlRuJE8b.cTjsJ3oHWaSDEbwzrZc88Sq3HG','1,2',1,NULL,'+919883318811','babisumit@gmail.com','98798','6756677',2,2),(1,27,1,'Sumit','Samaddar','$2a$10$T4q/AFrwX2Rv692hJzvlRuJE8b.cTjsJ3oHWaSDEbwzrZc88Sq3HG','1,2',0,NULL,'+919883318811','babisumit@gmail.com','98798','6756677',2,2),(1,28,1,'Sumit','Samaddar','$2a$10$T4q/AFrwX2Rv692hJzvlRuJE8b.cTjsJ3oHWaSDEbwzrZc88Sq3HG','1,2',1,NULL,'+919883318811','babisumit@gmail.com','98798','6756677',2,2),(1,29,1,'Sumit','Samaddar','$2a$10$T4q/AFrwX2Rv692hJzvlRuJE8b.cTjsJ3oHWaSDEbwzrZc88Sq3HG','1',0,NULL,'+919883318811','babisumit@gmail.com','98798','6756677',2,2),(1,30,1,'Sumit','Samaddar','$2a$10$T4q/AFrwX2Rv692hJzvlRuJE8b.cTjsJ3oHWaSDEbwzrZc88Sq3HG','1',1,NULL,'+919883318811','babisumit@gmail.com','98798','6756677',2,2),(1,31,1,'Sumit','Samaddar','$2a$10$T4q/AFrwX2Rv692hJzvlRuJE8b.cTjsJ3oHWaSDEbwzrZc88Sq3HG','1',0,NULL,'+919883318811','babisumit@gmail.com','98798','6756677',2,2),(1,32,1,'Sumit','Samaddar','$2a$10$T4q/AFrwX2Rv692hJzvlRuJE8b.cTjsJ3oHWaSDEbwzrZc88Sq3HG','1',1,NULL,'+919883318811','babisumit@gmail.com','98798','6756677',2,2),(1,33,1,'Sumit','Samaddar','$2a$10$T4q/AFrwX2Rv692hJzvlRuJE8b.cTjsJ3oHWaSDEbwzrZc88Sq3HG','1',0,NULL,'+919883318811','babisumit@gmail.com','98798','6756677',2,2),(1,34,1,'Sumit','Samaddar','$2a$10$T4q/AFrwX2Rv692hJzvlRuJE8b.cTjsJ3oHWaSDEbwzrZc88Sq3HG','1',1,NULL,'+919883318811','babisumit@gmail.com','98798','6756677',2,2),(1,35,1,'Sumit','Samaddar','$2a$10$T4q/AFrwX2Rv692hJzvlRuJE8b.cTjsJ3oHWaSDEbwzrZc88Sq3HG','1',0,NULL,'+919883318811','babisumit@gmail.com','98798','6756677',2,2),(1,36,1,'Sumit','Samaddar','$2a$10$T4q/AFrwX2Rv692hJzvlRuJE8b.cTjsJ3oHWaSDEbwzrZc88Sq3HG','1',1,NULL,'+919883318811','babisumit@gmail.com','98798','6756677',2,2),(1,37,1,'Sumit','Samaddar','$2a$10$T4q/AFrwX2Rv692hJzvlRuJE8b.cTjsJ3oHWaSDEbwzrZc88Sq3HG','1',0,NULL,'+919883318811','babisumit@gmail.com','98798','6756677',2,2),(43,38,0,'dsadas','asdsad','$2a$10$Wq6KBDoXFKnDZNbZmxgnKuNX8NyXOg0QwWQYnNomqWsVzGT3NKaRy','2',0,NULL,'+919999999999','Sumit.Samaddar@cognizant.com','','',NULL,NULL),(43,39,1,'dsadas','asdsad','$2a$10$Wq6KBDoXFKnDZNbZmxgnKuNX8NyXOg0QwWQYnNomqWsVzGT3NKaRy','2',1,NULL,'+919999999999','Sumit.Samaddar@cognizant.com','','',NULL,NULL),(43,40,1,'Jc','Samaddar','$2a$10$Wq6KBDoXFKnDZNbZmxgnKuNX8NyXOg0QwWQYnNomqWsVzGT3NKaRy','2',0,NULL,'+919999999999','Sumit.Samaddar@hmail.com','EMP000897','PJIP098',2,2),(43,41,1,'Jc','Samaddar','$2a$10$Wq6KBDoXFKnDZNbZmxgnKuNX8NyXOg0QwWQYnNomqWsVzGT3NKaRy','2',0,NULL,'+919999999999','Sumit.Samaddar@hmail.com','EMP000897','PJIP098',2,2),(43,42,1,'Jc','Samaddar','$2a$10$Wq6KBDoXFKnDZNbZmxgnKuNX8NyXOg0QwWQYnNomqWsVzGT3NKaRy','2',1,NULL,'+919999999999','Sumit.Samaddar@hmail.com','EMP000897','PJIP098',2,2),(44,43,0,'Biswajit','Bose','$2a$10$lDoSIaxRj4DbSKV77p.ZmeuvZlMY.9A80WP4yhdfrjDs3RgttMbDy','2',1,NULL,'+918888888888','biswajit@hmail.com','','',NULL,NULL),(45,44,0,'dlaskdjaskld','lkasjdkasld','$2a$10$zrc1GK.aSB7foQpQLP58NOeLhuJ692n4mCcoee.rCO8Ma3K37zhkC','2',0,NULL,'+919999999999','abc@hmail.com','','',NULL,NULL),(1,45,1,'Sumit','Samaddar','$2a$10$T4q/AFrwX2Rv692hJzvlRuJE8b.cTjsJ3oHWaSDEbwzrZc88Sq3HG','1',0,NULL,'+919883318811','babisumit@gmail.com','98798','6756677',2,2),(1,46,1,'Sumit','Samaddar','$2a$10$T4q/AFrwX2Rv692hJzvlRuJE8b.cTjsJ3oHWaSDEbwzrZc88Sq3HG','1',1,NULL,'+919883318811','babisumit@gmail.com','98798','6756677',2,2),(1,47,1,'Sumit','Samaddar','$2a$10$T4q/AFrwX2Rv692hJzvlRuJE8b.cTjsJ3oHWaSDEbwzrZc88Sq3HG','1',1,NULL,'+919883318811','babisumit@gmail.com','98798','6756677',1,2),(1,48,1,'Sumit','Samaddar','$2a$10$T4q/AFrwX2Rv692hJzvlRuJE8b.cTjsJ3oHWaSDEbwzrZc88Sq3HG','1',1,NULL,'+919883318811','babisumit@gmail.com','98798','6756677',2,2),(45,49,1,'dlaskdjaskld','lkasjdkasld','$2a$10$zrc1GK.aSB7foQpQLP58NOeLhuJ692n4mCcoee.rCO8Ma3K37zhkC','2',1,NULL,'+919999999999','abc@hmail.com','','',NULL,NULL),(45,50,1,'dlaskdjaskld','lkasjdkasld','$2a$10$zrc1GK.aSB7foQpQLP58NOeLhuJ692n4mCcoee.rCO8Ma3K37zhkC','2',0,NULL,'+919999999999','abc@hmail.com','','',NULL,NULL),(46,59,0,'Sumit','Samaddar','$2a$10$nt9X/SBsk/rOHDjrk/EIJux4x/zY6GmkQrokKTC8flaPlv0n6wCiq','3',1,9,'9882219911','babisumit@kmail.com','','',NULL,NULL),(47,61,0,'Sumit','Samaddar','$2a$10$ek0CWJ.fOYf/YGJ/bUyUMuLWHzrVG.LEPYL8zaUX6.G9PCh3COq1m','3',1,10,'9882219911','babisumit@lmail.com','EMP000897','PC233',1,2);
/*!40000 ALTER TABLE `user_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'centram'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-11-13 20:13:54
