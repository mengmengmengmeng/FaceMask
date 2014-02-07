package com.example.facemask;

import java.io.File;
import java.io.FileOutputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends Activity {

	private Camera mCamera;
	private CameraPreview mPreview;
	Bitmap bmp_image;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create camera object
		mCamera = getCameraInstance(this);

		// Set up the camera preview and pass in a camera
		mPreview = new CameraPreview(this, mCamera);

		// Set the camera preview view to the FrameLayout
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);

		// Add a button to take a photo
		Button captureButton = (Button) findViewById(R.id.capturePhoto);
		captureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Take the picture, pass in a jpeg image
				mCamera.takePicture(null, null, jpegCallback);
			}
		});// End of the onClick method for the Capture Button
	}

	// Create a picture callback object
	PictureCallback jpegCallback = new PictureCallback() {

		public void onPictureTaken(byte[] data, Camera camera) {

			// Create a file and store the path in fname
			FileOutputStream outStream = null;
			String filename = "face.jpg";
			File pic;
			pic = new File(Environment.getExternalStorageDirectory(), filename);
			String fname = pic.getAbsolutePath();

			// Write the byte array to the file
			try {
				outStream = new FileOutputStream(pic);
				outStream.write(data);
				outStream.close();
			} catch (Exception e) {
			}

			// Change preferences and decode the file using the preferences,
			// full path name is in the string fname
			BitmapFactory.Options bitmap_options = new BitmapFactory.Options();
			bitmap_options.inPreferredConfig = Bitmap.Config.RGB_565;
			bmp_image = BitmapFactory.decodeFile(fname, bitmap_options);

			// Rotate the image
			// Since the picture was taken in landscape mode
			Matrix matrix = new Matrix();
			matrix.postRotate(90);
			Bitmap bmp_image2 = Bitmap.createBitmap(bmp_image, 0, 0,
					bmp_image.getWidth(), bmp_image.getHeight(), matrix, true);

			// Create an intent and pass the rotated bitmap image to the second
			// activity
			Intent i = new Intent(getApplicationContext(), DrawActivity.class);
			i.putExtra("BitmapImage", bmp_image2);
			startActivity(i);
		}
	};

	public static Camera getCameraInstance(Context context) {
		Camera c = null;
		try {
			// Check to see if the device has a camera
			if (context.getPackageManager().hasSystemFeature(
					PackageManager.FEATURE_CAMERA)) {
				c = Camera.open();
				// Set the camera to portrait mode
				// Must rotate the bitmap image later using a matrix
				c.setDisplayOrientation(90);
			}
		} catch (Exception e) {
		}
		// If there is no camera or if the camera fails to open, it will return
		// null
		return c;
	}

	protected void onDestroy() {
		if (mCamera != null) {
			// Release the camera if the onDestroy method is called
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
