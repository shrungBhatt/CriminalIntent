package com.example.andorid.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by Bhatt on 15-10-2016.
 */

public class CrimeListActivity extends SingleFragmentActivity {




    @Override
    protected Fragment createFragment(){
        return new CrimeListFragment();
    }

}
