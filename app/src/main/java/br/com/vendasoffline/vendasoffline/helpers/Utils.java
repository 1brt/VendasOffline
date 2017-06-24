package br.com.vendasoffline.vendasoffline.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import br.com.vendasoffline.vendasoffline.model.Customer;
import br.com.vendasoffline.vendasoffline.sql.DatabaseHelper;
import br.com.vendasoffline.vendasoffline.webService.NetworkUtils;

/**
 * Created by allanromanato on 11/4/15.
 */
public class Utils {

    public Customer getInformacao(String end){
        String json;
        Customer retorno;
        json = NetworkUtils.getJSONFromAPI(end);
        Log.i("Resultado", json);

        retorno = parseJson(json);

        return retorno;
    }

    private Customer parseJson(String json){
        try {
            Customer cliente = new Customer();

            JSONObject jsonObj = new JSONObject(json);
            JSONArray array = jsonObj.getJSONArray("results");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date data;

            JSONObject objArray = array.getJSONObject(0);

            //JSONObject obj = objArray.getJSONObject("login");
            //Atribui os objetos que estão nas camadas mais altas
            /*cliente.setEmail(obj.getString("email"));
            cliente.setUsername(obj.getString("username"));
            cliente.setSenha(obj.getString("password"));
            cliente.setTelefone(obj.getString("phone"));*/
            //data = new Date(obj.getLong("dob")*1000);
            //cliente.setNascimento(sdf.format(data));

            //Nome da pessoa é um objeto, instancia um novo JSONObject
            JSONObject nome = objArray.getJSONObject("name");
            cliente.setNome(nome.getString("first")+" "+nome.getString("last"));

            //Endereco tambem é um Objeto
            JSONObject endereco = objArray.getJSONObject("location");
            cliente.setEndereco(endereco.getString("street"));
            cliente.setUf(endereco.getString("state"));
            cliente.setCidade(endereco.getString("city"));
            cliente.setCep(endereco.getString("postcode"));
            cliente.setCnpj(endereco.getString("postcode"));
            //Imagem eh um objeto
            /*JSONObject foto = obj.getJSONObject("picture");
            cliente.setFoto(baixarImagem(foto.getString("large")));*/

            return cliente;
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }
}