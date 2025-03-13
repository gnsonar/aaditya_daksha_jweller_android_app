package com.aaditya.inv.ui.calender;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Objects;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker.
        final Calendar c = Calendar.getInstance();

        // Create a new instance of TimePickerDialog and return it.
        return new DatePickerDialog(requireActivity(), this, c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

    }
}

