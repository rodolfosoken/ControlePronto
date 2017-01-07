package com.example.rodolfo.controlepronto;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class EdredomActivity extends Fragment {
    ListView edredomList;
    ArrayAdapter adaptadorEdredom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        edredomList = (ListView) getView().findViewById(R.id.edredomList);
        List<Edredom> edredoms = new ArrayList<>();


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edredom, container, false);
    }


}
