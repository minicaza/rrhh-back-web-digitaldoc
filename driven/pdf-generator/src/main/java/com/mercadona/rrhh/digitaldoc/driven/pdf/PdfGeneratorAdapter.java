package com.mercadona.rrhh.digitaldoc.driven.pdf;

import com.mercadona.framework.cna.commons.domain.MercadonaBusinessException;
import com.mercadona.rrhh.digitaldoc.application.ports.driven.PdfGeneratorPort;
import com.mercadona.rrhh.digitaldoc.domain.Document;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class PdfGeneratorAdapter implements PdfGeneratorPort {

    private static final PDType1Font FONT_BOLD    = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private static final PDType1Font FONT_REGULAR = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

    @Override
    public byte[] generate(Document document) {
        try (PDDocument pdf = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            pdf.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(pdf, page)) {
                cs.beginText();

                cs.setFont(FONT_BOLD, 16);
                cs.newLineAtOffset(50, 750);
                cs.showText("Certificado de Uso de IA — Mercadona");

                cs.setFont(FONT_REGULAR, 12);
                cs.newLineAtOffset(0, -40);
                cs.showText("Document ID:       " + document.getId());
                cs.newLineAtOffset(0, -20);
                cs.showText("Employee ID:       " + document.getEmployeeId());
                cs.newLineAtOffset(0, -20);
                cs.showText("Managed Group ID:  " + document.getManagedGroupId());

                cs.endText();
            }

            pdf.save(out);
            return out.toByteArray();

        } catch (IOException ex) {
            throw new MercadonaBusinessException("PDF_GENERATION_ERROR",
                    "Failed to generate PDF for document " + document.getId() + ": " + ex.getMessage());
        }
    }
}
