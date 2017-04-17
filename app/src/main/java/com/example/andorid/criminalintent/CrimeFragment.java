package com.example.andorid.criminalintent;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;


public class CrimeFragment extends Fragment {
    private static final String ARG_CRIME_ID = "crime_id";
    private static final int REQUEST_TIME = 0;
    private static final String DIALOG_TIME = "Dialog_time";
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;




    private Crime mCrime; //Variable with the reference of class.jave
    private EditText mTitleField;  //EditText widget in fragment_crime.xml
    private Button mDateButton;  //To display the date
    private CheckBox mSolvedCheckBox;  //Solved checkbox
    private String DIALOG_DATE = "Dialog_date";
    private static final int REQUEST_DATE = 0;
    private Button mTimeButton;//To Display the time of crime
    private Button mReportButton; // To send the report
    private Button mSuspectButton;//To choose the suspect button
    private ImageView mPhotoView;
    private ImageButton mCameraButton;
    public static String mSuspect;
    private File mPhotoFile;

    DateFormat formatTime = DateFormat.getTimeInstance();
    DateFormat formatDate = DateFormat.getDateInstance();


    public static CrimeFragment newInstance (UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public void onPause(){
        super.onPause();

        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);//To inflate(set) the fragment in the container

        //EditText field in fragment_crime.xml

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());//To get the title of the crime pressed from the crimelistactivity using extra.
        mTitleField.addTextChangedListener(new TextWatcher() { //Adds a listener to the crimeFragment
            @Override
            public void beforeTextChanged (CharSequence s, int start, int count, int after) {
                //This space intentionally left blank
            }

            @Override
            public void onTextChanged (CharSequence s, int start, int before, int count) {  //To set the crime title
                mCrime.setTitle(s.toString());

            }

            @Override
            public void afterTextChanged (Editable s) {

                //This space intentionally left blank
            }
        });

        //DateButton in fragment_crime.xml

        mDateButton = (Button) v.findViewById(R.id.crime_date);  //Giving value of the id of button in fragment_crime.xml
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        //TimeButton in fragment_crime.xml
        mTimeButton = (Button) v.findViewById(R.id.crime_time);
        updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getTime());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });


        //ReportButton is fragment_crime.xml
        mReportButton = (Button)v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT,getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.crime_report_subject));
                i = Intent.createChooser(i,getString(R.string.send_report));
                startActivity(i);
            }
        });


        //suspectButton in fragment_crim.xml
        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button)v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                startActivityForResult(pickContact,REQUEST_CONTACT);

            }
        });
        if(mCrime.getSuspect() != null){
            mSuspectButton.setText(mCrime.getSuspect());
        }

        //If no contact app is present the app wont crash bcos of this method
        PackageManager pM = getActivity().getPackageManager();
        if(pM.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY)== null){
            mSuspectButton.setEnabled(false);
        }


        //Crime imageView
        mPhotoView = (ImageView)v.findViewById(R.id.crime_photo);
        updatePhotoView();






        //Camera button
        mCameraButton = (ImageButton)v.findViewById(R.id.camera_button);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(pM) != null;
        mCameraButton.setEnabled(canTakePhoto);


        if(canTakePhoto){
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        }
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                startActivityForResult(captureImage,REQUEST_PHOTO);
            }
        });


        //Solved CheckBox fragment_crime.xml


        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);  //Giving the value to the checkbox in the fragment_crime.xml
        mSolvedCheckBox.setChecked(mCrime.isSolved());//To set the extra value in crimeFragment of solved checkbox
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//Setting an check listener to the checkbox to respond to the user interface of checking or unchecking
            @Override
            public void onCheckedChanged (CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);//Set the crime's solved property
            }
        });

        return v;
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_remove_crime:
                if (mCrime != null) {
                    Toast.makeText(getActivity(), "Deleting this crime", Toast.LENGTH_SHORT).show();
                    UUID crimeId = mCrime.getId();
                    CrimeLab.get(getActivity()).removeCrime(crimeId);
                    getActivity().finish();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_TIME && data.getSerializableExtra(TimePickerFragment.EXTRA_TIME) != null) {

            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);

            mCrime.setTime(date);
            updateTime();
        }


        if (requestCode == REQUEST_DATE && data.getSerializableExtra(DatePickerFragment.EXTRA_DATE) != null) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            mCrime.setDate(date);
            updateDate();
        }else if(requestCode == REQUEST_CONTACT && data != null){
            Uri contactUri = data.getData();
            //Specify which fields you want your query to return
            //values for
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME

            };
            //Perform your query - the contractUri is like a "Where"
            //clause here
            Cursor c = getActivity().getContentResolver().query(contactUri,queryFields,null,null,null);
            try{
                //Double - check that you actually got results
                if(c.getCount() == 0){
                    return;
                }
                // pull out the first column to the first row of the data
                //that is your suspects name.
                c.moveToFirst();
                String suspect = c.getString(0);
                mSuspectButton.setText(suspect);
                mSuspect = suspect;
            }finally{
                c.close();
            }
        }else if(requestCode == REQUEST_PHOTO){
            updatePhotoView();
        }

    }

    private void updateTime () {
        mTimeButton.setText(formatTime.format(mCrime.getTime()));
    }

    private void updateDate () {
        mDateButton.setText(formatDate.format(mCrime.getDate()));
    }
    private String getCrimeReport(){
        String solvedString = null;
        if(mCrime.isSolved()){
            solvedString = getString(R.string.crime_report_solved);
        }else{
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateString = formatDate.format(mCrime.getDate());

        String suspect;
        if(mSuspect == null){
            suspect = getString(R.string.crime_report_no_suspect);

        }else{
            suspect = "the Suspect is " + mSuspect;
        }
        String report = getString(R.string.crime_report,mCrime.getTitle(),dateString,solvedString,suspect);
        return report;
    }

    public static String getSuspectName(){
        return mSuspect;
    }

    private void updatePhotoView(){
        if (mPhotoFile == null || !mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
        }else{
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(),getActivity());
            mPhotoView.setImageBitmap(bitmap);
            mPhotoView.setRotation(90);
        }
    }


}
