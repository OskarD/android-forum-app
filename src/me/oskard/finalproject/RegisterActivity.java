package me.oskard.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class RegisterActivity extends Activity {

	final String TAG = "RegisterActivity.java";

	public EditText
		username,
		email,
		password,
		repeatedPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		username = (EditText) findViewById(R.id.new_username_et);
		email = (EditText) findViewById(R.id.new_email_et);
		password = (EditText) findViewById(R.id.new_password_et);
		repeatedPassword = (EditText) findViewById(R.id.new_repeat_password_et);
	}
	
	public void goLoginPressed(View v) {
		Log.d(TAG, "Login text pressed, starting LoginActivity and finishing RegisterActivity...");
		
		// Load register activity
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
	
	public void registerPressed(View v) {
		Log.d(TAG, "Register-button pressed");
		
		username.setEnabled(false);
		email.setEnabled(false);
		password.setEnabled(false);
		repeatedPassword.setEnabled(false);
		
		if(verifyInput())
			registerUser();
		else {
			username.setEnabled(true);
			email.setEnabled(true);
			password.setEnabled(true);
			repeatedPassword.setEnabled(true);
		}
	}
	
	public boolean verifyInput() {
		Log.d(TAG, "verifyInput() called");
		if(username.getText().length() < 3) {
			Toast.makeText(this, "Your username needs to be longer than 3 characters", Toast.LENGTH_LONG).show();
			return false;
		}
		
		if(email.getText().length() < 6) {
			Toast.makeText(this, "Your email needs to be longer than 6 characters", Toast.LENGTH_LONG).show();
			return false;
		}
		
		if(password.getText().length() < 4) {
			Toast.makeText(this, "Your password needs to be longer than 4 characters", Toast.LENGTH_LONG).show();
			return false;
		}
		
		String
			passwordString = password.getText().toString().trim(),
			repeatString = repeatedPassword.getText().toString().trim();
		
		if(passwordString.compareTo(repeatString) != 0) {
			Toast.makeText(this, "Your passwords aren't matching", Toast.LENGTH_LONG).show();
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.about) {
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public boolean registerUser() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		String nameString, emailString, passwordString;
		
		try {
			nameString = URLEncoder.encode(username.getText().toString().trim(), "UTF-8");
			emailString = URLEncoder.encode(email.getText().toString().trim(), "UTF-8");
			passwordString = URLEncoder.encode( MD5.getMD5(password.getText().toString().trim() ), "UTF-8");
			
			String urlString = "http://oskard.me/forum/mobile/app_register.php?name=" + nameString + "&email=" + emailString + "&password=" + passwordString;
			
			URL url = new URL(urlString);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			in.read();
			urlConnection.disconnect();

			// Registered.
			Log.d(TAG, "User registered: " + nameString);
			Toast.makeText(this, "Account has been registered!", Toast.LENGTH_LONG).show();

			Intent intent = new Intent(this, LoginActivity.class);
			intent.putExtra("stored_username", username.getText().toString());
			startActivity(intent);

			finish();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
}
