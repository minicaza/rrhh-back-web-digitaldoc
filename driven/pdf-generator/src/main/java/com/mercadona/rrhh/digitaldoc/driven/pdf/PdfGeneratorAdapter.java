package com.mercadona.rrhh.digitaldoc.driven.pdf;

import com.mercadona.framework.cna.commons.domain.MercadonaBusinessException;
import com.mercadona.rrhh.digitaldoc.application.ports.driven.PdfGeneratorPort;
import com.mercadona.rrhh.digitaldoc.domain.AiCertificationInfo;
import com.mercadona.rrhh.digitaldoc.domain.Document;
import com.mercadona.rrhh.digitaldoc.domain.EmployeeInfo;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Component
public class PdfGeneratorAdapter implements PdfGeneratorPort {

    private static final PDType1Font FONT_BOLD    = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private static final PDType1Font FONT_REGULAR = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

    private static final float MARGIN   = 50f;
    private static final float LINE     = 18f;
    private static final float GAP      = 10f;

    @Override
    public byte[] generate(Document document) {
        try (PDDocument pdf = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            pdf.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(pdf, page)) {
                float y = 750f;

                y = writeTitle(cs, y, "Certificado de Uso de IA — Mercadona");
                y -= GAP;

                y = writeSection(cs, y, "Documento");
                y = writeField(cs, y, "ID Documento", document.getId().toString());
                y -= GAP;

                EmployeeInfo info = document.getEmployeeInfo();
                if (info != null) {
                    y = writeSection(cs, y, "Empleado");
                    y = writeField(cs, y, "Nombre",       info.fullName());
                    y = writeField(cs, y, "ID Empleado",  info.employeeId());
                    y = writeField(cs, y, "Grupo",        info.managedGroupId());
                    y = writeField(cs, y, "Función",      info.jobFunction());
                    y = writeField(cs, y, "Departamento", info.department());
                    y = writeField(cs, y, "Email",        info.email());
                    y = writeField(cs, y, "Extensión",    info.phoneExtension());
                    y = writeField(cs, y, "Localización", info.location());
                    y -= GAP;

                    AiCertificationInfo cert = info.certification();
                    if (cert != null) {
                        y = writeSection(cs, y, "Certificación IA");
                        y = writeField(cs, y, "ID Certificación",  cert.certificationId());
                        y = writeField(cs, y, "Estado",            cert.status());
                        y = writeField(cs, y, "Válido",            cert.valid() ? "Sí" : "No");
                        y = writeField(cs, y, "Nivel",             cert.level());
                        y = writeField(cs, y, "Fecha emisión",     dateStr(cert.issuedDate()));
                        y = writeField(cs, y, "Inicio vigencia",   dateStr(cert.startDate()));
                        y = writeField(cs, y, "Fin vigencia",      dateStr(cert.expirationDate()));
                        y = writeField(cs, y, "Emitido por",       cert.issuedBy());

                        List<String> tools = cert.approvedTools();
                        if (tools != null && !tools.isEmpty()) {
                            y = writeField(cs, y, "Herramientas", String.join(", ", tools));
                        }
                        if (cert.description() != null && !cert.description().isBlank()) {
                            y = writeField(cs, y, "Descripción", cert.description());
                        }
                    }
                }
            }

            pdf.save(out);
            return out.toByteArray();

        } catch (IOException ex) {
            throw new MercadonaBusinessException("PDF_GENERATION_ERROR",
                    "Failed to generate PDF for document " + document.getId() + ": " + ex.getMessage());
        }
    }

    private float writeTitle(PDPageContentStream cs, float y, String text) throws IOException {
        cs.beginText();
        cs.setFont(FONT_BOLD, 16);
        cs.newLineAtOffset(MARGIN, y);
        cs.showText(text);
        cs.endText();
        return y - 30f;
    }

    private float writeSection(PDPageContentStream cs, float y, String title) throws IOException {
        cs.beginText();
        cs.setFont(FONT_BOLD, 12);
        cs.newLineAtOffset(MARGIN, y);
        cs.showText(title);
        cs.endText();
        return y - LINE;
    }

    private float writeField(PDPageContentStream cs, float y, String label, String value) throws IOException {
        cs.beginText();
        cs.setFont(FONT_REGULAR, 11);
        cs.newLineAtOffset(MARGIN + 10f, y);
        cs.showText(label + ":  " + (value != null ? value : "—"));
        cs.endText();
        return y - LINE;
    }

    private static String dateStr(LocalDate date) {
        return date != null ? date.toString() : "—";
    }
}
