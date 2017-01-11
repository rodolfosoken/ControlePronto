package com.example.rodolfo.controlepronto;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ConsultaActivity extends Fragment {

    public static List<Edredom> edredons;
    public static ArrayAdapter<Edredom> adaptadorEdredom;
    public static List<Tapete> tapetes;
    public static ArrayAdapter<Tapete> adaptadorTapete;
    public static String rolConsulta;
    public static final String DELETE_TAPETE_ROL = "DELETE FROM tapete WHERE rol = ";
    public static final String DELETE_EDREDOM_ROL = "DELETE FROM edredom WHERE rol = ";
    public static final String INSERT_REGISTRO_HISTORICO = "INSERT INTO historico (rol, data) VALUES ";
    EditText rolText;
    TextView textEdredom;
    TextView tapeteText;

    public void consulta(View view){
        rolConsulta = rolText.getText().toString();

        SQLiteDatabase dados = getContext().openOrCreateDatabase(MainActivity.NOME_BD, Context.MODE_PRIVATE, null);

        // =======  REGISTRA CONSULTA NO HISTORICO =======

        try{
            SimpleDateFormat sdf = new SimpleDateFormat("DD-MM-yyyy HH:MM");
            String currentDateandTime = sdf.format(new Date());
            dados.execSQL(INSERT_REGISTRO_HISTORICO+ "("+rolConsulta+", '"+currentDateandTime+"')");
        }catch (Exception e){
            Log.e("Registro no historico", e.toString());
        }


        //============ CONSULTA DE EDREDOM =========

        //limpa a busca
        edredons.clear();
        try{
            Cursor c = dados.rawQuery(EdredomActivity.SELECT_EDREDONS+ "WHERE rol = " + rolConsulta, null);

            textEdredom.setText("Edredom | Qtd.: "+c.getCount());
            if(c.getCount() == 0){
                Toast.makeText(getContext(), "Nenhum edredom encontrado", Toast.LENGTH_SHORT).show();
            }else {
                int indexId = c.getColumnIndex(EdredomActivity.COLUNAS_EDREDOM[0]);
                int indexRol = c.getColumnIndex(EdredomActivity.COLUNAS_EDREDOM[1]);
                int indexPrateleira = c.getColumnIndex(EdredomActivity.COLUNAS_EDREDOM[2]);
                int indexRetirado = c.getColumnIndex(EdredomActivity.COLUNAS_EDREDOM[3]);
                c.moveToFirst();
                do {
                    Edredom edredom = new Edredom(c.getInt(indexId), c.getLong(indexRol), c.getInt(indexPrateleira), c.getInt(indexRetirado));
                    //add no indice 0 para inverter a ordem da lista
                    edredons.add(0, edredom);
                }while(c.moveToNext());
                rolText.setText("");
                //esconde o teclado
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            c.close();
        }catch (Exception e){
            Log.e("Erro consulta Edredom", e.toString());
            e.printStackTrace();
        }
        adaptadorEdredom.notifyDataSetChanged();

        //============== CONSULTA DE TAPETES ===========

        //limpa a consulta de tapetes
        tapetes.clear();

        try{
            Cursor cT = dados.rawQuery(TapeteActivity.SELECT_TAPETES + "WHERE rol = "+rolConsulta, null);

            tapeteText.setText("Tapete | Qtd.: "+cT.getCount());

            if(cT.getCount() == 0) {
                Toast.makeText(getContext(), "Nenhum tapete encontrado", Toast.LENGTH_SHORT).show();
            }else{
                int indexId = cT.getColumnIndex(TapeteActivity.COLUNAS_TAPETE[0]);
                int indexRol = cT.getColumnIndex(TapeteActivity.COLUNAS_TAPETE[1]);
                int indexMetragem = cT.getColumnIndex(TapeteActivity.COLUNAS_TAPETE[2]);

                if (cT.moveToFirst()) {
                    do {
                        Tapete tapete = new Tapete(cT.getInt(indexId), cT.getLong(indexRol), cT.getDouble(indexMetragem));
                        tapetes.add(0, tapete);
                    } while (cT.moveToNext());
                }
                rolText.setText("");
                //esconde o teclado
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            cT.close();
        }catch (Exception e){
            Log.e("BD select Tapete", e.toString());
        }finally {
            dados.close();
        }
        adaptadorTapete.notifyDataSetChanged();
    }

    public void deleteRol(View view) {

        if (rolConsulta != null) {
            new AlertDialog.Builder(getContext())
                    .setIcon(android.R.drawable.ic_delete)
                    .setTitle("Excluir")
                    .setMessage("Excluir todos os itens do Rol " + rolConsulta + "?")
                    .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i2) {
                            SQLiteDatabase dados = getContext().openOrCreateDatabase(MainActivity.NOME_BD, Context.MODE_PRIVATE, null);
                            try {
                                dados.execSQL(DELETE_EDREDOM_ROL + rolConsulta);
                                dados.execSQL(DELETE_TAPETE_ROL + rolConsulta);
                                Toast.makeText(getContext(), "Rol excluido: " + rolConsulta, Toast.LENGTH_SHORT).show();
                                edredons.clear();
                                tapetes.clear();
                                tapeteText.setText("Edredom | Qtd.: " + tapetes.size());
                                textEdredom.setText("Tapete | Qtd.: " + edredons.size());
                                //atualiza a aba de tapetes apos modificar o banco de dados nesta tela
                                TapeteActivity.selectTapete(getContext());
                                EdredomActivity.selectEdredom(getContext());
                            } catch (Exception e) {
                                Log.e("Erro delete Rol ", e.toString());
                            } finally {
                                dados.close();
                                adaptadorEdredom.notifyDataSetChanged();
                                adaptadorTapete.notifyDataSetChanged();
                            }
                        }
                    })
                    .setNegativeButton("Cancelar", null).show();

        }else{
            Toast.makeText(getContext(), "Nenhum rol selecionado", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rolText = (EditText) getView().findViewById(R.id.rolConsulta);

        //================ TAPETES =========================

        ListView tapeteList = (ListView) getView().findViewById(R.id.tapeteListConsulta);
        tapeteText = (TextView) getView().findViewById(R.id.textTapeteConsulta);

        tapetes = new ArrayList<>();

        adaptadorTapete = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, tapetes);
        tapeteList.setAdapter(adaptadorTapete);

        //listener para excluir tapetes do banco de dados
        tapeteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int index = i;
                //cria um alerta antes de excluir um edredom
                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Excluir")
                        .setMessage("Excluir o Rol "+ tapetes.get(index).getRol()+"?")
                        .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i2) {
                                SQLiteDatabase dados = getContext().openOrCreateDatabase(MainActivity.NOME_BD, Context.MODE_PRIVATE, null);
                                try {
                                    dados.execSQL(TapeteActivity.DELETE_TAPETE + tapetes.get(index).getId());
                                    Toast.makeText(getContext(), "Tapete excluido: "+tapetes.get(index).getRol(), Toast.LENGTH_SHORT).show();
                                    tapetes.remove(index);
                                    adaptadorTapete.notifyDataSetChanged();
                                    tapeteText.setText("Edredom | Qtd.: "+tapetes.size());
                                    //atualiza a aba de tapetes apos modificar o banco de dados nesta tela
                                    TapeteActivity.selectTapete(getContext());
                                }catch (Exception e){
                                    Log.e("Erro delete tapete ",e.toString());
                                }finally {
                                    dados.close();
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", null).show();
            }
        });


        //================ EDREDOM =========================
        ListView edredomList = (ListView) getView().findViewById(R.id.edredomConsulta);
        textEdredom =(TextView) getView().findViewById(R.id.textViewEdredom);

        edredons = new ArrayList<>();

        adaptadorEdredom = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, edredons);
        edredomList.setAdapter(adaptadorEdredom);

        //listener para excluir edredons do banco de dados
        edredomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int index = i;
                //cria um alerta antes de excluir um edredom
                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Excluir")
                        .setMessage("Excluir o Rol "+ edredons.get(index).getRol()+"?")
                        .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i2) {
                                SQLiteDatabase dados = getContext().openOrCreateDatabase(MainActivity.NOME_BD, Context.MODE_PRIVATE, null);
                                try {
                                    dados.execSQL(EdredomActivity.DELETE_EDREDOM + edredons.get(index).getId());
                                    Toast.makeText(getContext(), "Edredom excluido: "+edredons.get(index).getRol(), Toast.LENGTH_SHORT).show();
                                    edredons.remove(index);
                                    adaptadorEdredom.notifyDataSetChanged();
                                    textEdredom.setText("Edredom | Qtd.: "+edredons.size());
                                    //atualiza a aba de edredons apos modificar o banco de dados nesta tela
                                    EdredomActivity.selectEdredom(getContext());
                                }catch (Exception e){
                                    Log.e("Erro del edredom ",e.toString());
                                }finally {
                                    dados.close();
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", null).show();
            }
        });

        //=============== Fim EDREDOM ======================================

        //adiciona listener ao botao de exclusão
        FloatingActionButton botaoDelete = (FloatingActionButton) getView().findViewById(R.id.botaoDelete);
        botaoDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRol(view);
            }
        });

        //Coloca o listener no botao Ok de consulta
        Button bConsulta = (Button) getView().findViewById(R.id.okConsulta);
        bConsulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rolText.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "O Rol não pode ser vazio", Toast.LENGTH_SHORT).show();
                }else {
                    consulta(view);
                }
            }
        });

        //adicionar listener para fazer consulta ao pressionar enter
        rolText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean ok = false;
                if (i == EditorInfo.IME_ACTION_GO){
                    if(rolText.getText().toString().isEmpty()){
                        Toast.makeText(getContext(), "O Rol não pode ser vazio", Toast.LENGTH_SHORT).show();
                    }else {
                        consulta(getView());
                        ok = true;
                    }
                }
                return ok;
            }
        });
    } // fim onViewCreated

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_consulta, container, false);
    }

}
