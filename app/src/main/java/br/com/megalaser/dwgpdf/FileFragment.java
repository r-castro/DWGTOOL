package br.com.megalaser.dwgpdf;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

public class FileFragment extends Fragment {

    public static final String TAG = "file";
    private static final String FILECACHE = "fileCache";
    private static final int WRITE_REQUEST_CODE = 43;
    private static final String shfiles = "shfiles";
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private View view;
    private ArrayList<String> files;

    SharedPreferences sharedPreferences;




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sharedPreferences = this.getActivity().getSharedPreferences(FILECACHE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        Log.d("Log_SMB", "onAttach: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Log.d("Log_SMB", "onCreateView: ");
        view = inflater.inflate(R.layout.file_fragment, container, false);
        return view;
    }




    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("Log_SMB", "onActivityCreated: ");

        listView = view.findViewById(R.id.listViewFiles);
        files = new ArrayList<String>();
        sharedPreferences = this.getActivity().getSharedPreferences(FILECACHE, Context.MODE_PRIVATE);

        if (savedInstanceState != null) {

            files = savedInstanceState.getStringArrayList("files");
            arrayAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, files);
            listView.setAdapter(arrayAdapter);
            Log.d("Log_SMB", "SvIns: " + String.valueOf(files.size()));

        } else if (sharedPreferences.contains("value") && DwgActivity.results.isEmpty()) {
            Set<String> set = sharedPreferences.getStringSet("value", null);

            for (String str : set) {
                files.add(str);
            }

            arrayAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, files);
            listView.setAdapter(arrayAdapter);
            Log.d("Log_SMB", "Share: " + String.valueOf(files.size()));

        } else {
            files = DwgActivity.results;
            arrayAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, files);
            listView.setAdapter(arrayAdapter);
            Log.d("Log_SMB", "Dwg: " + String.valueOf(files.size()));
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String fileName = adapterView.getItemAtPosition(i).toString();
                File storageDir = new File(Environment.getExternalStorageDirectory() + "/smb/");
                File dwgPdf = new File(storageDir, fileName);


                Uri uri = FileProvider.getUriForFile(view.getContext(), "br.com.megalaser.dwgpdf", dwgPdf);

                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(intent);

            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();
        sharedPreferences = this.getActivity().getSharedPreferences(FILECACHE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> set = new HashSet<String>();
        for (String value : files) {
            set.add(value);
        }

        editor.putStringSet("value", set);
        editor.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("files", files);
        Log.d("Log_SMB", "Save: " + String.valueOf(files.size()));
    }
}
