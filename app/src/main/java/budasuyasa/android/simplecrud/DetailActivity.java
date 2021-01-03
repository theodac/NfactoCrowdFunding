package budasuyasa.android.simplecrud;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.common.StringUtil;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import budasuyasa.android.simplecrud.Config.ApiEndpoint;
import budasuyasa.android.simplecrud.Models.APIResponse;
import budasuyasa.android.simplecrud.Models.Project;
import budasuyasa.android.simplecrud.Models.SommeDon;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetailActivity extends AppCompatActivity {
    Project editProject;
    TextView etTitle;
    TextView etAmount;
    TextView etDescription;
    Button etButton;
    ImageView etImage;
    OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(new StethoInterceptor())
            .build();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        etTitle = (TextView) findViewById(R.id.title);
        etAmount = (TextView) findViewById(R.id.amount);
        etDescription = (TextView) findViewById(R.id.description);
        etButton = (Button) findViewById(R.id.don);
        etImage = (ImageView) findViewById(R.id.imageP);
        editProject = (Project) getIntent().getParcelableExtra("book");

        etTitle.setText(editProject.getTitle());
        etAmount.setText(editProject.getMontant());
        etDescription.setText(editProject.getDescription());
        etButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)  {
                Log.d("MARCHE","MARCHE");
                update(editProject);

            }
        });
        Picasso.get().load(ApiEndpoint.BASE + editProject.getPicture()).into(etImage);
        getDons();

    }
    private void getDons(){
        String URL = "";

        URL = ApiEndpoint.SUMDON + editProject.getId().toString();


        Request request = new Request.Builder()
                .url(URL) //Ingat sesuaikan dengan URL
                .get()
                .build();

        //Handle response dari request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                Log.d("Error","Error");

                DetailActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Main Activity", e.getMessage());
                        Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                Log.d("Good","Good" +new Gson().toJson(response.body()));
                final Gson gson = new Gson();
                final SommeDon entity = gson.fromJson(response.body().string(), SommeDon.class);

                Log.d("APIR", String.valueOf(entity.getTOTAL_COSTS()));

                if (response.isSuccessful()) {
                    try {
                        DetailActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(response.code() == 200){
                                    int percent = (int)((entity.getTOTAL_COSTS() * 100.0f) / Integer.parseInt(editProject.getMontant().toString()));
                                    Log.d("PERCENT", String.valueOf(percent));
                                    String percentD = Integer.toString(percent) + "%";
                                    etAmount.setText(percentD);
                                }else{

                                    Toast.makeText(DetailActivity.this, "Error: "+response.code(), Toast.LENGTH_SHORT).show();
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
                    DetailActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DetailActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    private void update(Project project){
        Intent intent = new Intent(this, DonActivity.class);
        intent.putExtra("project", project);
        startActivity(intent);
    }
}