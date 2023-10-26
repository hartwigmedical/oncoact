# OncoAct patient reporter

The OncoAct patient reporter summarizes the key outputs from all algorithms in the Hartwig suite into a single PDF/JSON/XML file:

1. The algo depends exclusively on config and data produced by the [Hartwig platinum pipeline](https://github.com/hartwigmedical/platinum)
   and hence can always be run as final step without any additional local data or config required.
2. After the data by the [Hartwig platinum pipeline](https://github.com/hartwigmedical/platinum) is produced, the Hartwig reporting pipeline
   will be performed. This pipeline has as output the OncoAct patient reporter, and in this pipeline also the reporting tools ROSE and
   PROTECT will be performed.

## Contents

- [Inputs](#Inputs)
- [Findings](#Findings)
- [Outputs](#Outputs)
- [Version history and download links](#version-history-and-download-links)

## Inputs

The OncoAct patient reporter has 2 different flowers (failed- and analysed analysis). For both flavors the following input is needed:

- LAMA
    - This contains all tumor sample information eg. IDs, biopsy/tumor information, contract information
- SILO
    - This data contains patient related information (day of birth, initials and gender) and is only available for diagnostic patients
- The signature of the director
- The logo of Hartwig Medical Foundation
- The logo of Raa of Accreditatie (RvA) for quality purposes.

When we report on the analysed flow, the additional inputs are needed:

- The ORANGE output which contains all the relevant genomic events in the tumor sample
- The PROTECT output which contains all the clinical evidence (clinical trial and treatment options) for the patient
- The ROSE output which has generated the clinical relevant actionability summary in the Netherlands for this patient
- The CUPPA plot which visualize the molecular tissue of origin prediction of the patient
- The PURPLE circos plot visualize shows an overview of the tumor

// Resources used for generating an analysed patient report

- germline reporting
- clinical transcripts
- udi di

When we report on the failed flow, the additional inputs are needed:

## Findings

#### Genomic events

###### SNVs and (small) INDELs

###### Copy numbers

###### Homozygous disruptions

###### Fusions

###### Viral presence

###### Pharmacogenetics

###### Viral presence

###### HLA Alleles

#### Signatures

###### HRD

###### MSI

###### TMB

###### TML

###### CUPPA

## Outputs

The OncoAct patient reporter gen

## Version History and Download Links

- [8.0.0](https://github.com/hartwigmedical/oncoact/releases/tag/patient-reporter-8.0.0)
    - Major changes to datamodel in general (different packages, different classes etc)
    - Rebuild of patient reporter to only ORANGE input from the molecular pipeline (instead of many separate files of every tool)
    - Rebuild of patient reporter to directly use LAMA input as JSON file
    - Rebuild of patient reporter to use data from diagnostic silo as JSON file
    - Report lay-out updated
        - Addition/change of patient identifiers on the report
    - Failure reports lay-out updated
        - Addition/change of patient identifiers on the report
    - Adding the reporting of germline SVs without any information about the germline of the patient
    - Adding the clinical transcript annotation for a selection of genes
    - The therapy approaches based on drugs are clustered on the drug class instead mentioned all the
      drugs separately
    - Replace the status of TML with TMB with the cut-off value of 16
- All previous version the release notes are not mentioned here 