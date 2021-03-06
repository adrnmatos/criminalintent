package com.bignerdranch.android.criminalintent;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static android.widget.CompoundButton.*;

/**
 * Created by Adriano on 08/06/2017.
 */

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // empty method
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDateButton();
        mDateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mTimeButton = (Button) v.findViewById(R.id.crime_time);
        updateTimeButton();
        mTimeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK)
            return;
        if(requestCode == REQUEST_DATE) {
            Date updatedDate = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            Calendar updatedCalendar = Calendar.getInstance();
            updatedCalendar.setTime(updatedDate);
            Date crimeDate = mCrime.getDate();
            Calendar crimeCalendar = Calendar.getInstance();
            crimeCalendar.setTime(crimeDate);
            crimeCalendar.set(Calendar.YEAR, updatedCalendar.get(Calendar.YEAR));
            crimeCalendar.set(Calendar.MONTH, updatedCalendar.get(Calendar.MONTH));
            crimeCalendar.set(Calendar.DAY_OF_MONTH, updatedCalendar.get(Calendar.DAY_OF_MONTH));
            mCrime.setDate(crimeCalendar.getTime());
            updateDateButton();
        }

        if(requestCode == REQUEST_TIME) {
            Date updatedDate = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            Calendar updatedCalendar = Calendar.getInstance();
            updatedCalendar.setTime(updatedDate);
            Date crimeDate = mCrime.getDate();
            Calendar crimeCalendar = Calendar.getInstance();
            crimeCalendar.setTime(crimeDate);
            crimeCalendar.set(Calendar.HOUR_OF_DAY, updatedCalendar.get(Calendar.HOUR_OF_DAY));
            crimeCalendar.set(Calendar.MINUTE, updatedCalendar.get(Calendar.MINUTE));
            mCrime.setDate(crimeCalendar.getTime());
            updateTimeButton();
        }
    }

    private void updateDateButton() {
        mDateButton.setText(DateFormat.format("EEEE, MMM d, yyyy",mCrime.getDate()));
    }

    private void updateTimeButton() {
        mTimeButton.setText(DateFormat.format("hh:mm a",mCrime.getDate()));
    }
}


