package br.com.vendasoffline.vendasoffline.classes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import br.com.vendasoffline.vendasoffline.activities.MainActivity;
import br.com.vendasoffline.vendasoffline.helpers.Utils;
import br.com.vendasoffline.vendasoffline.model.Customer;
import br.com.vendasoffline.vendasoffline.sql.DatabaseHelper;

/**
 * Created by home on 26/06/17.
 */

public class GetJson extends AsyncTask<Void, Void, Customer> {

    private Activity activity;
    private ProgressDialog load;

    public GetJson(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        load = ProgressDialog.show(activity, "Por favor Aguarde ...", "Recuperando Informações do Servidor...");
    }

    @Override
    protected Customer doInBackground(Void... params) {

        Utils util = new Utils();
        Customer c;
        c = util.getInformacao("https://randomuser.me/api/");
        c.setPais("sdioasjd");
        c.setTipoPessoa("J");

        (new DatabaseHelper(activity)).addCustomer(c);
        return c;
    }

    @Override
    protected void onPostExecute(Customer cliente){
        load.dismiss();
    }
}
