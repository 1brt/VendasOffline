package br.com.vendasoffline.vendasoffline.helpers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import br.com.vendasoffline.vendasoffline.model.Customer;
import br.com.vendasoffline.vendasoffline.model.Produto;
import br.com.vendasoffline.vendasoffline.webService.NetworkUtils;

/**
 * Created by allanromanato on 11/4/15.
 */

public class Utils {

    private String tipo;

    public Utils(String tipo){
        this.tipo = tipo;
    }

    public <T> ArrayList<T> getInformacao(String end){
        String json;

        json = NetworkUtils.getJSONFromAPI(end);

        return parseJson(json);
    }

    private <T> ArrayList<T> parseJson(String json){
        ArrayList<T> arrayList = new ArrayList<>();

        String userDet="";
        String userArr="";

        try {

            if (tipo.equals("Cliente")){
                userDet = "AndroidClientes";
                userArr = "AndroidCliente";
            }else if (tipo.equals("Produto")){
                userDet = "AndroidProdutos";
                userArr = "AndroidProduto";
            }

            JSONObject jsonObj = XML.toJSONObject(json);
            JSONObject userDetails = jsonObj.getJSONObject(userDet);
            JSONArray array = userDetails.getJSONArray(userArr);
            JSONObject dado;

            for (int i=0; i <= array.length() - 1 ; i++){
                dado = array.getJSONObject(i);

                if (tipo.equals("Cliente")){
                    Customer cliente = new Customer();
                    cliente.setNome(dado.getString("NomePessoa"));
                    cliente.setTipoPessoa(dado.getString("TipoPessoa"));
                    cliente.setCnpj(dado.getString("Cnpj"));
                    cliente.setPais(dado.getString("Pais"));
                    cliente.setUf(dado.getString("Uf"));
                    cliente.setCidade(dado.getString("Cidade"));
                    cliente.setEndereco(dado.getString("Endereco"));
                    cliente.setCep(dado.getString("Cep"));
                    cliente.setSinc(1);

                    arrayList.add((T) cliente);

                }else if (tipo.equals("Produto")){
                    Produto produto = new Produto();

                    produto.setCodigo(dado.getString("CodProduto"));
                    produto.setDescricao(dado.getString("DescrProduto"));

                    arrayList.add((T) produto);

                }
            }

            return arrayList;

        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }
}