package com.example.instagramclone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private EditText etDescription;
    private Button btnSubmit;
    private ImageButton btnCaptureImage;
    private Button btnLogout;
    private ImageButton btnProfile;
    private ImageView ivPostImage;
    private File photoFile;
    public String photoFileName = "photo.jpg";
    private ProgressBar progressBar;
    private int LogOutVisibility =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etDescription = findViewById(R.id.etDescripton);
        btnCaptureImage = findViewById(R.id.ibCapture);
        btnProfile = findViewById(R.id.ibLogOut);
        btnSubmit = findViewById(R.id.btnSubmit);
        ivPostImage = findViewById(R.id.ivPostImage);
        progressBar = findViewById(R.id.pbLoading);
        btnLogout = findViewById(R.id.btnLogout);

        // btnCapture onClick launches an implicit intent for the Camera
        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Clicked Capture Image button");
                launchCamera();

            }
        });

        // onClick listener for submitting picture, description to Parse
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(ProgressBar.VISIBLE);     // Sets progress bar on when you click btnSubmit
                String description = etDescription.getText().toString(); // Get text from etDescription
                // Handle empty text
                if (description.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Description can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Handle empty image file
                if (photoFile == null || ivPostImage.getDrawable() == null) {
                    Toast.makeText(MainActivity.this, "There is no image!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Save post to Parse backend
                ParseUser currentUser = ParseUser.getCurrentUser();     // Get current user
                savePost(description, currentUser, photoFile);      // Use current user to send description, and photoFile to Parse backend

            }
        });

        // Toggle visibility of btnLogout
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnLogout.getVisibility() == View.INVISIBLE) {
                    btnLogout.setVisibility(View.VISIBLE);
                }
                else if (btnLogout.getVisibility() == View.VISIBLE) {
                    btnLogout.setVisibility(View.INVISIBLE);
                }

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Clicked Logout button");
                goLoginActivity();
            }
        });
    }

    // Logs out to LogActivity
    private void goLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        ParseUser.logOut();
        ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
        finish();
    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(MainActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        Log.i(TAG, "Package manager" + intent.resolveActivity(getPackageManager()));
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            Log.i(TAG, "Entered intent for capture picture");
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivPostImage.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    // URI - Uniform Resource Identifier
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);

    }

    // savePost method sends post object to Parse backend
    private void savePost(String description, ParseUser currentUser, File photoFile) {

        // Create post object
        Post post = new Post();
        post.setDescription(description);
        post.setImage(new ParseFile(photoFile));
        post.setUser(currentUser);

        // save post object to Parse backend on different thread
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                // checks if the save was successful. When save is successful, ParseException is null
                if (e != null) {
                    Log.e(TAG, "Error while saving: " + e);
                    Toast.makeText(MainActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    return;
                }
                Log.i(TAG, "Post save was successful!");
                Toast.makeText(MainActivity.this, "Submit Success!", Toast.LENGTH_SHORT).show();

                // Reset etDescription and ivPostImage and end progressBar
                etDescription.setText("");
                ivPostImage.setImageResource(R.mipmap.insta_cam_shadow_foreground);
                progressBar.setVisibility(ProgressBar.INVISIBLE);

            }
        });
    }

    // queryPost allows attributes to be retrieved from Parse backend
    private void queryPost() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        // query post object to Parse backend on different thread
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // checks if the query was successful. When save is successful, ParseException is null
                 if (e != null){
                     Log.e(TAG, "Issue with retrieving posts", e);
                     //return;
                 }
                 for (Post post: posts) {
                     Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                 }
            }
        });
    }
}