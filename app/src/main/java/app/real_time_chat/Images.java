package app.real_time_chat;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;

import static app.real_time_chat.R.id.btn_browseImg;


public class Images extends AppCompatActivity {

    private Button btn_browse;
    private Button btn_upload;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef ;
    private ImageView imageView;
    private Uri imgUri;
    private String user_name;
    private String room_name;

    public static final String FB_STORAGE_PATH = "image/";
    public static final int REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.chaticon);

        btn_browse = (Button) findViewById(btn_browseImg);
        btn_upload = (Button) findViewById(R.id.btn_uploadImg);
        imageView = (ImageView)findViewById(R.id.imageView);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        //get props from chatroom activity

        room_name = getIntent().getStringExtra("room_name");
        user_name = getIntent().getStringExtra("user_name");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(room_name);

        btn_browse.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),REQUEST_CODE);
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (imgUri != null){
                    final ProgressDialog dialog = new ProgressDialog(Images.this);
                    dialog.setTitle("Uploading image..");
                    dialog.show();

                    //Get storage reference
                    StorageReference ref = mStorageRef.child(FB_STORAGE_PATH + System.currentTimeMillis() + "." + getImageExt(imgUri));

                    //Add file to reference
                    ref.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Dismiss dialog on success
                            dialog.dismiss();
                            //Display toast message
                            Toast.makeText(getApplicationContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                            Image_Upload image_Upload = new Image_Upload( taskSnapshot.getDownloadUrl().toString(), user_name );


                            //Save image info in Firebase DB
                            String uploadId = mDatabaseRef.push().getKey();

                            mDatabaseRef.child(uploadId).setValue(image_Upload);


                            finish();




                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //Dismiss dialog on error
                                    dialog.dismiss();
                                    //Display error toast message
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    //Show upload progress
                                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                    dialog.setMessage("Uploaded " + (int)progress + "%");
                                }
                            });
                }else {
                    Toast.makeText(getApplicationContext(), "Select image to upload", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null ) {
            imgUri = data.getData();

            try{
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
                imageView.setImageBitmap(bm);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public String getImageExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
