package me.oskard.finalproject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends Activity {
	public TextView
		title,
		text;
	
	public Button
		autoLoadButton;
	
	public String 
		username,
		loginString,
		storedLoginString;
	
	SharedPreferences 
		mySharedPreferences;
	
	SharedPreferences.Editor
		editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("ProfileActivity", "onCreate start");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		mySharedPreferences = getSharedPreferences("mySharedPreferences", Activity.MODE_PRIVATE);
		editor = mySharedPreferences.edit();
		
		Intent intent = getIntent();
		
		username = intent.getStringExtra("username");
		
		title = (TextView) findViewById(R.id.profile_title);
		text = (TextView) findViewById(R.id.profile_text);
		autoLoadButton = (Button) findViewById(R.id.enable_autoload_button);
		
		storedLoginString = getStoredLoginString();
		
		updateAutoloadButton();
		
		loadProfile();
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
	
	public void logout(View v) {
		Log.d("ProfileActivity", "Log Out button clicked");
		
		removeStoredUsername();
		removeStoredLoginString();
		
		finish();
	}
	
	public void toggleAutoLoad(View v) {
		Log.d("ProfileActivity", "Autoload button clicked");
		
		if(storedLoginString.length() == 0) {
			setStoredUsername();
			setStoredLoginString();
			
			Toast.makeText(ProfileActivity.this, "You have activated automagic(!) login", Toast.LENGTH_LONG).show();
			
			Log.d("ProfileActivity", "loginString stored");
		} else {
			removeStoredLoginString();
			
			Toast.makeText(ProfileActivity.this, "You have disabled automagic(!) login", Toast.LENGTH_LONG).show();
			
			Log.d("ProfileActivity", "loginString removed");
		}
		
		storedLoginString = getStoredLoginString();
		updateAutoloadButton();
	}
	
	public void updateAutoloadButton() {
		if(storedLoginString.length() == 0)
			autoLoadButton.setText("Enable Automagic Login");
		else
			autoLoadButton.setText("Disable Automagic Login");
	}
	
	public void loadProfile() {
		Log.d("ProfileActivity", "Loading profile...");
		title.setText(username);
		text.setText("Loading...");
		new AsyncTaskParseJson(this).execute();
	}
	
	public void finishLoadingProfile(User user) {
		Log.d("ProfileActivity", "Finished loading profile");
		text.setText(
				"E-mail: " + user.getEmail()
		);
	}
	
	public void setStoredUsername() {
		editor.putString("stored_username", getStoredUsername());
		editor.commit();
	}
	
	public void setStoredLoginString() {
		editor.putString("stored_login_string", loginString);
		editor.commit();
	}
	
	public void removeStoredLoginString() {
		editor.remove("stored_login_string");
		editor.commit();
	}
	
	public void removeStoredUsername() {
		editor.remove("stored_usernameg");
		editor.commit();
	}
	
	/**
	 * Gets a stored username from local storage if available
	 * @return String stored username, or empty string if nothing found
	 */
	public String getStoredUsername() {
		SharedPreferences mySharedPreferences = getSharedPreferences("mySharedPreferences", Activity.MODE_PRIVATE);
		return mySharedPreferences.getString("stored_username", "");
	}
	
	/**
	 * Gets a stored login string from local storage if available
	 * @return String stored login string, or empty string if nothing found
	 */
	public String getStoredLoginString() {
		SharedPreferences mySharedPreferences = getSharedPreferences("mySharedPreferences", Activity.MODE_PRIVATE);
		return mySharedPreferences.getString("stored_login_string", "");
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
	
	    String name = username;
	    String yourJsonStringUrl;
	    
	    // contacts JSONArray
	    JSONArray dataJsonArr = null;
	    
	    public ProfileActivity ProfileActivity;
	    
	    public AsyncTaskParseJson(ProfileActivity activity) {
	    	ProfileActivity = activity;
	    }
	
	    @Override
	    protected void onPreExecute() {}
	
	    @Override
	    protected String doInBackground(String... arg0) {
	    	Log.d("ProfileActivity", "Started background process");
	    	name = username;
	    	
	    	try {
				yourJsonStringUrl = "http://oskard.me/forum/app_login.php?name=" + URLEncoder.encode(name, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	Log.d("ProfileActivity", "url: " + yourJsonStringUrl);
	
	        // instantiate our json parser
			JsonParser jParser = new JsonParser();

			// get json string from url
			JSONObject json = jParser.getJSONFromUrl(yourJsonStringUrl);

			Log.d("ProfileActivity", "JSON: " + json.toString());
			
			// Attempt login
			Log.d("ProfileActivity", "Username entered: " + name);
			
			String result;
			
			try {
				Log.d("ProfileActivity", "Username returned: " + json.getString(TAG_NAME));
				
				final User user = new User(
						json.getInt(TAG_ID),
						json.getString(TAG_NAME),
						json.getString(TAG_LOGIN_STRING),
						json.getString(TAG_EMAIL),
						json.getInt(TAG_DEFAULT_GROUP),
						json.optInt(TAG_AVATAR)
				);
				
				loginString = user.getLoginString();
				
				runOnUiThread(new Runnable() {
				     @Override
				     public void run() {
				    	 ProfileActivity.finishLoadingProfile(user);
				    }
				});
				
				Log.d("ProfileActivity", "User loaded: " + user.toString());
			} catch (JSONException e) {
				Log.d("ProfileActivity", "The user could not be loaded. Probably doesn't exist, or there is no internet connection");
				runOnUiThread(new Runnable() {
				     @Override
				     public void run() {
						ProfileActivity.finishLoadingProfile(null);
				    }
				});
			}
			
	        return null;
	    }
	
	    @Override
	    protected void onPostExecute(String strFromDoInBg) {}
	}
}
