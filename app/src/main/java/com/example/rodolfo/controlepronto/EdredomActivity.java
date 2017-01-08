package com.example.rodolfo.controlepronto;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class EdredomActivity extends Fragment {
    List<Edredom> edredons;
    ArrayAdapter adaptadorEdredom;
    public static final String SELECT_EDREDONS = "SELECT * FROM edredom";
    public static final String INSERT_EDREDOM = "INSERT INTO edredom (rol, prateleira, retirado) VALUES ";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button buttonAdicionar = (Button) getView().findViewById(R.id.bAdicionarEdredom);
        buttonAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adicionarEdredom(view);
            }
        });
        ListView edredomList = (ListView) getView().findViewById(R.id.edredomList);
        edredons = new ArrayList<>();

        try {
            Cursor c = MainActivity.dados.rawQuery(SELECT_EDREDONS, null);

            int indexRol = c.getColumnIndex("rol");
            int indexPrateleira = c.getColumnIndex("prateleira");
            int indexRetirado = c.getColumnIndex("retirado");

            c.moveToFirst();

            while (c != null){
                Edredom edredom  = new Edredom(c.getLong(indexRol), c.getInt(indexPrateleira), c.getInt(indexRetirado));
                //add no indice 0 para inverter a ordem da lista
                edredons.add(0,edredom);
                c.moveToNext();
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        adaptadorEdredom = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, edredons);
        edredomList.setAdapter(adaptadorEdredom);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edredom, container, false);
    }

    public void adicionarEdredom(View view){
        EditText rolText = (EditText) getView().findViewById(R.id.rolEdredom);
        EditText prateleiraText = (EditText) getView().findViewById(R.id.prateleiraText);
        //System.out.println(rolText.getText().toString());

        if(!rolText.getText().toString().isEmpty() &&  !prateleiraText.getText().toString().isEmpty()) {
            long rol = Long.parseLong(rolText.getText().toString());
            int prateleira = Integer.parseInt(prateleiraText.getText().toString());

            Edredom edredom = new Edredom(rol, prateleira, 0);
            //System.out.println(edredom.toString());
            try {
                MainActivity.dados.execSQL(INSERT_EDREDOM + "(" + edredom.getRol() + ", " + edredom.getPrateleira() + ", " + edredom.getRetirado() + ")");
                //aparece no topo da lista
                edredons.add(0,edredom);
                //atualiza a lista
                adaptadorEdredom.notifyDataSetChanged();
                //limpa a entrada
                rolText.setText("");
                prateleiraText.setText("");
                rolText.requestFocus();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(this.getContext(), "Insira o Rol e prateleira", Toast.LENGTH_SHORT).show();
        }

    }


}
