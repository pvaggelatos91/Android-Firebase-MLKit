package msc.mobile_development.demo.firebase_mlkit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;

import msc.mobile_development.demo.firebase_mlkit.databinding.ActivityFaceDetectionBinding;

public class FaceDetectionActivity extends AppCompatActivity
{
	/*
	 * ============================== *
	 * = Member Variables (Private) = *
	 * ============================== *
	 */

	private final int    PERMISSIONS_REQUEST_CODE = 1000;

	@NonNull
	private ActivityFaceDetectionBinding binding;

	@NonNull
	private ImageCapture ImageCaptureObject;

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
		this.binding = ActivityFaceDetectionBinding.inflate(getLayoutInflater());

		// Set layout
		setContentView(this.binding.getRoot());

		// Instantiate the "ImageCaptureObject" object
		this.ImageCaptureObject = new ImageCapture.Builder().build();

		// Register onClick event for "Take Photo" button
		this.binding.cameraCaptureButton.setOnClickListener(this::takePhoto);

		// Request user permissions
		this.requestUserPermissions();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		// Call parent method
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (requestCode == this.PERMISSIONS_REQUEST_CODE)
		{
			// Case: Permission granted
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
			{
				this.startCamera();
			}
			else
			{
				finish();
			}
		}
	}

	/*
	 * ===================== *
	 * = Helpers (Private) = *
	 * ===================== *
	 */

	private void showMessage(@NonNull final String message)
	{
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	private void openPreviewImageActivity()
	{
		startActivity(new Intent(this, PreviewImageActivity.class));
	}

	/*
	 * ===================== *
	 * = Methods (Private) = *
	 * ===================== *
	 */

	private File getOutputFileDirectory()
	{
		final File parent = getExternalMediaDirs()[0];
		final File outputFileDirectory = new File(parent, getString(R.string.app_name));

		if (outputFileDirectory != null)
		{
			outputFileDirectory.mkdir();

			if (outputFileDirectory.exists())
			{
				return outputFileDirectory;
			}
			else
			{
				return this.getFilesDir();
			}
		}
		else
		{
			return this.getFilesDir();
		}
	}

	private void requestUserPermissions()
	{
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
		{
			this.startCamera();
		}
		else
		{
			// Request permissions
			requestPermissions(new String[] { Manifest.permission.CAMERA }, PERMISSIONS_REQUEST_CODE);
		}
	}

	private void startCamera()
	{
		// Local variables
		ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

		//
		cameraProviderFuture.addListener(() ->
		{
			try
			{
				// Instantiate the "cameraProvider" object
				final ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

				// Instantiate the "preview" object
				final Preview preview = new Preview.Builder().build();

				preview.setSurfaceProvider(this.binding.viewFinder.getSurfaceProvider());
				cameraProvider.unbindAll();

				if (CachedObjects.ORIGIN_BUTTON == R.id.FaceDetectionButton)
				{
					cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_FRONT_CAMERA, this.ImageCaptureObject, preview);
				}

				if (CachedObjects.ORIGIN_BUTTON == R.id.ItemDetectionButton)
				{
					cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, this.ImageCaptureObject, preview);
				}
			}
			catch (ExecutionException | InterruptedException e)
			{
				Log.e("FirebaseMLKit_DEMO", "[msc.mobile_development.demo.firebase_mlkit][FaceDetectionActivity][startCamera] " + e.getMessage());
			}
		}, ContextCompat.getMainExecutor(this));
	}

	private void takePhoto(View view)
	{
		this.ImageCaptureObject.takePicture(ContextCompat.getMainExecutor(this), new ImageCapture.OnImageCapturedCallback()
		{
			@Override
			public void onCaptureSuccess(@NonNull ImageProxy image)
			{
				// Call parent method
				super.onCaptureSuccess(image);

				// Set to the object that contains the captured image the new captured image
				CachedObjects.CapturedImage = image;

				// Start "Preview Image" activity
				openPreviewImageActivity();
			}

			@Override
			public void onError(@NonNull ImageCaptureException exception)
			{
				// Call parent method
				super.onError(exception);

				// Show the error
				showMessage("Failed to capture image.\n\n" + exception.getMessage());
			}
		});
	}
}