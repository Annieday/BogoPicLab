package es.softwareprocess.bogopicgen;

/*
 * Copyright 2012  Bryan Liles <iam@smartic.us> and Abram Hindle <abram.hindle@softwareprocess.es> . All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, are
 permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice, this list of
 conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice, this list
 of conditions and the following disclaimer in the documentation and/or other materials
 provided with the distribution.

 THIS SOFTWARE IS PROVIDED BY Bryan Liles <iam@smartic.us> and Abram Hindle <abram.hindle@softwareprocess.es> ''AS IS'' AND ANY EXPRESS OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR
 CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 The views and conclusions contained in the software and documentation are those of the
 authors and should not be interpreted as representing official policies, either expressed
 or implied, of Bryan Liles <iam@smartic.us> and Abram Hindle <abram.hindle@softwareprocess.es>.

 * 
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class BogoPicGenActivity extends Activity {

	Uri imageFileUri;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		setBogoPic();
		setText();
		Button randomButton = (Button) findViewById(R.id.randomButton);

		ImageButton button = (ImageButton) findViewById(R.id.TakeAPhoto);
		OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				setBogoPic();
			}
		};
		OnClickListener listener1 = new OnClickListener() {
			public void onClick(View v) {
				setText();
			}
		};
		button.setOnClickListener(listener);
		randomButton.setOnClickListener(listener1);
		Button acceptButton = (Button) findViewById(R.id.Accept);

		acceptButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				processIntent(false);
			}
		});

		Button cancelButton = (Button) findViewById(R.id.Cancel);

		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				processIntent(true);
			}
		});

	}

	private Bitmap ourBMP;
	private String randomString;

	private void setBogoPic() {
		// TODO: Show a toast with message "Generating Photo"
		Toast.makeText(this, "Generating Photo", Toast.LENGTH_LONG).show();

		// TODO: Get a reference to the image button
		ImageButton button = (ImageButton) findViewById(R.id.TakeAPhoto);

		// Generate a bogopic
		ourBMP = BogoPicGen.generateBitmap(400, 400);

		// TODO: Assign the bogopic to the button with setImageBitmap
		button.setImageBitmap(ourBMP);

	}

	private void setText() {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Generating Text", Toast.LENGTH_LONG).show();
		TextView randomView = (TextView) findViewById(R.id.randomString);
		randomString = getRandomString();
		randomView.setText(randomString);
	}

	// http://iamvijayakumar.blogspot.ca/2013/04/generate-random-string-in-android.html
	// 2014 Oct 7
	Random random = new Random();
	private static final String _CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	private static final int RANDOM_STR_LENGTH = 12;

	public String getRandomString() {
		StringBuffer randStr = new StringBuffer();
		for (int i = 0; i < RANDOM_STR_LENGTH; i++) {
			int number = getRandomNumber();
			char ch = _CHAR.charAt(number);
			randStr.append(ch);
		}
		return randStr.toString();
	}

	private int getRandomNumber() {
		int randomInt = 0;
		randomInt = random.nextInt(_CHAR.length());
		if (randomInt - 1 == -1) {
			return randomInt;
		} else {
			return randomInt - 1;
		}
	}

	// Call this to accept
	private void processIntent(boolean cancel) {
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}

		try {
			if (intent.getExtras() != null) {
				// TODO: If cancelled, show a toast, set result to
				// RESULT_CANCELED, finish and return
				if (cancel) {
					Toast.makeText(this, "Canceling Photo", Toast.LENGTH_LONG)
							.show();
					setResult(RESULT_CANCELED);
					finish();
					return;
				}

				// If accepted save the picture
				File intentPicture = getPicturePath(intent);
				saveBMP(intentPicture, ourBMP);
				
				intent.putExtra("random",randomString);
				// TODO: set result to RESULT_OK
				setResult(RESULT_OK);

			} else {
				Toast.makeText(this, "Photo Cancelled: No Reciever?",
						Toast.LENGTH_LONG).show();
				setResult(RESULT_CANCELED);
			}
		} catch (FileNotFoundException e) {
			Toast.makeText(this, "Couldn't Find File to Write to?",
					Toast.LENGTH_LONG).show();
			setResult(RESULT_CANCELED);
		} catch (IOException e) {
			Toast.makeText(this, "Couldn't Write File!", Toast.LENGTH_LONG)
					.show();
			setResult(RESULT_CANCELED);
		}
		finish();
	}

	private void saveBMP(File intentPicture, Bitmap ourBMP) throws IOException,
			FileNotFoundException {
		OutputStream out = new FileOutputStream(intentPicture);
		ourBMP.compress(Bitmap.CompressFormat.JPEG, 75, out);
		out.close();
	}

	private File getPicturePath(Intent intent) {
		Uri uri = (Uri) intent.getExtras().get(MediaStore.EXTRA_OUTPUT);
		return new File(uri.getPath());
	}
}