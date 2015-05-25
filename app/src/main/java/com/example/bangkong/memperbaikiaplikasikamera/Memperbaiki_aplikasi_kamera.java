package com.example.bangkong.memperbaikiaplikasikamera;

        import android.content.ContentValues;
        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.net.Uri;
        import android.provider.MediaStore;
        import android.support.v7.app.ActionBarActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.io.FileNotFoundException;


public class MainActivity extends ActionBarActivity {

    final static int CAMERA_RESULT = 0;
    Uri imageFileUri;
    // User interface elements, specified in res/layout/main.xml
    ImageView returnedImageView;
    Button takePictureButton;
    Button lookPictureButton;
    Button saveDataButton;
    TextView titleTextView;
    TextView descriptionTextView;
    EditText titleEditText;
    EditText descriptionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get references to UI elements
        returnedImageView = (ImageView) findViewById(R.id.ReturnedImageView);
        takePictureButton = (Button) findViewById(R.id.TakePictureButton);
        lookPictureButton = (Button)findViewById(R.id.LookPictureButton);
        saveDataButton = (Button) findViewById(R.id.SaveDataButton);
        titleTextView = (TextView) findViewById(R.id.TitleTextView);
        descriptionTextView = (TextView) findViewById(R.id.DescriptionTextView);
        titleEditText = (EditText) findViewById(R.id.TitleEditText);
        descriptionEditText = (EditText) findViewById(R.id.DescriptionEditText);

        // Set all except takePictureButton to not be visible initially
        // View.GONE is invisible and doesn't take up space in the layout
        returnedImageView.setVisibility(View.GONE);
        saveDataButton.setVisibility(View.GONE);
        titleTextView.setVisibility(View.GONE);
        descriptionTextView.setVisibility(View.GONE);
        titleEditText.setVisibility(View.GONE);
        descriptionEditText.setVisibility(View.GONE);

        // When the Take Picture Button is clicked
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                // Add a new record without the bitmap
                // returns the URI of the new record
                imageFileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,new ContentValues());
                // Start the Camera App
                Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);
                startActivityForResult(i, CAMERA_RESULT);
            }
        });
        lookPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getBaseContext(),MainActivity2.class);
                startActivity(i);
            }
        });

        saveDataButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                // Update the MediaStore record with Title and Description
                ContentValues contentValues = new ContentValues(3);
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME,titleEditText.getText().toString());
                contentValues.put(MediaStore.Images.Media.DESCRIPTION,descriptionEditText.getText().toString());
                getContentResolver().update(imageFileUri,contentValues,null,null);
                // Tell the user
                Toast bread = Toast.makeText(MainActivity.this, "Record Updated", Toast.LENGTH_SHORT);
                bread.show();
                // Go back to the initial state, set Take Picture Button Visible
                // hide other UI elements
                takePictureButton.setVisibility(View.VISIBLE);
                lookPictureButton.setVisibility(View.VISIBLE);
                returnedImageView.setVisibility(View.GONE);
                saveDataButton.setVisibility(View.GONE);
                titleTextView.setVisibility(View.GONE);
                descriptionTextView.setVisibility(View.GONE);
                titleEditText.setVisibility(View.GONE);
                descriptionEditText.setVisibility(View.GONE);
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK){
            // The Camera App has returned
            // Hide the Take Picture Button
            takePictureButton.setVisibility(View.GONE);
            lookPictureButton.setVisibility(View.GONE);
            // Show the other UI Elements
            saveDataButton.setVisibility(View.VISIBLE);
            returnedImageView.setVisibility(View.VISIBLE);
            titleTextView.setVisibility(View.VISIBLE);
            descriptionTextView.setVisibility(View.VISIBLE);
            titleEditText.setVisibility(View.VISIBLE);
            descriptionEditText.setVisibility(View.VISIBLE);
            // Scale the image
            int dw = 500;// Make it at most 200 pixels wide
            int dh = 500;// Make it at most 200 pixels tall

            try{
                // Load up the image's dimensions not the image itself
                BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
                bmpFactoryOptions.inJustDecodeBounds = true;
                Bitmap bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, bmpFactoryOptions);

                int heightRatio = (int)Math.ceil(bmpFactoryOptions.outHeight/(float)dh);
                int widthRatio = (int)Math.ceil(bmpFactoryOptions.outWidth/(float)dw);
                Log.v("HEIGHTRATIO", "" + heightRatio);
                Log.v("WIDTHRATIO",""+widthRatio);
                // If both of the ratios are greater than 1,
                // one of the sides of the image is greater than the screen

                if (heightRatio > 1 && widthRatio > 1){
                    if (heightRatio > widthRatio){
                        // Height ratio is larger, scale according to it
                        bmpFactoryOptions.inSampleSize = heightRatio;
                    }else{
                        // Width ratio is larger, scale according to it
                        bmpFactoryOptions.inSampleSize = widthRatio;
                    }
                }
                // Decode it for real
                bmpFactoryOptions.inJustDecodeBounds = false;
                bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, bmpFactoryOptions);
                // Display it
                returnedImageView.setImageBitmap(bmp);

            } catch (FileNotFoundException e){
                Log.v("ERROR",e.toString());
            }
        }

    }
}