package br.com.vendasoffline.vendasoffline.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import br.com.vendasoffline.vendasoffline.R;

/**
 * Created by lrgabriel on 26/05/17.
 */

public class FragmentCadastroCliente extends Fragment{

    private View view;
    private Button btnCadastrar;
    private Button btnCancelar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //---Inflate the layout for this fragment---
        view = inflater.inflate(R.layout.fragment_cadastro_cliente, container, false);

        btnCancelar = (Button) view.findViewById(R.id.btnCancelar);

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Saí do fragment atual.
                getActivity().getFragmentManager().popBackStack();
            }
        });

        return view;
    }

}
