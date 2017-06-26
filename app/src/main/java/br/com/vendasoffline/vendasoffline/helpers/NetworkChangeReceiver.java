package br.com.vendasoffline.vendasoffline.helpers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.security.spec.ECField;

import br.com.vendasoffline.vendasoffline.classes.GetJson;
import br.com.vendasoffline.vendasoffline.model.Customer;
import br.com.vendasoffline.vendasoffline.sql.DatabaseHelper;

/**
 * Created by lrgabriel on 06/06/17.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (isOnline(context)){
            // Do something

            Toast.makeText(context, "network available", Toast.LENGTH_SHORT).show();
            try {
                /*Customer c = new Customer();
                c.setNome("d9awud9wa");
                c.setCnpj("224242");
                c.setTipoPessoa("Jurídica");
                new DatabaseHelper(context).addCustomer(c);*/
                (new GetJson(context)).execute();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

}
