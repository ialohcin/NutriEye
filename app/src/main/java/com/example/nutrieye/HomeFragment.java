package com.example.nutrieye;

import static android.widget.Toast.LENGTH_SHORT;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.harrywhewell.scrolldatepicker.DayScrollDatePicker;
import com.harrywhewell.scrolldatepicker.OnDateSelectedListener;

import java.time.LocalDate;
import java.util.Date;

public class HomeFragment extends Fragment {

    DayScrollDatePicker dayScrollDatePicker;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        MaterialCardView cardView = view.findViewById(R.id.homeCardView);

        //Breakfast
        TextView bCarbsTotal = view.findViewById(R.id.breakfastCarbsTotal);
        TextView bProTotal = view.findViewById(R.id.breakfastProteinTotal);
        TextView bFatTotal = view.findViewById(R.id.breakfastFatsTotal);
        TextView bCalTotal = view.findViewById(R.id.breakfastCal);

        //Lunch
        TextView lCarbsTotal = view.findViewById(R.id.lunchCarbsTotal);
        TextView lProTotal = view.findViewById(R.id.lunchProteinTotal);
        TextView lFatTotal = view.findViewById(R.id.lunchFatsTotal);
        TextView lCalTotal = view.findViewById(R.id.lunchCal);

        //Dinner
        TextView dCarbsTotal = view.findViewById(R.id.dinnerCarbsTotal);
        TextView dProTotal = view.findViewById(R.id.dinnerProteinTotal);
        TextView dFatTotal = view.findViewById(R.id.dinnerFatsTotal);
        TextView dCalTotal = view.findViewById(R.id.dinnerCal);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });

        return view;
    }

    private void showBottomSheetDialog() {
        // Inflate the bottom sheet layout
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog_calendar, null);

        // Create a BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.TransparentAlertDialog);

        // Set the content view of the dialog to your layout
        bottomSheetDialog.setContentView(bottomSheetView);

        // Show the dialog
        bottomSheetDialog.show();

        LocalDate today = LocalDate.now();
        int day = today.getDayOfMonth();
        int month = today.getMonthValue();
        int year = today.getYear();

        dayScrollDatePicker = bottomSheetView.findViewById(R.id.dayDatePicker);
        dayScrollDatePicker.setStartDate(day-6,month,year);
        dayScrollDatePicker.setEndDate(day,month,year);

        dayScrollDatePicker.getSelectedDate(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@Nullable Date date) {
                Toast.makeText(requireContext(), "Hello", LENGTH_SHORT).show();
            }
        });
    }
}