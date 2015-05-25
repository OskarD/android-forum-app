package me.oskard.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class LoginRegisterActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login_register);
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
	
	public void loginPressed(View v) {
		Log.d("LoginRegisterActivity", "Login-button pressed, starting LoginActivity...");
		
		// Load login activity
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}
	
	public void registerPressed(View v) {
		Log.d("LoginRegisterActivity", "Register-button pressed, starting RegisterActivity");
		
		// Load register activity
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
	}
}
