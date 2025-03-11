package com.aaditya.inv.ui.dj;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aaditya.inv.custom.RecyclerViewAdapterGoldRates;
import com.aaditya.inv.databinding.DjManageGoldRatePageBinding;
import com.aaditya.inv.db.SQLConstants;
import com.aaditya.inv.db.SQLiteDatabaseHelper;
import com.aaditya.inv.utils.Commons;
import com.aaditya.inv.utils.Constants;
import com.aaditya.inv.utils.InMemoryInfo;
import com.airbnb.lottie.LottieAnimationView;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ManageGoldRatesFragment extends Fragment {
    private DjManageGoldRatePageBinding binding;
    private static final int CAMERA_REQUEST = 1888;
    private static final int STORAGE_REQUEST = 1999;
    SQLiteDatabase db = null;
    private final String[] REQUIRED_PERMISSIONS = new String[]{ "android.permission.CAMERA"};
    ImageView imageView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = DjManageGoldRatePageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.rateBanks.setAdapter(new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, InMemoryInfo.loanBankList));

        binding.goldRateDateTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/YYYY hh:mm a")));

        final SQLiteDatabaseHelper sqlLite = new SQLiteDatabaseHelper(getContext());
        db = sqlLite.getReadableDatabase();
        List<Float[]> rateList = new ArrayList<>();

        RecyclerView.Adapter goldRatesItems = new RecyclerViewAdapterGoldRates(rateList);
        binding.goldRateRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.goldRateRecyclerView.setAdapter(goldRatesItems);

        binding.loanTypes.setAdapter(new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, InMemoryInfo.loanTypes));

        Commons.fetchGoldRates(goldRatesItems, db, binding.rateBanks, binding.loanTypes, rateList, binding.goldRateSection, binding.goldRateAddMessage);

        binding.rateBanks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                rateList.clear();
                Commons.fetchGoldRates(goldRatesItems, db, binding.rateBanks, binding.loanTypes, rateList, binding.goldRateSection, binding.goldRateAddMessage);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.loanTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                rateList.clear();
                Commons.fetchGoldRates(goldRatesItems, db, binding.rateBanks, binding.loanTypes, rateList, binding.goldRateSection, binding.goldRateAddMessage);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.addNewRate.setOnClickListener(view -> {
            binding.goldRateSection.setVisibility(View.VISIBLE);
            binding.goldRateAddMessage.setVisibility(View.GONE);
            List<Float[]> newRateList = new ArrayList<>();
            for (int i = 0; i < rateList.size(); i++) {
                RecyclerViewAdapterGoldRates.ViewHolder viewHolder = (RecyclerViewAdapterGoldRates.ViewHolder) binding.goldRateRecyclerView.findViewHolderForAdapterPosition(i);
                newRateList.add(new Float[]{Float.valueOf(viewHolder.getKarat().getText().toString()), Float.valueOf(viewHolder.getRate().getText().toString())});
            }
            rateList.clear();
            rateList.addAll(newRateList);
            rateList.add(new Float[]{0.0f,0.0f});
            goldRatesItems.notifyDataSetChanged();
        });

        binding.goldRateRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                if(rateList.size() == 0) {
                    binding.goldRateSection.setVisibility(View.GONE);
                    binding.goldRateAddMessage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                if(rateList.size() == 0) {
                    binding.goldRateSection.setVisibility(View.GONE);
                    binding.goldRateAddMessage.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.saveRates.setOnClickListener((view -> {
            Commons.minimizeKeyboard(getContext(), binding.saveRates);
            List<Float[]> newRateList = new ArrayList<>();
            try {
                if(rateList.size() > 0) {
                    for (int i = 0; i < rateList.size(); i++) {
                        RecyclerViewAdapterGoldRates.ViewHolder viewHolder = (RecyclerViewAdapterGoldRates.ViewHolder) binding.goldRateRecyclerView.findViewHolderForAdapterPosition(i);
                        newRateList.add(new Float[] {
                                viewHolder.getKarat().getText() != null && !viewHolder.getKarat().getText().toString().isEmpty() ? Float.valueOf(viewHolder.getKarat().getText().toString()) : 0.0f,
                                viewHolder.getRate().getText() != null && !viewHolder.getRate().getText().toString().isEmpty() ? Float.valueOf(viewHolder.getRate().getText().toString()) : 0.0f
                        });
                    }
                    if(validateRateList(rateList, newRateList)) {
                        SQLiteDatabase db1 = sqlLite.getWritableDatabase();
                        db1.execSQL(MessageFormat.format(SQLConstants.AUDIT_RATES_TABLE, binding.rateBanks.getSelectedItem().toString(), binding.loanTypes.getSelectedItem().toString()));
                        db1.execSQL(MessageFormat.format(SQLConstants.TRUNCATE_RATE_TABLE, binding.rateBanks.getSelectedItem().toString(), binding.loanTypes.getSelectedItem().toString()));

                        LocalDateTime timestamp = LocalDateTime.now();
                        for (int i = 0; i < newRateList.size(); i++) {
                            db1.execSQL(MessageFormat.format(SQLConstants.INSERT_RATE, newRateList.get(i)[0], newRateList.get(i)[1].toString(), binding.rateBanks.getSelectedItem().toString(), binding.loanTypes.getSelectedItem().toString(), timestamp));
                        }
                        Toast.makeText(getContext(), "New Rates Saved", Toast.LENGTH_SHORT).show();
                    }
                }
                if(rateList.size() == 0) {
                    binding.goldRateSection.setVisibility(View.GONE);
                    binding.goldRateAddMessage.setVisibility(View.VISIBLE);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Commons.showAlertBox(getContext(), "Application Error", "Application Error", false);
            }
        }));
        return root;
    }

    private boolean validateRateList(List<Float[]> oldRateList, List<Float[]> newRateList) {

        if(newRateList.stream().filter(rate -> rate[0] <= 0.0f).findFirst().isPresent()) {
            Toast.makeText(getContext(), "Invalid Karat value", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(newRateList.stream().filter(rate -> rate[1] <= 0.0f).findFirst().isPresent()) {
            Toast.makeText(getContext(), "Invalid Rate Value", Toast.LENGTH_SHORT).show();
            return false;
        }
        Set<Float> karats = newRateList.stream().map(rate -> rate[0]).collect(Collectors.toSet());
        if(karats.size() != newRateList.size()) {
            Toast.makeText(getContext(), "Duplicate karat value", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(oldRateList.size() == newRateList.size() &&
                oldRateList.stream()
                        .filter(rate -> newRateList.stream()
                                .filter(newRate -> rate[0].floatValue() == newRate[0].floatValue() && rate[1].floatValue() == newRate[1].floatValue())
                                .findAny()
                                .isPresent())
                        .count() == newRateList.size()) {
            Toast.makeText(getContext(), "No change in rate", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        }
    }

    private boolean allPermissionsGranted(){
        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == CAMERA_REQUEST || requestCode == STORAGE_REQUEST) {
            if(!allPermissionsGranted()) {
                Toast.makeText(getContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            } else {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        }
    }
}