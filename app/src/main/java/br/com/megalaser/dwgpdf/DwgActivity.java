package br.com.megalaser.dwgpdf;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DwgActivity extends AppCompatActivity {

    public static final String config = "config";
    public static final String server = "server";
    public static final String port = "port";
    public static final String database = "database";
    public static final String user = "user";
    public static final String password = "password";
    SharedPreferences sharedPreferences;
    private String value;
    DetailFragment detailFragment;
    FileFragment fileFragment;

    private static boolean STATE = false;

    private static StringBuilder url = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dwg);


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

                ArrayList<PartsInfo> list = new ArrayList<PartsInfo>();
                list = testConection(s);
                if (!list.isEmpty()) {
                    for (PartsInfo info : list) {
                        TextView textNameClient = findViewById(R.id.textNameClient);
                        TextView textRefCod = findViewById(R.id.textRefCod);
                        TextView textRefClient = findViewById(R.id.textCodClient);
                        TextView textRevision = findViewById(R.id.textRevision);
                        TextView textTypeMaterial = findViewById(R.id.textTypeMaterial);
                        TextView textDenomination = findViewById(R.id.textDenomination);

                        textNameClient.setText(info.getNameClient());
                        textRefCod.setText(info.getRefCod());
                        textRefClient.setText(info.getRefCodClient());
                        textRevision.setText(info.getRevision());
                        textTypeMaterial.setText(info.getTypeMaterial());
                        textDenomination.setText(info.getDenomination());
                    }

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
}

