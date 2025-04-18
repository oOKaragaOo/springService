CREATE DATABASE  IF NOT EXISTS `commission` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `commission`;
-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: commission
-- ------------------------------------------------------
-- Server version	9.2.0

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
-- Table structure for table `artist_style_mapping`
--

DROP TABLE IF EXISTS `artist_style_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `artist_style_mapping` (
  `user_id` int NOT NULL,
  `style_id` int NOT NULL,
  PRIMARY KEY (`user_id`,`style_id`),
  KEY `style_id` (`style_id`),
  CONSTRAINT `artist_style_mapping_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `artist_style_mapping_ibfk_2` FOREIGN KEY (`style_id`) REFERENCES `artist_styles` (`style_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `artist_style_mapping`
--

LOCK TABLES `artist_style_mapping` WRITE;
/*!40000 ALTER TABLE `artist_style_mapping` DISABLE KEYS */;
/*!40000 ALTER TABLE `artist_style_mapping` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `artist_styles`
--

DROP TABLE IF EXISTS `artist_styles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `artist_styles` (
  `style_id` int NOT NULL AUTO_INCREMENT,
  `style_name` varchar(50) NOT NULL,
  PRIMARY KEY (`style_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `artist_styles`
--

LOCK TABLES `artist_styles` WRITE;
/*!40000 ALTER TABLE `artist_styles` DISABLE KEYS */;
/*!40000 ALTER TABLE `artist_styles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comments`
--

DROP TABLE IF EXISTS `comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comments` (
  `comment_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `post_id` int NOT NULL,
  `content` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`comment_id`),
  KEY `user_id` (`user_id`),
  KEY `post_id` (`post_id`),
  CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comments`
--

LOCK TABLES `comments` WRITE;
/*!40000 ALTER TABLE `comments` DISABLE KEYS */;
INSERT INTO `comments` VALUES (1,26,1,'This is a comment!','2025-04-16 18:08:25'),(2,26,1,'This is a comment 2!','2025-04-16 18:15:09'),(3,26,1,'This is a comment 3!','2025-04-16 18:26:19'),(4,26,1,'This is a comment 4!','2025-04-16 18:26:22'),(5,27,1,'This is a comment by A!','2025-04-16 18:28:43'),(6,26,1,'This is a comment!-ภถ-ภถ','2025-04-17 14:20:29');
/*!40000 ALTER TABLE `comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `commission_briefs`
--

DROP TABLE IF EXISTS `commission_briefs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `commission_briefs` (
  `brief_id` int NOT NULL AUTO_INCREMENT,
  `commission_id` int NOT NULL,
  `customer_id` int NOT NULL,
  `file_url` text NOT NULL,
  `file_type` varchar(50) DEFAULT NULL,
  `description` text,
  `uploaded_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`brief_id`),
  KEY `commission_id` (`commission_id`),
  KEY `customer_id` (`customer_id`),
  CONSTRAINT `commission_briefs_ibfk_1` FOREIGN KEY (`commission_id`) REFERENCES `commissions` (`commission_id`),
  CONSTRAINT `commission_briefs_ibfk_2` FOREIGN KEY (`customer_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commission_briefs`
--

LOCK TABLES `commission_briefs` WRITE;
/*!40000 ALTER TABLE `commission_briefs` DISABLE KEYS */;
/*!40000 ALTER TABLE `commission_briefs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `commission_files`
--

DROP TABLE IF EXISTS `commission_files`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `commission_files` (
  `file_id` int NOT NULL AUTO_INCREMENT,
  `commission_id` int NOT NULL,
  `artist_id` int NOT NULL,
  `file_url` text NOT NULL,
  `file_type` enum('sketch','progress','final') NOT NULL,
  `status` enum('pending','delivered','approved','rejected') DEFAULT 'pending',
  `uploaded_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`file_id`),
  KEY `commission_id` (`commission_id`),
  KEY `artist_id` (`artist_id`),
  CONSTRAINT `commission_files_ibfk_1` FOREIGN KEY (`commission_id`) REFERENCES `commissions` (`commission_id`),
  CONSTRAINT `commission_files_ibfk_2` FOREIGN KEY (`artist_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commission_files`
--

LOCK TABLES `commission_files` WRITE;
/*!40000 ALTER TABLE `commission_files` DISABLE KEYS */;
/*!40000 ALTER TABLE `commission_files` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `commissions`
--

DROP TABLE IF EXISTS `commissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `commissions` (
  `commission_id` int NOT NULL AUTO_INCREMENT,
  `customer_id` int NOT NULL,
  `artist_id` int NOT NULL,
  `title` varchar(100) NOT NULL,
  `description` text,
  `price` decimal(10,2) NOT NULL,
  `deadline` date DEFAULT NULL,
  `status` enum('requested','accepted','rejected','in_progress','delivered','completed','cancelled') DEFAULT 'requested',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`commission_id`),
  KEY `customer_id` (`customer_id`),
  KEY `artist_id` (`artist_id`),
  CONSTRAINT `commissions_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `commissions_ibfk_2` FOREIGN KEY (`artist_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commissions`
--

LOCK TABLES `commissions` WRITE;
/*!40000 ALTER TABLE `commissions` DISABLE KEYS */;
/*!40000 ALTER TABLE `commissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `likes`
--

DROP TABLE IF EXISTS `likes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `likes` (
  `like_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `post_id` int NOT NULL,
  `liked_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`like_id`),
  UNIQUE KEY `user_id` (`user_id`,`post_id`),
  KEY `post_id` (`post_id`),
  CONSTRAINT `likes_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `likes_ibfk_2` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `likes`
--

LOCK TABLES `likes` WRITE;
/*!40000 ALTER TABLE `likes` DISABLE KEYS */;
INSERT INTO `likes` VALUES (7,27,1,'2025-04-16 18:28:26'),(9,26,1,'2025-04-17 14:20:20'),(10,26,4,'2025-04-17 19:25:06');
/*!40000 ALTER TABLE `likes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `messages`
--

DROP TABLE IF EXISTS `messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `messages` (
  `message_id` int NOT NULL AUTO_INCREMENT,
  `commission_id` int DEFAULT NULL,
  `sender_id` int DEFAULT NULL,
  `receiver_id` int DEFAULT NULL,
  `message_text` text NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_read` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`message_id`),
  KEY `commission_id` (`commission_id`),
  KEY `sender_id` (`sender_id`),
  KEY `receiver_id` (`receiver_id`),
  CONSTRAINT `messages_ibfk_1` FOREIGN KEY (`commission_id`) REFERENCES `commissions` (`commission_id`) ON DELETE CASCADE,
  CONSTRAINT `messages_ibfk_2` FOREIGN KEY (`sender_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `messages_ibfk_3` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `messages`
--

LOCK TABLES `messages` WRITE;
/*!40000 ALTER TABLE `messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `payment_id` int NOT NULL AUTO_INCREMENT,
  `commission_id` int DEFAULT NULL,
  `customer_id` int DEFAULT NULL,
  `amount` decimal(10,2) NOT NULL,
  `payment_status` enum('pending','paid','refunded') DEFAULT 'pending',
  `payment_method` varchar(50) DEFAULT NULL,
  `transaction_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`payment_id`),
  KEY `commission_id` (`commission_id`),
  KEY `customer_id` (`customer_id`),
  CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`commission_id`) REFERENCES `commissions` (`commission_id`) ON DELETE CASCADE,
  CONSTRAINT `payments_ibfk_2` FOREIGN KEY (`customer_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `posts`
--

DROP TABLE IF EXISTS `posts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `posts` (
  `post_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `caption` text,
  `image_url` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`post_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `posts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `posts`
--

LOCK TABLES `posts` WRITE;
/*!40000 ALTER TABLE `posts` DISABLE KEYS */;
INSERT INTO `posts` VALUES (1,26,'Unauthorized Post','unauth.jpg','2025-04-16 18:04:31'),(2,26,'My First Post','https://example.com/image.jpg','2025-04-16 18:04:48'),(3,26,'Unauthorized Post าสฟหกนดฟหกด','unauth.jpg','2025-04-17 14:20:51'),(4,26,'My First Post','https://example.com/image.jpg','2025-04-17 14:20:57');
/*!40000 ALTER TABLE `posts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reports`
--

DROP TABLE IF EXISTS `reports`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reports` (
  `report_id` int NOT NULL AUTO_INCREMENT,
  `reporter_id` int DEFAULT NULL,
  `reported_user_id` int DEFAULT NULL,
  `commission_id` int DEFAULT NULL,
  `report_type` enum('Scam','Harassment','Late Delivery') NOT NULL,
  `description` text,
  `status` enum('pending','reviewed','resolved') DEFAULT 'pending',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `resolved_by_admin_id` int DEFAULT NULL,
  `resolved_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`report_id`),
  KEY `reporter_id` (`reporter_id`),
  KEY `reported_user_id` (`reported_user_id`),
  KEY `commission_id` (`commission_id`),
  KEY `resolved_by_admin_id` (`resolved_by_admin_id`),
  CONSTRAINT `reports_ibfk_1` FOREIGN KEY (`reporter_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `reports_ibfk_2` FOREIGN KEY (`reported_user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `reports_ibfk_3` FOREIGN KEY (`commission_id`) REFERENCES `commissions` (`commission_id`) ON DELETE SET NULL,
  CONSTRAINT `reports_ibfk_4` FOREIGN KEY (`resolved_by_admin_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reports`
--

LOCK TABLES `reports` WRITE;
/*!40000 ALTER TABLE `reports` DISABLE KEYS */;
/*!40000 ALTER TABLE `reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
  `review_id` int NOT NULL AUTO_INCREMENT,
  `commission_id` int DEFAULT NULL,
  `customer_id` int DEFAULT NULL,
  `artist_id` int DEFAULT NULL,
  `rating` int DEFAULT NULL,
  `comment` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`review_id`),
  KEY `commission_id` (`commission_id`),
  KEY `customer_id` (`customer_id`),
  KEY `artist_id` (`artist_id`),
  CONSTRAINT `reviews_ibfk_1` FOREIGN KEY (`commission_id`) REFERENCES `commissions` (`commission_id`) ON DELETE CASCADE,
  CONSTRAINT `reviews_ibfk_2` FOREIGN KEY (`customer_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `reviews_ibfk_3` FOREIGN KEY (`artist_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `reviews_chk_1` CHECK ((`rating` between 1 and 5))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shares`
--

DROP TABLE IF EXISTS `shares`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shares` (
  `share_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `post_id` int NOT NULL,
  `shared_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`share_id`),
  KEY `user_id` (`user_id`),
  KEY `post_id` (`post_id`),
  CONSTRAINT `shares_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `shares_ibfk_2` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shares`
--

LOCK TABLES `shares` WRITE;
/*!40000 ALTER TABLE `shares` DISABLE KEYS */;
INSERT INTO `shares` VALUES (1,26,1,'2025-04-16 18:08:32'),(2,27,1,'2025-04-16 18:28:46'),(3,26,1,'2025-04-17 14:20:36');
/*!40000 ALTER TABLE `shares` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_follows`
--

DROP TABLE IF EXISTS `user_follows`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_follows` (
  `follower_user_id` int NOT NULL,
  `following_user_id` int NOT NULL,
  `follow_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`follower_user_id`,`following_user_id`),
  KEY `following_user_id` (`following_user_id`),
  CONSTRAINT `user_follows_ibfk_1` FOREIGN KEY (`follower_user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_follows_ibfk_2` FOREIGN KEY (`following_user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_follows`
--

LOCK TABLES `user_follows` WRITE;
/*!40000 ALTER TABLE `user_follows` DISABLE KEYS */;
INSERT INTO `user_follows` VALUES (16,26,'2025-04-18 10:40:50'),(26,20,'2025-04-18 10:37:44');
/*!40000 ALTER TABLE `user_follows` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(50) NOT NULL,
  `profile_picture` text,
  `description` text,
  `commission_status` enum('open','closed') DEFAULT 'open',
  `status` enum('active','banned','pending','offline') DEFAULT 'offline',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'AliceDoe','alice1@example.com','password123','customer',NULL,'','closed','active','2025-04-14 10:31:32','2025-04-14 10:31:32'),(2,'BobSmith','bob2@example.com','password123','customer',NULL,'','closed','banned','2025-04-14 10:31:32','2025-04-16 10:05:22'),(3,'CharlieRay','charlie3@example.com','password123','customer',NULL,'','closed','active','2025-04-14 10:31:32','2025-04-14 10:31:32'),(4,'DianaHope','diana4@example.com','password123','customer',NULL,'','closed','active','2025-04-14 10:31:32','2025-04-14 10:31:32'),(5,'EthanKai','ethan5@example.com','password123','customer',NULL,'','closed','active','2025-04-14 10:31:32','2025-04-14 10:31:32'),(6,'FionaLee','fiona6@example.com','password123','customer',NULL,'','closed','active','2025-04-14 10:31:32','2025-04-14 10:31:32'),(7,'GeorgeTan','george7@example.com','password123','customer',NULL,'','closed','active','2025-04-14 10:31:32','2025-04-14 10:31:32'),(8,'HanaMoon','hana8@example.com','password123','customer',NULL,'','closed','active','2025-04-14 10:31:32','2025-04-14 10:31:32'),(9,'ArturoVega','art1@example.com','password123','artist',NULL,'I love digital painting and 3D art.','open','active','2025-04-14 10:31:32','2025-04-14 10:31:32'),(10,'BellaInk','art2@example.com','password123','artist',NULL,'Currently closed for commissions.','closed','active','2025-04-14 10:31:32','2025-04-14 10:31:32'),(11,'CatoDraw','art3@example.com','password123','artist',NULL,'Specializing in anime and game characters.','open','active','2025-04-14 10:31:32','2025-04-14 10:31:32'),(12,'AdminJohn','admin1@example.com','password123','admin',NULL,'Superuser for system management.','closed','active','2025-04-14 10:31:32','2025-04-14 10:31:32'),(13,'AdminJane','admin2@example.com','password123','admin',NULL,'Handles support and moderation.','closed','active','2025-04-14 10:31:32','2025-04-14 10:31:32'),(14,'Test User','test@example.com','$2a$10$brI9DtMqMVqo.ZWfnbxhN.G5AFbP8qzC5IXx/3j9/.76jeDUQRPfO','customer',NULL,NULL,'open','active','2025-04-15 11:04:29','2025-04-15 11:04:29'),(15,'New Name','a@a.com','$2a$10$17/ZhGlstNT6YnuPEuhL3u3D5uth3ieXR.kjZxmHle4Rt/gJL9uuK','Artist','https://cdn.midjourney.com/0c2d3cbb-0dbe-4502-88da-6ff2f43521f3/0_0.png','Updated profile description','closed','active','2025-04-15 18:11:46','2025-04-15 21:17:38'),(16,'nhoon','n@n.com','$2a$10$pqi1Dy2zKyTJZVNN27SmseyE63ClvZGf28K5fHsmA9sGwzjouarBe','Customer',NULL,NULL,'open','active','2025-04-15 18:34:34','2025-04-15 18:34:34'),(17,'b','b@b.com','$2a$10$r4vaeq/rk6LJOcxZoSXcjePNdHjRva1YmJjbA6Xy/rXgfaC61aff6','Customer',NULL,NULL,'open','active','2025-04-15 19:35:44','2025-04-15 19:35:44'),(18,'asdf','a@b.com','$2a$10$0jyNuemZTqUBxb./8rwWeuEQ4XIzMjaiDSReNxKnbGHF1ksltuSTi','Artist',NULL,NULL,'open','active','2025-04-15 20:31:48','2025-04-15 20:31:48'),(19,'Admin Updated','newadmin@example.com','$2a$10$9jgmBwZ/86fd9gQu1e/Sq.zF.BSgzqIRgFI9IsIrKVP83AYZv0wd2','admin','https://example.com/img.jpg','Admin bio','open','active','2025-04-16 10:03:44','2025-04-16 10:03:45'),(20,'Admin Updated','admin@example.com','$2a$10$pMb.dULh8PtUMiRvQhTXaeesr6CDKooAyIXh5DFSB4/gFhu8JjJku','admin','https://example.com/img.jpg','Admin bio','open','active','2025-04-16 10:05:21','2025-04-16 10:05:21'),(21,'Admin Updated','newadmiwwn@example.com','$2a$10$pUUSgppnMgZzzEodux9EF.fBzt5pAgP2KhZ7sKC6QXd8XV0fhQ4jW','admin','https://example.com/img.jpg','Admin bio','open','active','2025-04-16 10:15:35','2025-04-16 10:15:36'),(26,'Adminp','adminp@example.com','$2a$10$oSC/i6KXZzSmgJGkmIE3Hej6ST3iqHI/J85MaVdihsgXojhuArbUq','admin',NULL,NULL,'open','active','2025-04-16 18:00:57','2025-04-16 18:00:57'),(27,'myA','aaap@example.com','$2a$10$zh4z93SnBXaScCp/u7nzLup3.KyvnFtvwyU1XYv2DaY4LjXMa443G','customer',NULL,NULL,'open','active','2025-04-16 18:28:01','2025-04-16 18:28:01');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-18 18:27:06
