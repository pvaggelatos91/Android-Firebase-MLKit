package msc.mobile_development.demo.firebase_mlkit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageProxy;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.face.Landmark;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import msc.mobile_development.demo.firebase_mlkit.databinding.ActivityPreviewImageBinding;

public class PreviewImageActivity extends AppCompatActivity
{
	/*
	 * ============================== *
	 * = Member Variables (Private) = *
	 * ============================== *
	 */

	private final int CapturedImageRotationDegrees = CachedObjects.CapturedImage.getImageInfo().getRotationDegrees();

	@NonNull
	private ActivityPreviewImageBinding binding;

	@NonNull
	private Bitmap CapturedImage;

	private Canvas CaptureImageCanvas;

	@NonNull
	private FaceDetector FaceDetectorObject;

	@NonNull
	private ObjectDetector ObjectDetectorObject;

	@NonNull
	private Paint FacePaint;

	@NonNull
	private Paint FaceLandmarkPaint;

	@NonNull
	private Paint FaceContourPaint;

	private Paint ItemPaint;

	@NonNull
	private PleaseWaitAlertDialog PleaseWaitAlertDialogObject;

	/*
	 * ================================== *
	 * = Overridden Methods (Protected) = *
	 * ================================== *
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Call parent method
		super.onCreate(savedInstanceState);

		// Instantiate the "binding" object
		this.binding = ActivityPreviewImageBinding.inflate(getLayoutInflater());

		// Set layout
		setContentView(this.binding.getRoot());

		// Register OnClickListener for "Detect Faces" button
		this.binding.DetectFacesButton.setOnClickListener(this::onClickDetectFacesButton);

		// Instantiate the "highAccuracyOptions" object
		final FaceDetectorOptions highAccuracyOptions = new FaceDetectorOptions.Builder()
				.setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
				.setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
				.setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
				.build();

		// Instantiate the "multipleObjectsDetectionOptions" object
		final ObjectDetectorOptions multipleObjectsDetectionOptions = new ObjectDetectorOptions.Builder()
						.setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
						.enableMultipleObjects()
						.enableClassification()
						.build();

		// Instantiate the "FaceDetectorObject" object
		this.FaceDetectorObject = FaceDetection.getClient(highAccuracyOptions);

		// Instantiate the "ObjectDetectorObject" object
		this.ObjectDetectorObject = ObjectDetection.getClient(multipleObjectsDetectionOptions);

		// Instantiate the "PleaseWaitAlertDialogObject" object
		this.PleaseWaitAlertDialogObject = new PleaseWaitAlertDialog(this);

		// Get the captured image as Bitmap
		final Bitmap tmp = getImageAsBitmap();
		this.CapturedImage      = tmp.copy(tmp.getConfig(), true);
		this.CaptureImageCanvas = new Canvas(this.CapturedImage);

		// Local captured image to image view object
		this.binding.imageView.setImageBitmap(this.CapturedImage);

		// Instantiate the "FacePaint" object
		this.FacePaint = new Paint();
		this.FacePaint.setColor(Color.RED);
		this.FacePaint.setStyle(Paint.Style.STROKE);
		this.FacePaint.setStrokeWidth(10f);

		// Instantiate the "FaceLandmarkPaint" object
		this.FaceLandmarkPaint = new Paint();
		this.FaceLandmarkPaint.setColor(Color.BLUE);
		this.FaceLandmarkPaint.setStyle(Paint.Style.STROKE);
		this.FaceLandmarkPaint.setStrokeWidth(25f);

		this.FaceContourPaint = new Paint();
		this.FaceContourPaint.setColor(Color.RED);
		this.FaceContourPaint.setStyle(Paint.Style.STROKE);
		this.FaceContourPaint.setStrokeWidth(8f);

		this.ItemPaint = new Paint();
		this.ItemPaint.setColor(Color.BLUE);
		this.ItemPaint.setStyle(Paint.Style.STROKE);
		this.ItemPaint.setStrokeWidth(25f);
	}

	/*
	 * ===================== *
	 * = Helpers (Private) = *
	 * ===================== *
	 */

	private void showToastMessage(@NonNull final String errorMessage)
	{
		Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
	}

	/*
	 * ===================== *
	 * = Helpers (Private) = *
	 * ===================== *
	 */

	private void detectFace()
	{
		this.FaceDetectorObject.process(InputImage.fromBitmap(this.CapturedImage, this.CapturedImageRotationDegrees))
			.addOnSuccessListener(faces ->
			{
			   for (Face currentFace : faces)
			   {
			   		this.CaptureImageCanvas.drawRect(currentFace.getBoundingBox(), this.FacePaint);

			   		final List<FaceLandmark> allFaceLandmarks = currentFace.getAllLandmarks();

					for (FaceLandmark currentFaceLandmark : allFaceLandmarks)
					{
						this.CaptureImageCanvas.drawPoint(currentFaceLandmark.getPosition().x, currentFaceLandmark.getPosition().y, this.FaceLandmarkPaint);
					}

					final List<FaceContour> allFaceContour = currentFace.getAllContours();

					for (FaceContour currentFaceContour : allFaceContour)
					{
						List<PointF> points = currentFaceContour.getPoints();
						List<Float> allNoseBottomPoints = new ArrayList<>();

					   for (PointF point : points)
					   {
						   allNoseBottomPoints.add(point.x);
						   allNoseBottomPoints.add(point.y);
					   }

					   float[] pointsAsArray = new float[allNoseBottomPoints.size()];
					   int i = 0;
					   for (Float currentPoint: allNoseBottomPoints)
					   {
							pointsAsArray[i] = currentPoint;
							i++;
					   }

					   this.CaptureImageCanvas.drawLines(pointsAsArray, this.FaceContourPaint);
					}
				}

			   this.binding.imageView.setImageBitmap(this.CapturedImage);

				// Close "Please Wait" alert dialog
				PleaseWaitAlertDialogObject.close();
			})
			.addOnFailureListener(e ->
			{
				// Close "Please Wait" alert dialog
				PleaseWaitAlertDialogObject.close();

				// Show error message
				showToastMessage("Error while processing images...\n\n" + e.getMessage());
			});
	}

	private void detectItems()
	{
		this.ObjectDetectorObject.process(InputImage.fromBitmap(this.CapturedImage, this.CapturedImageRotationDegrees))
				.addOnCompleteListener(task ->
				{
					if (task.isComplete() && task.isSuccessful())
					{
						for (DetectedObject currentDetectedObject : task.getResult())
						{
							this.CaptureImageCanvas.drawRect(currentDetectedObject.getBoundingBox(), this.ItemPaint);
						}
					}

					// Close "Please Wait" alert dialog
					PleaseWaitAlertDialogObject.close();
				})
				.addOnFailureListener(e ->
				{
					// Close "Please Wait" alert dialog
					PleaseWaitAlertDialogObject.close();

					// Show error message
					showToastMessage("Error while processing images...\n\n" + e.getMessage());
				});
	}

	/*
	 * ===================== *
	 * = Methods (Private) = *
	 * ===================== *
	 */

	private Bitmap getImageAsBitmap()
	{
		@NonNull final ImageProxy.PlaneProxy imagePlaneProxy = CachedObjects.CapturedImage.getPlanes()[0];
		@NonNull final ByteBuffer            byteBuffer      = imagePlaneProxy.getBuffer();
		byte[] imageAsBytes;

		if (byteBuffer.hasArray())
		{
			imageAsBytes = byteBuffer.array();
		}
		else
		{
			imageAsBytes = new byte[byteBuffer.remaining()];
			byteBuffer.get(imageAsBytes);
		}

		// Release image
		CachedObjects.CapturedImage.close();

		return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
	}

	@SuppressLint( "UnsafeExperimentalUsageError" )
	private void onClickDetectFacesButton(View view)
	{
		try
		{
			// Open "Please Wait" alert dialog
			this.PleaseWaitAlertDialogObject.open();

			if (CachedObjects.ORIGIN_BUTTON == R.id.FaceDetectionButton)
			{
				this.detectFace();
			}

			if (CachedObjects.ORIGIN_BUTTON == R.id.ItemDetectionButton)
			{
				this.detectItems();
			}
		}
		catch (Exception e)
		{
			this.showToastMessage("Error while loading image...\n\n" + e.getMessage());
		}
	}
}