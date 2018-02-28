package br.com.megalaser.dwgpdf;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import jcifs.Config;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

public class DwgActivity extends AppCompatActivity {

    public static NtlmPasswordAuthentication auth;

    public static ArrayList<String> results;

    public static final String config = "config";
    public static final String server = "server";
    public static final String port = "port";
    public static final String database = "database";
    public static final String user = "user";
    public static final String password = "password";
    private static boolean STATE = false;
    private static StringBuilder url = null;
    SharedPreferences sharedPreferences;
    DetailFragment detailFragment;
    FileFragment fileFragment;
    private String value;
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.details_menu:
                    fragment = getSupportFragmentManager().findFragmentByTag("detail");
                    loadFragment(fragment, "detail");
                    return true;
                case R.id.file_menu:
                    fragment = getSupportFragmentManager().findFragmentByTag("file");
                    loadFragment(fragment, "file");
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dwg);

        ActivityCompat.requestPermissions(DwgActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        ActivityCompat.requestPermissions(DwgActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        sharedPreferences = getSharedPreferences(config, Context.MODE_PRIVATE);
        url = new StringBuilder();


        if (sharedPreferences.contains(server) && sharedPreferences.contains(port) &&
                sharedPreferences.contains(database) && sharedPreferences.contains(user) &&
                sharedPreferences.contains(password)) {

            url.append("jdbc:firebirdsql://")
                    .append(sharedPreferences.getString(server, null))
                    .append(":")
                    .append(sharedPreferences.getString(port, null))
                    .append("/")
                    .append(sharedPreferences.getString(database, null));

        }

        BottomNavigationView navigationView = findViewById(R.id.navigator);
        navigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        boolean frag = getSupportFragmentManager().getFragments().isEmpty();

        if (frag) {
            detailFragment = new DetailFragment();
            fileFragment = new FileFragment();
            loadFragment(fileFragment, "file");
            loadFragment(detailFragment, "detail");
        }

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectNetwork()
                .build());

        Config.setProperty("jcifs.netbios.wins", "192.168.23.2");
        auth = new NtlmPasswordAuthentication("megalaser", "administrador", "MlAdmSrv410t");

        results = new ArrayList<String>();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                TextView textNameClient = findViewById(R.id.textNameClient);
                TextView textRefCod = findViewById(R.id.textRefCod);
                TextView textRefClient = findViewById(R.id.textCodClient);
                TextView textRevision = findViewById(R.id.textRevision);
                TextView textTypeMaterial = findViewById(R.id.textTypeMaterial);
                TextView textDenomination = findViewById(R.id.textDenomination);

                ArrayList<PartsInfo> list = new ArrayList<PartsInfo>();
                list = testConection(s);
                if (!list.isEmpty()) {
                    for (PartsInfo info : list) {
                        textNameClient.setText(info.getNameClient());
                        textRefCod.setText(info.getRefCod());
                        textRefClient.setText(info.getRefCodClient());
                        textRevision.setText(info.getRevision());
                        textTypeMaterial.setText(info.getTypeMaterial());
                        textDenomination.setText(info.getDenomination());
                    }

                    getFilesFromServer(s);
                    saveFilesForTempFolder(s, results);

                } else {

                    AlertDialog alertDialog = new AlertDialog.Builder(DwgActivity.this).create();
                    alertDialog.setMessage("Ref. não encontrada");
                    alertDialog.show();
                    TextView text = findViewById(R.id.textRefCod);
                    text.setText("");
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("detail");
                loadFragment(fragment, "detail");
                return false;
            }

        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.configuration:
                startActivity(new Intent(DwgActivity.this, DwgConfig.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public ArrayList<PartsInfo> testConection(String value) {
        String urlConnection = url.toString();
        String userConnection = sharedPreferences.getString(user, null);
        String passwdConnection = sharedPreferences.getString(password, null);
        String driveName = "org.firebirdsql.jdbc.FBDriver";
        ArrayList<PartsInfo> list = new ArrayList<PartsInfo>();

        java.sql.Connection con;

        try {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectNetwork()
                    .build());
            Class.forName(driveName).newInstance();
            con = DriverManager.getConnection(urlConnection, userConnection, passwdConnection);
            String sql = "SELECT A.REF_N, A.REF_C, A.DENOMINACION, A.TIPOM, a.C_VR, B.NOM FROM PIEZAS "
                + "AS A INNER JOIN CLIENTES AS B ON A.CODCLIENTE = B.CODIGO WHERE A.REF_N = '" + normalizedValueFind(value.toString()) + "'";
            //Log.d("Value", value.toString());
            java.sql.Statement stmt = con.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                PartsInfo partsInfos = new PartsInfo();
                partsInfos.setRefCod(rs.getString("REF_N"));
                partsInfos.setRefCodClient(rs.getString("REF_C"));
                partsInfos.setDenomination(rs.getString("DENOMINACION"));
                partsInfos.setRevision(rs.getString("C_VR"));
                partsInfos.setTypeMaterial(rs.getString("TIPOM"));
                partsInfos.setNameClient(rs.getString("NOM"));
                list.add(partsInfos);
            }
            stmt.close();
            con.close();
            return list;


        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return list;
    }

    private void loadFragment(Fragment fragment, String tag) {
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("state", true);
    }

    private String normalizedValueFind(String value) {
        String newValue;
        newValue = String.format("%08d", Integer.parseInt(value));
        //Log.d("Value", newValue);
        return newValue;
    }

    /**
     * Return path converted for folder filename
     * @param fileName
     * @return
     */
    private static String convertPathNumber(String fileName) {
        int size = fileName.length();
        StringBuilder newValue = new StringBuilder();
        switch (size) {
            case 5:
                newValue.append("0").append(fileName.substring(0, 2)).append("000");
                break;
            case 6:
                newValue.append(fileName.substring(0, 3)).append("000");
                break;
            default:
        }
        return newValue.toString();
    }

    /**
     * Get file list from server
     * @param fileName
     */

    private void getFilesFromServer(String fileName) {
        TextView textView = findViewById(R.id.textRefCod);
        String reference = fileName;

        try {
            SmbFile smbFile = new SmbFile("smb://192.168.23.2/piezas/" + convertPathNumber(reference) + "/", auth);
            SmbFile[] files = smbFile.listFiles(reference + "*");

            if (!results.isEmpty()) {
                results.clear();
            }

            for (int i = 0; i < files.length; i++) {
                SmbFile file = files[i];
                if (!file.isDirectory()) {
                    results.add(file.getName().toString());
                }

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SmbException e) {
            e.printStackTrace();
        }
    }

    static void saveFilesForTempFolder(String fileName, ArrayList<String> listFiles) {

        SmbFileInputStream in = null;
        String value = convertPathNumber(fileName);
        Log.d("Log_SMB", value);

        if (!listFiles.isEmpty()) {

            File storageDir = new File(Environment.getExternalStorageDirectory() + "/smb/");
            storageDir.mkdirs();

            if (storageDir.listFiles() != null) {
                for (File file : storageDir.listFiles()) {
                    file.delete();
                }
            }

            for (String str : listFiles) {
                try {
                    in = new SmbFileInputStream(new SmbFile("smb://192.168.23.2/piezas/" + value + "/" + str, DwgActivity.auth));
                    byte[] data = new byte[8192];
                    int n;

                    File dwgPdf = new File(storageDir, str);

                    dwgPdf.createNewFile();
                    FileOutputStream out = new FileOutputStream(dwgPdf);
                    while ((n = in.read(data)) > 0) {
                        out.write(data, 0, n);
                    }

                    out.flush();
                    out.close();

                } catch (SmbException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

