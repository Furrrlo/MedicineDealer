ALTER TABLE `eventi`
    CHANGE COLUMN `finito` `finito` TINYINT(1) NOT NULL DEFAULT '0' AFTER `occorrenze_fine_intervallo`;

ALTER TABLE `medicine_dealer`.`assunzioni`
    DROP FOREIGN KEY `fk_assunzione_orario`;
ALTER TABLE `medicine_dealer`.`assunzioni`
    ADD COLUMN `id_evento` INT(11) NOT NULL AFTER `id`,
    ADD COLUMN `ora` TIME NOT NULL AFTER `data`,
    DROP COLUMN `id_orario`,
    DROP INDEX `fk_assunzione_orario`,
    ADD CONSTRAINT `fk_assunzione_evento` FOREIGN KEY (`id_evento`) REFERENCES `eventi` (`id`)
        ON UPDATE CASCADE
        ON DELETE RESTRICT;
ALTER TABLE `assunzioni`
    ADD UNIQUE INDEX `id_evento_data_ora` (`id_evento`, `data`, `ora`);