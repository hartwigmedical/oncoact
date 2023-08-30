package com.hartwig.oncoact.patientreporter.cfreport;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import com.hartwig.oncoact.patientreporter.PatientReporterApplication;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.Style;

import org.jetbrains.annotations.NotNull;

public final class ReportResources {

    private static final String HARTWIG_NAME = "Hartwig Medical Foundation";
    public static final String HARTWIG_ADDRESS = HARTWIG_NAME + ", Science Park 408, 1098XH Amsterdam";
    public static final String CONTACT_EMAIL_GENERAL = "diagnosticssupport@hartwigmedicalfoundation.nl";
    public static final String CONTACT_EMAIL_QA = "qualitysystem@hartwigmedicalfoundation.nl";
    public static final String SIGNATURE_NAME = "Edwin Cuppen";
    public static final String SIGNATURE_TITLE = "Director " + HARTWIG_NAME;
    public static final String VERSION_REPORT = "version " + PatientReporterApplication.VERSION;
    public static final String MANUAL = "https://www.oncoact.nl/manual";

    public static final double PURITY_CUTOFF = 0.195;

    static final String METADATA_TITLE = "HMF Sequencing Report v" + PatientReporterApplication.VERSION;
    static final String METADATA_AUTHOR = HARTWIG_NAME;

    static final float PAGE_MARGIN_TOP = 150;
    // Top margin also excludes the chapter title, which is rendered in the header
    public static final float PAGE_MARGIN_LEFT = 55.5f;
    static final float PAGE_MARGIN_RIGHT = 29;
    static final float PAGE_MARGIN_BOTTOM = 62;

    public static final float CONTENT_WIDTH_NARROW = 330;
    // Width of the content on a narrow page (page with full side panel)
    public static final float CONTENT_WIDTH_WIDE = 510;
    // Width of the content on a narrow page (page without full side panel)
    public static final float CONTENT_WIDTH_WIDE_SMALL = 240;
    // Width of the content on a narrow page (page with full side panel)
    public static final float CONTENT_WIDTH_WIDE_SUMMARY_LEFT = 320;
    // Width of the content on a narrow page (page without full side panel)
    public static final float CONTENT_WIDTH_WIDE_SUMMARY_RIGHT = 170;
    // Width of the content on a narrow page (page without full side panel)

    public static final DeviceRgb PALETTE_WHITE = new DeviceRgb(255, 255, 255);
    public static final DeviceRgb PALETTE_BLACK = new DeviceRgb(0, 0, 0);
    public static final DeviceRgb PALETTE_BLUE = new DeviceRgb(38, 90, 166);
    public static final DeviceRgb PALETTE_MID_BLUE = new DeviceRgb(110, 139, 189);
    public static final DeviceRgb PALETTE_DARK_BLUE = new DeviceRgb(93, 85, 164);
    public static final DeviceRgb PALETTE_RED = new DeviceRgb(232, 60, 55);
    public static final DeviceRgb PALETTE_CYAN = new DeviceRgb(0, 179, 233);
    private static final DeviceRgb PALETTE_DARK_GREY = new DeviceRgb(39, 47, 50);
    public static final DeviceRgb PALETTE_MID_GREY = new DeviceRgb(101, 106, 108);
    public static final DeviceRgb PALETTE_LIGHT_GREY = new DeviceRgb(205, 206, 207);
    public static final DeviceRgb PALETTE_PINK = new DeviceRgb(230, 21, 124);
    public static final DeviceRgb PALETTE_VIOLET = new DeviceRgb(156, 97, 168);

    private static final String FONT_REGULAR_PATH = "fonts/nimbus-sans/NimbusSansL-Regular.ttf";
    private static final String FONT_BOLD_PATH = "fonts/nimbus-sans/NimbusSansL-Bold.ttf";
    private static final String ICON_FONT_PATH = "fonts/hmf-icons/hmf-icons.ttf";

    public static final float BODY_TEXT_LEADING = 10F;

    private final PdfFont fontRegular;
    private final PdfFont fontBold;
    private final PdfFont fontIcon;

    private ReportResources(@NotNull PdfFont fontRegular, @NotNull PdfFont fontBold, @NotNull PdfFont fontIcon) {
        this.fontRegular = fontRegular;
        this.fontBold = fontBold;
        this.fontIcon = fontIcon;
    }

    public static ReportResources create() {
        return new ReportResources(createFontFromProgram(loadFontProgram(FONT_REGULAR_PATH)),
                createFontFromProgram(loadFontProgram(FONT_BOLD_PATH)),
                createFontFromProgram(loadFontProgram(ICON_FONT_PATH)));
    }

    public static float maxPointSizeForWidth(@NotNull PdfFont font, float initialFontSize, float minFontSize, @NotNull String text,
            float maxWidth) {
        float fontIncrement = 0.1f;

        float fontSize = initialFontSize;
        float width = font.getWidth(text, initialFontSize);
        while (width > maxWidth && fontSize > minFontSize) {
            fontSize -= fontIncrement;
            width = font.getWidth(text, fontSize);
        }

        return fontSize;
    }

    @NotNull
    public static DecimalFormat decimalFormat(@NotNull String format) {
        // To make sure every decimal format uses a dot as separator rather than a comma.
        return new DecimalFormat(format, DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    }

    @NotNull
    public PdfFont fontRegular() {
        // Cannot be created statically as every PDF needs their own private font objects.
        return fontRegular;
    }

    @NotNull
    public PdfFont fontBold() {
        // Cannot be created statically as every PDF needs their own private font objects.
        return fontBold;
    }

    @NotNull
    public PdfFont iconFont() {
        // Cannot be created statically as every PDF needs their own private font objects.
        return fontIcon;
    }

    public Style responseStyle() {
        return new Style().setFont(fontBold()).setFontSize(8).setFontColor(ReportResources.PALETTE_BLUE);
    }

    public Style resistantStyle() {
        return new Style().setFont(fontBold()).setFontSize(8).setFontColor(ReportResources.PALETTE_RED);
    }

    public Style predictedStyle() {
        return new Style().setFont(fontBold()).setFontSize(8).setFontColor(ReportResources.PALETTE_VIOLET);
    }

    public Style chapterTitleStyle() {
        return new Style().setFont(fontBold()).setFontSize(16).setFontColor(ReportResources.PALETTE_BLUE).setMarginTop(0);
    }

    public Style sectionTitleStyle() {
        return new Style().setFont(fontBold()).setFontSize(11).setFontColor(ReportResources.PALETTE_BLUE);
    }

    public Style sectionSubTitleStyle() {
        return new Style().setFont(fontRegular()).setFontSize(8).setFontColor(ReportResources.PALETTE_BLUE);
    }

    public Style tableHeaderStyle() {
        return new Style().setFont(fontRegular()).setFontSize(7).setFontColor(ReportResources.PALETTE_MID_GREY);
    }

    public Style tableContentStyle() {
        return new Style().setFont(fontRegular()).setFontSize(8).setFontColor(ReportResources.PALETTE_DARK_GREY);
    }

    public Style bodyTextStyle() {
        return new Style().setFont(fontRegular()).setFontSize(8).setFontColor(ReportResources.PALETTE_BLACK);
    }

    public Style smallBodyHeadingStyle() {
        return new Style().setFont(fontBold()).setFontSize(10).setFontColor(ReportResources.PALETTE_BLACK);
    }

    public Style smallBodyHeadingDisclaimerStyle() {
        return new Style().setFont(fontBold()).setFontSize(10).setFontColor(ReportResources.PALETTE_RED);
    }

    public Style smallBodyTextStyle() {
        return new Style().setFont(fontRegular()).setFontSize(7).setFontColor(ReportResources.PALETTE_BLACK);
    }

    public Style smallBodyTextStyleRed() {
        return new Style().setFont(fontRegular()).setFontSize(7).setFontColor(ReportResources.PALETTE_RED);
    }

    public Style smallBodyBoldTextStyle() {
        return new Style().setFont(fontBold()).setFontSize(7).setFontColor(ReportResources.PALETTE_BLACK);
    }

    public Style subTextStyle() {
        return new Style().setFont(fontRegular()).setFontSize(7).setFontColor(ReportResources.PALETTE_BLACK);
    }

    public Style subTextSmallStyle() {
        return new Style().setFont(fontRegular()).setFontSize(5).setFontColor(ReportResources.PALETTE_BLACK);
    }

    public Style subTextBoldStyle() {
        return new Style().setFont(fontBold()).setFontSize(7).setFontColor(ReportResources.PALETTE_BLACK);
    }

    public Style dataHighlightStyle() {
        return new Style().setFont(fontBold()).setFontSize(11).setFontColor(ReportResources.PALETTE_BLUE);
    }

    public Style dataHighlightNaStyle() {
        return new Style().setFont(fontBold()).setFontSize(7).setFontColor(ReportResources.PALETTE_BLUE);
    }

    public Style pageNumberStyle() {
        return new Style().setFont(fontBold()).setFontSize(8).setFontColor(ReportResources.PALETTE_BLUE);
    }

    public Style sidePanelLabelStyle() {
        return new Style().setFont(fontBold()).setFontSize(7).setFontColor(ReportResources.PALETTE_WHITE);
    }

    public Style sidePanelValueStyle() {
        return new Style().setFont(fontBold()).setFontSize(11).setFontColor(ReportResources.PALETTE_WHITE);
    }

    public Style dataHighlightLinksStyle() {
        return new Style().setFont(fontRegular()).setFontSize(8).setFontColor(ReportResources.PALETTE_BLUE);
    }

    public Style urlStyle() {
        return new Style().setFont(fontRegular()).setFontSize(7).setFontColor(ReportResources.PALETTE_BLUE);
    }

    @NotNull
    private static PdfFont createFontFromProgram(@NotNull FontProgram program) {
        return PdfFontFactory.createFont(program, PdfEncodings.IDENTITY_H);
    }

    @NotNull
    private static FontProgram loadFontProgram(@NotNull String resourcePath) {
        try {
            return FontProgramFactory.createFont(resourcePath);
        } catch (IOException exception) {
            // Should never happen, fonts are loaded from code
            throw new IllegalStateException(exception);
        }
    }
}
