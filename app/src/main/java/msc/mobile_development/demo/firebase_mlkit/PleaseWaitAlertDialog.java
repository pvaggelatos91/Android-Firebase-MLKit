package msc.mobile_development.demo.firebase_mlkit;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class PleaseWaitAlertDialog
{
	/*
	 * ============================== *
	 * = Member Variables (Private) = *
	 * ============================== *
	 */

	@NonNull
	private final AlertDialog AlertDialog_PleaseWait;


	/*
	 * ============================= *
	 * = Constructors / Destructor = *
	 * ============================= *
	 */

	/**
	 * Initialize a new instance of "PleaseWaitAlertDialog" class.
	 * @param context The context to be used to initialize alert dialog objects
	 */
	public PleaseWaitAlertDialog(@NonNull final Context context)
	{
		this.AlertDialog_PleaseWait = new AlertDialog.Builder(context)
				.setCancelable(false)
				.setView(R.layout.activity_please_wait)
				.create();
	}

	/*
	 * ==================== *
	 * = Methods (Public) = *
	 * ==================== *
	 */

	/**
	 * Open "Please Wait" alert dialog.
	 */
	public void open()
	{
		// Show "Please Wait" alert dialog
		this.AlertDialog_PleaseWait.show();
	}

	/**
	 * Close "Please Wait" alert dialog.
	 */
	public void close()
	{
		// Dismiss (close) the "Please Wait" dialog
		this.AlertDialog_PleaseWait.dismiss();
	}
}
