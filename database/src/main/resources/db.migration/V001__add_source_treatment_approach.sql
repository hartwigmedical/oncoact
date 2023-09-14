ALTER TABLE `protect`
    ADD COLUMN `sourceTreatmentApproach` varchar(500) AFTER `treatment`,
    ADD COLUMN `treatmentApproach` varchar(500) AFTER `sourceTreatmentApproach`,
    ADD COLUMN `rangeRank` int AFTER `evidenceType`,
    MODIFY COLUMN `evidenceUrls` varchar(2500) NULL;