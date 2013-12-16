SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE SCHEMA IF NOT EXISTS `lfh_shop` DEFAULT CHARACTER SET utf8 ;
USE `lfh_shop` ;

-- -----------------------------------------------------
-- Table `lfh_shop`.`product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lfh_shop`.`product` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(250) NOT NULL,
  `price` DECIMAL(10,2) NOT NULL,
  `description` TEXT NULL,
  `ean` BIGINT NOT NULL,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  `updated_at` DATETIME NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `lfh_shop`.`user_type`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lfh_shop`.`user_type` (
  `id` VARCHAR(45) NOT NULL,
  `description` TEXT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `lfh_shop`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lfh_shop`.`user` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `type_id` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `salt` VARCHAR(40) NULL,
  `password` VARCHAR(40) NULL,
  `is_active` TINYINT NOT NULL DEFAULT 1,
  `updated_at` DATETIME NOT NULL,
  `created_at` DATETIME NOT NULL,
  `token` VARCHAR(40) NULL,
  `deleted` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  INDEX `LOGIN` (`email` ASC, `password` ASC),
  INDEX `fk_user_user_type1_idx` (`type_id` ASC),
  CONSTRAINT `fk_user_user_type1`
    FOREIGN KEY (`type_id`)
    REFERENCES `lfh_shop`.`user_type` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `lfh_shop`.`country`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lfh_shop`.`country` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL DEFAULT NULL,
  `updated_at` DATETIME NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `lfh_shop`.`address`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lfh_shop`.`address` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `country_id` INT NULL,
  `firstname` VARCHAR(45) NULL,
  `lastname` VARCHAR(45) NULL,
  `street` VARCHAR(45) NULL,
  `zip` VARCHAR(20) NULL,
  `place` VARCHAR(45) NULL,
  `phone` VARCHAR(45) NULL,
  `email` VARCHAR(45) NULL,
  `birthday` DATETIME NULL,
  `is_active` TINYINT NOT NULL DEFAULT '1',
  `updated_at` DATETIME NOT NULL,
  `created_at` DATETIME NOT NULL,
  `deleted` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  INDEX `fk_address_user1_idx` (`user_id` ASC),
  INDEX `fk_address_country1_idx` (`country_id` ASC),
  CONSTRAINT `fk_address_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `lfh_shop`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_address_country1`
    FOREIGN KEY (`country_id`)
    REFERENCES `lfh_shop`.`country` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `lfh_shop`.`cart_status`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lfh_shop`.`cart_status` (
  `id` VARCHAR(45) NOT NULL,
  `description` TEXT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `lfh_shop`.`cart`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lfh_shop`.`cart` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `address_id` INT NULL,
  `status_id` VARCHAR(45) NOT NULL,
  `updated_at` DATETIME NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_basket_address1_idx` (`address_id` ASC),
  INDEX `fk_basket_user1_idx` (`user_id` ASC),
  INDEX `fk_cart_cart_status1_idx` (`status_id` ASC),
  CONSTRAINT `fk_basket_address1`
    FOREIGN KEY (`address_id`)
    REFERENCES `lfh_shop`.`address` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_basket_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `lfh_shop`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_cart_cart_status1`
    FOREIGN KEY (`status_id`)
    REFERENCES `lfh_shop`.`cart_status` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `lfh_shop`.`cart_has_product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lfh_shop`.`cart_has_product` (
  `cart_id` INT NOT NULL,
  `product_id` INT NOT NULL,
  `price` DECIMAL(10,2) NOT NULL,
  `amount` INT NOT NULL DEFAULT 1,
  `discount` FLOAT NOT NULL DEFAULT 0,
  `updated_at` DATETIME NOT NULL,
  `created_at` DATETIME NOT NULL,
  INDEX `fk_productbasket_basket1_idx` (`cart_id` ASC),
  INDEX `fk_productbasket_product1_idx` (`product_id` ASC),
  CONSTRAINT `fk_productbasket_basket1`
    FOREIGN KEY (`cart_id`)
    REFERENCES `lfh_shop`.`cart` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_productbasket_product1`
    FOREIGN KEY (`product_id`)
    REFERENCES `lfh_shop`.`product` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `lfh_shop`.`tag`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lfh_shop`.`tag` (
  `id` VARCHAR(45) NOT NULL,
  `description` TEXT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `lfh_shop`.`tag_product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lfh_shop`.`tag_product` (
  `product_id` INT NOT NULL,
  `tag_id` VARCHAR(45) NOT NULL,
  INDEX `fk_tagproduct_product1_idx` (`product_id` ASC),
  INDEX `fk_tagproduct_tag1_idx` (`tag_id` ASC),
  CONSTRAINT `fk_tagproduct_product1`
    FOREIGN KEY (`product_id`)
    REFERENCES `lfh_shop`.`product` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_tagproduct_tag1`
    FOREIGN KEY (`tag_id`)
    REFERENCES `lfh_shop`.`tag` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `lfh_shop`.`rating`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lfh_shop`.`rating` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `product_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `rate` INT NOT NULL,
  `comment` TEXT NULL,
  `updated_at` DATETIME NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_rating_product1_idx` (`product_id` ASC),
  INDEX `fk_rating_user1_idx` (`user_id` ASC),
  CONSTRAINT `fk_rating_product1`
    FOREIGN KEY (`product_id`)
    REFERENCES `lfh_shop`.`product` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_rating_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `lfh_shop`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `lfh_shop`.`log_login`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lfh_shop`.`log_login` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NULL,
  `info` TEXT NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_log_login_user1_idx` (`user_id` ASC),
  CONSTRAINT `fk_log_login_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `lfh_shop`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `lfh_shop`.`log_api`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lfh_shop`.`log_api` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NULL,
  `request_uri` VARCHAR(255) NOT NULL,
  `params` TEXT NULL,
  `info` TEXT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_log_api_user1_idx` (`user_id` ASC),
  CONSTRAINT `fk_log_api_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `lfh_shop`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `lfh_shop`.`image`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lfh_shop`.`image` (
  `id` INT NOT NULL,
  `product_id` INT NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `description` VARCHAR(255) NULL,
  `created_at` VARCHAR(45) NOT NULL,
  `extension` VARCHAR(255) NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_image_product1_idx` (`product_id` ASC),
  CONSTRAINT `fk_image_product1`
    FOREIGN KEY (`product_id`)
    REFERENCES `lfh_shop`.`product` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `lfh_shop`.`attribute`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lfh_shop`.`attribute` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `product_id` INT NOT NULL,
  `value` VARCHAR(255) NOT NULL,
  `updated_at` DATETIME NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_attribute_product1_idx` (`product_id` ASC),
  CONSTRAINT `fk_attribute_product1`
    FOREIGN KEY (`product_id`)
    REFERENCES `lfh_shop`.`product` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
