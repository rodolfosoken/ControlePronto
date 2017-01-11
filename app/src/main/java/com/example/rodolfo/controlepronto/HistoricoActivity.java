package com.example.rodolfo.controlepronto;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HistoricoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        ListView listHistorico = (ListView) findViewById(R.id.historicoList);
        List<String> historico = new ArrayList<>();

        SQLiteDatabase dados = this.openOrCreateDatabase(MainActivity.NOME_BD, Context.MODE_PRIVATE, null);
        try{
            Cursor c = dados.rawQuery("SELECT * FROM historico ", null);
            if(c.moveToFirst()) {
                int rolIndex = c.getColumnIndex("rol");
                int dataIndex = c.getColumnIndex("data");

                SimpleDateFormat sdf = new SimpleDateFormat("DD-MM-yyyy HH:MM");

                do {
                    String registro = "Rol: " + c.getString(rolIndex) + "  |   " + c.getString(dataIndex);
                    historico.add(0, registro);
                } while (c.moveToNext());
            }
        }catch (Exception e){
            Log.e("Historico", e.toString());
        }finally {
            dados.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, historico);
        listHistorico.setAdapter(adapter);

    }
}
