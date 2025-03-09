package com.aaditya.inv.pdfs;
import android.content.Context;
import android.database.Cursor;
import android.text.Layout;

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
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class StateBankOfIndiaPDF {

    public static void generateForm(Cursor loanApplicationDtl, Cursor loanAppItemsDetails, File file, Context context) throws DocumentException, IOException {
        if(loanApplicationDtl == null || loanAppItemsDetails == null || loanApplicationDtl.getCount() <= 0 || loanAppItemsDetails.getCount() <= 0)
            return;
        int noOfBoxesPackettNo = 7;
        loanApplicationDtl.moveToFirst();
        String name = loanApplicationDtl.getString(loanApplicationDtl.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_CUST_NAME));
        String address = loanApplicationDtl.getString(loanApplicationDtl.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_CUST_ADDRESS));
        String accountNo = Commons.appendTrailingZeros(loanApplicationDtl.getString(loanApplicationDtl.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ACC_NO)), 11);
        String mobNo = Commons.appendTrailingZeros(loanApplicationDtl.getString(loanApplicationDtl.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_MOBILE_NO)), 10);
        String loanId = loanApplicationDtl.getString(loanApplicationDtl.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ID));
        String packetNo = Commons.appendTrailingZeros(loanApplicationDtl.getString(loanApplicationDtl.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_BAG_PACKET_NO)), noOfBoxesPackettNo);
        String date = LocalDateTime.parse(loanApplicationDtl.getString(loanApplicationDtl.getColumnIndexOrThrow(Constants.SQLiteDatabase.CREATED_AT))).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String imagePathItems = loanApplicationDtl.getString(loanApplicationDtl.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_PACKET_PHOTO));
        String jointCustody = loanApplicationDtl.getString(loanApplicationDtl.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_JOINT_CUSTODY));

        String loanType = loanApplicationDtl.getString(loanApplicationDtl.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_LOAN_TYPE));
        String loanAmt  = Commons.convertNumberToINFormat(loanApplicationDtl.getString(loanApplicationDtl.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_LOAN_AMOUNT)));
        String swdName = "";

        if(packetNo == null || packetNo.isEmpty())
            packetNo = "       ";

        String dottedLines = "              ";
        Document document = new Document();
        try {
            BaseFont font = BaseFont.createFont("Roboto-Medium.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED, true, context.getAssets().open("Roboto-Medium.ttf").readAllBytes(), null);
            BaseFont extraBold = BaseFont.createFont("Roboto-ExtraBold.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED, true, context.getAssets().open("Roboto-ExtraBold.ttf").readAllBytes(), null);
            Font font10Size = new Font(font, 10);
            Font font10AccNoSize = new Font(font, 10);

            Font font10SizeBold = new Font(font, 10);
            font10SizeBold.setStyle(Font.BOLD);

            Font font09SizeBold = new Font(font, 8);
            font09SizeBold.setStyle(Font.BOLD);

            Font font12SizeBold = new Font(font, 12);
            font12SizeBold.setStyle(Font.BOLD);

            Font extraBold18Size = new Font(extraBold, 16);
            extraBold18Size.setStyle("underline|italic");

            Font font10SizePackageNo = new Font(font, 10);
            font10SizePackageNo.setColor(BaseColor.RED);

            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            /*font10Size.setStyle(Font.ITALIC);
            document.add(getHeadingParagraph("DAKSHA JEWELLERS", new Font(extraBold, 20), Element.ALIGN_CENTER));
            document.add(getHeadingParagraph("Gandhi Market, Balaji Road, Shirpur", font10Size, Element.ALIGN_CENTER));
            document.add(getHeadingParagraph("Tal Shirpur, Dist Dhule, 425 405", font10Size, Element.ALIGN_CENTER));
            font10Size.setStyle(Font.NORMAL);*/

            // packet no
            document.add(getPacketNoElementTable(packetNo, font10Size, font10AccNoSize, noOfBoxesPackettNo));

            // Heading
            font10Size.setStyle(Font.ITALIC);
            document.add(getHeadingParagraph("APPRAISER CERTIFICATE", extraBold18Size, Element.ALIGN_CENTER));
            document.add(new Paragraph(" "));

            font10Size.setStyle(Font.NORMAL);
            document.add(getHeaderElementTable(loanId, accountNo, font10Size, font10SizePackageNo, font10AccNoSize));

            document.add(getMobileElementTable(mobNo, font10Size, font10AccNoSize));

            document.add(new Paragraph(" "));

            document.add(getHeadingParagraph("Dear Sir,", font10Size, Element.ALIGN_LEFT));
            document.add(getPhrase("I hereby certify that Shri/Smt ", font10Size));
            document.add(getPhrase(name , font10SizeBold));
            document.add(getPhrase(" S/W/D of ", font10Size));
            document.add(getPhrase("Shirpur", font10SizeBold));
            document.add(getPhrase(" Resident of ", font10Size));
            document.add(getPhrase(address + " ", font10SizeBold));
            document.add(getPhrase(" who has sought gold Loan " +
                    "from the Bank is not my relative and gold loan against which the loan sought is not purchased from me. The ornaments/Coins have been weighted and appraised by me on  ", font10Size));
            document.add(getPhrase(date + " ", font10SizeBold));
            document.add(getPhrase("in the presence of Shri/Smt ", font10Size));
            document.add(getPhrase(jointCustody == null && jointCustody.isEmpty() ? dottedLines : jointCustody , font10SizeBold));
            document.add(getPhrase(" (Cash in charge) and the exact weight, purity of the metal and market value " +
                    "of each item as on date indicated. \nbelow:", font10Size));


            // add Header to items table
            PdfPTable itemsTable = new PdfPTable(8);
            itemsTable.setWidthPercentage(100);
            itemsTable.setSpacingBefore(10f);
            itemsTable.setWidths(new float[]{1.5f, 3f, 1.5f, 2f, 3f, 2f, 3f, 3f});
            addHeaderElementToTable(itemsTable, Arrays.asList("Sr. No.", "Description of the article", "Quantity", "Gross Weight" , "Approximate weight of the precious stones in the ornaments(Grams)",
                    "Purity\n(Carat)", "Net weight\n(Grams)", "Market Value Rs."), font09SizeBold);

            int srNo = 1;
            int totalItems = 0;
            BigDecimal totalGrossWeight = Commons.weightBigDecimal("0.0");
            BigDecimal totalNetWeight = Commons.weightBigDecimal("0.0");
            BigDecimal totalAppWeight = Commons.weightBigDecimal("0.0");
            BigDecimal totalMarketValue = Commons.amountBigDecimal("0.0");
            while (loanAppItemsDetails.moveToNext()) {
                addHeaderElementToTable(itemsTable, Arrays.asList("" + srNo,
                        loanAppItemsDetails.getString(loanAppItemsDetails.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_ITEM_DESC)),
                        loanAppItemsDetails.getString(loanAppItemsDetails.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_NO_ITEMS)),
                        loanAppItemsDetails.getString(loanAppItemsDetails.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_GROSS_WT)),
                        loanAppItemsDetails.getString(loanAppItemsDetails.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_APPROX_WT)),
                        loanAppItemsDetails.getString(loanAppItemsDetails.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_PURITY)) + "K",
                        loanAppItemsDetails.getString(loanAppItemsDetails.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_NET_WT)),
                        Commons.convertNumberToINFormat(loanAppItemsDetails.getString(loanAppItemsDetails.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_MARKET_VALUE)))), font10AccNoSize);

                srNo++;
                totalItems += loanAppItemsDetails.getInt(loanAppItemsDetails.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_NO_ITEMS));
                totalGrossWeight = totalGrossWeight.add(Commons.weightBigDecimal(loanAppItemsDetails.getString(loanAppItemsDetails.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_GROSS_WT))));
                totalNetWeight = totalNetWeight.add(Commons.weightBigDecimal(loanAppItemsDetails.getString(loanAppItemsDetails.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_NET_WT))));
                totalAppWeight = totalAppWeight.add(Commons.weightBigDecimal(loanAppItemsDetails.getString(loanAppItemsDetails.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_APPROX_WT))));
                totalMarketValue = totalMarketValue.add(Commons.amountBigDecimal(loanAppItemsDetails.getString(loanAppItemsDetails.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_ITEMS_MARKET_VALUE))));
            }
            if(srNo <= 5) {
                while(srNo <= 5) {
                    addHeaderElementToTable(itemsTable, Arrays.asList("" + srNo, "", "", "", "", "", "", ""), font10AccNoSize);
                    srNo++;
                }
            }
            addHeaderElementToTable(itemsTable, Arrays.asList("", "Total", String.valueOf(totalItems), totalGrossWeight.toString(),
                    totalAppWeight.toString(), "-", totalNetWeight.toString(), Commons.convertNumberToINFormat(totalMarketValue.toString())), font10AccNoSize);

            document.add(itemsTable);

            document.add(getHeadingParagraph("Method(s) used for purity testing :\n" +
                    "I solemnly declare that weight, purity of the gold ornaments/precious stones indicated above are correct \n" +
                    "and I undertake to indemnify the Bank against any loss it may sustain on account of any inaccuracy in above appraisal.", font10Size, Element.ALIGN_LEFT));

            document.add(getTableWithTwoFieldsPopulated("Place:", "Shirpur", "", "", "Loan Type", loanType, new float[]{2.5f, 3f, 2.5f}, new float[]{2f, 2.5f, 4f}, font10SizeBold));
            document.add(getTableWithTwoFieldsPopulated("Date:", date, "", "", "Loan Amount", loanAmt, new float[]{2.5f, 3f, 2.5f}, new float[]{2f, 2.5f, 4f}, font10SizeBold));

            document.add(getSignatureTable("Yours faithfully", font10SizeBold));

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            document.add(getTwoSignatureTable("Name & Signature of the Borrower", "Name & Signature of the Appraiser", font10SizeBold));
            document.add(new Paragraph(" "));
            document.add(nameAndAddress(font12SizeBold, font10Size));

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

    private static PdfPTable getHeaderElementTable(String packageNo, String accNo, Font font, Font fontPackageNo, Font font10AccNoSize) throws DocumentException {

        PdfPTable dateTable = new PdfPTable(5);
        dateTable.setWidthPercentage(100);
        dateTable.setWidths(new float[]{3f, 5f, 1f, 1.2f, 5f});

        PdfPCell pdfPCell1 = new PdfPCell();
        pdfPCell1.addElement(new Paragraph("The Branch Manager\nState Bank of India\nShirpur Branch", font));
        pdfPCell1.setBorder(PdfPCell.NO_BORDER);
        dateTable.addCell(pdfPCell1);

        PdfPCell pdfPCell2 = new PdfPCell();
        pdfPCell2.addElement(new Paragraph("", font));
        pdfPCell2.setBorder(PdfPCell.NO_BORDER);
        dateTable.addCell(pdfPCell2);

        PdfPCell pdfPCell3 = new PdfPCell();
        Paragraph p = new Paragraph(packageNo, fontPackageNo);
        p.setAlignment(Element.ALIGN_MIDDLE);
        pdfPCell3.addElement(p);
        pdfPCell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
        pdfPCell3.setBorder(PdfPCell.NO_BORDER);
        dateTable.addCell(pdfPCell3);


        PdfPCell pdfPCell4 = new PdfPCell();
        p = new Paragraph("Acc No.", font);
        p.setAlignment(Element.ALIGN_MIDDLE);
        p.setPaddingTop(0f);
        pdfPCell4.addElement(p);
        pdfPCell4.setPaddingTop(0f);
        pdfPCell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
        pdfPCell4.setBorder(PdfPCell.NO_BORDER);
        dateTable.addCell(pdfPCell4);


        PdfPCell pdfPCell5 = new PdfPCell();
        PdfPTable accNoTable = new PdfPTable(11);
        accNoTable.setWidthPercentage(100);
        accNoTable.setWidths(new float[]{1f, 1f, 1f, 1f,1f, 1f,1f, 1f,1f, 1f, 1f});
        IntStream.range(0, 11).forEach(count -> {
            PdfPCell pdfPCell = new PdfPCell();
            Paragraph p1 = new Paragraph("" + accNo.charAt(count), font10AccNoSize);
            p1.setAlignment(Element.ALIGN_MIDDLE);
            p1.setSpacingBefore(15f);
            p1.setSpacingAfter(5f);
            pdfPCell.addElement(p1);
            pdfPCell.setPaddingTop(0);
            pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            accNoTable.addCell(pdfPCell);
        });
        pdfPCell5.addElement(accNoTable);
        pdfPCell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
        pdfPCell5.setBorder(PdfPCell.NO_BORDER);
        dateTable.addCell(pdfPCell5);

        return dateTable;
    }
    private static PdfPTable getMobileElementTable(String mobileNo, Font font, Font font10AccNoSize) throws DocumentException {

        PdfPTable dateTable = new PdfPTable(5);
        dateTable.setWidthPercentage(100);
        dateTable.setWidths(new float[]{3f, 4.5f, 1f, 1.5f, 5f});

        PdfPCell pdfPCell1 = new PdfPCell();
        pdfPCell1.addElement(new Paragraph("", font));
        pdfPCell1.setBorder(PdfPCell.NO_BORDER);
        dateTable.addCell(pdfPCell1);

        PdfPCell pdfPCell2 = new PdfPCell();
        pdfPCell2.addElement(new Paragraph("", font));
        pdfPCell2.setBorder(PdfPCell.NO_BORDER);
        dateTable.addCell(pdfPCell2);

        PdfPCell pdfPCell3 = new PdfPCell();
        Paragraph p = new Paragraph("", font);
        p.setAlignment(Element.ALIGN_MIDDLE);
        pdfPCell3.addElement(p);
        pdfPCell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
        pdfPCell3.setBorder(PdfPCell.NO_BORDER);
        dateTable.addCell(pdfPCell3);


        PdfPCell pdfPCell4 = new PdfPCell();
        p = new Paragraph("Mob No.", font);
        p.setAlignment(Element.ALIGN_MIDDLE);
        p.setPaddingTop(0f);
        pdfPCell4.addElement(p);
        pdfPCell4.setPaddingTop(0f);
        pdfPCell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
        pdfPCell4.setBorder(PdfPCell.NO_BORDER);
        dateTable.addCell(pdfPCell4);


        PdfPCell pdfPCell5 = new PdfPCell();
        PdfPTable accNoTable = new PdfPTable(10);
        accNoTable.setWidthPercentage(100);
        accNoTable.setWidths(new float[]{1f, 1f, 1f, 1f,1f, 1f,1f, 1f,1f, 1f});
        IntStream.range(0, 10).forEach(count -> {
            PdfPCell pdfPCell = new PdfPCell();
            Paragraph p1 = new Paragraph("" + mobileNo.charAt(count), font10AccNoSize);
            p1.setAlignment(Element.ALIGN_MIDDLE);
            p1.setSpacingBefore(15f);
            p1.setSpacingAfter(5f);
            pdfPCell.addElement(p1);
            pdfPCell.setPaddingTop(0);
            pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            accNoTable.addCell(pdfPCell);
        });
        pdfPCell5.addElement(accNoTable);
        pdfPCell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
        pdfPCell5.setBorder(PdfPCell.NO_BORDER);
        dateTable.addCell(pdfPCell5);

        return dateTable;
    }
    private static PdfPTable getPacketNoElementTable(String packetNo, Font font, Font font10AccNoSize, int noOfBoxes) throws DocumentException {

        PdfPTable dateTable = new PdfPTable(3);
        dateTable.setWidthPercentage(100);
        dateTable.setWidths(new float[]{1f, 2f, 6f});


        PdfPCell pdfPCell1 = new PdfPCell();
        Paragraph p = new Paragraph("Pouch No: ", font);
        p.setAlignment(Element.ALIGN_MIDDLE);
        p.setPaddingTop(0f);
        pdfPCell1.addElement(p);
        pdfPCell1.setBorder(PdfPCell.NO_BORDER);
        dateTable.addCell(pdfPCell1);

        PdfPCell pdfPCell2 = new PdfPCell();
        PdfPTable accNoTable = new PdfPTable(noOfBoxes);
        accNoTable.setWidthPercentage(100);
        accNoTable.setWidths(new float[]{1f, 1f, 1f, 1f, 1f, 1f, 1f});
        IntStream.range(0, noOfBoxes).forEach(count -> {
            PdfPCell pdfPCell = new PdfPCell();
            Paragraph p1 = new Paragraph("" + packetNo.charAt(count), font10AccNoSize);
            p1.setAlignment(Element.ALIGN_MIDDLE);
            p1.setSpacingBefore(15f);
            p1.setSpacingAfter(5f);
            pdfPCell.addElement(p1);
            pdfPCell.setPaddingTop(0);
            pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            accNoTable.addCell(pdfPCell);
        });
        pdfPCell2.addElement(accNoTable);
        pdfPCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
        pdfPCell2.setBorder(PdfPCell.NO_BORDER);
        dateTable.addCell(pdfPCell2);

        PdfPCell pdfPCell3 = new PdfPCell();
        pdfPCell3.addElement(new Paragraph("", font));
        pdfPCell3.setBorder(PdfPCell.NO_BORDER);
        dateTable.addCell(pdfPCell3);

        return dateTable;
    }
    private static void addHeaderElementToTable(PdfPTable itemsTable, List<String> list, Font font) {

        list.forEach(column -> {
            PdfPCell cell = new PdfPCell();
            Paragraph p = new Paragraph(column, font);
            p.setAlignment(Element.ALIGN_CENTER);
            p.setPaddingTop(0f);
            p.setSpacingAfter(2f);
            cell.addElement(p);
            itemsTable.addCell(cell);
        });
    }
    public static Phrase getPhrase(String text, Font font) {
        return new Phrase(text, font);
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
        dateTable.setSpacingAfter(0f);

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
        Paragraph p = new Paragraph(cell3, font12Size);
        p.setAlignment(Element.ALIGN_MIDDLE);
        pdfPCell3.addElement(p);
        pdfPCell3.setBorder(PdfPCell.NO_BORDER);
        if(cell1.isEmpty())
            pdfPCell3.setBorder(PdfPCell.BOTTOM);

        if(!cell3.isEmpty()) {
            pdfPCell3.setBorder(PdfPCell.BOX);
        }

        dateTable.addCell(pdfPCell3);

        return dateTable;
    }
    public static PdfPTable getHeaderTable2(String cell1, String cell2, String cell3, Font font12Size, float[] width) throws DocumentException {

        PdfPTable dateTable = new PdfPTable(3);
        dateTable.setWidthPercentage(100);
        dateTable.setWidths(width);
        dateTable.setPaddingTop(0f);

        PdfPCell pdfPCell1 = new PdfPCell();
        Paragraph p = new Paragraph(cell1, font12Size);
        p.setAlignment(Element.ALIGN_MIDDLE);
        p.setSpacingAfter(5f);
        pdfPCell1.addElement(p);
        dateTable.addCell(pdfPCell1);

        PdfPCell pdfPCell2 = new PdfPCell();
        Paragraph p1 = new Paragraph(cell2, font12Size);
        p1.setAlignment(Element.ALIGN_MIDDLE);
        p1.setSpacingAfter(5f);
        pdfPCell2.addElement(p1);
        dateTable.addCell(pdfPCell2);

        PdfPCell pdfPCell3 = new PdfPCell();
        pdfPCell3.addElement(new Paragraph(cell3, font12Size));
        pdfPCell3.setBorder(PdfPCell.NO_BORDER);

        dateTable.addCell(pdfPCell3);

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
        table.setWidths(new float[]{2.5f, 3f, 2.5f});

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

    public static PdfPTable nameAndAddress(Font fontBold, Font font ) throws DocumentException {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2.5f, 3f, 2.5f});


        PdfPCell pdfPCell1 = new PdfPCell();
        pdfPCell1.addElement(new Paragraph(""));
        pdfPCell1.setPadding(0);
        pdfPCell1.setBorder(PdfPCell.NO_BORDER);
        table.addCell(pdfPCell1);

        PdfPCell pdfPCell2 = new PdfPCell();
        pdfPCell2.addElement(new Paragraph(""));
        pdfPCell2.setBorder(PdfPCell.NO_BORDER);
        pdfPCell2.setPadding(0);
        table.addCell(pdfPCell2);

        PdfPCell pdfPCell3 = new PdfPCell();
        pdfPCell3.addElement(getHeadingParagraph("Daksha Jeweller's", fontBold, PdfPTable.ALIGN_CENTER));
        pdfPCell3.addElement(getHeadingParagraph("Saraf Bazar, Shirpur", font, PdfPTable.ALIGN_CENTER));
        pdfPCell3.addElement(getHeadingParagraph("Tal Shirpur, Dist Dhule, 425 405", font, PdfPTable.ALIGN_CENTER));
        pdfPCell3.setPadding(0);
        pdfPCell3.setBorder(PdfPCell.NO_BORDER);
        table.addCell(pdfPCell3);

        return table;
    }
    public static PdfPTable getTableWithTwoFieldsPopulated(String cell1, String cell1Val, String cell2, String cell2Val,
                                                           String label1, String label1Val,
                                                           float[] cell1Widths, float[] cell2Widths, Font font) throws DocumentException {
        PdfPTable dateTimePacket = new PdfPTable(2);
        dateTimePacket.setWidthPercentage(100);
        dateTimePacket.setWidths(new float[]{1f, 2f}); // Left column is wider
        dateTimePacket.setSpacingAfter(0f);

        PdfPCell dateCell = new PdfPCell();
        dateCell.setBorder(PdfPCell.NO_BORDER);
        dateCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        dateCell.addElement(getHeaderTable(cell1, cell1Val, "", font, cell1Widths));
        dateTimePacket.addCell(dateCell);


        PdfPCell packetNoCell = new PdfPCell();
        packetNoCell.setBorder(PdfPCell.NO_BORDER);
        packetNoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        packetNoCell.setPadding(0f);
        if(!cell2.isEmpty())
            packetNoCell.addElement(getHeaderTable("", cell2, cell2Val, font, cell2Widths));
        else
            packetNoCell.addElement(getHeaderTable2(label1, label1Val, "", font, cell2Widths));
        dateTimePacket.addCell(packetNoCell);

        return dateTimePacket;
    }
}

