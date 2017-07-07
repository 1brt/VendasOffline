package br.com.vendasoffline.vendasoffline.activities;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import br.com.vendasoffline.vendasoffline.R;
import br.com.vendasoffline.vendasoffline.fragments.FragmentCadastroCliente;
import br.com.vendasoffline.vendasoffline.sql.DatabaseHelper;

/**
 * Created by lrgabriel on 21/06/17.
 */

public class ListCliente extends AppCompatActivity{

    private ListView clienteListView;
    private EditText pesquisar;
    private CursorAdapter clienteAdapter; // Adaptador para a ListView
    private String whereClause="";
    private String[] whereArgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);
        getSupportActionBar().hide();

        clienteListView = (ListView) findViewById(R.id.listView);
        pesquisar = (EditText) findViewById(R.id.edtConsulta);

        // mapeia cada coluna da tabela com um componente da tela
        String[] origem = new String[]{"CLA001_NOME","CLA001_TIPOPESSOA","CLA001_CNPJ","CLA001_CIDADE","CLA001_UF"};
        int[] destino = new int[] { R.id.txtNome, R.id.txtTipoPessoa, R.id.txtCPF,R.id.txtCidade,R.id.txtUf};
        int flags = 0;

        clienteAdapter = new SimpleCursorAdapter(ListCliente.this,R.layout.activity_view_cliente,null,origem,destino,flags);
        clienteListView.setAdapter(clienteAdapter);

        registerForContextMenu(clienteListView);

        FloatingActionButton fabCliente = (FloatingActionButton) findViewById(R.id.fabCliente);
        fabCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new FragmentCadastroCliente();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.drawer_layout_cliente, fragment, fragment.getClass().getSimpleName()).addToBackStack("cadastroCliente").commit();
            }
        });

        pesquisar.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                getCustomer();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

    }

    @Override
    protected void onStart(){
        //sempre que executar onResume, irá fazer uma busca no banco de dados
        super.onStart();
        getCustomer();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.listView) {
            //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            // Utilizado para vibrar o celular.
            Vibrator vibe = (Vibrator) getBaseContext().getSystemService(Context.VIBRATOR_SERVICE) ;
            vibe.vibrate(50);

            String[] menuItems = new String[] {"Excluir"};
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();

        if (menuItemIndex == 0){  // Excluir
            if (new DatabaseHelper(getBaseContext()).delCustomer(info.id)){
                new getCustomer().execute();
            }else{
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_delete_message), Snackbar.LENGTH_LONG).show();
            }
        }

        return true;
    }

    private class getCustomer extends AsyncTask<Object, Object, Cursor> {
        DatabaseHelper dbConnection = new DatabaseHelper(ListCliente.this);
        @Override
        protected Cursor doInBackground(Object... params){
            return dbConnection.getCustomer(whereClause,whereArgs); //retorna a Pontuação
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

    public void getCustomer(){
        whereClause = "CLA001_NOME LIKE ? OR CLA001_CIDADE LIKE ? OR CLA001_TIPOPESSOA LIKE ? OR CLA001_CNPJ LIKE ?";
        whereArgs = new String[] {"%"+pesquisar.getText().toString()+"%",
                "%"+pesquisar.getText().toString()+"%",
                "%"+pesquisar.getText().toString()+"%",
                "%"+pesquisar.getText().toString()+"%"};
        new getCustomer().execute();
    }
}
