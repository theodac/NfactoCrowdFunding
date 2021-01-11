package budasuyasa.android.simplecrud;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

import budasuyasa.android.simplecrud.Config.ApiEndpoint;
import budasuyasa.android.simplecrud.Models.APIResponse;
import budasuyasa.android.simplecrud.Models.Project;
import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DonActivity extends AppCompatActivity {
    private static final String PREFS = "PREFS";
    private static final String PREFS_AGE = "PREFS_AGE";
    private static final String PREFS_NAME = "PREFS_NAME";
    private static final String PREFS_PROJECT = "PREFS_PROJECT";
    @BindView(R.id.button2)
    Button btnAddCover;
    @BindView(R.id.amount)
    TextView etAmount;
    OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(new StethoInterceptor())
            .build();
    SharedPreferences sharedPreferences;
    String id ;
    Project editProject;

    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_don);

        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        editProject = (Project) getIntent().getParcelableExtra("project");
        etAmount = (EditText) findViewById(R.id.amount);


        if (sharedPreferences.contains(PREFS_NAME)) {

            id = sharedPreferences.getString(PREFS_NAME, null);
            Log.d("StorageDON", id);


        }

        btnAddCover = (Button) findViewById(R.id.button2);

        btnAddCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)  {
                Log.d("MARCHE","MARCHE");
                saveDons(view);

            }
        });

    }

    private void saveDons(final View view){
        String amount = etAmount.getText().toString();

        if(StringUtils.isEmpty(amount)) return;

        RequestBody requestBody = null;
        String URL = "";


            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("montant", amount)
                    .addFormDataPart("user_id", id)
                    .addFormDataPart("project_id", editProject.getId())
                    .build();
            URL = ApiEndpoint.DONS;

        Request request = new Request.Builder()
                .url(URL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                DonActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Main Activity", e.getMessage());
                        Toast.makeText(DonActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    final APIResponse res =  gson.fromJson(response.body().string(), APIResponse.class);

                    try {
                        DonActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(StringUtils.equals(res.getStatus(), "success")){
                                    Toast.makeText(DonActivity.this, "Merci pour votre don !", Toast.LENGTH_SHORT).show();
                                    finish();
                                }else{
                                    Toast.makeText(DonActivity.this, "Error: "+res.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (JsonSyntaxException e) {
                        Log.e("MainActivity", "JSON Errors:"+e.getMessage());
                    } finally {
                        response.body().close();
                    }

                } else {
                    DonActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DonActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}