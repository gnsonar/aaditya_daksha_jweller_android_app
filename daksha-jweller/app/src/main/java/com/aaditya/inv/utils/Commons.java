package com.aaditya.inv.utils;

import static android.content.Context.TELEPHONY_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.aaditya.inv.R;
import com.aaditya.inv.apis.ApiConnection;
import com.aaditya.inv.pdfs.BankOfBarodaPDF;
import com.aaditya.inv.pdfs.ConsolidatedReportPDF;
import com.aaditya.inv.pdfs.StateBankOfIndiaPDF;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.itextpdf.text.DocumentException;

import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Commons {
    public static Gson gson = new GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).disableHtmlEscaping().create();

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");

    public static void showProgressBar(LinearLayout container1, LottieAnimationView loading, View parent) {
        container1.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        parent.setBackgroundColor(Color.parseColor("#524F57"));
    }

    public static void hideProgressBar(LinearLayout container1, LottieAnimationView loading, View parent, Context context) {
        container1.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
        parent.setBackground(ContextCompat.getDrawable(context, R.drawable.side_nav_bar));
    }

    public static String appendUrlParams(String path, Map<String, String> params) {
        StringBuffer sb = new StringBuffer(path);
        if(params != null && !params.isEmpty()) {
            sb.append("?");
            params.entrySet().stream().forEach(entry -> sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&"));
            return sb.toString().substring(0, sb.length() - 1);
        }
        return sb.toString();
    }

    public static Map<String, Object> mapHttpResponse(InputStream inputStream) throws IOException {
        return gson.fromJson(IOUtils.toString(inputStream, Constants.UTF_8_ENCODING), Map.class);
    }
    public static String mapHttpResponseToString(InputStream inputStream) throws IOException {
        return IOUtils.toString(inputStream, Constants.UTF_8_ENCODING);
    }
    /*public static void setBranchId() {
        String[] chunks = InMemoryInfo.token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        InMemoryInfo.branchId = gson.fromJson(payload, Map.class).get("id").toString();
    }*/

    public static void showAlertBox(Context context, String message, String title, boolean exit) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message != null && !message.isEmpty() ? message : Constants.ERROR_MESSAGE)
                .setCancelable(false)
                .setPositiveButton("OK", (v1, v2) -> {
                    if(exit) System.exit(-1);
                })
                .show();
    }

    public static String parseDbDate(Object date)  {
        if(date != null) {
            String dateStr = date.toString();
            try {
                return format1.format(format.parse(dateStr));
            } catch (Exception e) {
            }
        }
        return "";
    }

    public static void minimizeKeyboard(Context context, View btn) {
        InputMethodManager imm=(InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(btn.getWindowToken(), 0);
    }

    public static float getTwoPrecisionFloat(String value) {
        return BigDecimal.valueOf(Double.valueOf(value)).setScale(2, RoundingMode.HALF_EVEN).floatValue();
    }
    public static String getTwoPrecisionString(float value) {
        return BigDecimal.valueOf(Double.valueOf(value)).setScale(2, RoundingMode.HALF_EVEN).toString();
    }

    public static File createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getDir("customer_photos", Context.MODE_PRIVATE);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    public static File createImageFile(Context context, String imageFileName) throws IOException {
        File storageDir = context.getDir("customer_photos", Context.MODE_PRIVATE);
        /*File image = File.createTempFile(
                imageFileName,  *//* prefix *//*
                "",             *//* suffix *//*
                storageDir      *//* directory *//*
        );*/
        File image = new File(storageDir.getAbsolutePath() + "/" + imageFileName);
        if(image.exists())
            image.delete();
        image.createNewFile();
        return image;
    }

    public static boolean isInt(String value) {
        try {
            Integer.parseInt(value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void createAndDownloadForm(Context context, Cursor cursorLoan, Cursor cursorItems) throws IOException, DocumentException {
        cursorLoan.moveToFirst();
        File f = context.getDir("reports", Context.MODE_PRIVATE);
        File f1 = new File(f.getAbsolutePath() +
                "/" +
                cursorLoan.getString(cursorLoan.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_CUST_NAME)) + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMYYYYHHmmss")) +
                ".pdf");
        if(f1.exists())
            f1.delete();

        f1.createNewFile();

        String bank = cursorLoan.getString(cursorLoan.getColumnIndexOrThrow(Constants.SQLiteDatabase.BANK_LOAN_APP_BANK));
        if(bank.contentEquals("Bank of Baroda"))
            BankOfBarodaPDF.generateForm(cursorLoan, cursorItems, f1, context);

        if(bank.contentEquals("State Bank of India"))
            StateBankOfIndiaPDF.generateForm(cursorLoan, cursorItems, f1, context);

        downloadFile(f1, context);
    }

    public static void downloadConsolidatedReport(Context context, List<Map<String, String>> loanApplications) throws IOException, DocumentException {
        File f = context.getDir("reports", Context.MODE_PRIVATE);
        File f1 = new File(f.getAbsolutePath() + "/" + InMemoryInfo.loanAppSearchObject.getLoanBank() + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMYYYYHHmmss")) + ".pdf");
        if(f1.exists())
            f1.delete();
        f1.createNewFile();

        ConsolidatedReportPDF.generateForm(f1,context, loanApplications);
        downloadFile(f1, context);
    }

    private static void downloadFile(File f1, Context context) {
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", f1);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        String mime = context.getContentResolver().getType(uri);
        intent.setDataAndType(uri, mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }
    public static String saveImageView(Context context, ImageView imageView) throws IOException {
        File file = Commons.createImageFile(context);
        imageView.setDrawingCacheEnabled(true);
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file.getAbsolutePath()));
        return file.getAbsolutePath();
    }

    public static void saveImageBytes(Context context, String fileName, byte[] content) {
        try {
            File file = Commons.createImageFile(context, fileName);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            Bitmap bitmap = BitmapFactory.decodeByteArray(content, 0, content.length, options);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file.getAbsolutePath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getFileName(String filePath) {
        return filePath.substring(filePath.lastIndexOf('/') + 1);
    }

    public static String fetchGoldRates(RecyclerView.Adapter goldRatesItems, SQLiteDatabase db, Spinner rateBanks, Spinner loanTypes, List<Float[]> rateList, LinearLayout goldRates, LinearLayout noGoldRates) {
        Cursor cursor = null;
        String lastUpdate = null;
        try {
            cursor = db.query(Constants.SQLiteDatabase.TABLE_GOLD_RATES, null, "bank = ? and loan_type = ?", new String[]{rateBanks.getSelectedItem().toString(), loanTypes.getSelectedItem().toString()}, null, null, null, null);
            if(cursor.getCount() > 0) {
                goldRates.setVisibility(View.VISIBLE);
                noGoldRates.setVisibility(View.GONE);
                while (cursor.moveToNext()) {
                    Float rate_details[] = {  cursor.getFloat(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.GOLD_RATES_KARAT_COLUMN)),
                            cursor.getFloat(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.GOLD_RATES_RATE_COLUMN))};
                    rateList.add(rate_details);
                    lastUpdate = cursor.getString(cursor.getColumnIndexOrThrow(Constants.SQLiteDatabase.GOLD_RATES_TIMESTAMP_COLUMN));
                }
                goldRatesItems.notifyDataSetChanged();
            } else {
                goldRates.setVisibility(View.GONE);
                noGoldRates.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(cursor != null) cursor.close();
        }
        return lastUpdate;
    }

    public static void saveFileToDownloadsAndroid10Plus(String fileName, String content, Context context) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
        values.put(MediaStore.Downloads.MIME_TYPE, "application/json");
        values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/loan-app-data");

        Uri uri =  context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI , values);
        /*File f = new File(uri.toString());
        if(!f.exists())
            f.mkdir();*/

        try {
            OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
            assert outputStream != null;
            outputStream.write(content.getBytes());
            outputStream.close();
            Toast.makeText(context, "File saved successfully to Downloads folder!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error saving file", Toast.LENGTH_SHORT).show();
        }
    }

    public static BigDecimal weightBigDecimal(String value) {
        return new BigDecimal(value).setScale(3, RoundingMode.HALF_UP);
    }

    public static BigDecimal amountBigDecimal(String value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
    }

    public static String appendTrailingZeros(String value, int lengthNeeded) {
        if(value != null && value.length() < lengthNeeded) {
            List<String> zeroes = new ArrayList<>();
            IntStream.range(0, lengthNeeded - value.length()).forEach(i -> zeroes.add("0"));
            value =  String.join("", zeroes) + value;
        }
        return value;
    }

    public static String getByteFileContent(String path) {
        try {
            if(path != null) {
                File f = new File(path.replace("file:", ""));
                if(f.exists()) {
                    byte[] fileBytes = new byte[(int) f.length()];
                    try (FileInputStream fileInputStream = new FileInputStream(f)) {
                        fileInputStream.read(fileBytes);
                    }
                    return Base64.getEncoder().encodeToString(fileBytes);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String convertNumberToINFormat(String amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        return formatter.format(Float.valueOf(amount));
    }
}
