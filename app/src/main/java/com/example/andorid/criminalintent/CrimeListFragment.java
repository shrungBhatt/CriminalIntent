package com.example.andorid.criminalintent;

import android.content.Intent;
import android.database.CursorWrapper;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;


import com.example.andorid.criminalintent.databse.CrimeDbSchema;

import java.text.DateFormat;
import java.util.List;
import java.util.UUID;


public class CrimeListFragment extends Fragment {
    private static final String SAVED_SUBTITLE_VISIBLE = "mSubtitleVisible";
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private boolean mSubtitleVisible;
    private int selectedRowRefresh;
    private TextView mEmptyView;
    private Button mNewCrimeButton;

    DateFormat formatTime = DateFormat.getTimeInstance();
    DateFormat formatDate = DateFormat.getDateInstance();

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);// Inflating the fragment_crime_list.xml

        mEmptyView = (TextView)view.findViewById(R.id.empty_crimes_view);
        mNewCrimeButton = (Button)view.findViewById(R.id.new_crime_button);
        mNewCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                createNewCrime();
            }
        });

        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view); //Allocating the id of RecyclerView from fragment_crime_list.xml
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); //Passing it to the layoutManager to manage the views otherwise it will crash

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();

        return view;
    }

    @Override
    public void onResume () {
        super.onResume();
        updateUI();// To update the data in recyclerView after editing the data in crimeFragment
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);


        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                createNewCrime();
                return true;

            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void updateSubtitle () {

        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural,crimeCount,crimeCount);

        if (!mSubtitleVisible) {
            subtitle = null;
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(subtitle);
        }
    }

    private void updateUI () {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if (mAdapter == null) {

            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyItemChanged(selectedRowRefresh);//Just to change the data of the row whose data is altered.
            mAdapter.setCrimes(crimes);
        }

        updateSubtitle();
    }


    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {//Its recyclerView's viewHolder
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private TextView mTimeTextView;
        private CheckBox mSolvedCheckBox;
        private Crime mCrime;


        public CrimeHolder (View itemView) {//For Defining what the viewHolder in the recyclerView show
            super(itemView);
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_textView);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_date_textView);
            mSolvedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_checkbox);
            mTimeTextView = (TextView) itemView.findViewById(R.id.list_item_time_textView);
        }

        @Override
        public void onClick (View v) {//Functioning When the ViewHolder is clicked
            selectedRowRefresh = getAdapterPosition();
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            startActivity(intent);
        }

        public void bindCrime (Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(formatDate.format(mCrime.getDate()));
            mTimeTextView.setText(formatTime.format(mCrime.getTime()));
            mSolvedCheckBox.setChecked(mCrime.isSolved());
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {//Its the adapter of the recyclerView
        private List<Crime> mCrimes;

        public CrimeAdapter (List<Crime> crimes) {
            mCrimes = crimes;
        }


        @Override
        public CrimeHolder onCreateViewHolder (ViewGroup parent, int viewType) {//Called by the recyclerView when it needs a view to display an item
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
            return new CrimeHolder(view);
        }


        @Override
        public void onBindViewHolder (CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bindCrime(crime);
        }

        @Override
        public int getItemCount () {
            if(mCrimes.size() == 0) {
                mCrimeRecyclerView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
                mNewCrimeButton.setVisibility(View.VISIBLE);
            }
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> crimes){
            mCrimes = crimes;
        }

    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);

    }
    public void createNewCrime(){
        Crime crime = new Crime();
        CrimeLab.get(getActivity()).addCrime(crime);
        Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
        startActivity(intent);
    }

}
