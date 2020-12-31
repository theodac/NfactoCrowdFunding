package budasuyasa.android.simplecrud;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import budasuyasa.android.simplecrud.Config.ApiEndpoint;
import budasuyasa.android.simplecrud.Models.APIResponse;
import budasuyasa.android.simplecrud.Models.Project;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Activity untuk menambahkan dan mengupdate data buku
 */
public class AddProject extends AppCompatActivity {

    String imagePath;
    String imageFileName;
    private static int EDIT_MODE = 0;
    private static int ADD_MODE = 1;
    int MODE = 1;

    Project editProject;

    ImagePicker imagePicker;
    CameraImagePicker cameraImagePicker;

    String TAG = getClass().getName().toString();
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpeg");

    @BindView(R.id.button) Button btnAddCover;
    @BindView(R.id.imageView2) ImageView imageView;
    @BindView(R.id.etISBN)
    TextInputEditText etTitle;
    @BindView(R.id.etName)
    TextInputEditText etMontant;
    @BindView(R.id.etYear)
    TextInputEditText etEnd_Date;
    @BindView(R.id.etDescription)
    TextInputEditText etDescription;

    OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(new StethoInterceptor())
            .build();

    Gson gson = new Gson();

    ImagePickerCallback callback = new ImagePickerCallback(){
        @Override
        public void onImagesChosen(List<ChosenImage> images) {
            // get image path
            if(images.size() > 0){
                imagePath = images.get(0).getOriginalPath();
                imageFileName = images.get(0).getDisplayName();
                Picasso.get().load(new File(imagePath)).into(imageView);
            }
        }

        @Override
        public void onError(String message) {
            // Do error handling
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        ButterKnife.bind(this); //Bind ButterKnife

        //inisiaisasi ImagePicker dan CameraImagePicker
        imagePicker = new ImagePicker(AddProject.this);
        cameraImagePicker = new CameraImagePicker(AddProject.this);

        //set callback handler
        imagePicker.setImagePickerCallback(callback);
        cameraImagePicker.setImagePickerCallback(callback);

        //beri action pada tombol thumbnail
        btnAddCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageChoice();
            }
        });

        //beri nilai di form input agar lebih mudah
        if(getIntent().getParcelableExtra("book") != null){
            editProject = (Project) getIntent().getParcelableExtra("book");
            MODE = EDIT_MODE;
            etTitle.setText(editProject.getTitle());
            etMontant.setText(editProject.getEnd_date());
            etEnd_Date.setText(editProject.getMontant());
            Picasso.get().load(ApiEndpoint.BASE + editProject.getPicture()).into(imageView);
            Log.d(TAG, "onCreate: "+ApiEndpoint.BASE + editProject.getPicture());

        }else{
            MODE = ADD_MODE;

            //Set default isi form
            etTitle.setText("21212121");
            etMontant.setText("3");
            etEnd_Date.setText("2018");
            etDescription.setText("Build Android application in no time with without reinventing +" +
                    "the wheels, by using various existing Android Library. Great book for lazy people");
        }
    }

    /**
     * Method untuk mengambil gambar ketika tombol Thumbnail di click
     */
    private void pickImageChoice(){
        //Pertama, minta permission untuk mengakses camera dan storage (untuk Android M ke atas)
        //Biar gampang, kita pakai library namanya Dexter.
        //https://github.com/Karumi/Dexter

        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    //Jika permission diijinkan user, buat dialog pilihan
                    //untuk memilih gambar diambil dari gallery atau camera

                    // setup the alert builder
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddProject.this);
                    builder.setTitle("Pick image from?");

                    // add a list
                    String[] menus = {"Gallery", "Camera"};
                    builder.setItems(menus, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0: // dari gallery
                                    imagePicker.pickImage();
                                    break;
                                case 1: // dari camera
                                    imagePath = cameraImagePicker.pickImage();
                                    break;
                            }
                        }
                    });
                    // create and show the alert dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                }
            }).check();
    }

    /**
     * onActivityResult untuk menghandle data yang diambil dari camera atau gallery
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(requestCode == Picker.PICK_IMAGE_DEVICE) {
                if(imagePicker == null) {
                    imagePicker = new ImagePicker(AddProject.this);
                    imagePicker.setImagePickerCallback(callback);
                }
                imagePicker.submit(data);
            }
            if(requestCode == Picker.PICK_IMAGE_CAMERA) {
                if(cameraImagePicker == null) {
                    cameraImagePicker = new CameraImagePicker(AddProject.this);
                    cameraImagePicker.reinitialize(imagePath);
                    // OR in one statement
                    // imagePicker = new CameraImagePicker(Activity.this, outputPath);
                    cameraImagePicker.setImagePickerCallback(callback);
                }
                cameraImagePicker.submit(data);
            }
        }
    }

    /**
     * Jangan lupa handle reference path gambar agar tidak hilang saat activity restart
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // You have to save path in case your activity is killed.
        // In such a scenario, you will need to re-initialize the CameraImagePicker
        outState.putString("picker_path", imagePath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // After Activity recreate, you need to re-initialize these
        // two values to be able to re-initialize CameraImagePicker
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("picker_path")) {
                imagePath = savedInstanceState.getString("picker_path");
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Buat menu SIMPAN di pojok kanan atas
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add, menu);
        return true;
    }

    /**
     * Handle menu, ketika diklik, panggil method save()
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.save:
                saveBook();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Save data buku ketika user mengklik menu SAVE
     */
    private void saveBook(){
        //Get nilai edit text, assign ke variabel
        String title = etTitle.getText().toString();
        String montant = etMontant.getText().toString();
        String end_date = etEnd_Date.getText().toString();
        String description = etDescription.getText().toString();

        //Tambahkan sedikit validasi, jangan simpan jika edit text masih kosong
        if(StringUtils.isEmpty(title)) return;
        if(StringUtils.isEmpty(montant)) return;
        if(StringUtils.isEmpty(end_date)) return;
        if(StringUtils.isEmpty(description)) return;

        //Sesuaikan parameter input. Jika edit mode, gambar tidak harus diisi
        //Sesuaikan juga URL dari API yang digunakan
        RequestBody requestBody = null;
        String URL = "";

        if(MODE == ADD_MODE){
            //tambahkan validasi pada gambar jika mode tambah
            if(StringUtils.isEmpty(imagePath)) return;

            //Buat parameter input form
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("title", title)
                    .addFormDataPart("montant", montant)
                    .addFormDataPart("end_date", end_date)
                    .addFormDataPart("description", description)
                    .addFormDataPart("image", imageFileName,
                            RequestBody.create(MEDIA_TYPE_PNG, new File(imagePath)))
                    .build();
            URL = ApiEndpoint.ADD_BOOK;

        }else if(MODE == EDIT_MODE) {
            if(StringUtils.isBlank(imageFileName)){
                //Buat parameter input form
                requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("title", title)
                        .addFormDataPart("montant", montant)
                        .addFormDataPart("end_date", end_date)
                        .addFormDataPart("description", description)
                        .build();
            }else{
                requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("title", title)
                        .addFormDataPart("montant", montant)
                        .addFormDataPart("end_date", end_date)
                        .addFormDataPart("description", description)
                        .addFormDataPart("image", imageFileName,
                                RequestBody.create(MEDIA_TYPE_PNG, new File(imagePath)))
                        .build();
            }
            URL = ApiEndpoint.BOOKS+"/"+title+"/update";
        }

        Request request = new Request.Builder()
                .url(URL) //Ingat sesuaikan dengan URL
                .post(requestBody)
                .build();

        //Handle response dari request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                AddProject.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Main Activity", e.getMessage());
                        Toast.makeText(AddProject.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        //Finish activity
                        AddProject.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                APIResponse res =  gson.fromJson(response.body().charStream(), APIResponse.class);
                                //Jika response success, finish activity
                                if(StringUtils.equals(res.getStatus(), "success")){
                                    Toast.makeText(AddProject.this, "Book saved!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }else{
                                    //Tampilkan error jika ada
                                    Toast.makeText(AddProject.this, "Error: "+res.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (JsonSyntaxException e) {
                        Log.e("MainActivity", "JSON Errors:"+e.getMessage());
                    } finally {
                        response.body().close();
                    }

                } else {
                    AddProject.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddProject.this, "Server error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
