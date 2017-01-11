package com.example.rodolfo.controlepronto;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class TapeteActivity extends Fragment {
    public static List<Tapete> tapetes;
    public static ArrayAdapter adaptadorTapete;
    private EditText tapeteRol;
    private EditText metragemText;
    public static final String SELECT_TAPETES = "SELECT * FROM tapete ";
    public static final String INSERT_TAPETE = "INSERT INTO tapete (rol, metragem) VALUES ";
    public static final String DELETE_TAPETE = "DELETE FROM tapete WHERE id = ";
    public static final String[] COLUNAS_TAPETE = {"id","rol", "metragem","retirado"};


    public static List<Tapete> selectTapete(Context context){
        List<Tapete> tapetesList = new ArrayList<>();
        //limpa a lista de tapetes
        tapetes.clear();

        SQLiteDatabase dados = context.openOrCreateDatabase(MainActivity.NOME_BD, Context.MODE_PRIVATE, null);
        try{
            Cursor c = dados.rawQuery(SELECT_TAPETES, null);
            int indexId = c.getColumnIndex(COLUNAS_TAPETE[0]);
            int indexRol = c.getColumnIndex(COLUNAS_TAPETE[1]);
            int indexMetragem = c.getColumnIndex(COLUNAS_TAPETE[2]);

            if (c.moveToFirst()){
                do {
                    Tapete tapete = new Tapete(c.getInt(indexId), c.getLong(indexRol), c.getDouble(indexMetragem));
                    tapetesList.add(0, tapete);
                    tapetes.add(0, tapete);
                }while(c.moveToNext());
            }
            c.close();
        }catch (Exception e){
            Log.e("BD select Tapete", e.toString());
        }finally {
            dados.close();
            adaptadorTapete.notifyDataSetChanged();
        }

        return tapetesList;
    }

    public boolean salvarTapete(Context context, Tapete tapete){
        boolean ok = false;
        SQLiteDatabase dados = context.openOrCreateDatabase(MainActivity.NOME_BD, Context.MODE_PRIVATE, null);
        try {
            dados.execSQL(INSERT_TAPETE + " (" +tapete.getRol() + ", "+ tapete.getMetragem()+")");
            //recuperar o id gerado pelo banco de dados
            Cursor c = dados.rawQuery(SELECT_TAPETES, null);
            int indexId = c.getColumnIndex("id");
            if(c.moveToLast()) {
                tapete.setId(c.getInt(indexId));
                tapetes.add(0, tapete);
            }
            ok = true;
            c.close();
        }catch(Exception e){
            Log.e("BD adicionar tapete", e.toString());
        }finally {
            dados.close();
            adaptadorTapete.notifyDataSetChanged();
        }

        return ok;
    }

    public void adicionarTapete(){
        if(!tapeteRol.getText().toString().isEmpty() && !metragemText.getText().toString().isEmpty()) {
            long rol = Long.parseLong(tapeteRol.getText().toString());
            double metragem = Double.parseDouble(metragemText.getText().toString());
            Tapete tapete = new Tapete(rol, metragem);

            if(salvarTapete(getContext(),tapete)){
                tapeteRol.setText("");
                metragemText.setText("");
                tapeteRol.requestFocus();
                tapeteRol.getBackground().clearColorFilter();
                metragemText.getBackground().clearColorFilter();
            }
        }else{
            tapeteRol.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SCREEN);
            metragemText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SCREEN);
            Toast.makeText(getContext(), "Insira o Rol e a Metragem", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean removerTapete(Context context, int index){
        boolean ok = false;

        SQLiteDatabase dados = context.openOrCreateDatabase(MainActivity.NOME_BD, Context.MODE_PRIVATE, null);
        try{
            dados.execSQL(DELETE_TAPETE + tapetes.get(index).getId());
            Toast.makeText(context, "TAPETE excluido: "+tapetes.get(index).getId() , Toast.LENGTH_SHORT).show();
            tapetes.remove(index);
            adaptadorTapete.notifyDataSetChanged();

            ConsultaActivity.tapetes.clear();
            ConsultaActivity.adaptadorTapete.notifyDataSetChanged();
            ok = true;
        }catch(Exception e){
            Log.e("BD delete Tapete", e.toString());
        }finally {
            dados.close();
        }

        return ok;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tapeteRol = (EditText) getView().findViewById(R.id.rolTapete);
        metragemText = (EditText) getView().findViewById(R.id.metragemText);
        ListView tapeteListView = (ListView) getView().findViewById(R.id.tapeteList);
        Button bAdd = (Button) getView().findViewById(R.id.bAdicionarTapete);

        //inicializa a list de tapetes
        tapetes = new ArrayList<>();
        adaptadorTapete = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, tapetes);
        tapeteListView.setAdapter(adaptadorTapete);

        selectTapete(getContext());

        tapeteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int index = i;
                //cria um alerta antes de excluir um tapete
                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Excluir")
                        .setMessage("Excluir o Rol "+ tapetes.get(index).getRol()+"?")
                        .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i2) {
                                removerTapete(getContext(),index);
                            }
                        })
                        .setNegativeButton("Cancelar", null).show();
            }
        });

        //coloca o listener no botao
        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adicionarTapete();
            }
        });

        metragemText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_GO){
                    adicionarTapete();
                    return true;
                }
                return false;
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tapete, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tapeteRol = null;
        metragemText = null;
    }
}
