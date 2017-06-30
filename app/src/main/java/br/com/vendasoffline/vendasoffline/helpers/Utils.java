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

        //Log.i("Resultado", json);

        //retorno = parseJson("<Clientes><Cliente><CodPessoa>21312sad</CodPessoa><NomePessoa>TESTE</NomePessoa><Endereco>RUA TRONCA</Endereco><Cidade>CAXIAS DO SUL</Cidade><Cep>95010100</Cep><Uf>RS</Uf><Pais>BR</Pais><Cnpj>01845526058</Cnpj><TipoPessoa>Física</TipoPessoa><Telefone/><Email/><CodTransportadora>940</CodTransportadora><NomeTransportadora>ALFA TRANSPORTES ESPECIAIS LTDA</NomeTransportadora><TipoFrete>97</TipoFrete><DescrTipoFrete>Frete FOB</DescrTipoFrete><CodListaPreco>LSTATUAL</CodListaPreco><DescrListaPreco>LISTA TESTE</DescrListaPreco></Cliente><Cliente><CodPessoa>2102</CodPessoa><NomePessoa>TESTE</NomePessoa><Endereco>RUA TRONCA</Endereco><Cidade>CAXIAS DO SUL</Cidade><Cep>95010100</Cep><Uf>RS</Uf><Pais>BR</Pais><Cnpj>01845526058</Cnpj><TipoPessoa>Física</TipoPessoa><Telefone/><Email/><CodTransportadora>940</CodTransportadora><NomeTransportadora>ALFA TRANSPORTES ESPECIAIS LTDA</NomeTransportadora><TipoFrete>97</TipoFrete><DescrTipoFrete>Frete FOB</DescrTipoFrete><CodListaPreco>LSTATUAL</CodListaPreco><DescrListaPreco>LISTA TESTE</DescrListaPreco></Cliente><Cliente><CodPessoa>2103</CodPessoa><NomePessoa>TESTE</NomePessoa><Endereco>RUA TRONCA</Endereco><Cidade>CAXIAS DO SUL</Cidade><Cep>95010100</Cep><Uf>RS</Uf><Pais>BR</Pais><Cnpj>01845526058</Cnpj><TipoPessoa>Física</TipoPessoa><Telefone/><Email/><CodTransportadora>940</CodTransportadora><NomeTransportadora>ALFA TRANSPORTES ESPECIAIS LTDA</NomeTransportadora><TipoFrete>97</TipoFrete><DescrTipoFrete>Frete FOB</DescrTipoFrete><CodListaPreco>LSTATUAL</CodListaPreco><DescrListaPreco>LISTA TESTE</DescrListaPreco></Cliente></Clientes>");

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
            //JSONObject jsonObj = new JSONObject(json);
            JSONObject userDetails = jsonObj.getJSONObject(userDet);
            //JSONObject usera = userDetails.getJSONObject("Cliente");
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