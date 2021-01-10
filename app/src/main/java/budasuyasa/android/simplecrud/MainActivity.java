package budasuyasa.android.simplecrud;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import budasuyasa.android.simplecrud.Adapter.BookAdapter;
import budasuyasa.android.simplecrud.Adapter.RecyclerItemClickListener;
import budasuyasa.android.simplecrud.Config.ApiEndpoint;
import budasuyasa.android.simplecrud.Models.APIResponse;
import budasuyasa.android.simplecrud.Models.Project;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.support.v7.widget.RecyclerView.VERTICAL;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS = "PREFS";
    private static final String PREFS_AGE = "PREFS_AGE";
    private static final String PREFS_NAME = "PREFS_NAME";
    RecyclerView recyclerView;
    BookAdapter recycleAdapter;
    OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(new StethoInterceptor())
            .build();
    private List<Project> projectList = new ArrayList<Project>();
    Gson gson = new Gson();
    SharedPreferences sharedPreferences;
    FloatingActionButton buttonAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.d("TESTT","TESTCO");
        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);


        if (sharedPreferences.contains(PREFS_AGE) && sharedPreferences.contains(PREFS_NAME)) {

            boolean age = sharedPreferences.getBoolean(PREFS_AGE, false);
            String name = sharedPreferences.getString(PREFS_NAME, null);
            Log.d("Storage", name);


        }
        //Set floating button action (add new book)
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddProject.class);
                startActivity(i);
            }
        });
        fab.setVisibility(View.INVISIBLE);
        if (sharedPreferences.contains(PREFS_AGE)) {

            boolean age = sharedPreferences.getBoolean(PREFS_AGE, false);
            Log.d("Storage", String.valueOf(age));
            if(age){
                fab.setVisibility(View.VISIBLE);

            }


        }
        //Prepare RecycleView adapter
        recyclerView= (RecyclerView) findViewById(R.id.listView);
        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        recyclerView.addItemDecoration(decoration);
        recycleAdapter = new BookAdapter(this, projectList);
        recyclerView.setAdapter(recycleAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(MainActivity.this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final Project project = projectList.get(position);
                Log.d("MainActivity", "onItemClick: "+ project.getEnd_date());

                // setup the alert builder
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date parsed = sdf.parse(project.getEnd_date());

                    Date now = new Date(); // 2016-03-10 22:06:10
                    Log.d("DATE", project.getEnd_date() + ' ' + now.toString());
                    if(parsed.compareTo(now) == -1){
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Le projet est terminé");

                        // add a list

                        // create and show the alert dialog
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }else{
                        update(project);

                    }

                    System.out.println(parsed.compareTo(now));
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

            getBooks();

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private void update(Project project){
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("book", project);
        startActivity(intent);
    }

    private void delete(String isbn) {
        //Buat request body mulipart
        RequestBody formBody = new FormBody.Builder()
                .add("isbn", isbn)
                .build();

        Request request = new Request.Builder()
                .url(ApiEndpoint.BOOKS+"/"+isbn+"/delete") //Ingat sesuaikan dengan URL
                .post(formBody)
                .build();

        //Handle response dari request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.d("Main Activity", e.getMessage());
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        //Finish activity
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                APIResponse res =  gson.fromJson(response.body().charStream(), APIResponse.class);
                                if(StringUtils.equals(res.getStatus(), "success")){
                                    //Refresh book
                                    getBooks();
                                }
                            }
                        });
                    } catch (JsonSyntaxException e) {
                        Log.e("MainActivity", "JSON Errors:"+e.getMessage());
                    } finally {
                        response.body().close();
                    }

                } else {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    /**
     * Get Book from REST-API and populate into RecycleView
     */
    private void getBooks(){
        Request request = new Request.Builder()
                .url(ApiEndpoint.BOOKS)
                .build();

        //Handle response dari request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("OFF","OFFILE");
                        projectList = new Gson().<ArrayList<Project>>fromJson(PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("My_SAVED_LIST", ""), new TypeToken<ArrayList<Project>>() {
                        }.getType());
                        Log.d("Main Activitye", e.getMessage());
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        final ArrayList<Project> res = gson.fromJson(response.body().string(), new TypeToken<ArrayList<Project>>(){}.getType());
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                projectList.clear();
                                projectList.addAll(res);
                                String mySavedList= new Gson().toJson(projectList);
                                PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("My_SAVED_LIST", mySavedList).apply();
                                recycleAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (JsonSyntaxException e) {
                        Log.e("MainActivity", "JSON Errors:"+e.getMessage());
                    } finally {
                        response.body().close();
                    }

                } else {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (sharedPreferences.contains(PREFS_AGE)) {

            boolean age = sharedPreferences.getBoolean(PREFS_AGE, false);
            if(age){
                MenuItem item = menu.findItem(R.id.Logout);
                item.setVisible(true);
                MenuItem items = menu.findItem(R.id.action_login);
                items.setVisible(false);
                MenuItem items2 = menu.findItem(R.id.action_settings);
                items2.setVisible(false);

            }else{

                MenuItem item = menu.findItem(R.id.Logout);
                item.setVisible(false);
                MenuItem items = menu.findItem(R.id.action_login);
                items.setVisible(true);
            }

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        Log.d("OK", "OK");
        if (id == R.id.action_settings) {
            Log.d("OK", "OKE");
            Intent show = new Intent(this, RegisterActivity.class);

            startActivity(show);
            return true;
        }
        if (id == R.id.action_login) {
            Intent show = new Intent(this, LoginActivity.class);

            startActivity(show);
            return true;
        }

        if (id == R.id.Logout) {
            sharedPreferences = getBaseContext().getSharedPreferences("PREFS", MODE_PRIVATE);

            sharedPreferences
                    .edit()
                    .putBoolean(PREFS_AGE, false)
                    .putString(PREFS_NAME, "florent")
                    .apply();
            Intent show = new Intent(this, MainActivity.class);

            startActivity(show);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBooks();
    }
}
