package com.hec.app.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hec.app.R;

/**
 * �Ƽ�Fragment
 *
 * @author jiangqq
 */
public class ScienceFragment extends Fragment {
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.science, container, false);
        return mView;
    }
}
