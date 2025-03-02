package com.aaditya.inv;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;

import android.view.Menu;
import android.widget.Toast;


import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.aaditya.inv.databinding.ActivityMainBinding;
import com.aaditya.inv.db.SQLConstants;
import com.aaditya.inv.db.SQLiteDatabaseHelper;
import com.aaditya.inv.ui.dj.LoanApplicationItemsFragment;
import com.aaditya.inv.utils.Commons;
import com.aaditya.inv.utils.Constants;
import com.aaditya.inv.utils.InMemoryInfo;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.MessageFormat;

public class MainActivity extends AppCompatActivity  {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    SQLiteDatabaseHelper sqlLite = null;
    SQLiteDatabase db1 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sqlLite = new SQLiteDatabaseHelper(getApplicationContext());
        db1 = sqlLite.getWritableDatabase();
        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_about, R.id.nav_feedback, R.id.sign_out)
                .setOpenableLayout(drawer)
                .build();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(R.id.action_logout == item.getItemId()) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("App Message")
                    .setMessage("Do you want to Log out?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (v1, v2) -> {
                        db1.execSQL(MessageFormat.format(SQLConstants.SQL_UPDATE_LOGGED_IN_STATUS, false, InMemoryInfo.branchId));
                        System.exit(-1);
                    })
                    .setNegativeButton("No", (v1, v2) -> v1.dismiss())
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }
}