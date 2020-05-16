UPDATE `medicine_dealer`.`eventi` SET `cadenza` = 'settimanale', `intervallo` = '1', `giorni_settimana` = '67' WHERE (`id` = '3');
UPDATE `medicine_dealer`.`eventi` SET `cadenza` = 'settimanale', `intervallo` = '5', `giorni_settimana` = '24' WHERE (`id` = '4');
UPDATE `medicine_dealer`.`eventi` SET `cadenza` = 'giornaliera', `intervallo` = '1' WHERE (`id` = '1');
UPDATE `medicine_dealer`.`eventi` SET `cadenza` = 'giornaliera', `intervallo` = '3' WHERE (`id` = '2');
UPDATE `medicine_dealer`.`eventi` SET `id_porta_medicine` = '1' WHERE (`id` = '4');

INSERT INTO `medicine_dealer`.`eventi` (`id_porta_medicine`, `aic_farmaco`, `data`, `cadenza`, `intervallo`, `giorni_settimana`)
VALUES ('1', '045359027', '2020-02-11', 'settimanale', '1', '67');
INSERT INTO `medicine_dealer`.`eventi` (`id_porta_medicine`, `aic_farmaco`, `data`, `cadenza`, `intervallo`, `giorni_settimana`)
VALUES ('1', '045359027', '2020-01-13', 'settimanale', '5', '24');
INSERT INTO `medicine_dealer`.`eventi` (`id_porta_medicine`, `aic_farmaco`, `data`, `cadenza`, `intervallo`, `data_fine_intervallo`)
VALUES ('1', '045359027', '2020-01-13', 'giornaliera', '1', '2020-05-16');
INSERT INTO `medicine_dealer`.`eventi` (`id_porta_medicine`, `aic_farmaco`, `data`, `cadenza`, `intervallo`, `giorni_settimana`, `data_fine_intervallo`)
VALUES ('1', '045359027', '2020-01-13', 'settimanale', '1', '24', '2020-05-16');
INSERT INTO `medicine_dealer`.`eventi` (`id_porta_medicine`, `aic_farmaco`, `data`, `cadenza`, `intervallo`, `occorrenze_fine_intervallo`)
VALUES ('1', '045359027', '2020-01-13', 'giornaliera', '1', '5');
INSERT INTO `medicine_dealer`.`eventi` (`id_porta_medicine`, `aic_farmaco`, `data`, `cadenza`, `intervallo`, `giorni_settimana`, `occorrenze_fine_intervallo`)
VALUES ('1', '045359027', '2020-01-13', 'settimanale', '1', '24', '5');

INSERT INTO `medicine_dealer`.`orari` (`id_evento`, `ora`) VALUES ('1', '10:00');
INSERT INTO `medicine_dealer`.`orari` (`id_evento`, `ora`) VALUES ('1', '12:00');
INSERT INTO `medicine_dealer`.`orari` (`id_evento`, `ora`) VALUES ('1', '13:00');
INSERT INTO `medicine_dealer`.`orari` (`id_evento`, `ora`) VALUES ('2', '10:00');
INSERT INTO `medicine_dealer`.`orari` (`id_evento`, `ora`) VALUES ('3', '10:00');
INSERT INTO `medicine_dealer`.`orari` (`id_evento`, `ora`) VALUES ('4', '10:00');
INSERT INTO `medicine_dealer`.`orari` (`id_evento`, `ora`) VALUES ('4', '14:00');
INSERT INTO `medicine_dealer`.`orari` (`id_evento`, `ora`) VALUES ('5', '10:00');
INSERT INTO `medicine_dealer`.`orari` (`id_evento`, `ora`) VALUES ('5', '03:00');
INSERT INTO `medicine_dealer`.`orari` (`id_evento`, `ora`) VALUES ('6', '10:00');
INSERT INTO `medicine_dealer`.`orari` (`id_evento`, `ora`) VALUES ('6', '19:00');
INSERT INTO `medicine_dealer`.`orari` (`id_evento`, `ora`) VALUES ('7', '10:00');
INSERT INTO `medicine_dealer`.`orari` (`id_evento`, `ora`) VALUES ('7', '19:00');
INSERT INTO `medicine_dealer`.`orari` (`id_evento`, `ora`) VALUES ('8', '10:00');
INSERT INTO `medicine_dealer`.`orari` (`id_evento`, `ora`) VALUES ('8', '19:00');
INSERT INTO `medicine_dealer`.`orari` (`id_evento`, `ora`) VALUES ('9', '10:00');
INSERT INTO `medicine_dealer`.`orari` (`id_evento`, `ora`) VALUES ('9', '19:00');
INSERT INTO `medicine_dealer`.`orari` (`id_evento`, `ora`) VALUES ('10', '10:00');
INSERT INTO `medicine_dealer`.`orari` (`id_evento`, `ora`) VALUES ('10', '19:00');
