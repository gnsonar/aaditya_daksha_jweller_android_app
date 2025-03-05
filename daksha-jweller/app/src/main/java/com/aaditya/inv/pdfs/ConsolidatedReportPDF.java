package com.aaditya.inv.pdfs;

import android.content.Context;
import android.database.Cursor;

import com.aaditya.inv.utils.Commons;
import com.aaditya.inv.utils.Constants;
import com.aaditya.inv.utils.InMemoryInfo;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class ConsolidatedReportPDF {

    public static void generateForm(File file, Context context, List<Map<String, String>> loanApplications) throws DocumentException, IOException {
        if(loanApplications == null || loanApplications.isEmpty())
            return;

        String bankName = InMemoryInfo.loanAppSearchObject.getLoanBank();

        Document document = new Document();
        try {
            BaseFont font = BaseFont.createFont("Roboto-Medium.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED, true, context.getAssets().open("Roboto-Medium.ttf").readAllBytes(), null);
            BaseFont extraBold = BaseFont.createFont("Roboto-ExtraBold.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED, true, context.getAssets().open("Roboto-ExtraBold.ttf").readAllBytes(), null);
            Font font10Size = new Font(font, 10);
            Font font10AccNoSize = new Font(font, 10);

            Font font10SizeBold = new Font(font, 10);
            font10SizeBold.setStyle(Font.BOLD);

            Font extraBold18Size = new Font(extraBold, 16);
            extraBold18Size.setStyle("italic");

            Font font10SizePackageNo = new Font(font, 10);
            font10SizePackageNo.setColor(BaseColor.RED);

            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            // Heading
            font10Size.setStyle(Font.ITALIC);
            document.add(getHeadingParagraph("Consolidated report for " + (bankName.contentEquals("All") ? "All Banks" : bankName), extraBold18Size, Element.ALIGN_CENTER));
            document.add(new Paragraph(" "));

            font10Size.setStyle(Font.NORMAL);

            // add Header to items table
            PdfPTable itemsTable = new PdfPTable(9);
            itemsTable.setWidthPercentage(100);
            itemsTable.setSpacingBefore(20f);
            itemsTable.setWidths(new float[]{1.5f, 2.5f, 2.5f, 2.5f, 3.5f, 2f, 2f, 2f, 3f});
            addHeaderElementToTable(itemsTable, Arrays.asList("Sr.No.", "Date", "Bank for Loan", "Loan Type", "Customer Name", "Mobile", "Gross wt.", "Net wt.", "Loan Amount"), font10SizeBold);

            int srNo = 1;
            for(Map<String, String> loanApplication : loanApplications) {
                addHeaderElementToTable(itemsTable, Arrays.asList(
                        "" + srNo,
                        loanApplication.get(Constants.SQLiteDatabase.CREATED_AT),
                        loanApplication.get(Constants.SQLiteDatabase.BANK_LOAN_APP_BANK),
                        loanApplication.get(Constants.SQLiteDatabase.BANK_LOAN_APP_LOAN_TYPE),
                        loanApplication.get(Constants.SQLiteDatabase.BANK_LOAN_APP_CUST_NAME),
                        loanApplication.get(Constants.SQLiteDatabase.BANK_LOAN_APP_MOBILE_NO),
                        loanApplication.get(Constants.SQLiteDatabase.LOAN_APPLICATION_TOTAL_GROSS_WT),
                        loanApplication.get(Constants.SQLiteDatabase.LOAN_APPLICATION_TOTAL_NET_WT),
                        loanApplication.get(Constants.SQLiteDatabase.BANK_LOAN_APP_LOAN_AMOUNT) != null &&
                                !loanApplication.get(Constants.SQLiteDatabase.BANK_LOAN_APP_LOAN_AMOUNT).trim().isEmpty() ?
                                Commons.convertNumberToINFormat(loanApplication.get(Constants.SQLiteDatabase.BANK_LOAN_APP_LOAN_AMOUNT)) : "Not Updated"), font10AccNoSize);

                srNo++;
            }

            document.add(itemsTable);
            document.close();
        } catch (DocumentException | IOException e) {
            throw e;
        }
    }

    private static void addHeaderElementToTable(PdfPTable itemsTable, List<String> list, Font font) {

        list.stream().forEach(column -> {
            PdfPCell cell = new PdfPCell();
            Paragraph p = new Paragraph(column, font);
            p.setAlignment(Element.ALIGN_CENTER);
            p.setSpacingAfter(5f);
            cell.addElement(p);
            itemsTable.addCell(cell);
        });
    }
    public static Paragraph getHeadingParagraph(String text, Font font, int alignement) {
        Paragraph intro = new Paragraph(text, font);
        intro.setAlignment(alignement);
        return intro;
    }
    public static PdfPTable getHeaderTable(String cell1, String cell2, String cell3, Font font12Size, float[] width) throws DocumentException {

        PdfPTable dateTable = new PdfPTable(3);
        dateTable.setWidthPercentage(100);
        dateTable.setWidths(width);

        PdfPCell pdfPCell1 = new PdfPCell();
        pdfPCell1.addElement(new Paragraph(cell1, font12Size));
        pdfPCell1.setBorder(PdfPCell.NO_BORDER);
        dateTable.addCell(pdfPCell1);

        PdfPCell pdfPCell2 = new PdfPCell();
        pdfPCell2.addElement(new Paragraph(cell2, font12Size));
        pdfPCell2.setBorder(PdfPCell.BOTTOM);
        if(cell1.isEmpty())
            pdfPCell2.setBorder(PdfPCell.NO_BORDER);
        dateTable.addCell(pdfPCell2);

        PdfPCell pdfPCell3 = new PdfPCell();
        pdfPCell3.addElement(new Paragraph(cell3, font12Size));
        pdfPCell3.setBorder(PdfPCell.NO_BORDER);
        if(cell1.isEmpty())
            pdfPCell3.setBorder(PdfPCell.BOTTOM);

        dateTable.addCell(pdfPCell3);

        return dateTable;
    }
}

