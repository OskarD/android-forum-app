package me.oskard.finalproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class User {

	final static String TAG = "User.java";

	// Node names from JSON
	public static final String
			TAG_ID = "id",
			TAG_NAME = "name",
			TAG_LOGIN_STRING = "loginString",
			TAG_EMAIL = "email",
			TAG_DEFAULT_GROUP = "defaultGroup",
			TAG_AVATAR = "avatar",
			TAG_AVATAR_DATA = "avatarData";

	private int
		id,
		defaultGroup,
		avatar;
	
	private String
		name,
		loginString,
		email;

	private Bitmap
		avatarData;

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

	public Bitmap getAvatarData() {
		return avatarData;
	}

	public void setAvatarData(Bitmap avatarData) {
		this.avatarData = avatarData;
	}

	public User() {}

	public User(int id, String name, String password,
				String email, int defaultGroup, int avatar) {
		this.id = id;
		this.defaultGroup = defaultGroup;
		this.name = name;
		this.loginString = password;
		this.email = email;
		this.avatar = avatar;
	}

	public User(int id, String name, String password,
			String email, int defaultGroup, int avatar,
				Bitmap avatarData) {
		this.id = id;
		this.defaultGroup = defaultGroup;
		this.name = name;
		this.loginString = password;
		this.email = email;
		this.avatar = avatar;
		this.avatarData = avatarData;
	}

	public static User getUserWithName(String name) {

		Log.v(TAG, "loadUserWithName started with name: [" + name + "]");

		String url = "";

		try {
			url = "http://oskard.me/forum/mobile/app_login.php?name=" + URLEncoder.encode(name, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "Encoding is unsupported", e);
		}

		Log.i(TAG, "url: " + url);
		
		JsonParser jParser = new JsonParser();
		JSONObject json = jParser.getJSONFromUrl(url);

		Log.i(TAG, "json result: " + json.toString());

		String loginString;
		User user = null;

		try {
			Log.i(TAG, "Username returned: " + json.getString(TAG_NAME));

			String avatar = json.optString(TAG_AVATAR_DATA);
			byte[] decodedString = Base64.decode(avatar, Base64.DEFAULT);
			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

			user = new User(
					json.getInt(TAG_ID),
					json.getString(TAG_NAME),
					json.getString(TAG_LOGIN_STRING),
					json.getString(TAG_EMAIL),
					json.getInt(TAG_DEFAULT_GROUP),
					json.optInt(TAG_AVATAR),
					decodedByte
			);

		} catch (JSONException e) {
			Log.e(TAG, "Failed to parse json string: " + json.toString(), e);
		}

		Log.d(TAG, "User loaded: " + user.toString());

		return user;
	}
}
