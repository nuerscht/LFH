SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE TABLE IF NOT EXISTS `product` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(250) NOT NULL,
  `price` DECIMAL(10,2) NOT NULL,
  `description` TEXT NULL DEFAULT NULL,
  `ean` BIGINT(20) NOT NULL,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  `updated_at` DATETIME NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `cart_has_product` (
  `cart_id` INT(11) NOT NULL,
  `product_id` INT(11) NOT NULL,
  `price` DECIMAL(10,2) NOT NULL,
  `amount` INT(11) NOT NULL DEFAULT 1,
  `discount` FLOAT(11) NOT NULL DEFAULT 0,
  `updated_at` DATETIME NOT NULL,
  `created_at` DATETIME NOT NULL,
  INDEX `fk_productbasket_basket1_idx` (`cart_id` ASC),
  INDEX `fk_productbasket_product1_idx` (`product_id` ASC),
  CONSTRAINT `fk_productbasket_basket1`
    FOREIGN KEY (`cart_id`)
    REFERENCES `cart` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_productbasket_product1`
    FOREIGN KEY (`product_id`)
    REFERENCES `product` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `cart` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL,
  `address_id` INT(11) NULL DEFAULT NULL,
  `status_id` VARCHAR(45) NOT NULL,
  `updated_at` DATETIME NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_basket_address1_idx` (`address_id` ASC),
  INDEX `fk_basket_user1_idx` (`user_id` ASC),
  INDEX `fk_cart_cart_status1_idx` (`status_id` ASC),
  CONSTRAINT `fk_basket_address1`
    FOREIGN KEY (`address_id`)
    REFERENCES `address` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_basket_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_cart_cart_status1`
    FOREIGN KEY (`status_id`)
    REFERENCES `cart_status` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `address` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL,
  `country_id` INT(11) NULL DEFAULT NULL,
  `firstname` VARCHAR(45) NULL DEFAULT NULL,
  `lastname` VARCHAR(45) NULL DEFAULT NULL,
  `street` VARCHAR(45) NULL DEFAULT NULL,
  `zip` VARCHAR(20) NULL DEFAULT NULL,
  `place` VARCHAR(45) NULL DEFAULT NULL,
  `phone` VARCHAR(45) NULL DEFAULT NULL,
  `email` VARCHAR(45) NULL DEFAULT NULL,
  `birthday` DATETIME NULL DEFAULT NULL,
  `is_active` TINYINT(4) NOT NULL DEFAULT '1',
  `updated_at` DATETIME NOT NULL,
  `created_at` DATETIME NOT NULL,
  `deleted` TINYINT(4) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  INDEX `fk_address_user1_idx` (`user_id` ASC),
  INDEX `fk_address_country1_idx` (`country_id` ASC),
  CONSTRAINT `fk_address_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_address_country1`
    FOREIGN KEY (`country_id`)
    REFERENCES `country` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `user` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `type_id` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `salt` VARCHAR(40) NULL DEFAULT NULL,
  `password` VARCHAR(40) NULL DEFAULT NULL,
  `is_active` TINYINT(4) NOT NULL DEFAULT 1,
  `updated_at` DATETIME NOT NULL,
  `created_at` DATETIME NOT NULL,
  `token` VARCHAR(40) NULL DEFAULT NULL,
  `deleted` TINYINT(4) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  INDEX `LOGIN` (`email` ASC, `password` ASC),
  INDEX `fk_user_user_type1_idx` (`type_id` ASC),
  CONSTRAINT `fk_user_user_type1`
    FOREIGN KEY (`type_id`)
    REFERENCES `user_type` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `tag` (
  `id` VARCHAR(45) NOT NULL,
  `description` TEXT NULL DEFAULT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `tag_product` (
  `product_id` INT(11) NOT NULL,
  `tag_id` VARCHAR(45) NOT NULL,
  INDEX `fk_tagproduct_product1_idx` (`product_id` ASC),
  INDEX `fk_tagproduct_tag1_idx` (`tag_id` ASC),
  CONSTRAINT `fk_tagproduct_product1`
    FOREIGN KEY (`product_id`)
    REFERENCES `product` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_tagproduct_tag1`
    FOREIGN KEY (`tag_id`)
    REFERENCES `tag` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `rating` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `product_id` INT(11) NOT NULL,
  `user_id` INT(11) NOT NULL,
  `rate` INT(11) NOT NULL,
  `comment` TEXT NULL DEFAULT NULL,
  `updated_at` DATETIME NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_rating_product1_idx` (`product_id` ASC),
  INDEX `fk_rating_user1_idx` (`user_id` ASC),
  CONSTRAINT `fk_rating_product1`
    FOREIGN KEY (`product_id`)
    REFERENCES `product` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_rating_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `log_login` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NULL DEFAULT NULL,
  `info` TEXT NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_log_login_user1_idx` (`user_id` ASC),
  CONSTRAINT `fk_log_login_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `log_api` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NULL DEFAULT NULL,
  `request_uri` VARCHAR(255) NOT NULL,
  `params` TEXT NULL DEFAULT NULL,
  `info` TEXT NULL DEFAULT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_log_api_user1_idx` (`user_id` ASC),
  CONSTRAINT `fk_log_api_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `image` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `product_id` INT(11) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `description` VARCHAR(255) NULL DEFAULT NULL,
  `created_at` VARCHAR(45) NOT NULL,
  `extension` VARCHAR(255) NULL DEFAULT NULL,
  `data` LONGBLOB NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_image_product1_idx` (`product_id` ASC),
  CONSTRAINT `fk_image_product1`
    FOREIGN KEY (`product_id`)
    REFERENCES `product` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `user_type` (
  `id` VARCHAR(45) NOT NULL,
  `description` TEXT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `cart_status` (
  `id` VARCHAR(45) NOT NULL,
  `description` TEXT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `attribute` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `product_id` INT(11) NOT NULL,
  `value` VARCHAR(255) NOT NULL,
  `updated_at` DATETIME NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_attribute_product1_idx` (`product_id` ASC),
  CONSTRAINT `fk_attribute_product1`
    FOREIGN KEY (`product_id`)
    REFERENCES `product` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `country` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL DEFAULT NULL,
  `updated_at` DATETIME NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
