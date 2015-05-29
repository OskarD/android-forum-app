package me.oskard.finalproject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import me.oskard.finalproject.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class SplashActivity extends Activity {
	final String TAG = "SplashActivity.java";

	private AppSharedPreferences appSharedPreferences = new AppSharedPreferences();;

	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;
	
	public String
		storedUsername,
		storedLoginString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_splash);

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_content);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.cancel_button).setOnTouchListener(
				mDelayHideTouchListener);
		
		processStartup();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
	
	/**
	 * When the user presses "Cancel" button
	 * @param v
	 */
	public void cancel(View v) {
		finish();
	}
	
	/**
	 * Perform background-actions for startup
	 */
	public boolean processStartup() {
		Log.d(TAG, "STARTED");
		
		// Check if a username is stored
		storedUsername = getStoredUsername();
		storedLoginString = getStoredLoginString();
		Log.d(TAG, "Stored username: " + storedUsername);
		Log.d(TAG, "Stored loginString: " + storedLoginString);
		
		if(storedUsername.length() > 0) {
			// Attempt automatic login
			if(storedLoginString.length() > 0) {
				Log.d(TAG, "Stored login string is available, attempting to automatically login...");

				// Start process to automatically log in. It will run while the user is directed to the login page
				// and if it works, the user will be redirected automatically
				attemptAutoLogin();
			}

			Log.d(TAG, "Stored username is available, starting LoginActivity and finishing SplashActivity...");
			
			// Load login activity with saved username
			Intent intent = new Intent(this, LoginActivity.class);
			intent.putExtra(AppSharedPreferences.TAG_STORED_USERNAME, storedUsername);
			startActivity(intent);
			
			finish();
			return true;
		}
		
		startLoginRegisterActivity();
		return false;
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
	
	public void startLoginRegisterActivity() {
		Log.d(TAG, "Starting LoginRegisterActivity...");
		// Load login/register activity
		Intent intent = new Intent(this, LoginRegisterActivity.class);
		startActivity(intent);
		Log.d(TAG, "Finishing SplashActivity...");
		finish();
	}
	
	/**
	 * Attempt to login automatically with stored login-string
	 * @return boolean true if succeeded, false if failed
	 */
	public void attemptAutoLogin() {
		new AsyncTaskParseJson(this).execute();
	}
	
	public void autoLoginFinish(User user) {
		if(user == null) {
			Log.d(TAG, "Could not get user with the name. Probably no internet connection.");
			
			startLoginRegisterActivity();
		}
		else if(storedLoginString.compareTo(user.getLoginString()) != 0) {// Compare loginstrings
			Log.d(TAG, "Stored login strings does not match DB, removing stored login string.");
			
			// Remove stored login string
			appSharedPreferences.getSharedPreferencesEditor(this).remove(AppSharedPreferences.TAG_STORED_LOGIN_STRING);
			appSharedPreferences.getSharedPreferencesEditor(this).commit();
			
			processStartup();
		}
		else {
			Log.d(TAG, "attemptAutoLogin succeeded, starting SplashActivity and finishing SplashActivity...");
			Intent intent = new Intent(this, ProfileActivity.class);
			intent.putExtra("username", storedUsername);
			startActivity(intent);
			finish();
		}
	}
	
	/**
	 * Gets a stored username from local storage if available
	 * @return String stored username, or empty string if nothing found
	 */
	public String getStoredUsername() {
		SharedPreferences test = appSharedPreferences.getSharedPreferences(this);
		return test.getString(AppSharedPreferences.TAG_STORED_USERNAME, "");
	}
	
	/**
	 * Gets a stored login string from local storage if available
	 * @return String stored login string, or empty string if nothing found
	 */
	public String getStoredLoginString() {
		return appSharedPreferences.getSharedPreferences(this).getString(AppSharedPreferences.TAG_STORED_LOGIN_STRING, "");
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
	
	    String name;
	    String yourJsonStringUrl;
	    
	    // contacts JSONArray
	    JSONArray dataJsonArr = null;
	    
	    public SplashActivity SplashActivity;
	    
	    public AsyncTaskParseJson(SplashActivity activity) {
	    	SplashActivity = activity;
	    }
	
	    @Override
	    protected void onPreExecute() {}
	
	    @Override
	    protected String doInBackground(String... arg0) {
	    	Log.d(TAG, "Started background process");
	    	name = storedUsername;
	    	
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
			
			try {
				Log.d(TAG, "Username returned: " + json.getString(TAG_NAME));
				
				final User user = new User(
						json.getInt(TAG_ID),
						json.getString(TAG_NAME),
						json.getString(TAG_LOGIN_STRING),
						json.getString(TAG_EMAIL),
						json.getInt(TAG_DEFAULT_GROUP),
						json.optInt(TAG_AVATAR)
				);
				
				storedLoginString = user.getLoginString();
				
				runOnUiThread(new Runnable() {
				     @Override
				     public void run() {
				    	 SplashActivity.autoLoginFinish(user);
				    }
				});
				
				Log.d(TAG, "User loaded: " + user.toString());
			} catch (JSONException e) {
				Log.d(TAG, "The user could not be loaded. Probably doesn't exist, or there is no internet connection");
				runOnUiThread(new Runnable() {
				     @Override
				     public void run() {
						SplashActivity.autoLoginFinish(null);
				    }
				});
			}
			
	        return null;
	    }
	
	    @Override
	    protected void onPostExecute(String strFromDoInBg) {}
	}
}
