package br.com.megalaser.dwgpdf;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by rodrigo on 02/02/18.
 */

public class DetailFragment extends Fragment {

    public static final String TAG = "detail";
    private static final String detailCache = "detailCache";

    private static final String textRefCod = "text_ref_cod";
    private static final String textRefClient = "text_ref_client";
    private static final String textRevision = "text_revision";
    private static final String textTypeMaterial = "text_type_material";
    private static final String textDenomination = "text_denomination";
    private static final String textNameClient = "text_name_client";
    SharedPreferences sharedPreferences;
    //TextView _textViewClientName;
    TextView _textRefCod;
    TextView _textRefClient;
    TextView _textRevision;
    TextView _textTypeMaterial;
    TextView _textDenomination;
    TextView _textNameClient;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
        sharedPreferences = this.getActivity().getSharedPreferences(detailCache, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: ");
        View view;
        view = inflater.inflate(R.layout.fragment_detail, container, false);

        _textRefCod = getActivity().findViewById(R.id.textRefCod);
        _textRefClient = getActivity().findViewById(R.id.textCodClient);
        _textRevision = getActivity().findViewById(R.id.textRevision);
        _textDenomination = getActivity().findViewById(R.id.textDenomination);
        _textTypeMaterial = getActivity().findViewById(R.id.textTypeMaterial);
        _textNameClient = getActivity().findViewById(R.id.textNameClient);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();

        _textRefCod = getActivity().findViewById(R.id.textRefCod);
        _textRefClient = getActivity().findViewById(R.id.textCodClient);
        _textRevision = getActivity().findViewById(R.id.textRevision);
        _textDenomination = getActivity().findViewById(R.id.textDenomination);
        _textTypeMaterial = getActivity().findViewById(R.id.textTypeMaterial);
        _textNameClient = getActivity().findViewById(R.id.textNameClient);

        if (savedInstanceState != null) {
            _textRefCod.setText(savedInstanceState.getString("_ref_cod"));
            _textRefClient.setText(savedInstanceState.getString("_ref_client"));
            _textRevision.setText(savedInstanceState.getString("_revision"));
            _textDenomination.setText(savedInstanceState.getString("_denomination"));
            _textTypeMaterial.setText(savedInstanceState.getString("_type_material"));
            _textNameClient.setText(savedInstanceState.getString("_name_client"));
        } else {
             sharedPreferences = this.getActivity().getSharedPreferences(detailCache, Context.MODE_PRIVATE);
            if (sharedPreferences.contains(textRefCod)) {
                _textRefCod.setText(sharedPreferences.getString(textRefCod, ""));
            }
            if (sharedPreferences.contains(textRefClient)) {
                _textRefClient.setText(sharedPreferences.getString(textRefClient, ""));
            }
            if (sharedPreferences.contains(textRevision)) {
                _textRevision.setText(sharedPreferences.getString(textRevision, ""));
            }
            if (sharedPreferences.contains(textDenomination)) {
                _textDenomination.setText(sharedPreferences.getString(textDenomination, ""));
            }
            if (sharedPreferences.contains(textTypeMaterial)) {
                _textTypeMaterial.setText(sharedPreferences.getString(textTypeMaterial, ""));
            }
            if (sharedPreferences.contains(textNameClient)) {
                _textNameClient.setText(sharedPreferences.getString(textNameClient, ""));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sharedPreferences = this.getActivity().getSharedPreferences(detailCache, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(textRefCod, _textRefCod.getText().toString());
        editor.putString(textRefClient, _textRefClient.getText().toString());
        editor.putString(textRevision, _textRevision.getText().toString());
        editor.putString(textDenomination, _textDenomination.getText().toString());
        editor.putString(textTypeMaterial, _textTypeMaterial.getText().toString());
        editor.putString(textNameClient, _textNameClient.getText().toString());
        editor.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("_name_client", _textNameClient.getText().toString());
        outState.putString("_ref_cod", _textRefCod.getText().toString());
        outState.putString("_ref_client", _textRefClient.getText().toString());
        outState.putString("_revision", _textRevision.getText().toString());
        outState.putString("_type_material", _textTypeMaterial.getText().toString());
        outState.putString("_denomination", _textDenomination.getText().toString());

    }

}


