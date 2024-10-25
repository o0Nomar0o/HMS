CREATE DATABASE  IF NOT EXISTS `snowy_resort` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `snowy_resort`;
-- MySQL dump 10.13  Distrib 8.0.36, for macos14 (arm64)
--
-- Host: localhost    Database: snowy_resort
-- ------------------------------------------------------
-- Server version	8.3.0

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
-- Table structure for table `audit_log`
--

DROP TABLE IF EXISTS `audit_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `action` varchar(255) NOT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `data` text,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `FK_USER_ID` (`user_id`),
  CONSTRAINT `FK_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `booking`
--

DROP TABLE IF EXISTS `booking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `booking` (
  `booking_id` int NOT NULL AUTO_INCREMENT,
  `customer_id` int NOT NULL,
  `booking_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `check_in` datetime NOT NULL,
  `check_out` datetime NOT NULL,
  `last_accepted_date` datetime NOT NULL,
  `stay_duration_night` int NOT NULL,
  `stay_duration_hour` int NOT NULL,
  PRIMARY KEY (`booking_id`),
  KEY `FK_customer_id` (`customer_id`),
  CONSTRAINT `FK_customer_id` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `booking_charges`
--

DROP TABLE IF EXISTS `booking_charges`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `booking_charges` (
  `booking_id` int NOT NULL,
  `total_room_charges` decimal(10,2) NOT NULL,
  `deposite` decimal(10,2) NOT NULL,
  `total_order_charges` decimal(10,2) NOT NULL,
  `total_booking_charges` decimal(10,2) NOT NULL,
  `remaining_amount` decimal(10,2) NOT NULL,
  PRIMARY KEY (`booking_id`),
  CONSTRAINT `FKK_booking_id` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`booking_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `booking_room_detail`
--

DROP TABLE IF EXISTS `booking_room_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `booking_room_detail` (
  `booking_id` int NOT NULL,
  `room_no` varchar(5) NOT NULL,
  `booking_status` varchar(50) NOT NULL,
  PRIMARY KEY (`booking_id`,`room_no`),
  KEY `FKK_room_no` (`room_no`),
  CONSTRAINT `FK_booking_id` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`booking_id`),
  CONSTRAINT `FKK_room_no` FOREIGN KEY (`room_no`) REFERENCES `room` (`room_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `calculate_and_insert_room_charges` AFTER INSERT ON `booking_room_detail` FOR EACH ROW BEGIN
    
    DECLARE v_room_type_id varchar(3);
    DECLARE v_price_per_night decimal;
    DECLARE v_price_per_hour decimal;
    SELECT room_type_id INTO v_room_type_id FROM room WHERE room_no = NEW.room_no;
    SELECT price_per_night into v_price_per_night FROM room_price WHERE room_type_id = v_room_type_id;
    SELECT price_per_hour into v_price_per_hour FROM room_price WHERE room_type_id = v_room_type_id;
    
    INSERT INTO room_charges (booking_id, room_no, room_type_id, room_charges)
    VALUES (NEW.Booking_ID, NEW.Room_no, v_room_type_id,
    (SELECT stay_duration_night FROM booking WHERE booking_id = NEW.booking_id) * v_price_per_night +
    (SELECT stay_duration_hour FROM booking WHERE booking_id = NEW.booking_id) * v_price_per_hour);
    
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `customer_id` int NOT NULL AUTO_INCREMENT,
  `customer_name` varchar(50) NOT NULL,
  `phone_no` varchar(25) NOT NULL,
  `id_card` varchar(50) NOT NULL,
  `email` varchar(255) NOT NULL,
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `Unique index` (`phone_no`,`id_card`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `deposite`
--

DROP TABLE IF EXISTS `deposite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `deposite` (
  `deposite_rate` float NOT NULL,
  PRIMARY KEY (`deposite_rate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `food`
--

DROP TABLE IF EXISTS `food`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `food` (
  `food_id` int NOT NULL AUTO_INCREMENT,
  `food_name` varchar(50) NOT NULL,
  `food_price` decimal(10,2) NOT NULL,
  `food_image` longblob NOT NULL,
  `category_id` int NOT NULL,
  `current_stock` int NOT NULL,
  `stock_status` varchar(30) NOT NULL,
  PRIMARY KEY (`food_id`),
  KEY `FK_category_id` (`category_id`),
  CONSTRAINT `FK_category_id` FOREIGN KEY (`category_id`) REFERENCES `food_category` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `food_category`
--

DROP TABLE IF EXISTS `food_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `food_category` (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `food_category` varchar(30) NOT NULL,
  `category_image` longblob NOT NULL,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `food_order_detail`
--

DROP TABLE IF EXISTS `food_order_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `food_order_detail` (
  `order_food_id` int NOT NULL AUTO_INCREMENT,
  `food_id` int NOT NULL,
  `food_quantity` int NOT NULL,
  `food_charges` decimal(10,2) NOT NULL,
  `booking_id` int NOT NULL,
  `room_no` varchar(5) NOT NULL,
  `order_time` datetime NOT NULL,
  PRIMARY KEY (`order_food_id`),
  KEY `FK_food_id` (`food_id`),
  KEY `FK_booking_id8` (`booking_id`),
  KEY `FK_room_id5` (`room_no`),
  CONSTRAINT `FK_booking_id8` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`booking_id`),
  CONSTRAINT `FK_food_id` FOREIGN KEY (`food_id`) REFERENCES `food` (`food_id`),
  CONSTRAINT `FK_room_id5` FOREIGN KEY (`room_no`) REFERENCES `room` (`room_no`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `calculate_food_total_charges` AFTER INSERT ON `food_order_detail` FOR EACH ROW BEGIN
  	
    UPDATE booking_charges
    SET total_order_charges = total_order_charges + NEW.food_charges,
    total_booking_charges = total_booking_charges + NEW.food_charges,
    remaining_amount = total_booking_charges - deposite
    WHERE booking_id = NEW.booking_id;
  
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `payment`
--

DROP TABLE IF EXISTS `payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment` (
  `payment_id` int NOT NULL AUTO_INCREMENT,
  `booking_id` int NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `payment_date` date NOT NULL,
  `payment_method` varchar(30) NOT NULL,
  PRIMARY KEY (`payment_id`),
  KEY `FK_booking_id9` (`booking_id`),
  CONSTRAINT `FK_booking_id9` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`booking_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `insert_monthly_after_payment_insert` AFTER INSERT ON `payment` FOR EACH ROW BEGIN
    DECLARE p_years INT;
    DECLARE p_months INT;
    DECLARE p_count INT;
    
    SELECT YEAR(NEW.payment_date) INTO p_years;
    SELECT MONTH(NEW.payment_date) INTO p_months;
    SELECT COUNT(*) INTO p_count FROM revenue_monthly
    WHERE years = p_years AND months = p_months;
    
    IF p_count = 0 THEN
    INSERT INTO revenue_monthly
    VALUES (p_years, p_months, NEW.amount);
    ELSE
    UPDATE revenue_monthly
    SET revenue = revenue + NEW.amount 
    WHERE years = p_years AND months = p_months;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `revenue_monthly`
--

DROP TABLE IF EXISTS `revenue_monthly`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `revenue_monthly` (
  `years` int NOT NULL,
  `months` int NOT NULL,
  `revenue` decimal(10,2) NOT NULL,
  PRIMARY KEY (`years`,`months`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `update_yearly_after_monthly_insert` AFTER INSERT ON `revenue_monthly` FOR EACH ROW BEGIN
	DECLARE total decimal(10,2);
    
    SELECT SUM(revenue) INTO total
    FROM revenue_monthly
    WHERE years = NEW.years;
    
    IF EXISTS (SELECT 1 FROM revenue_yearly WHERE years = NEW.years) THEN
    UPDATE revenue_yearly
    SET revenue_per_year = total
    WHERE years = NEW.years;
    ELSE
    INSERT INTO revenue_yearly
    VALUES(NEW.years, NEW.revenue);
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `update_yearly_after_monthly_update` AFTER UPDATE ON `revenue_monthly` FOR EACH ROW BEGIN
	DECLARE total decimal(10,2);
    
    SELECT SUM(revenue) INTO total
    FROM revenue_monthly
    WHERE years = NEW.years;
    
    UPDATE revenue_yearly
    SET revenue_per_year = total
    WHERE years = NEW.years;
   
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `revenue_yearly`
--

DROP TABLE IF EXISTS `revenue_yearly`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `revenue_yearly` (
  `years` int NOT NULL,
  `revenue_per_year` decimal(10,2) NOT NULL,
  PRIMARY KEY (`years`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `room`
--

DROP TABLE IF EXISTS `room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room` (
  `room_no` varchar(5) NOT NULL,
  `room_type_id` varchar(3) NOT NULL,
  `floor` int NOT NULL,
  `room_status` varchar(30) NOT NULL,
  PRIMARY KEY (`room_no`),
  KEY `FK_roomtype_id` (`room_type_id`),
  CONSTRAINT `FK_roomtype_id` FOREIGN KEY (`room_type_id`) REFERENCES `room_type` (`room_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `room_charges`
--

DROP TABLE IF EXISTS `room_charges`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room_charges` (
  `booking_id` int NOT NULL,
  `room_no` varchar(5) NOT NULL,
  `room_type_id` varchar(3) NOT NULL,
  `room_charges` decimal(10,2) NOT NULL,
  PRIMARY KEY (`booking_id`,`room_no`),
  KEY `FK_room_no2` (`room_no`),
  KEY `FK_room_type2` (`room_type_id`),
  CONSTRAINT `FK_booking_id6` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`booking_id`),
  CONSTRAINT `FK_room_no2` FOREIGN KEY (`room_no`) REFERENCES `room` (`room_no`),
  CONSTRAINT `FK_room_type2` FOREIGN KEY (`room_type_id`) REFERENCES `room_type` (`room_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `add_new_booking_charges` BEFORE INSERT ON `room_charges` FOR EACH ROW BEGIN
DECLARE mycount INT;

  SET mycount = (SELECT COUNT(*) FROM booking_charges WHERE booking_id = NEW.booking_id);

  IF mycount = 0 THEN
	INSERT INTO booking_charges(booking_id) VALUES (NEW.booking_id);
  END IF;
  
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `calculate_total_booking_charges_for_room` AFTER INSERT ON `room_charges` FOR EACH ROW BEGIN
	DECLARE p_total_room_charges decimal(10,2);
    SELECT total_room_charges INTO p_total_room_charges 	
    FROM booking_charges 
    WHERE booking_id = NEW.booking_id;
    
    UPDATE booking_charges
    SET total_room_charges = p_total_room_charges + NEW.room_charges 
    WHERE booking_id = NEW.booking_id;
    
    UPDATE booking_charges
    SET total_booking_charges = total_booking_charges - p_total_room_charges + total_room_charges 
    WHERE booking_id = NEW.booking_id;

	UPDATE booking_charges
    SET deposite = total_room_charges * 0.3
    WHERE booking_id = NEW.booking_id;
    
    UPDATE booking_charges
    SET remaining_amount = total_booking_charges - deposite
    WHERE booking_id = NEW.booking_id;

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `room_price`
--

DROP TABLE IF EXISTS `room_price`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room_price` (
  `room_type_id` varchar(3) NOT NULL,
  `price_per_night` decimal(10,2) NOT NULL,
  `price_per_hour` decimal(10,2) NOT NULL,
  PRIMARY KEY (`room_type_id`),
  CONSTRAINT `FKK_roomtype_id` FOREIGN KEY (`room_type_id`) REFERENCES `room_type` (`room_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `room_type`
--

DROP TABLE IF EXISTS `room_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room_type` (
  `room_type_id` varchar(3) NOT NULL,
  `description` text,
  PRIMARY KEY (`room_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `service`
--

DROP TABLE IF EXISTS `service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service` (
  `service_id` int NOT NULL AUTO_INCREMENT,
  `service_name` varchar(50) NOT NULL,
  `service_price` decimal(10,2) NOT NULL,
  `service_image` longblob,
  `service_description` text,
  PRIMARY KEY (`service_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `service_order_detail`
--

DROP TABLE IF EXISTS `service_order_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service_order_detail` (
  `order_service_id` int NOT NULL AUTO_INCREMENT,
  `service_id` int NOT NULL,
  `service_charges` decimal(10,2) NOT NULL,
  `booking_id` int NOT NULL,
  `room_no` varchar(5) NOT NULL,
  `order_time` datetime NOT NULL,
  PRIMARY KEY (`order_service_id`),
  KEY `FK_service_id2` (`service_id`),
  KEY `FK_booking_id7` (`booking_id`),
  KEY `FK_room_id4` (`room_no`),
  CONSTRAINT `FK_booking_id7` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`booking_id`),
  CONSTRAINT `FK_room_id4` FOREIGN KEY (`room_no`) REFERENCES `room` (`room_no`),
  CONSTRAINT `FK_service_id2` FOREIGN KEY (`service_id`) REFERENCES `service` (`service_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `calculate_service_total_charges_update` AFTER INSERT ON `service_order_detail` FOR EACH ROW BEGIN 

    UPDATE booking_charges
    SET total_order_charges = total_order_charges + 		NEW.service_charges, 
    total_booking_charges = total_booking_charges + NEW.service_charges,
    remaining_amount = total_booking_charges - deposite
    WHERE booking_id = NEW.booking_id;
    

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` varchar(255) NOT NULL,
  `user_name` varchar(50) NOT NULL,
  `privilege` varchar(20) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `phone_no` varchar(25) NOT NULL,
  `status` varchar(20) NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping events for database 'snowy_resort'
--

--
-- Dumping routines for database 'snowy_resort'
--
/*!50003 DROP PROCEDURE IF EXISTS `add_booking` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `add_booking`(IN `p_room_no` VARCHAR(5), IN `p_customer_name` VARCHAR(50), IN `p_ph_no` VARCHAR(25), IN `p_id_card` VARCHAR(50), IN `p_email` VARCHAR(25), IN `p_check_in` DATETIME, IN `p_stay_night` INT, IN `p_stay_hour` INT)
BEGIN
    DECLARE v_customer_id INT;
    DECLARE v_check_out DATETIME;
    DECLARE last_accepted_date DATETIME;
    DECLARE v_booking_id INT;
    DECLARE checkCustomerExist INT DEFAULT 0;
    DECLARE v_room_status VARCHAR(30);
    DECLARE overlap_exists INT;

    -- Calculate check-out date
    SET v_check_out = DATE_ADD(p_check_in, INTERVAL p_stay_night DAY);
    SET v_check_out = DATE_ADD(v_check_out, INTERVAL p_stay_hour HOUR);
    SET last_accepted_date = DATE_ADD(p_check_in, INTERVAL 1 DAY);

    -- Check if customer already exists by ID card
    SELECT customer_id INTO v_customer_id 
    FROM customer 
    WHERE id_card = p_id_card;

    IF v_customer_id IS NULL THEN
        -- Insert new customer if not exists
        INSERT INTO customer(customer_name, phone_no, id_card, email) 
        VALUES (p_customer_name, p_ph_no, p_id_card, p_email);
        
        -- Retrieve new customer ID
        SET v_customer_id = LAST_INSERT_ID();
    END IF;

    -- Check if there's an existing booking for this customer with the same check-in/check-out dates
    SELECT booking_id INTO v_booking_id 
    FROM booking 
    WHERE customer_id = v_customer_id 
      AND check_in = p_check_in 
      AND check_out = v_check_out;

    -- Get room status
    SELECT room_status INTO v_room_status FROM room WHERE room_no = p_room_no;

    IF v_room_status = 'Available' THEN
        IF v_booking_id IS NULL THEN
            -- No existing booking found, create a new booking
            INSERT INTO booking (customer_id, check_in, check_out, last_accepted_date, stay_duration_night, stay_duration_hour) 
            VALUES (v_customer_id, p_check_in, v_check_out, last_accepted_date, p_stay_night, p_stay_hour);

            -- Get the last booking ID
            SET v_booking_id = LAST_INSERT_ID();
        END IF;

        -- Insert into booking_room_detail
        INSERT INTO booking_room_detail (booking_id, room_no) 
        VALUES (v_booking_id, p_room_no);

        -- Update room status
        UPDATE room SET room_status = 'Booked' WHERE room_no = p_room_no;
        UPDATE booking_room_detail SET booking_status = 'Booked' WHERE booking_id = v_booking_id;

    ELSEIF v_room_status = 'Booked' OR v_room_status = 'Unavailable' THEN
        -- Check for overlapping bookings for the same room
        SELECT COUNT(*) INTO overlap_exists
        FROM booking 
        JOIN booking_room_detail ON booking.booking_id = booking_room_detail.booking_id 
        WHERE booking_room_detail.room_no = p_room_no
          AND ((p_check_in < booking.check_out) AND (v_check_out > booking.check_in));

        IF overlap_exists > 0 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = "Room is already booked during the requested time.";
        ELSE
            -- No overlap, insert into the same booking
            IF v_booking_id IS NULL THEN
                -- Create a new booking
                INSERT INTO booking (customer_id, check_in, check_out, last_accepted_date, stay_duration_night, stay_duration_hour) 
                VALUES (v_customer_id, p_check_in, v_check_out, last_accepted_date, p_stay_night, p_stay_hour);
                SET v_booking_id = LAST_INSERT_ID();
            END IF;

            -- Insert into booking_room_detail
            INSERT INTO booking_room_detail (booking_id, room_no) 
            VALUES (v_booking_id, p_room_no);
        END IF;

        -- Update room status
        UPDATE room SET room_status = 'Booked' WHERE room_no = p_room_no;
		UPDATE booking_room_detail SET booking_status = 'Booked' WHERE booking_id = v_booking_id;

    ELSE
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = "No Room Available";
    END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `add_checkIn` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `add_checkIn`(
    IN p_customer_name VARCHAR(100),  -- Customer's full name
    IN p_ph_no VARCHAR(15),           -- Customer's phone number
    IN p_id_card VARCHAR(50),         -- Customer's ID card number
    IN p_email VARCHAR(100),          -- Customer's email
    IN p_room_no VARCHAR(20),         -- Room number being booked
    IN p_check_in DATETIME,           -- Check-in date and time
    IN p_stay_night INT,              -- Number of nights to stay
    IN p_stay_hour INT                -- Additional hours to stay
)
BEGIN
    DECLARE v_customer_id INT;
    DECLARE v_check_out DATETIME;
    DECLARE last_accepted_date DATETIME;
    DECLARE v_booking_id INT;
    DECLARE checkCustomerExist INT DEFAULT 0;
    DECLARE v_room_status VARCHAR(30);
    DECLARE overlap_exists INT;

    -- Calculate check-out date
    SET v_check_out = DATE_ADD(p_check_in, INTERVAL p_stay_night DAY);
    SET v_check_out = DATE_ADD(v_check_out, INTERVAL p_stay_hour HOUR);
    SET last_accepted_date = DATE_ADD(p_check_in, INTERVAL 1 DAY);

    -- Check if customer already exists by ID card
    SELECT customer_id INTO v_customer_id 
    FROM customer 
    WHERE id_card = p_id_card;

    IF v_customer_id IS NULL THEN
        -- Insert new customer if not exists
        INSERT INTO customer(customer_name, phone_no, id_card, email) 
        VALUES (p_customer_name, p_ph_no, p_id_card, p_email);
        
        -- Retrieve new customer ID
        SET v_customer_id = LAST_INSERT_ID();
    END IF;

    -- Check if there's an existing booking for this customer with the same check-in/check-out dates
    SELECT booking_id INTO v_booking_id 
    FROM booking 
    WHERE customer_id = v_customer_id 
      AND check_in = p_check_in 
      AND check_out = v_check_out;

    -- Get room status
    SELECT room_status INTO v_room_status 
    FROM room 
    WHERE room_no = p_room_no;

    IF v_room_status = 'Available' THEN
        IF v_booking_id IS NULL THEN
            -- No existing booking found, create a new booking
            INSERT INTO booking (customer_id, check_in, check_out, last_accepted_date, stay_duration_night, stay_duration_hour) 
            VALUES (v_customer_id, p_check_in, v_check_out, last_accepted_date, p_stay_night, p_stay_hour);

            -- Get the last booking ID
            SET v_booking_id = LAST_INSERT_ID();
        END IF;

        -- Insert into booking_room_detail
        INSERT INTO booking_room_detail (booking_id, room_no) 
        VALUES (v_booking_id, p_room_no);

        -- Update room status to 'Unavailable' since it's check-in
        UPDATE room 
        SET room_status = 'Unavailable' 
        WHERE room_no = p_room_no;
        
        UPDATE booking_room_detail 
        SET booking_status = 'Arrived' 
        WHERE booking_id = v_booking_id;

    ELSEIF v_room_status = 'Unavailable' OR v_room_status = 'Occupied' THEN
        -- Check for overlapping bookings for the same room
        SELECT COUNT(*) INTO overlap_exists
        FROM booking 
        JOIN booking_room_detail 
        ON booking.booking_id = booking_room_detail.booking_id 
        WHERE booking_room_detail.room_no = p_room_no
          AND ((p_check_in < booking.check_out) AND (v_check_out > booking.check_in));

        IF overlap_exists > 0 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = "Room is already occupied during the requested time.";
        ELSE
            -- No overlap, insert into the same booking
            IF v_booking_id IS NULL THEN
                -- Create a new booking
                INSERT INTO booking (customer_id, check_in, check_out, last_accepted_date, stay_duration_night, stay_duration_hour) 
                VALUES (v_customer_id, p_check_in, v_check_out, last_accepted_date, p_stay_night, p_stay_hour);
                SET v_booking_id = LAST_INSERT_ID();
            END IF;

            -- Insert into booking_room_detail
            INSERT INTO booking_room_detail (booking_id, room_no) 
            VALUES (v_booking_id, p_room_no);
        END IF;

        -- Update room status to 'Unavailable' since it's check-in
        UPDATE room 
        SET room_status = 'Unavailable' 
        WHERE room_no = p_room_no;
        
        UPDATE booking_room_detail 
        SET booking_status = 'Arrived' 
        WHERE booking_id = v_booking_id;

    ELSE
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = "No Room Available";
    END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `add_food_order_and_update_stock` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `add_food_order_and_update_stock`(IN `roomNo` VARCHAR(5), IN `foodName` VARCHAR(50), IN `foodQuantity` INT)
BEGIN
	DECLARE bookingId INT;
    DECLARE foodId INT;
    DECLARE currentStock int;
    
    SELECT booking.booking_id INTO bookingId 
    FROM booking_room_detail JOIN booking 
    ON booking_room_detail.booking_id = booking.booking_id 
	WHERE booking_room_detail.booking_status = 'Arrived'
    AND room_no = roomNo;
    

    SELECT food_id INTO foodId FROM food WHERE food_name = foodName;
    
    IF foodId IS null THEN
    	SIGNAL SQLSTATE'45000'
        SET MESSAGE_TEXT= 'Food item not found';
    ELSE
     
        SELECT current_stock INTO currentStock
        FROM food
        WHERE food_id = foodId;

        IF currentStock >= foodQuantity THEN

            INSERT INTO food_order_detail( food_id, food_quantity, food_charges, booking_id, room_no, order_time)
            VALUES ( foodId, foodQuantity, foodQuantity * (SELECT food_price FROM food WHERE food_id = foodId), bookingId, roomNo, CURRENT_TIME);

            UPDATE food
            SET current_stock = current_stock - foodQuantity
            WHERE food_id = foodId;
        ELSE
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Not enough stock available for this food item';
        END IF;
  
    END IF;
    
 END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `add_food_stock` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `add_food_stock`(IN `foodName` VARCHAR(50), IN `foodStock` INT)
    NO SQL
BEGIN
DECLARE foodId INT;

SELECT food_id INTO foodId FROM food WHERE foodName = food_name;
IF foodId IS NOT NULL THEN
	UPDATE food
    SET current_stock = foodStock
    WHERE food_id = foodId;
    
    IF (SELECT current_stock FROM food WHERE food_id = foodId) > 10 THEN
    UPDATE food
    SET stock_status = 'Available stock' WHERE food_id = foodId;
    
    ELSEIF (SELECT current_stock FROM food WHERE food_id = foodId) > 0 AND (SELECT current_stock FROM food WHERE food_id = foodId) <= 10 THEN
    UPDATE food
    SET stock_status = 'Low stock' WHERE food_id = foodId;
    
    ELSE
    UPDATE food
    SET stock_status = 'Out of stock' WHERE food_id = foodId;
    END IF;
ELSE 
	SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = "Food name doesn't exists";
END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `add_new_food_menu` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `add_new_food_menu`(IN `food_name` VARCHAR(50), IN `food_price` DECIMAL(10,2), IN `foodImage` LONGBLOB, IN `v_food_category` TEXT, OUT `generatedId` INT)
    NO SQL
BEGIN 

DECLARE p_category_id int;
SELECT category_id INTO p_category_id FROM food_category WHERE food_category = v_food_category;

INSERT INTO food(food_name, food_price,food_image, category_id, current_stock, stock_status) VALUES (food_name, food_price, foodImage, p_category_id, 0, 'No stock added');
	SET generatedId = LAST_INSERT_ID();

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `add_new_room` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `add_new_room`(IN `p_room_no` VARCHAR(5), IN `p_room_type_id` VARCHAR(3), IN `p_floor` INT)
BEGIN 
	BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    ROLLBACK;
    RESIGNAL;
    END;
    
    START TRANSACTION;
    INSERT INTO room VALUES(p_room_no, p_room_type_id, p_floor, 'Available');
    COMMIT;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `add_new_room_type_and_price` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `add_new_room_type_and_price`(IN `p_room_type_id` VARCHAR(3), IN `p_description` TEXT, IN `p_night_price` DECIMAL(10,2), IN `p_hour_price` DECIMAL(10,2))
BEGIN 
	BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    ROLLBACK;
    RESIGNAL;
    END;
    
    START TRANSACTION;
    INSERT INTO room_type VALUES(p_room_type_id, p_description);
    
    SELECT room_type_id INTO p_room_type_id
    FROM room_type 
    WHERE `description` = p_description;
    
    IF p_room_type_id IS NOT NULL THEN
    	INSERT INTO room_price(room_type_id, price_per_night, price_per_hour)
        VALUES (p_room_type_id, p_night_price, p_hour_price);
    ELSE
    	SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = "Room type ID does't match.";
    END IF;
    COMMIT;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `add_new_service` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `add_new_service`(IN `service_name` VARCHAR(50), IN `service_price` DECIMAL(10,2), IN `service_description` TEXT)
    NO SQL
BEGIN 

INSERT INTO service(service_name, service_price, service_decription) VALUES (service_name, service_price, service_description);

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `add_service_order` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `add_service_order`(IN `roomNo` VARCHAR(5), IN `serviceName` VARCHAR(50))
BEGIN
	DECLARE bookingId INT;
    DECLARE serviceId INT;
    DECLARE servicePrice DECIMAL(10,2);
   
	SELECT booking.booking_id INTO bookingId 
    FROM booking_room_detail JOIN booking 
    ON booking_room_detail.booking_id = booking.booking_id 
	WHERE booking_room_detail.booking_status = 'Arrived'
    AND room_no = roomNo;
    

    SELECT service_id INTO serviceId FROM service WHERE service_name = serviceName;
    SELECT service_price INTO servicePrice  FROM service WHERE service_name = serviceName;
    
    IF serviceId IS null THEN
    	SIGNAL SQLSTATE'45000'
        SET MESSAGE_TEXT= 'Desired Service is still unavailable!';
    ELSE
            
    		INSERT INTO service_order_detail( service_id, service_charges, booking_id, room_no, order_time)
            VALUES ( serviceId, servicePrice, bookingId, roomNo, CURRENT_TIME);
           
    END IF;
 END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `add_user` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `add_user`(IN `v_user_name` VARCHAR(50), IN `v_privilege` VARCHAR(20), IN `v_password` VARCHAR(20), IN `v_email` VARCHAR(255), IN `v_ph_no` VARCHAR(25))
BEGIN

INSERT INTO user(user_name,privilege,password,email,phone_no) VALUES (v_user_name,v_privilege,v_password,v_email,v_ph_no);

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `cancel_booking_all_rooms` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `cancel_booking_all_rooms`(IN `in_booking_id` INT)
BEGIN
	
    DECLARE v_roomNO INT;
    DECLARE done INT DEFAULT 0;
    DECLARE roomCursor CURSOR FOR SELECT room_no FROM booking_room_detail WHERE booking_id = in_booking_id;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
    
    UPDATE booking_room_detail SET booking_status = 'Cancel' WHERE booking_id = in_booking_id;
    
    OPEN roomCursor; 
        fetch_loop:LOOP
            FETCH roomCursor INTO v_roomNO; 
            
            -- If no more rows, exit the loop
            IF done = 1 THEN
                LEAVE fetch_loop;
            END IF;

	UPDATE room SET room_status = 'Available' WHERE room_no = v_roomNo;
    
    /*
    UPDATE room_charges_table
    SET room_charges = room_charges * (SELECT deposite_rate FROM deposite)
    WHERE booking_id = in_booking_id AND room_no = v_roomNo;
    
    UPDATE booking_charges
    SET total_room_charges = (SELECT SUM(room_charges) FROM room_charges_table WHERE room_charges_table.booking_id = in_booking_id)
    WHERE booking_charges.booking_id = in_booking_id;
    
    UPDATE booking_charges
    SET total_booking_charges = total_room_charges + total_order_charges
    WHERE booking_charges.booking_id = in_booking_id;
    
    UPDATE booking_charges
    SET remaining_amount = total_booking_charges - deposite
    WHERE booking_charges.booking_id = in_booking_id;
    */
    
    END LOOP;

    -- Close the cursor after processing all rows
    CLOSE roomCursor;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `cancel_booking_manually` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `cancel_booking_manually`(IN `p_customer_name` VARCHAR(50), IN `p_room_no` VARCHAR(5))
BEGIN
	DECLARE v_customer_id INT;
    DECLARE v_booking_id INT;
    DECLARE temp_booking_id varchar(50);
    
    SELECT customer_id INTO v_customer_id
    FROM customer
    WHERE customer.customer_name = p_customer_name;
    
    SELECT booking_id INTO temp_booking_id FROM booking_room_detail 
    WHERE room_no = p_room_no AND booking_status = 'Booked' AND booking_id = (SELECT booking_id FROM booking WHERE customer_id = v_customer_id);
    
    SELECT booking_id INTO v_booking_id
    FROM booking
    WHERE booking.customer_id = v_customer_id AND booking_id = temp_booking_id;
    
    IF  v_booking_id IS NOT NULL THEN
    
    	UPDATE booking_room_detail
        SET booking_room_detail.booking_status = 'Canceled'
        WHERE booking_room_detail.booking_id = v_booking_id
        AND booking_room_detail.booking_status = 'Booked'
        AND booking_room_detail.room_no = p_room_no;
        
        UPDATE room
        SET room.room_status = 'Available'
        WHERE room.room_no = p_room_no;
     	
    /*
    UPDATE room_charges_table
    SET room_charges = room_charges * (SELECT deposite_rate FROM deposite)
    WHERE booking_id = room_charges_table.booking_id AND room_no = room_charges_table.room_no;
    
    UPDATE booking_charges
    SET total_room_charges = (SELECT SUM(room_charges) FROM room_charges_table WHERE room_charges_table.booking_id = v_booking_id)
    WHERE booking_charges.booking_id = v_booking_id;
    
    UPDATE booking_charges
    SET total_booking_charges = total_room_charges + total_order_charges
    WHERE booking_charges.booking_id = v_booking_id;
    
    UPDATE booking_charges
    SET remaining_amount = total_booking_charges - deposite
    WHERE booking_charges.booking_id = v_booking_id;
    */
    
        
    END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `change_food_price` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `change_food_price`(IN `name` VARCHAR(50), IN `price` DECIMAL(10,2))
BEGIN 
	UPDATE food 
    SET food_price = price WHERE food.food_name = name;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `change_room_price` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `change_room_price`(IN `p_decription` TEXT, IN `p_night_price` DECIMAL(10,2), IN `p_hour_price` DECIMAL(10,2))
BEGIN
	DECLARE p_room_type_id VARCHAR(3);
    
	SELECT room_type_id INTO p_room_type_id
    FROM room_type
    WHERE decription = p_decription;
    
    IF p_room_type_id is NOT NULL THEN
    
    	UPDATE room_price
    	SET price_per_night = p_night_price WHERE room_type_id = p_room_type_id;
    
    	UPDATE room_price
    	SET price_per_hour = p_hour_price WHERE room_type_id = p_room_type_id;
    
    ELSE 
    	SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = "Room Type ID doesn't match.";
    
    END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `change_room_status` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `change_room_status`(IN `p_room_no` VARCHAR(5), IN `p_room_status` VARCHAR(30))
BEGIN
	
    UPDATE room
    SET room.room_status = p_room_status
    WHERE room.room_no = p_room_no;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `change_room_type` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `change_room_type`(
    IN p_room_type_id VARCHAR(3),         -- Room type ID as input
    IN p_description TEXT,                -- New description input
    IN p_night_price DECIMAL(10,2),       -- New price per night
    IN p_hour_price DECIMAL(10,2)         -- New price per hour
)
BEGIN
    -- Check if the room_type_id exists in the room_type table
    DECLARE room_type_exists INT DEFAULT 0;

SELECT 
    COUNT(*)
INTO room_type_exists FROM
    room_type
WHERE
    room_type_id = p_room_type_id;

    -- If the room_type_id exists, update the description and prices
    IF room_type_exists > 0 THEN

        -- Update room_type description
        UPDATE room_type
        SET description = p_description
        WHERE room_type_id = p_room_type_id;

        -- Update room_price per night and per hour
UPDATE room_price 
SET 
    price_per_night = p_night_price,
    price_per_hour = p_hour_price
WHERE
    room_type_id = p_room_type_id;

    ELSE
        -- If room_type_id does not exist, raise an error
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = "Room Type ID doesn't exist.";
    END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `change_user_privilege` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `change_user_privilege`(IN `userName` VARCHAR(50), IN `old_privilege` VARCHAR(20), IN `new_privilege` VARCHAR(20))
    NO SQL
BEGIN
	DECLARE userID integer;
    DECLARE current_privilege varchar(20);
    DECLARE user_exists int DEFAULT 0;
    
	SELECT user_id INTO userID
    FROM user
    WHERE user_name = userName;
    
    SELECT user.privilege INTO current_privilege 
    FROM user
    WHERE user.user_id = userID;
    
    IF current_privilege is NOT NULL THEN
    	SET user_exists = 1;
        
        IF current_privilege = old_privilege THEN 
        	UPDATE user
        	SET privilege = new_privilege
        	WHERE user_id = userID;
        ELSE
        	SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = "Incorrect privilege!";
        END IF;
    ELSE 
    	SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = "User not found!";
    END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `change_user_pw_with_ID` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `change_user_pw_with_ID`(IN `userID` INT, IN `old_pw` VARCHAR(255), IN `new_pw` VARCHAR(255))
    NO SQL
BEGIN
    DECLARE current_pw varchar(20);
    DECLARE user_exists int DEFAULT 0;
    
    SELECT user.password INTO current_pw 
    FROM user WHERE user.user_id = userID;
    
    IF current_pw IS NOT null THEN
    	SET user_exists = 1;
        
        IF current_pw = old_pw THEN 
        	UPDATE user
        	SET password = new_pw
        	WHERE user_id = userID;
        ELSE
        	SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = "Incorrect password!";
        END IF;
    ELSE 
    	SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = "User not found!";
    END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `change_user_pw_with_name` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `change_user_pw_with_name`(IN `userName` VARCHAR(50), IN `old_pw` VARCHAR(255), IN `new_pw` VARCHAR(255))
    NO SQL
BEGIN
	DECLARE userID integer;
    DECLARE current_pw varchar(20);
    DECLARE user_exists int DEFAULT 0;
    
	SELECT user_id INTO userID
    FROM user
    WHERE user_name = userName;
    
    SELECT user.password INTO current_pw 
    FROM user WHERE user.user_id = userID;
    
    IF current_pw IS NOT null THEN
    	SET user_exists = 1;
        
        IF current_pw = old_pw THEN 
        	UPDATE user
        	SET password = new_pw
        	WHERE user_id = userID;
        ELSE
        	SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = "Incorrect password!";
        END IF;
    ELSE 
    	SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = "User not found!";
    END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `comfirm_booking_all_rooms` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `comfirm_booking_all_rooms`(IN `in_booking_id` INT)
BEGIN
	
    DECLARE v_roomNO INT;
    DECLARE done INT DEFAULT 0;
    DECLARE roomCursor CURSOR FOR SELECT room_no FROM booking_room_detail WHERE booking_id = in_booking_id;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
    
    UPDATE booking_room_detail SET booking_status = 'Arrived' WHERE booking_id = in_booking_id;
    
    OPEN roomCursor; 
        fetch_loop:LOOP
            FETCH roomCursor INTO v_roomNO; 
            
            -- If no more rows, exit the loop
            IF done = 1 THEN
                LEAVE fetch_loop;
            END IF;

	UPDATE room SET room_status = 'Unavailable' WHERE room_no = v_roomNo;
    
    END LOOP;

    -- Close the cursor after processing all rows
    CLOSE roomCursor;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `confirm_booking_manually` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `confirm_booking_manually`(IN `p_customer_name` VARCHAR(50), IN `p_room_no` VARCHAR(5))
BEGIN
	DECLARE matched INT;
    DECLARE v_customer_id INT;
    DECLARE v_booking_id INT;
    DECLARE temp_booking_id varchar(50);
    
    SELECT customer_id INTO v_customer_id
    FROM customer
    WHERE customer_name = p_customer_name;
    
    SELECT booking_id INTO temp_booking_id FROM booking_room_detail WHERE room_no = p_room_no AND booking_status = 'Booked' AND booking_id = (SELECT booking_id FROM booking WHERE customer_id = v_customer_id);
     
    SELECT booking_id INTO v_booking_id
    FROM booking
    WHERE customer_id = v_customer_id AND booking_id = temp_booking_id;
    
    SELECT COUNT(*) INTO matched
    FROM booking_room_detail
    WHERE booking_room_detail.booking_id = v_booking_id 
    AND booking_room_detail.room_no = p_room_no;
    
    IF matched = 0 THEN
    	
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'ERROR: BOOKING ID and ROOM NO are not matched.';
        
    ELSE
    	
        UPDATE booking_room_detail
        SET booking_room_detail.booking_status = 'Arrived'
        WHERE booking_room_detail.booking_id = v_booking_id
        AND booking_room_detail.booking_status = 'Booked'
        AND booking_room_detail.room_no = p_room_no;
        
        UPDATE room
        SET room.room_status = 'Unavailable'
        WHERE room.room_no = p_room_no;
        
    END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `delete_food_menu` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `delete_food_menu`(IN `foodName` VARCHAR(50))
BEGIN
   
	UPDATE food  SET stock_status = 'NIL' WHERE food.food_name = foodName;
    
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `perform_payment` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `perform_payment`(IN `booking_id` VARCHAR(30), IN `amount` DECIMAL(10,2), IN `payment_method` VARCHAR(30))
BEGIN

	INSERT INTO payment(booking_id, amount, payment_date, payment_method) VALUES(booking_id, amount, CURRENT_TIME, payment_method);
    
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `remove_user` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `remove_user`(IN `userName` VARCHAR(50), IN `Privilege` VARCHAR(20))
BEGIN
	DECLARE userID integer;
    DECLARE current_privilege varchar(20);
    DECLARE user_exists int DEFAULT 0;
    
	SELECT user_id INTO userID
    FROM user
    WHERE user_name = userName;
    
    SELECT user.privilege INTO current_privilege 
    FROM user
    WHERE user.user_id = userID;
    
    IF current_privilege is NOT NULL THEN
    	SET user_exists = 1;
        IF current_privilege = Privilege THEN 
        	DELETE FROM user WHERE user_id = userID; 
        ELSE
        	SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = "Incorrect privilege!";
        END IF;
    ELSE 
    	SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = "User not found!";
    END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `search_available_room_with_date` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `search_available_room_with_date`(IN `start_date` DATE, IN `end_date` DATE)
BEGIN
	SELECT room.room_no,room_type.decription,room.floor
    FROM room JOIN room_type
    ON room.room_type_id = room_type.room_type_id
    WHERE room_no NOT IN(
        SELECT room_no FROM booking
        WHERE check_in<end_date AND check_out>start_date);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `search_booking_with_id_card` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `search_booking_with_id_card`(IN `p_id_card` VARCHAR(50))
BEGIN
	SELECT booking.booking_id, booking.room_no, room_type.decription, customer.customer_name, customer.id_card
    FROM booking 
    JOIN customer ON booking.customer_id = customer.customer_id
    JOIN room ON booking.room_no = room.room_no
    JOIN room_type ON room.room_type_id = room_type.room_type_id
    WHERE customer.id_card = p_id_card;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `search_with_customer_name` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `search_with_customer_name`(IN `p_customer_name` VARCHAR(50))
BEGIN
SELECT customer.customer_name, customer.id_card, customer.phone_no, booking_detail.room_no, booking_detail.floor FROM customer JOIN booking_detail ON customer.customer_id = booking_detail.customer_id WHERE customer.customer_name = p_customer_name;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `search_with_room_no` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `search_with_room_no`(IN `p_room_no` VARCHAR(5))
BEGIN
SELECT customer.customer_name, customer.id_card, customer.phone_no, booking_detail.room_no, booking_detail.floor FROM customer JOIN booking_detail ON customer.customer_id = booking_detail.customer_id AND booking_detail.check_in <= CURRENT_TIMESTAMP AND booking_detail.check_out >= CURRENT_TIMESTAMP WHERE booking_detail.room_no = p_room_no;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `search_with_room_type_name` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `search_with_room_type_name`(IN `p_decription` TEXT)
BEGIN
	SELECT room.room_no,room_type.decription,room.floor
	FROM room JOIN room_type
    ON room.room_type_id = room_type.room_type_id
    WHERE room_type.decription = p_decription;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `select food with category` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `select food with category`(IN `category` VARCHAR(30))
BEGIN
/*
	DECLARE id int;
    SELECT category_id into id FROM food_category WHERE food_category = category;
    SELECT food_name, food_price, food_image, current_stock FROM food WHERE category_id = id;
*/
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `show_booking_summary` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `show_booking_summary`(IN `roomType` TEXT, IN `num_rooms` INT, IN `stay_night` INT, IN `stay_hour` INT)
BEGIN
	DECLARE roomPrice_night decimal(10,2);
    DECLARE roomPrice_hour decimal(10,2);
    DECLARE totalRoomPrice decimal(10,2);
    DECLARE deposit_amt decimal(10,2);
    DECLARE d_rate float;
    
    SET d_rate = 0.3;
    
    SELECT rp.price_per_night , rp.price_per_hour INTO roomPrice_night, roomPrice_hour
    FROM room_price rp
    JOIN room_type rt ON rp.room_type_id = rt.room_type_id
    WHERE rt.decription = roomType;
    
    SET totalRoomPrice = (roomPrice_night * stay_night * num_rooms)+(roomPrice_hour * stay_hour * num_rooms);
    SET deposit_amt = totalRoomPrice * d_rate;
    
    SELECT roomType, num_rooms , totalRoomPrice, deposit_amt;
    
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `show_remaining_payment_amount` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `show_remaining_payment_amount`(IN `cusName` VARCHAR(50))
BEGIN
	DECLARE bookingID int;
    DECLARE cusID int;
    DECLARE roomCharges DECIMAL(10,2);
    
    SELECT customer_id INTO cusID
    FROM customer WHERE customer.customer_name = cusName;
    
    SELECT booking_id INTO bookingID
    FROM booking WHERE customer_id = cusID AND CURRENT_DATE BETWEEN check_in and check_out;
    
    SELECT remaining_amount INTO roomCharges
    FROM booking_charges WHERE booking_id = bookingID;
    
    SELECT bookingID , roomCharges;
	
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-10-25 12:42:57
