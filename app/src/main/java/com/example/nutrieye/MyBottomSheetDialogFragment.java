package com.example.nutrieye;

import static android.widget.Toast.LENGTH_SHORT;

import java.time.LocalDate;
import java.util.Date;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.harrywhewell.scrolldatepicker.DayScrollDatePicker;
import com.harrywhewell.scrolldatepicker.OnDateSelectedListener;

public class MyBottomSheetDialogFragment extends BottomSheetDialogFragment {

    DayScrollDatePicker dayScrollDatePicker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_dialog_calendar, container, false);

        LocalDate today = LocalDate.now();
        int day = today.getDayOfMonth();
        int month = today.getMonthValue();
        int year = today.getYear();

        dayScrollDatePicker = view.findViewById(R.id.dayDatePicker);
        dayScrollDatePicker.setStartDate(day,month,year);
        dayScrollDatePicker.getSelectedDate(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@Nullable Date date) {
                Toast.makeText(requireContext(), "Hello", LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
