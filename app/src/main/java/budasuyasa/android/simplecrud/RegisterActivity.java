package budasuyasa.android.simplecrud;

import android.support.design.widget.TextInputEditText;
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
import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RegisterActivity extends AppCompatActivity {
    EditText etUsername;
    EditText etPseudo;
    EditText etPassword;
    EditText etBirthday;
    Button btnAddCover;

    OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(new StethoInterceptor())
            .build();

    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etPseudo = (EditText) findViewById(R.id.pseudo);
        etUsername = (EditText) findViewById(R.id.username);
        etPassword = (EditText) findViewById(R.id.password);
        etBirthday = (EditText) findViewById(R.id.birthday);
        btnAddCover = (Button) findViewById(R.id.login);

        btnAddCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MARCHE","MARCHE");
                register();
            }
        });

    }

    private void register(){
        String username = etUsername.getText().toString();
        String pseudo = etPseudo.getText().toString();
        String password = etPassword.getText().toString();
        String birthday = etBirthday.getText().toString();

        Log.d("TESTE", username + pseudo + password + birthday);

       if(StringUtils.isEmpty(username)) return;
        if(StringUtils.isEmpty(pseudo)) return;
        if(StringUtils.isEmpty(password)) return;
        if(StringUtils.isEmpty(birthday)) return;

        RequestBody requestBody = null;
        String URL = "";


            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("username", username)
                    .addFormDataPart("pseudo", pseudo)
                    .addFormDataPart("password", password)
                    .addFormDataPart("birthday", birthday)
                    .build();
            URL = ApiEndpoint.REGISTER;


        Request request = new Request.Builder()
                .url(URL) //Ingat sesuaikan dengan URL
                .post(requestBody)
                .build();

        //Handle response dari request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                Log.d("Error","Error");
                RegisterActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Main Activity", e.getMessage());
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                Log.d("Good","Good");

                if (response.isSuccessful()) {
                    try {
                        RegisterActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                APIResponse res =  gson.fromJson(response.body().charStream(), APIResponse.class);
                                if(StringUtils.equals(res.getStatus(), "success")){
                                    Toast.makeText(RegisterActivity.this, "Inscription réussi ! ", Toast.LENGTH_SHORT).show();
                                    finish();
                                }else{
                                    Toast.makeText(RegisterActivity.this, "Error: "+res.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (JsonSyntaxException e) {

                        Log.e("MainActivity", "JSON Errors:"+e.getMessage());
                    } finally {
                        response.body().close();
                    }

                } else {
                    RegisterActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RegisterActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}