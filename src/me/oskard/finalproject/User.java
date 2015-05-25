package me.oskard.finalproject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class User {
	private int
		id,
		defaultGroup,
		avatar;
	
	private String
		name,
		loginString,
		email;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDefaultGroup() {
		return defaultGroup;
	}

	public void setDefaultGroup(int defaultGroup) {
		this.defaultGroup = defaultGroup;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLoginString() {
		return loginString;
	}

	public void setLoginString(String loginString) {
		this.loginString = loginString;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getAvatar() {
		return avatar;
	}

	public void setAvatar(int avatar) {
		this.avatar = avatar;
	}

	public User() {}

	public User(int id, String name, String password,
			String email, int defaultGroup, int avatar) {
		super();
		this.id = id;
		this.defaultGroup = defaultGroup;
		this.name = name;
		this.loginString = password;
		this.email = email;
		this.avatar = avatar;
	}

	/**
	 * @todo Clean up or use
	 */
	public static User loadUserWithName(String name) {
		String url = "";
		
		try {
			url = "http://oskard.me/forum/app/get_user.php?name=" + URLEncoder.encode(name, "UTF-8");
			Log.d("User", "url: " + url);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JsonParser jParser = new JsonParser();
		JSONObject json = jParser.getJSONFromUrl(url);
		try {
			Log.d("User", json.toString(2));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		User user = new User(0, "", "", "", 0, 0);
		/*
		try {
			// Get JSON array
			JSONObject c = json.get
			
			// Create new user
			user = new User(
				json.getInt(TAG_ID),
				json.getString(TAG_NAME),
				json.getString(TAG_LOGIN_STRING),
				json.getString(TAG_EMAIL),
				json.getInt(TAG_DEFAULT_GROUP)
			);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}*/
		
		return user;
	}
}
