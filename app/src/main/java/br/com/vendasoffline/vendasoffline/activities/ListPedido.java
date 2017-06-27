package br.com.vendasoffline.vendasoffline.activities;

import android.database.Cursor;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import br.com.vendasoffline.vendasoffline.R;
import br.com.vendasoffline.vendasoffline.fragments.FragmentCadastroCliente;
import br.com.vendasoffline.vendasoffline.fragments.FragmentCadastroPedido;
import br.com.vendasoffline.vendasoffline.sql.DatabaseHelper;

public class ListPedido extends AppCompatActivity {

    private ListView clienteListView;
    private EditText pesquisar;
    private CursorAdapter clienteAdapter; // Adaptador para a ListView
    private String whereClause="";
    private String[] whereArgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);
        getSupportActionBar().hide();

        clienteListView = (ListView) findViewById(R.id.listView);
        pesquisar = (EditText) findViewById(R.id.edtConsulta);

        // mapeia cada coluna da tabela com um componente da tela
        String[] origem = new String[]{"CLA001_NOME","CLA001_TIPOPESSOA","CLA001_CNPJ","CLA001_CIDADE","CLA001_UF"};
        int[] destino = new int[] { R.id.txtNome, R.id.txtTipoPessoa, R.id.txtCPF,R.id.txtCidade,R.id.txtUf};
        int flags = 0;

        clienteAdapter = new SimpleCursorAdapter(ListPedido.this,R.layout.activity_view_pedido,null,origem,destino,flags);
        clienteListView.setAdapter(clienteAdapter);

        FloatingActionButton fabPedido = (FloatingActionButton) findViewById(R.id.fabPedido);
        fabPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new FragmentCadastroPedido();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.drawer_layout_pedido, fragment, fragment.getClass().getSimpleName()).addToBackStack("cadastroPedido").commit();
            }
        });

        pesquisar.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                getPedidos();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    @Override
    protected void onStart(){
        //sempre que executar onResume, irá fazer uma busca no banco de dados
        super.onStart();
        getPedidos();
    }

    private class getPedidos extends AsyncTask<Object, Object, Cursor> {
        DatabaseHelper dbConnection = new DatabaseHelper(ListPedido.this);
        @Override
        protected Cursor doInBackground(Object... params){
            return dbConnection.getClientes(whereClause,whereArgs); //retorna a Pontuação
        }
        // usa o cursor retornado pelo doInBackground
        @Override
        protected void onPostExecute(Cursor result){
            try {
                clienteAdapter.changeCursor(result); //altera o cursor para um novo cursor
                dbConnection.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void getPedidos(){
        whereClause = "CLA001_NOME LIKE ? OR CLA001_CIDADE LIKE ? OR CLA001_TIPOPESSOA LIKE ? OR CLA001_CNPJ LIKE ?";
        whereArgs = new String[] {"%"+pesquisar.getText().toString()+"%",
                "%"+pesquisar.getText().toString()+"%",
                "%"+pesquisar.getText().toString()+"%",
                "%"+pesquisar.getText().toString()+"%"};
        new ListPedido.getPedidos().execute();
    }
}
