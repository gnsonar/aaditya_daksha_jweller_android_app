package com.aaditya.inv.pdfs;
import android.content.Context;
import android.database.Cursor;

import com.aaditya.inv.utils.Commons;
import com.aaditya.inv.utils.Constants;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class BankOfBarodaPDF {

    public static void generateForm(Cursor loanApplicationDtl, Cursor loanAppItemsDetails, File file, Context context) throws DocumentException, IOException {

        if(loanApplicationDtl == null || loanAppItemsDetails == null || loanApplicationDtl.getCount() <= 0 || loanAppItemsDetails.getCount() <= 0)
            return;

        loanApplicationDtl.moveToFirst();
        String name = loanApplicationDtl.getString(loanApplicationDtl.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_CUST_NAME));

        String address = loanApplicationDtl.getString(loanApplicationDtl.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_CUST_ADDRESS));
        String mobileNo = loanApplicationDtl.getString(loanApplicationDtl.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_MOBILE_NO));
        String accountNo = loanApplicationDtl.getString(loanApplicationDtl.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ACC_NO));
        String packetNo = loanApplicationDtl.getString(loanApplicationDtl.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ID));
        String date = LocalDateTime.parse(loanApplicationDtl.getString(loanApplicationDtl.getColumnIndexOrThrow(Constants.SQLiteDatabase.CREATED_AT))).format(DateTimeFormatter.ofPattern("dd/MM/YYYY"));
        String loanAmount = loanApplicationDtl.getString(loanApplicationDtl.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_LOAN_AMOUNT));
        String loanType = loanApplicationDtl.getString(loanApplicationDtl.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_LOAN_TYPE));

        String imagePath = loanApplicationDtl.getString(loanApplicationDtl.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_PHOTO));
        String imagePathItems = loanApplicationDtl.getString(loanApplicationDtl.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_PACKET_PHOTO));


        Document document = new Document();
        try {
            BaseFont font = BaseFont.createFont("Roboto-Medium.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED, true, context.getAssets().open("Roboto-Medium.ttf").readAllBytes(), null);
            BaseFont extraBold = BaseFont.createFont("Roboto-ExtraBold.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED, true, context.getAssets().open("Roboto-ExtraBold.ttf").readAllBytes(), null);
            Font font14Size = new Font(font, 14);
            Font font12Size = new Font(font, 12);
            Font font11Size = new Font(font, 11);
            Font font10Size = new Font(font, 10);
            Font font10SizeBold = new Font(font, 10);
            font10SizeBold.setStyle(Font.BOLD);

            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            document.add(getTableWithTwoFieldsPopulated("Date", date, "Packet No.", packetNo, new float[]{1f, 3f, 3f}, new float[]{4f, 2f, 2f}, font12Size));

            // Heading
            font10Size.setStyle(Font.ITALIC);
            document.add(getHeadingParagraph("TANISHAKA JEWELLERS", new Font(extraBold, 20), Element.ALIGN_CENTER));
            document.add(getHeadingParagraph("Gandhi Market, Balaji Road, Shirpur", font10Size, Element.ALIGN_CENTER));
            document.add(getHeadingParagraph("Tal Shirpur, Dist Dhule, 425 405", font10Size, Element.ALIGN_CENTER));
            font10Size.setStyle(Font.NORMAL);

            // Create a table with two columns for user info and photo
            PdfPTable userTable = new PdfPTable(2);
            userTable.setWidthPercentage(100);
            userTable.setWidths(new int[]{4, 1}); // Left column is wider

            // Left cell: User Information
            PdfPCell infoCell = new PdfPCell();
            infoCell.setBorder(PdfPCell.NO_BORDER);
            font14Size.setStyle(Font.UNDERLINE);

            Paragraph p1 = new Paragraph("Gold Appraisal Memo", font14Size);
            p1.setAlignment(Element.ALIGN_CENTER);
            infoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            infoCell.addElement(p1);
            userTable.addCell(infoCell);

            // Right cell: User Photo
            PdfPCell imageCell = new PdfPCell();
            imageCell.setBorderWidth(1f);
            imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            imageCell.setFixedHeight(100f);
            try {
                Image userImage = Image.getInstance(imagePath);
                userImage.scaleToFit(150f, 100f);  // Scale image to fit within the cell
                imageCell.setImage(userImage);
            } catch (IOException e) {
                imageCell.addElement(new Paragraph("Image not found"));
            }
            userTable.addCell(imageCell);

            // Add the user table to the document
            document.add(userTable);
            document.add(new Paragraph(" "));

            document.add(getInformationTable("Name:", name, font11Size, new float[]{0.5f, 6f}));
            document.add(getInformationTable("Address:", address, font11Size, new float[]{0.7f, 6f}));
            document.add(getInformationTable("Mobile No.:", mobileNo, font11Size, new float[]{0.8f, 6f}));
            document.add(getInformationTable("Account No.:", accountNo, font11Size, new float[]{1f, 6f}));

            //items details

            // Create dummy data table with 3 columns
            PdfPTable itemsTable = new PdfPTable(7);
            itemsTable.setWidthPercentage(100);
            itemsTable.setSpacingBefore(20f);
            itemsTable.setWidths(new float[]{1.5f, 3f, 3f, 3.5f, 2f, 3f, 3f});

            // add Header to items table
            font11Size.setStyle(Font.BOLD);
            addHeaderElementToTable(itemsTable, Arrays.asList("Sr. No.", "Description", "Nos. of articles", "Gross Wt.,Net Wt.", "Carat", "Market rate per Gram", "Market Value (In Rs)"), font11Size);
            font11Size.setStyle(Font.NORMAL);

            PdfPTable itemsTableData = new PdfPTable(8);
            itemsTableData.setWidthPercentage(100);
            itemsTableData.setPaddingTop(0);
            itemsTableData.setWidths(new float[]{1.5f, 3f, 3f, 1.75f, 1.75f, 2f, 3f, 3f});

            int srNo = 1;
            int totalItems = 0;
            BigDecimal totalGrossWeight = Commons.weightBigDecimal("0.0");
            BigDecimal totalNetWeight = Commons.weightBigDecimal("0.0");
            BigDecimal totalAppWeight = Commons.weightBigDecimal("0.0");
            BigDecimal totalMarketValue = Commons.amountBigDecimal("0.0");
            while (loanAppItemsDetails.moveToNext()) {
                addElementToTable(itemsTableData, Arrays.asList("" + srNo,
                        loanAppItemsDetails.getString(loanAppItemsDetails.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_ITEM_DESC)),
                        loanAppItemsDetails.getString(loanAppItemsDetails.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_NO_ITEMS)),
                        loanAppItemsDetails.getString(loanAppItemsDetails.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_GROSS_WT)),
                        loanAppItemsDetails.getString(loanAppItemsDetails.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_NET_WT)),
                        loanAppItemsDetails.getString(loanAppItemsDetails.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_PURITY)) + "K",
                        loanAppItemsDetails.getString(loanAppItemsDetails.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_RATE)),
                        Commons.convertNumberToINFormat(loanAppItemsDetails.getString(loanAppItemsDetails.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_MARKET_VALUE)))),
                        font11Size);
                srNo++;
                totalItems += loanAppItemsDetails.getInt(loanAppItemsDetails.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_NO_ITEMS));
                totalGrossWeight = totalGrossWeight.add(Commons.weightBigDecimal(loanAppItemsDetails.getString(loanAppItemsDetails.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_GROSS_WT))));
                totalNetWeight = totalNetWeight.add(Commons.weightBigDecimal(loanAppItemsDetails.getString(loanAppItemsDetails.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_NET_WT))));
                totalAppWeight = totalNetWeight.add(Commons.weightBigDecimal(loanAppItemsDetails.getString(loanAppItemsDetails.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_APPROX_WT))));
                totalMarketValue = totalMarketValue.add(Commons.amountBigDecimal(loanAppItemsDetails.getString(loanAppItemsDetails.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_MARKET_VALUE))));
            }
            addElementToTable(itemsTableData, Arrays.asList("", "Total", "" + totalItems, totalGrossWeight.toString(), totalNetWeight.toString(),
                    "-", "-",
                    Commons.convertNumberToINFormat(totalMarketValue.toString())), font11Size);


            // Add the dummy table to the document
            document.add(itemsTable);
            document.add(itemsTableData);

            document.add(getHeadingParagraph("01. I hereby confirm having pledged the above mentioned gold jewellery for the purpose of availing gold loan from Bank of Baroda", font10Size, Element.ALIGN_LEFT));

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            document.add(getSignatureTable("Sign of Borrower", font10SizeBold));
            document.add(getHeadingParagraph("02. I hereby confirm that I have tested/appraised the above & the gross weight of the article, net weight of gold, carat, purity of fineness, rate per gram " +
                    " & market value shown against the ornaments mentioned above are to the best of my knowledge correct & in order", font10Size, Element.ALIGN_LEFT));

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            document.add(getSignatureTable("Sign of Assayer", font10SizeBold));
            document.add(getHeadingParagraph("03. We hereby declare that above items of jewellery / ornaments have been received by us and are in our joint custody. ", font10Size, Element.ALIGN_LEFT));


            document.add(getTableWithTwoFieldsPopulated("Loan Amount:", loanAmount, "Scheme:", loanType, new float[]{2.5f, 2f, 4f}, new float[]{4f, 1.5f, 2f}, font10SizeBold));
            document.add(getTableWithTwoFieldsPopulated("Due Date:", date, "", "", new float[]{2.5f, 2f, 4f}, new float[]{4f, 1.5f, 2f}, font10SizeBold));

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            document.add(getTwoSignatureTable("Signature of Joint Custodian", "Signature of Branch Manager", font10SizeBold));

            if(imagePathItems != null && !imagePathItems.isEmpty()) {
                document.newPage();
                Image itemsImage = Image.getInstance(imagePathItems);
                itemsImage.scaleToFit(550f, 700f);
                document.add(itemsImage);
            }
            document.close();
        } catch (DocumentException | IOException e) {
            throw e;
        }
    }

    private static void addHeaderElementToTable(PdfPTable itemsTable, List<String> list, Font font) {

        list.stream().forEach(column -> {
            String[] columns = column.split(",");
            PdfPCell cell = new PdfPCell();
            if(columns.length == 1) {
                Paragraph p = new Paragraph(column, font);
                p.setAlignment(Element.ALIGN_LEFT);
                p.setSpacingAfter(5f);
                cell.addElement(p);
            } else if (columns.length == 2) {
                try {
                    cell.setPadding(0);

                    PdfPTable subTable = new PdfPTable(columns.length);
                    subTable.setWidthPercentage(100);
                    subTable.setWidths(new float[]{1f, 1f});
                    subTable.setSpacingAfter(0f);

                    PdfPCell cell1 = new PdfPCell();
                    Paragraph p = new Paragraph(columns[0], font);
                    p.setAlignment(Element.ALIGN_MIDDLE);
                    cell1.setBorder(PdfPCell.RIGHT);
                    cell1.addElement(p);


                    PdfPCell cell2 = new PdfPCell();
                    p = new Paragraph(columns[1], font);
                    p.setAlignment(Element.ALIGN_MIDDLE);
                    cell2.setBorder(PdfPCell.LEFT);
                    cell2.addElement(p);

                    subTable.addCell(cell1);
                    subTable.addCell(cell2);

                    if(columns[0].contains("Gross Wt")) {
                        PdfPCell cell3 = new PdfPCell();
                        p = new Paragraph("In Grams", font);
                        p.setAlignment(Element.ALIGN_CENTER);
                        p.setSpacingAfter(2f);
                        cell3.setColspan(2);
                        cell3.setBorder(PdfPCell.TOP);
                        cell3.addElement(p);
                        subTable.addCell(cell3);
                    }
                    cell.addElement(subTable);
                } catch (DocumentException e) {
                    throw new RuntimeException(e);
                }
            }
            itemsTable.addCell(cell);
        });
    }
    private static void addElementToTable(PdfPTable itemsTable, List<String> list, Font font) {

        list.stream().forEach(column -> {
            PdfPCell cell = new PdfPCell();
            Paragraph p = new Paragraph(column, font);
            p.setAlignment(Element.ALIGN_LEFT);
            p.setSpacingBefore(0f);
            p.setPaddingTop(0);
            cell.setPaddingTop(0);
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
    public static PdfPTable getInformationTable(String cell1, String cell2, Font font12Size, float[] width) throws DocumentException {

        PdfPTable dateTable = new PdfPTable(2);
        dateTable.setWidthPercentage(100);
        dateTable.setWidths(width);

        PdfPCell pdfPCell1 = new PdfPCell();
        pdfPCell1.addElement(new Paragraph(cell1, font12Size));
        pdfPCell1.setBorder(PdfPCell.NO_BORDER);
        dateTable.addCell(pdfPCell1);

        PdfPCell pdfPCell2 = new PdfPCell();
        pdfPCell2.addElement(new Paragraph(cell2, font12Size));
        pdfPCell2.setBorder(PdfPCell.BOTTOM);
        dateTable.addCell(pdfPCell2);

        return dateTable;
    }
    public static PdfPTable getSignatureTable(String label, Font font) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4f, 1f});

        PdfPCell pdfPCell1 = new PdfPCell();
        pdfPCell1.addElement(new Paragraph(""));
        pdfPCell1.setBorder(PdfPCell.NO_BORDER);
        pdfPCell1.setPadding(0);
        table.addCell(pdfPCell1);

        PdfPCell pdfPCell2 = new PdfPCell();
        Paragraph p = new Paragraph(label, font);
        p.setAlignment(Element.ALIGN_CENTER);
        pdfPCell2.addElement(p);
        pdfPCell2.setPadding(0);
        pdfPCell2.setBorder(PdfPCell.TOP);
        table.addCell(pdfPCell2);

        return table;
    }
    public static PdfPTable getTwoSignatureTable(String label1, String label2, Font font) throws DocumentException {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2f, 3.5f, 2.5f});

        PdfPCell pdfPCell1 = new PdfPCell();
        Paragraph p = new Paragraph(label1, font);
        p.setAlignment(Element.ALIGN_CENTER);
        pdfPCell1.addElement(p);
        pdfPCell1.setPadding(0);
        pdfPCell1.setBorder(PdfPCell.TOP);
        table.addCell(pdfPCell1);

        PdfPCell pdfPCell2 = new PdfPCell();
        pdfPCell2.addElement(new Paragraph(""));
        pdfPCell2.setBorder(PdfPCell.NO_BORDER);
        pdfPCell2.setPadding(0);
        table.addCell(pdfPCell2);

        PdfPCell pdfPCell3 = new PdfPCell();
        p = new Paragraph(label2, font);
        p.setAlignment(Element.ALIGN_CENTER);
        pdfPCell3.addElement(p);
        pdfPCell3.setPadding(0);
        pdfPCell3.setBorder(PdfPCell.TOP);
        table.addCell(pdfPCell3);

        return table;
    }
    public static PdfPTable getTableWithTwoFieldsPopulated(String cell1, String cell1Val, String cell2, String cell2Val, float[] cell1Widths, float[] cell2Widths, Font font) throws DocumentException {
        PdfPTable dateTimePacket = new PdfPTable(2);
        dateTimePacket.setWidthPercentage(100);
        dateTimePacket.setWidths(new int[]{1, 1}); // Left column is wider

        PdfPCell dateCell = new PdfPCell();
        dateCell.setBorder(PdfPCell.NO_BORDER);
        dateCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        dateCell.addElement(getHeaderTable(cell1, cell1Val, "", font, cell1Widths));
        dateTimePacket.addCell(dateCell);


        PdfPCell packetNoCell = new PdfPCell();
        packetNoCell.setBorder(PdfPCell.NO_BORDER);
        packetNoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        if(!cell2.isEmpty())
            packetNoCell.addElement(getHeaderTable("", cell2, cell2Val, font, cell2Widths));
        dateTimePacket.addCell(packetNoCell);

        return dateTimePacket;
    }
}

