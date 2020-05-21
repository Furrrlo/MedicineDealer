
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;

UPDATE `medicine_dealer`.`eventi`
SET `aic_farmaco` = 45359
WHERE `aic_farmaco` = 045359027;

UPDATE `medicine_dealer`.`farmaci`
SET `cod_aic` = 45359, `nome` = 'ABACAVIR E LAMIVUDINA ACCORD'
WHERE `cod_aic` = 045359027;

ALTER TABLE `medicine_dealer`.`farmaci`
    CHANGE COLUMN `cod_aic` `cod_aic` INT(6) UNSIGNED ZEROFILL NOT NULL FIRST,
    CHANGE COLUMN `nome` `nome` TEXT NOT NULL COLLATE 'utf8mb4_general_ci' AFTER `cod_aic`;
ALTER TABLE `medicine_dealer`.`eventi`
    CHANGE COLUMN `aic_farmaco` `aic_farmaco` INT(6) UNSIGNED ZEROFILL NOT NULL AFTER `id_porta_medicine`;

/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;