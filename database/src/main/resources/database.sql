DROP TABLE IF EXISTS `protect`;
CREATE TABLE `protect`
(   `id` int NOT NULL AUTO_INCREMENT,
    `modified` DATETIME NOT NULL,
    `sampleId` varchar(255) NOT NULL,
    `gene` varchar(255),
    `transcript` varchar(255),
    `isCanonical` BOOLEAN,
    `event` varchar(255) NOT NULL,
    `eventIsHighDriver` BOOLEAN,
    `germline` BOOLEAN NOT NULL,
    `reported` BOOLEAN NOT NULL,
    `treatment` varchar(255) NOT NULL,
    `sourceTreatmentApproach` varchar(500),
    `treatmentApproach` varchar(500),
    `onLabel` BOOLEAN NOT NULL,
    `level` varchar(255) NOT NULL,
    `direction` varchar(255) NOT NULL,
    `source` varchar(255) NOT NULL,
    `sourceEvent` varchar(255) NOT NULL,
    `sourceUrls` varchar(2500),
    `evidenceType` varchar(50) NOT NULL,
    `rangeRank` int,
    `evidenceUrls` varchar(2500),
    PRIMARY KEY (`id`)
);
CREATE INDEX `protect_sampleId` ON `protect` (`sampleId`);