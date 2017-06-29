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
import br.com.vendasoffline.vendasoffline.webService.NetworkUtils;

/**
 * Created by allanromanato on 11/4/15.
 */
public class Utils {

    public ArrayList<Customer> getInformacao(String end){
        String json;

        json = NetworkUtils.getJSONFromAPI(end);

        //Log.i("Resultado", json);

        //retorno = parseJson("<Clientes><Cliente><CodPessoa>21312sad</CodPessoa><NomePessoa>TESTE</NomePessoa><Endereco>RUA TRONCA</Endereco><Cidade>CAXIAS DO SUL</Cidade><Cep>95010100</Cep><Uf>RS</Uf><Pais>BR</Pais><Cnpj>01845526058</Cnpj><TipoPessoa>Física</TipoPessoa><Telefone/><Email/><CodTransportadora>940</CodTransportadora><NomeTransportadora>ALFA TRANSPORTES ESPECIAIS LTDA</NomeTransportadora><TipoFrete>97</TipoFrete><DescrTipoFrete>Frete FOB</DescrTipoFrete><CodListaPreco>LSTATUAL</CodListaPreco><DescrListaPreco>LISTA TESTE</DescrListaPreco></Cliente><Cliente><CodPessoa>2102</CodPessoa><NomePessoa>TESTE</NomePessoa><Endereco>RUA TRONCA</Endereco><Cidade>CAXIAS DO SUL</Cidade><Cep>95010100</Cep><Uf>RS</Uf><Pais>BR</Pais><Cnpj>01845526058</Cnpj><TipoPessoa>Física</TipoPessoa><Telefone/><Email/><CodTransportadora>940</CodTransportadora><NomeTransportadora>ALFA TRANSPORTES ESPECIAIS LTDA</NomeTransportadora><TipoFrete>97</TipoFrete><DescrTipoFrete>Frete FOB</DescrTipoFrete><CodListaPreco>LSTATUAL</CodListaPreco><DescrListaPreco>LISTA TESTE</DescrListaPreco></Cliente><Cliente><CodPessoa>2103</CodPessoa><NomePessoa>TESTE</NomePessoa><Endereco>RUA TRONCA</Endereco><Cidade>CAXIAS DO SUL</Cidade><Cep>95010100</Cep><Uf>RS</Uf><Pais>BR</Pais><Cnpj>01845526058</Cnpj><TipoPessoa>Física</TipoPessoa><Telefone/><Email/><CodTransportadora>940</CodTransportadora><NomeTransportadora>ALFA TRANSPORTES ESPECIAIS LTDA</NomeTransportadora><TipoFrete>97</TipoFrete><DescrTipoFrete>Frete FOB</DescrTipoFrete><CodListaPreco>LSTATUAL</CodListaPreco><DescrListaPreco>LISTA TESTE</DescrListaPreco></Cliente></Clientes>");

        return parseJson(json);
    }

    private ArrayList<Customer> parseJson(String json){
        ArrayList<Customer> arrayList = new ArrayList<>();

        try {

            JSONObject jsonObj = XML.toJSONObject(json);
            //JSONObject jsonObj = new JSONObject(json);
            JSONObject userDetails = jsonObj.getJSONObject("AndroidClientes");
            //JSONObject usera = userDetails.getJSONObject("Cliente");
            JSONArray array = userDetails.getJSONArray("AndroidCliente");
            JSONObject cli;
            for (int i=0; i <= array.length() - 1 ; i++){
                cli = array.getJSONObject(i);
                Customer cliente = new Customer();
                cliente.setNome(cli.getString("NomePessoa"));
                cliente.setTipoPessoa(cli.getString("TipoPessoa"));
                cliente.setCnpj(cli.getString("Cnpj"));
                cliente.setPais(cli.getString("Pais"));
                cliente.setUf(cli.getString("Uf"));
                cliente.setCidade(cli.getString("Cidade"));
                cliente.setEndereco(cli.getString("Endereco"));
                cliente.setCep(cli.getString("Cep"));
                cliente.setSinc(1);

                arrayList.add(cliente);
            }

            return arrayList;

            /*JSONObject usera = array.getJSONObject(0);
            cliente.setNome(usera.getString("NomePessoa"));
            cliente.setEndereco(usera.getString("Endereco"));
            cliente.setCidade(usera.getString("Cidade"));
            cliente.setCep(usera.getString("Cep"));
            cliente.setCnpj(usera.getString("Cnpj"));
            cliente.setPais(usera.getString("Pais"));
            cliente.setUf(usera.getString("Uf"));
            cliente.setTipoPessoa(usera.getString("TipoPessoa"));*/
            //JSONArray array = jsonObj.getJSONArray("Pedidos");

            /*SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date data;*/

            //JSONObject objArray = array.getJSONObject(0);

            //JSONObject obj = objArray.getJSONObject("login");
            //Atribui os objetos que estão nas camadas mais altas
            /*cliente.setEmail(obj.getString("email"));
            cliente.setUsername(obj.getString("username"));
            cliente.setSenha(obj.getString("password"));
            cliente.setTelefone(obj.getString("phone"));*/
            //data = new Date(obj.getLong("dob")*1000);
            //cliente.setNascimento(sdf.format(data));

            //Nome da pessoa é um objeto, instancia um novo JSONObject
            //JSONObject nome = usera.getJSONObject("CodPessoa");

            //cliente.setNome(nome.getString("first")+" "+nome.getString("last"));

            //Endereco tambem é um Objeto
            //JSONObject endereco = objArray.getJSONObject("location");
            /*cliente.setEndereco(endereco.getString("street"));
            cliente.setUf(endereco.getString("state"));
            cliente.setCidade(endereco.getString("city"));
            cliente.setCep(endereco.getString("postcode"));
            cliente.setCnpj(endereco.getString("postcode"));*/
            //Imagem eh um objeto
            /*JSONObject foto = obj.getJSONObject("picture");
            cliente.setFoto(baixarImagem(foto.getString("large")));*/

        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }
}