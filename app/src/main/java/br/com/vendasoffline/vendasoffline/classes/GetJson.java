package br.com.vendasoffline.vendasoffline.classes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import br.com.vendasoffline.vendasoffline.activities.MainActivity;
import br.com.vendasoffline.vendasoffline.helpers.NetworkChangeReceiver;
import br.com.vendasoffline.vendasoffline.helpers.Utils;
import br.com.vendasoffline.vendasoffline.model.Customer;
import br.com.vendasoffline.vendasoffline.sql.DatabaseHelper;

/**
 * Created by home on 26/06/17.
 */

public class GetJson extends AsyncTask<Void, Void, Customer> {

    private Activity activity;
    private Context context;
    private ProgressDialog load;

    public GetJson(Activity activity) {
        this.activity = activity;
    }

    public GetJson(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        if (activity != null){
            load = ProgressDialog.show(activity, "Por favor Aguarde ...", "Recuperando Informações do Servidor...");
        }
    }

    @Override
    protected Customer doInBackground(Void... params) {
        Customer c=null;

        // Feito isso pois quando é executado pelo broadcast receiver, vem o contexto e não a atividade.
        // E quando é executado direto pelo programa, vem a atividade, para que a animação do load, apareça na tela.
        if (context == null){
            context = activity;
        }

        if (new NetworkChangeReceiver().isOnline(context)){
            Utils util = new Utils();
            //c = util.getInformacao("https://randomuser.me/api/");
            c = util.getInformacao("http://web.effectiveerp.com.br:88/teste/ECommerce/ErpRestService.svc/AndroidListarClientes/2");

            (new DatabaseHelper(context)).addCustomer(c);
        }

        return c;
    }

    @Override
    protected void onPostExecute(Customer cliente){
        if (activity != null){
            load.dismiss();
        }
    }
}
