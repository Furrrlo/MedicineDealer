ALTER TABLE `medicine_dealer`.`eventi`
    DROP FOREIGN KEY `fk_eventi_assunzione`;
ALTER TABLE `medicine_dealer`.`eventi`
    DROP COLUMN `id_assunzione`,
    DROP COLUMN `ora`,
    CHANGE COLUMN `id_porta_medicine` `id_porta_medicine` INT(11) NOT NULL AFTER `id`,
    CHANGE COLUMN `aic_farmaco` `aic_farmaco` INT(9) UNSIGNED ZEROFILL NOT NULL AFTER `id_porta_medicine`,
    ADD COLUMN `cadenza` ENUM('settimanale', 'giornaliera') NULL DEFAULT NULL AFTER `data`,
    ADD COLUMN `intervallo` INT NULL DEFAULT NULL AFTER `cadenza`,
    ADD COLUMN `giorni_settimana` TINYINT(7) UNSIGNED NULL DEFAULT NULL AFTER `intervallo`,
    ADD COLUMN `data_fine_intervallo` DATE NULL DEFAULT NULL AFTER `giorni_settimana`,
    ADD COLUMN `occorrenze_fine_intervallo` INT NULL DEFAULT NULL AFTER `data_fine_intervallo`,
    ADD COLUMN `finito` TINYINT NOT NULL DEFAULT 0 AFTER `occorrenze_fine_intervallo`,
    CHANGE COLUMN `data` `data` DATE NOT NULL AFTER `aic_farmaco`,
    DROP INDEX `fk_eventi_assunzione_idx` ;

CREATE TABLE `medicine_dealer`.`orari` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `id_evento` INT(11) NOT NULL,
    `ora` TIME NOT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_orario_evento_idx` (`id_evento`),
    CONSTRAINT `fk_orario_evento` FOREIGN KEY (`id_evento`) REFERENCES `eventi` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;;

ALTER TABLE `medicine_dealer`.`assunzioni`
    ADD COLUMN `id_orario` INT(11) NOT NULL AFTER `data`,
    ADD COLUMN `data_reale` DATE NULL AFTER `id_orario`,
    CHANGE COLUMN `ora` `ora_reale` TIME NULL ,
    ADD INDEX `fk_assunzione_orario_idx` (`id_orario` ASC) VISIBLE;
;
ALTER TABLE `medicine_dealer`.`assunzioni`
    ADD CONSTRAINT `fk_assunzione_orario`
        FOREIGN KEY (`id_orario`)
            REFERENCES `medicine_dealer`.`orari` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

