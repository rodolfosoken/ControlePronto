package com.example.rodolfo.controlepronto;

import android.content.ContentValues;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class EdredomActivity extends Fragment {
    public static List<Edredom> edredons;
    public static ArrayAdapter adaptadorEdredom;
    public static final String SELECT_EDREDONS = "SELECT * FROM edredom ";
    public static final String INSERT_EDREDOM = "INSERT INTO edredom (rol, prateleira, retirado) VALUES ";
    public static final String DELETE_EDREDOM = "DELETE FROM edredom WHERE id = ";
    public static final String[] COLUNAS_EDREDOM = {"id","rol", "prateleira","retirado"};
    private EditText rolText;
    private EditText prateleiraText;
    private boolean isEditando;
    private int indexEditando;


    //retorna a listagem com os edredons do banco de dados e tbm atualiza a lista da aba de edredons
    public static List<Edredom> selectEdredom(Context context){
        List<Edredom> edredomList = new ArrayList<>();
        //limpa a lista de edredons
        edredons.clear();
        //tenta recuperar todos os edredons do banco de dados
        SQLiteDatabase dados = context.openOrCreateDatabase(MainActivity.NOME_BD, Context.MODE_PRIVATE, null);
        try {
            Cursor c = dados.rawQuery(SELECT_EDREDONS, null);

            int indexId = c.getColumnIndex(COLUNAS_EDREDOM[0]);
            int indexRol = c.getColumnIndex(COLUNAS_EDREDOM[1]);
            int indexPrateleira = c.getColumnIndex(COLUNAS_EDREDOM[2]);
            int indexRetirado = c.getColumnIndex(COLUNAS_EDREDOM[3]);

            if(c.moveToFirst()) {
                do {
                    Edredom edredom = new Edredom(c.getInt(indexId), c.getLong(indexRol),
                            c.getInt(indexPrateleira), c.getInt(indexRetirado));
                    //add no indice 0 para inverter a ordem da lista
                    edredomList.add(0, edredom);
                    edredons.add(0,edredom);
                    //System.out.println(edredom.toString());
                } while (c.moveToNext());
            }
            c.close();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            dados.close();
            adaptadorEdredom.notifyDataSetChanged();
        }
        return edredomList;
    }

    public boolean salvaEdredom(Context context, Edredom edredom){
        boolean ok = false;
        SQLiteDatabase dados = context.openOrCreateDatabase(MainActivity.NOME_BD, Context.MODE_PRIVATE, null);
        try {
            if(!isEditando) {
                dados.execSQL(INSERT_EDREDOM +
                        "(" + edredom.getRol() + ", " + edredom.getPrateleira() +
                        ", " + edredom.getRetirado() + ")");

                //recupera o id gerado pelo banco de dados;
                Cursor c = dados.rawQuery(SELECT_EDREDONS, null);
                c.moveToLast();
                int indexId = c.getColumnIndex(COLUNAS_EDREDOM[0]);
                if (!c.isNull(indexId)) edredom.setId(c.getInt(indexId));

                //aparece no topo da lista
                edredons.add(0, edredom);

                c.close();
                ok = true;
            }else{
                ContentValues cv = new ContentValues();
                cv.put(COLUNAS_EDREDOM[1], edredom.getRol());
                cv.put(COLUNAS_EDREDOM[2], edredom.getPrateleira());
                dados.update("edredom",cv,"id = "+edredom.getId(),null);
                edredons.set(indexEditando, edredom);
                isEditando = false;
                indexEditando = -1;
                ok = true;
            }
        } catch (Exception e) {
            Log.e("Erro ao add Edredom", e.toString());
        }finally {
            dados.close();
            Log.i("BD close","edredom activity");
            //atualiza a lista
            adaptadorEdredom.notifyDataSetChanged();
        }
        return ok;
    }

    public static boolean removeEdredom(Context context, int index){
        boolean ok = false;
        SQLiteDatabase dados = context.openOrCreateDatabase(MainActivity.NOME_BD, Context.MODE_PRIVATE, null);
        try {
            dados.execSQL(DELETE_EDREDOM + edredons.get(index).getId());
            Toast.makeText(context, "Edredom excluido: "+edredons.get(index).getRol(), Toast.LENGTH_SHORT).show();
            edredons.remove(index);
            adaptadorEdredom.notifyDataSetChanged();
            //limpa a lista de consulta
            ConsultaActivity.edredons.clear();
            ConsultaActivity.adaptadorEdredom.notifyDataSetChanged();
            ok= true;
        }catch (Exception e){
            Log.e("Erro del edredom ",e.toString());
        }finally {
            dados.close();
            Log.i("BD close","edredom activity");
        }
        return ok;
    }

    //ação do botao adicionar
    public void adicionarEdredom(View view){
        EditText rolText = (EditText) getView().findViewById(R.id.rolEdredom);
        EditText prateleiraText = (EditText) getView().findViewById(R.id.prateleiraText);
        //System.out.println(rolText.getText().toString());

        //verifica se a entrada está vazia antes de inserir no banco de dados
        if(!rolText.getText().toString().isEmpty() &&  !prateleiraText.getText().toString().isEmpty()) {
            long rol = Long.parseLong(rolText.getText().toString());
            int prateleira = Integer.parseInt(prateleiraText.getText().toString());

            Edredom edredom;
            if (!isEditando) {
                edredom = new Edredom(rol, prateleira);
                //System.out.println(edredom.toString());
            }else{
                edredom = new Edredom(edredons.get(indexEditando).getId(), rol, prateleira, 0);
            }
            //salva no banco de dados
            if(salvaEdredom(getContext(),edredom)) {
                //limpa a entrada
                rolText.setText("");
                prateleiraText.setText("");
                rolText.requestFocus();
                //esconde o teclado
                /*InputMethodManager imm = (InputMethodManager)
                getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);*/
                //restaura a cor dos editTexts
                rolText.getBackground().clearColorFilter();
                prateleiraText.getBackground().clearColorFilter();
            }
        }else {
            rolText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SCREEN);
            prateleiraText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SCREEN);
            Toast.makeText(this.getContext(), "Insira o Rol e prateleira", Toast.LENGTH_SHORT).show();
        }

    }


    public boolean atualizaEdredom(Context context, int index){
        boolean ok = false;
        isEditando = true;
        indexEditando = index;
        SQLiteDatabase dados = context.openOrCreateDatabase(MainActivity.NOME_BD, Context.MODE_PRIVATE, null);
        try{
            //carrega informações nos campos
            rolText.setText(String.valueOf(edredons.get(index).getRol()));
            prateleiraText.setText(String.valueOf(edredons.get(index).getPrateleira()));

            ok = true;
        }catch (Exception e){
            Log.e("BD atualizar edredom", e.toString());
        }finally {
            dados.close();
        }
        return ok;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rolText = (EditText) getView().findViewById(R.id.rolEdredom);
        prateleiraText = (EditText) getView().findViewById(R.id.prateleiraText);


        //adiciona o listener no botao adicionar
        Button buttonAdicionar = (Button) getView().findViewById(R.id.bAdicionarEdredom);
        buttonAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adicionarEdredom(view);
            }
        });

        ListView edredomList = (ListView) getView().findViewById(R.id.edredomList);
        edredons = new ArrayList<>();

        adaptadorEdredom = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, edredons);
        edredomList.setAdapter(adaptadorEdredom);

        selectEdredom(getContext());

        //listener para atualizar edredons do banco de dados
        edredomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int index = i;
                //cria um alerta antes de excluir um edredom
                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_menu_edit)
                        .setTitle("Editar")
                        .setMessage("Editar o Rol "+ edredons.get(index).getRol()+"?")
                        .setPositiveButton("Editar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i2) {
                                //editar
                                atualizaEdredom(getContext(), index);
                            }
                        })
                        .setNegativeButton("Cancelar", null).show();
            }
        });

        //adiciona listener para excluir com pressionar longo
        edredomList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int index = i;
                //cria um alerta antes de excluir um edredom
                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Excluir")
                        .setMessage("Excluir o Rol "+ edredons.get(index).getRol()+"?")
                        .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i2) {
                                removeEdredom(getContext(),index);
                            }
                        })
                        .setNegativeButton("Cancelar", null).show();
                return true;
            }
        });

        //adiciona um listener para adicionar edredom ao pressionar enter
        prateleiraText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean ok = false;
                if(i == EditorInfo.IME_ACTION_GO){
                    ok = true;
                    adicionarEdredom(getView());
                }
                return ok;
            }
        });



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edredom, container, false);
    }

}
