package com.example.facemask;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent;

public class DrawActivity extends Activity implements OnClickListener {

	Button button1;
	ImageView im1;
	Bitmap bitmap;
	Intent i;

	// Face detection
	private static final int MAX_FACES = 5;
	FaceDetector face_detector;
	int face_count;
	Face[] faces = null;
	float eyeDistance;
	int xFace, yFace;
	PointF midPoint;
	float confidence;

	// Drawing
	Paint ditherPaint, drawPaint2, drawPaint3, drawPaint4;
	Canvas canvas;
	RectF r;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.draw_lay);
		// Set up drawing objects, view objects, and face detection variables
		initialize();

		// Get the Bitmap from the other activity via an Intent
		i = getIntent();
		bitmap = (Bitmap) i.getParcelableExtra("BitmapImage");

		// Detect the faces from the bitmap that was passed in, store them in
		// the Face object array faces
		// Store the number of faces detected in face_count
		face_detector = new FaceDetector(bitmap.getWidth(), bitmap.getHeight(),
				MAX_FACES);
		faces = new FaceDetector.Face[MAX_FACES];
		face_count = face_detector.findFaces(bitmap, faces);

		// Draw the bitmap image to the canvas
		canvas.setBitmap(bitmap);
		canvas.drawBitmap(bitmap, 0, 0, ditherPaint);

		if (face_count > 0) {

			// If faces were detected, draw on all of them
			for (int index = 0; index < face_count; ++index) {

				faces[index].getMidPoint(midPoint);
				eyeDistance = faces[index].eyesDistance();
				confidence = faces[index].confidence();

				// Draw Scumbag Steve meme hat
				r = new RectF();
				r.left = midPoint.x - eyeDistance;
				r.right = midPoint.x + eyeDistance;
				r.top = midPoint.y - (2 * eyeDistance);
				r.bottom = midPoint.y - eyeDistance;
				if (r != null) {
					canvas.drawRect(r, drawPaint4);
				}

				// Draw hat brim
				r = new RectF();
				r.left = midPoint.x - eyeDistance;
				r.right = midPoint.x + eyeDistance + eyeDistance;
				r.top = (float) (midPoint.y - (1.2 * eyeDistance));
				r.bottom = midPoint.y - eyeDistance;
				if (r != null) {
					canvas.drawRect(r, drawPaint4);
				}

				// Draw the red nose
				r = new RectF();
				r.left = midPoint.x - faces[index].eyesDistance() / 6;
				r.right = midPoint.x + faces[index].eyesDistance() / 6;
				r.top = midPoint.y;
				r.bottom = midPoint.y + faces[index].eyesDistance() / 2;
				if (r != null) {
					canvas.drawRect(r, drawPaint3);
				}

				// Draw the left eye glasses
				r = new RectF();
				r.left = midPoint.x - faces[index].eyesDistance();
				r.right = midPoint.x - faces[index].eyesDistance() / 6;
				r.top = midPoint.y - faces[index].eyesDistance() / 2;
				r.bottom = midPoint.y + faces[index].eyesDistance() / 2;
				if (r != null) {
					canvas.drawRect(r, drawPaint2);
				}

				// Draw the right eye glasses
				r = new RectF();
				r.left = midPoint.x + faces[index].eyesDistance();
				r.right = midPoint.x + faces[index].eyesDistance() / 6;
				r.top = midPoint.y - faces[index].eyesDistance() / 2;
				r.bottom = midPoint.y + faces[index].eyesDistance() / 2;
				if (r != null) {
					canvas.drawRect(r, drawPaint2);
				}

				// Draw the metal connecting the left eye to the right eye,
				// glasses
				r = new RectF();
				r.left = midPoint.x - faces[index].eyesDistance() / 6;
				r.right = midPoint.x + faces[index].eyesDistance() / 6;
				r.top = midPoint.y;
				r.bottom = midPoint.y + faces[index].eyesDistance() / 50;
				if (r != null) {
					canvas.drawRect(r, drawPaint2);
				}
			}
		} else {
			if (face_count == 0) {
				Toast.makeText(getApplicationContext(),
						"There are no faces in this picture", Toast.LENGTH_LONG)
						.show();
			}
		}

		// Save the file to the device with the drawings on it
		String filepath = Environment.getExternalStorageDirectory()
				+ "/facedetect" + System.currentTimeMillis() + ".jpg";
		try {
			FileOutputStream fos = new FileOutputStream(filepath);
			bitmap.compress(CompressFormat.JPEG, 90, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Set the image view with the bitmap that was drawn on
		im1.setImageBitmap(bitmap);
	}

	public void initialize() {

		// Create new objects
		ditherPaint = new Paint();
		ditherPaint.setDither(true);
		drawPaint2 = new Paint();
		drawPaint3 = new Paint();
		drawPaint4 = new Paint();
		canvas = new Canvas();

		// Used to draw the eyes
		drawPaint2.setColor(Color.BLACK);
		drawPaint2.setStyle(Paint.Style.STROKE);
		drawPaint2.setStrokeWidth(8);

		// Used to draw the nose
		drawPaint3.setColor(Color.RED);
		drawPaint3.setStyle(Paint.Style.FILL);
		drawPaint3.setStrokeWidth(5);

		// Used to draw a hat
		drawPaint4.setColor(Color.DKGRAY);
		drawPaint4.setStyle(Paint.Style.FILL);
		drawPaint4.setStrokeWidth(5);

		// Used to detect the Face objects
		midPoint = new PointF();
		eyeDistance = 0.0f;
		confidence = 0.0f;

		// Set up image and buttons
		im1 = (ImageView) findViewById(R.id.im1);
		button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.button1:
			// Go back to the MainActivity
			finish();
		}
	}

}
