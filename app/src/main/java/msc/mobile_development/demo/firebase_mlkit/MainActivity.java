package msc.mobile_development.demo.firebase_mlkit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import msc.mobile_development.demo.firebase_mlkit.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity
{
	/*
	 * ============================== *
	 * = Member Variables (Private) = *
	 * ============================== *
	 */

	private ActivityMainBinding binding;

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
		this.binding = ActivityMainBinding.inflate(getLayoutInflater());

		// Set layout
		setContentView(this.binding.getRoot());

		// Set click listener for "Face Detection" button
		this.binding.FaceDetectionButton.setOnClickListener(this::onClickFaceDetectionButton);

		// Set click listener for "Items Detection" button
		this.binding.ItemDetectionButton.setOnClickListener(this::onClickItemDetectionButton);
	}

	/*
	 * ===================== *
	 * = Methods (Private) = *
	 * ===================== *
	 */

	private void startCameraActivity()
	{
		// Start the "FaceDetectionActivity" activity
		startActivity(new Intent(this, FaceDetectionActivity.class));
	}

	/*
	 * ===================== *
	 * = Methods (Private) = *
	 * ===================== *
	 */

	private void onClickFaceDetectionButton(View view)
	{
		// Set the origin button
		CachedObjects.ORIGIN_BUTTON = R.id.FaceDetectionButton;

		// Start camera activity
		this.startCameraActivity();
	}

	private void onClickItemDetectionButton(View view)
	{
		// Set the origin button
		CachedObjects.ORIGIN_BUTTON = R.id.ItemDetectionButton;

		// Start camera activity
		this.startCameraActivity();
	}
}