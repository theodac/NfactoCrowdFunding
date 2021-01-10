package budasuyasa.android.simplecrud;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import budasuyasa.android.simplecrud.Config.ApiEndpoint;
import budasuyasa.android.simplecrud.Models.APIResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    EditText etUsername;
    EditText etPassword;
    Button btnAddCover;
    SharedPreferences sharedPreferences;

    private static final String PREFS_AGE = "PREFS_AGE";
    private static final String PREFS_NAME = "PREFS_NAME";

    OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(new StethoInterceptor())
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUsername = (EditText) findViewById(R.id.username2);
        etPassword = (EditText) findViewById(R.id.password2);
        btnAddCover = (Button) findViewById(R.id.login2);

        btnAddCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)  {
                Log.d("MARCHE","MARCHE");
                login(view);
            }
        });
    }



    private void login(final View view){
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();


        if(StringUtils.isEmpty(username)) return;
        if(StringUtils.isEmpty(password)) return;

        RequestBody requestBody = null;
        String URL = "";


        requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", username)
                .addFormDataPart("password", password)
                .build();
        URL = ApiEndpoint.LOGIN;


        Request request = new Request.Builder()
                .url(URL) //Ingat sesuaikan dengan URL
                .post(requestBody)
                .build();

        //Handle response dari request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                Log.d("Error","Error");

                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Main Activity", e.getMessage());
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        sharedPreferences = getBaseContext().getSharedPreferences("PREFS", MODE_PRIVATE);

                        sharedPreferences
                                .edit()
                                .putBoolean(PREFS_AGE, false)
                                .putString(PREFS_NAME, "florent")
                                .apply();
                        Intent show = new Intent(view.getContext(), MainActivity.class);

                        startActivity(show);
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                Log.d("Good","Good" +new Gson().toJson(response.body()));
                final Gson gson = new Gson();
                final APIResponse entity = gson.fromJson(response.body().string(), APIResponse.class);
                Log.d("APIR",entity.getUser().getEmail());

                if (response.isSuccessful()) {
                    try {
                        LoginActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(response.code() == 200){
                                    Toast.makeText(LoginActivity.this, "Connexion réussi ! ", Toast.LENGTH_SHORT).show();
                                    sharedPreferences = getBaseContext().getSharedPreferences("PREFS", MODE_PRIVATE);


                                    sharedPreferences
                                            .edit()
                                            .putBoolean(PREFS_AGE, true)
                                            .putString(PREFS_NAME, entity.getUser().getId())
                                            .apply();
                                    Intent show = new Intent(view.getContext(), MainActivity.class);

                                    startActivity(show);
                                    finish();
                                }else{

                                    Toast.makeText(LoginActivity.this, "Error: "+response.code(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();

                        Log.e("MainActivity", "JSON Errors:"+e.getMessage());
                    } finally {
                        response.body().close();
                    }

                } else {
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }
}