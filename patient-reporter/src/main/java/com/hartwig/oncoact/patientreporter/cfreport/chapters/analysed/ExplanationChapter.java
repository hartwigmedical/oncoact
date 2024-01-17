package com.hartwig.oncoact.patientreporter.cfreport.chapters.analysed;

import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.ReportChapter;
import com.hartwig.oncoact.patientreporter.cfreport.components.TableUtil;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.UnitValue;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public class ExplanationChapter implements ReportChapter {

    @NotNull
    private final ReportResources reportResources;

    public ExplanationChapter(@NotNull final ReportResources reportResources) {
        this.reportResources = reportResources;
    }

    @NotNull
    @Override
    public String pdfTitle() {
        return Strings.EMPTY;
    }

    @NotNull
    @Override
    public String name() {
        return "Report explanation";
    }

    @Override
    public void render(@NotNull Document reportDocument) {
        Table table = new Table(UnitValue.createPercentArray(new float[] { 15, 2, 15, 2, 15, }));
        table.setWidth(contentWidth());

        table.addCell(TableUtil.createLayoutCell().add(createSectionTitle("Details on the report in general")));
        table.addCell(TableUtil.createLayoutCell());
        table.addCell(TableUtil.createLayoutCell().add(createSectionTitle("Details on the reported genomic based therapy approaches")));
        table.addCell(TableUtil.createLayoutCell());
        table.addCell(TableUtil.createLayoutCell().add(createSectionTitle("Details on the tumor observed variants")));

        table.addCell(TableUtil.createLayoutCell()
                .add(new Div().add(createParaGraphWithLinkTwo(
                        "This report is created using NovaSeq 6000 (Illumina) WGS analysis, which data is processed using Hartwig "
                                + "MedicalOncoAct® software and reporting. The OncoAct WGS specification sheet can be downloaded here:",
                        "https://www.oncoact.nl/specsheetOncoActWGS",
                        "https://www.oncoact.nl/specsheetOncoActWGS",
                        ". All activities are performed under ISO17025 accreditation (RVA, L633).")))
                .add(new Div().add(createParaGraphWithLinkTwo("The OncoAct WGS user manual can be downloaded here: ",
                        ReportResources.MANUAL,
                        ReportResources.MANUAL,
                        ".")))
                .add(new Div().add(createParaGraphWithLinkTwo(
                        "The analyses are performed using reference genome version GRCh37 (made available by the ",
                        "Genome Reference Consortium",
                        "https://www.ncbi.nlm.nih.gov/grc/human",
                        ").")))
                .add(new Div().add(createParaGraphWithLinkTwo(
                        "The genes and related gene transcripts used for reporting can be downloaded from the ",
                        "resources",
                        "https://storage.googleapis.com/hmf-public/OncoAct-Resources/latest_oncoact.zip",
                        ". In general the canonical transcripts as defined by ",
                        "https://www.ensembl.org/info/about/index.html",
                        "Ensembl are used.")))
                .add(createContentDiv(new String[] {
                        "Genomic event detection in samples with lower tumor purity is less sensitive. The likelihood of failing to detect "
                                + "potential events increases in case of a low (implied) tumor purity (< 20%).",
                        "The implied tumor purity is the percentage of tumor cells in the tumor material based on analysis of whole "
                                + "genome data." })));
        table.addCell(TableUtil.createLayoutCell());
        table.addCell(TableUtil.createLayoutCell()
                .add(createParaGraphWithLinkTwo("\nThe Clinical Knowledgebase (",
                        "CKB",
                        "https://ckbhome.jax.org/",
                        ") is used to annotate genomic events with clinical evidence. The evidence is gathered from CKB without "
                                + "further checks or interpretation. More details about CKB can be found in their ",
                        "https://ckbhome.jax.org/about/glossaryOfTerms",
                        "Glossary Of Terms."))

                .add(createParaGraphWithLinkTwo("\nThe ",
                        "iClusion database",
                        "https://www.trial-eye.com/",
                        " is used to annotate genomic events for potential clinical study eligibility. The studies are gathered "
                                + "from the iClusion database without further checks or interpretation. \n")));
        table.addCell(TableUtil.createLayoutCell());
        table.addCell(TableUtil.createLayoutCell()
                .add(createContentDiv(new String[] {
                        "The 'Read depth' indicates the raw number of reads supporting the variant versus the total number of reads on "
                                + "the mutated position." }))
                .add(createContentDiv(new String[] { "The 'Copies' field indicates the number of alleles present in the tumor on this "
                        + "particular mutated position." }))
                .add(createContentDiv(new String[] {
                        "The 'tVAF' field indicates the variant allele frequency corrected for the implied " + "tumor purity." }))
                .add(createContentDiv(new String[] {
                        "The 'Biallelic' field indicates whether the variant is present across all alleles in the tumor "
                                + "(and is including variants with loss-of-heterozygosity)." }))
                .add(createParaGraphHotspot("The 'Hotspot' field indicates whether a variant is part of the most sensitive calling "
                                + "tier used in the analyses. The tiers are determined based on different knowledge databases including ",
                        "CIViC,",
                        "https://civic.readthedocs.io/en/latest/about.html",
                        "DoCM,",
                        "http://www.docm.info/about",
                        "and",
                        " CGI.",
                        "https://www.cancergenomeinterpreter.org/about"))
                .add(createContentDiv(new String[] { "The 'Driver' field indicates the driver probability on gene level and is calculated "
                        + "using data in the Hartwig Medical Database. A variant in a gene with high driver likelihood is likely to be "
                        + "positively selected during the oncogenic process." }))
                .add(createParaGraphClinvar("The external ",
                        "ClinVar database",
                        "https://www.ncbi.nlm.nih.gov/clinvar/intro/",
                        " is used to determine the pathogenicity of observed " + "germline variants.")));

        table.addCell(TableUtil.createLayoutCell(1, 8).setHeight(30));

        table.addCell(TableUtil.createLayoutCell().add(createSectionTitle("Details on the reported tumor observed gains & losses")));
        table.addCell(TableUtil.createLayoutCell());
        table.addCell(TableUtil.createLayoutCell().add(createSectionTitle("Details on the reported tumor observed gene fusions")));
        table.addCell(TableUtil.createLayoutCell());
        table.addCell(TableUtil.createLayoutCell()
                .add(createSectionTitle("Details on the reported tumor observed homozygous / gene disruptions")));

        table.addCell(TableUtil.createLayoutCell()

                .add(createContentDiv(new String[] { "The lowest copy number value along the exonic regions of the canonical transcript is"
                        + " determined as a measure for the gene's copy number.",
                        "Copy numbers are corrected for the implied tumor purity and represent the number of copies in the tumor DNA.",
                        "Any gene with < 0.5 copies along the entire canonical transcript is reported as a full loss. Any gene where only "
                                + "a part along the canonical transcript has < 0.5 copies is reported as a partial loss. \n"
                                + "Any gene with ≥  3 times the average tumor ploidy in copies along the entire canonical transcript "
                                + "is reported as a full gain. Any gene where only a part of the canonical transcript has ≥  than 3 "
                                + "times the average tumor ploidy in copies is reported as a partial gain.", })));
        table.addCell(TableUtil.createLayoutCell());
        table.addCell(TableUtil.createLayoutCell()
                .add(createContentDiv(new String[] {
                        "The canonical, or otherwise longest transcript that is validly fused, is reported. " }))
                .add(new Div().add(createParaGraphWithLinkTwo("Reporting of fusions is restricted to a selection of known fusions and a "
                                + "selection of pre-defined fusions where one partner is promiscuous in either the 5' or 3' position. "
                                + "The full list of fusions can be downloaded from the ",
                        "resources",
                        "https://storage.googleapis.com/hmf-public/OncoAct-Resources/latest_oncoact.zip",
                        ".")))
                .add(createContentDiv(new String[] {
                        "The 'Driver' field is set to high in case the fusion is a known fusion, or a fusion where the promiscuous partner "
                                + "is fused in an exon range that is typically observed in literature.",
                        "All other fusions get assigned a low driver likelihood." })));
        table.addCell(TableUtil.createLayoutCell());
        table.addCell(TableUtil.createLayoutCell()
                .add(createContentDiv(new String[] {
                        "Genes are reported as being disrupted when their canonical transcript has been disrupted.",
                        "The range of the disruption is indicated by the intron/exon/promoter region of the break point "
                                + "and the direction the disruption faces.",
                        "The type of disruption can be INV (inversion), DEL (deletion), DUP (duplication), INS "
                                + "(insertion), SGL (single) or BND (translocation).",
                        "A gene for which no wild type exists anymore in the tumor DNA due to disruption(s) "
                                + "is reported in a separate section called 'homozygous disruptions'." })));

        // Is needed to set details on new page
        table.addCell(TableUtil.createLayoutCell(1, 5).setHeight(30));
        table.addCell(TableUtil.createLayoutCell(1, 5).setHeight(30));

        table.addCell(TableUtil.createLayoutCell().add(createSectionTitle("Details on the reported tumor observed viral insertions")));
        table.addCell(TableUtil.createLayoutCell());
        table.addCell(TableUtil.createLayoutCell().add(createSectionTitle("Details on the reported pharmacogenetics")));
        table.addCell(TableUtil.createLayoutCell());
        table.addCell(TableUtil.createLayoutCell().add(createSectionTitle("Details on the reported HLA Alleles")));

        table.addCell(TableUtil.createLayoutCell()
                .add(new Div().add(createParaGraphWithLinkTwo("The ",
                        "NCBI viral reference database",
                        "https://www.ncbi.nlm.nih.gov/home/about/",
                        " is used in the analyses to annotate and classify viral insertions. Reporting of viral "
                                + "insertions is restricted to a selection of clinically relevant viruses (HPV, MCV, HBV, EBV and HHV-8). "
                                + "Viral insertions are only reported when genomic integration of the virus in the tumor is detected or when "
                                + "the percentage of the viral genome that is covered is > 90% and the coverage of the virus genome is "
                                + "higher than the expected mean coverage of the tumor. For reporting of EBV both of the conditions should be met. "))));
        table.addCell(TableUtil.createLayoutCell());
        table.addCell(TableUtil.createLayoutCell()
                .add(new Div().add(createParaGraphPGX("The pharmacogenetic haplotypes are reported based on germline analysis. The ",
                        "PharmGKB database",
                        "https://www.pharmgkb.org/",
                        " is used to annotate the observed haplotypes. "
                                + "Details on the pharmacogenetic haplotypes and links to related treatment adjustments can be downloaded "
                                + "from the ",
                        "resources.",
                        "https://storage.googleapis.com/hmf-public/OncoAct-Resources/latest_oncoact.zip")))
                .add(createContentDiv(new String[] {
                        "The called haplotypes for a gene are the simplest combination of haplotypes that perfectly explains all of the "
                                + "observed variants for that gene. If no combination of haplotypes in the panel can perfectly explain the "
                                + "observed variants, then 'Unresolved Haplotype' is called.",
                        "Wild type is assumed when no variants are observed." })));
        table.addCell(TableUtil.createLayoutCell());
        table.addCell(TableUtil.createLayoutCell()
                .add(createContentDiv(new String[] { "HLA Class I types (HLA-A, HLA-B and HLA-C) are reported based on germline analysis, "
                        + "but also  the tumor status of each of those alleles is indicated (somatic mutations, complete loss, and/or "
                        + "allelic imbalance).\n" })

                        .add(new Div().add(createParaGraphWithLinkTwo("The",
                                " IMGT/HLA database ",
                                "https://www.ebi.ac.uk/ipd/imgt/hla/",
                                "is used as a reference set of Human MHC class I alleles. HLA typing is done to 4-digits, "
                                        + "which means it uniquely identifies a specific protein, but ignores synonymous variants "
                                        + "(6 digits) and intronic differences (8 digits).")))));

        reportDocument.add(table);
    }

    @NotNull
    private Paragraph createSectionTitle(@NotNull String sectionTitle) {
        return new Paragraph(sectionTitle).addStyle(reportResources.smallBodyHeadingStyle());
    }

    @NotNull
    private Paragraph createParaGraphWithLinkTwo(@NotNull String string1, @NotNull String string2, @NotNull String link,
            @NotNull String string3) {
        return new Paragraph(string1).addStyle(reportResources.subTextStyle())
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING)
                .add(new Text(string2).addStyle(reportResources.urlStyle()).setAction(PdfAction.createURI(link)))
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING)
                .add(new Text(string3))
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING);
    }

    @NotNull
    private Paragraph createParaGraphWithLinkTwo(@NotNull String string1, @NotNull String string2, @NotNull String link,
            @NotNull String string3, @NotNull String link1, @NotNull String string4) {
        return new Paragraph(string1).addStyle(reportResources.subTextStyle())
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING)
                .add(new Text(string2).addStyle(reportResources.urlStyle()).setAction(PdfAction.createURI(link)))
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING)
                .add(new Text(string3))
                .add(new Text(string4).addStyle(reportResources.urlStyle()).setAction(PdfAction.createURI(link1)))
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING);
    }

    @NotNull
    private Paragraph createParaGraphPGX(@NotNull String string1, @NotNull String string2, @NotNull String link1, @NotNull String string3,
            @NotNull String string4, @NotNull String link2) {
        return new Paragraph(string1).addStyle(reportResources.subTextStyle())
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING)
                .add(new Text(string2).addStyle(reportResources.urlStyle()).setAction(PdfAction.createURI(link1)))
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING)
                .add(new Text(string3).addStyle(reportResources.subTextStyle()))
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING)
                .add(new Text(string4).addStyle(reportResources.urlStyle()).setAction(PdfAction.createURI(link2)))
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING);
    }

    @NotNull
    private Paragraph createParaGraphClinvar(@NotNull String string1, @NotNull String string2, @NotNull String link1,
            @NotNull String string3) {
        return new Paragraph(string1).addStyle(reportResources.subTextStyle())
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING)
                .add(new Text(string2).addStyle(reportResources.urlStyle()).setAction(PdfAction.createURI(link1)))
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING)
                .add(new Text(string3))
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING);
    }

    @NotNull
    private Paragraph createParaGraphHotspot(@NotNull String string1, @NotNull String string2, @NotNull String link1,
            @NotNull String string3, @NotNull String link2, @NotNull String string4, @NotNull String string5, @NotNull String link3) {
        return new Paragraph(string1).addStyle(reportResources.subTextStyle())
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING)
                .add(new Text(string2).addStyle(reportResources.urlStyle()).setAction(PdfAction.createURI(link1)))
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING)
                .add(new Text(string3).addStyle(reportResources.urlStyle()).setAction(PdfAction.createURI(link2)))
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING)
                .add(new Text(string4))
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING)
                .add(new Text(string5).addStyle(reportResources.urlStyle()).setAction(PdfAction.createURI(link3)))
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING);
    }

    @NotNull
    private Div createContentDiv(@NotNull String[] contentParagraphs) {
        Div div = new Div();
        for (String s : contentParagraphs) {
            div.add(new Paragraph(s).addStyle(reportResources.smallBodyTextStyle()).setFixedLeading(ReportResources.BODY_TEXT_LEADING));
        }
        return div;
    }
}
