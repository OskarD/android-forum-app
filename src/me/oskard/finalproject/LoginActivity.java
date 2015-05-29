package me.oskard.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class LoginActivity extends Activity {

	final String TAG = "LoginActivity.java";
	
	public EditText
		username,
		password;
	
	public Switch
		rememberUsername;
	
	public Button
		loginButton;

	private AppSharedPreferences appSharedPreferences = new AppSharedPreferences();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate start");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		username = (EditText) findViewById(R.id.username_et);
		password = (EditText) findViewById(R.id.password_et);
		rememberUsername = (Switch) findViewById(R.id.remember_username_switch);
		loginButton = (Button) findViewById(R.id.login_button);
		
		// Get stored username if available
		Intent intent = getIntent();
		String storedName = intent.getStringExtra(AppSharedPreferences.TAG_STORED_USERNAME);
		Log.d(TAG, "Stored username: " + storedName);
		
		if(storedName != null)
			username.setText(storedName);
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
	
	public void goRegisterPressed(View v) {
		Log.d(TAG, "Login text pressed, starting RegisterActivity and finishing LoginActivity...");
		
		// Load register activity
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
		finish();
	}
	
	public void loginPressed(View v) {
		Log.d(TAG, "Login-button pressed");
		Log.d(TAG, "Username: " + username.getText());
		
		// Disable stuff
		username.setEnabled(false);
		password.setEnabled(false);
		rememberUsername.setEnabled(false);
		loginButton.setEnabled(false);
		
		new AsyncTaskParseJson(this).execute();
	}
	
	public void finishLogin(boolean loggedIn) {		
		if(loggedIn) {
			Toast.makeText(LoginActivity.this, "Login succeded!", Toast.LENGTH_LONG).show();
			
			if(rememberUsername.isChecked()) {
				Log.d(TAG, "Remember username switch is turned on, saving stored_username in shared preferences");
				appSharedPreferences.getSharedPreferencesEditor(this).putString(AppSharedPreferences.TAG_STORED_USERNAME, username.getText().toString().trim());
			}
			else {
				Log.d(TAG, "Remember username switch is turned off, deleting stored_username from shared preferences");
				appSharedPreferences.getSharedPreferencesEditor(this).remove(AppSharedPreferences.TAG_STORED_USERNAME);
			}
			appSharedPreferences.getSharedPreferencesEditor(this).commit();
			
			Log.d(TAG, "Starting intent ProfileActivity, finishing LoginActivity...");
			Intent intent = new Intent(this, ProfileActivity.class);
			intent.putExtra("username", username.getText().toString().trim());
			startActivity(intent);
			
		} else {
			Toast.makeText(LoginActivity.this, "Login failed. Please check your username and password and try again.", Toast.LENGTH_LONG).show();
		}
		
		// Enable stuff
		username.setEnabled(true);
		password.setEnabled(true);
		rememberUsername.setEnabled(true);
		loginButton.setEnabled(true);
	}
		
	public class AsyncTaskParseJson extends AsyncTask<String, String, String> {
		 
	    final String TAG = "AsyncTaskParseJson.java";
	    
	    // Node names from JSON
		private static final String
			TAG_ID = "id",
			TAG_NAME = "name",
			TAG_LOGIN_STRING = "loginString",
			TAG_EMAIL = "email",
			TAG_DEFAULT_GROUP = "defaultGroup",
			TAG_AVATAR = "avatar";
	
	    String name = username.getText().toString().trim();
	    String yourJsonStringUrl;
	    
	    // contacts JSONArray
	    JSONArray dataJsonArr = null;
	    
	    public LoginActivity LoginActivity;
	    
	    public AsyncTaskParseJson(LoginActivity activity) {
	    	LoginActivity = activity;
	    }
	
	    @Override
	    protected void onPreExecute() {}
	
	    @Override
	    protected String doInBackground(String... arg0) {	    	
	    	name = username.getText().toString().trim();
	    	
	    	try {
				yourJsonStringUrl = "http://oskard.me/forum/mobile/app_login.php?name=" + URLEncoder.encode(name, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	Log.d(TAG, "url: " + yourJsonStringUrl);
	
	        // instantiate our json parser
			JsonParser jParser = new JsonParser();

			// get json string from url
			JSONObject json = jParser.getJSONFromUrl(yourJsonStringUrl);

			Log.d(TAG, "JSON: " + json.toString());
			
			// Attempt login
			Log.d(TAG, "Username entered: " + name);
			
			User user = new User();
			String result;
			
			try {
				Log.d(TAG, "Username returned: " + json.getString(TAG_NAME));
				
				user = new User(
						json.getInt(TAG_ID),
						json.getString(TAG_NAME),
						json.getString(TAG_LOGIN_STRING),
						json.getString(TAG_EMAIL),
						json.getInt(TAG_DEFAULT_GROUP),
						json.optInt(TAG_AVATAR)
				);
				
				Log.d(TAG, "User loaded: " + user.toString());
			} catch (JSONException e) {
				Log.d(TAG, "The user could not be loaded. Probably doesn't exist, or no internet connection");
				Log.d(TAG, "Exception thrown: " + e.getMessage());
				runOnUiThread(new Runnable() {
				     @Override
				     public void run() {
						LoginActivity.finishLogin(false);
				    }
				});
			}
			
			String pass = MD5.getMD5(password.getText().toString().trim()); // Password is encrypted before added to loginString, where it is encrypted once more
			String loginString = MD5.getMD5(name.concat(pass));
			
			Log.d(TAG, "User loginString: " + loginString);
			Log.d(TAG, "DB loginString: " + user.getLoginString());
			
			if(user.getLoginString() != null && user.getLoginString().compareTo(loginString) == 0) {
				Log.d(TAG, "User/password matched!");
				
				runOnUiThread(new Runnable() {
				     @Override
				     public void run() {
						LoginActivity.finishLogin(true);
				    }
				});
			}
			else {
				Log.d(TAG, "User/password didn't match.");
				runOnUiThread(new Runnable() {
				     @Override
				     public void run() {
						LoginActivity.finishLogin(false);
				    }
				});
			}
			
	        return null;
	    }
	
	    @Override
	    protected void onPostExecute(String strFromDoInBg) {}
	}
}
