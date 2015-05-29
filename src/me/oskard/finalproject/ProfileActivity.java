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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends Activity {

	final String TAG = "ProfileActivity.java";

	public TextView
		title,
		text;
	
	public Button
		autoLoadButton;
	
	public String 
		username,
		loginString,
		storedLoginString;

	public ImageView
		avatar;

	private AppSharedPreferences
			appSharedPreferences = new AppSharedPreferences();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate start");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		Intent intent = getIntent();
		
		username = intent.getStringExtra("username");
		
		title = (TextView) findViewById(R.id.profile_title);
		text = (TextView) findViewById(R.id.profile_text);
		autoLoadButton = (Button) findViewById(R.id.enable_autoload_button);
		avatar = (ImageView) findViewById(R.id.imgAvatar);
		
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
		Log.d(TAG, "Log Out button clicked");
		
		removeStoredUsername();
		removeStoredLoginString();
		
		finish();
	}
	
	public void toggleAutoLoad(View v) {
		Log.d(TAG, "Autoload button clicked");
		
		if(storedLoginString.length() == 0) {
			setStoredUsername();
			setStoredLoginString();
			
			Toast.makeText(ProfileActivity.this, "You have activated automagic(!) login", Toast.LENGTH_LONG).show();
			
			Log.d(TAG, "loginString stored");
		} else {
			removeStoredLoginString();
			
			Toast.makeText(ProfileActivity.this, "You have disabled automagic(!) login", Toast.LENGTH_LONG).show();
			
			Log.d(TAG, "loginString removed");
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
		Log.d(TAG, "Loading profile...");
		title.setText(username);
		text.setText("Loading...");
		new AsyncTaskParseJson(this).execute();
	}
	
	public void finishLoadingProfile(User user) {
		Log.d(TAG, "Finished loading profile");
		text.setText(
				"E-mail: " + user.getEmail()
		);

		avatar.setImageBitmap(user.getAvatarData());
	}
	
	public void setStoredUsername() {
		appSharedPreferences.getSharedPreferencesEditor(this).putString("stored_username", getStoredUsername());
		appSharedPreferences.getSharedPreferencesEditor(this).commit();
	}
	
	public void setStoredLoginString() {
		appSharedPreferences.getSharedPreferencesEditor(this).putString("stored_login_string", loginString);
		appSharedPreferences.getSharedPreferencesEditor(this).commit();
	}
	
	public void removeStoredLoginString() {
		appSharedPreferences.getSharedPreferencesEditor(this).remove("stored_login_string");
		appSharedPreferences.getSharedPreferencesEditor(this).commit();
	}
	
	public void removeStoredUsername() {
		appSharedPreferences.getSharedPreferencesEditor(this).remove("stored_usernameg");
		appSharedPreferences.getSharedPreferencesEditor(this).commit();
	}
	
	/**
	 * Gets a stored username from local storage if available
	 * @return String stored username, or empty string if nothing found
	 */
	public String getStoredUsername() {
		return appSharedPreferences.getSharedPreferences(this).getString("stored_username", "");
	}
	
	/**
	 * Gets a stored login string from local storage if available
	 * @return String stored login string, or empty string if nothing found
	 */
	public String getStoredLoginString() {
		return appSharedPreferences.getSharedPreferences(this).getString("stored_login_string", "");
	}
	
	public class AsyncTaskParseJson extends AsyncTask<String, String, String> {

	    public ProfileActivity ProfileActivity;

	    public AsyncTaskParseJson(ProfileActivity activity) {
	    	ProfileActivity = activity;
	    }

	    @Override
	    protected void onPreExecute() {}
	
	    @Override
	    protected String doInBackground(String... arg0) {
	    	Log.d(TAG, "Started background process to load user");

			final User loadedUser = User.getUserWithName(username);

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					ProfileActivity.finishLoadingProfile(loadedUser);
				}
			});
			
	        return null;
	    }
	
	    @Override
	    protected void onPostExecute(String strFromDoInBg) {}
	}
}
