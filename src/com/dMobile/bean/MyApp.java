package com.dMobile.bean;

import java.util.Date;

import android.provider.BaseColumns;


public class MyApp {

	public static String[] colunas = new String[]{MyApps._ID, MyApps.NAME, MyApps.NAME_NO_ACCENT,
		MyApps.IMAGE, MyApps.PKG, MyApps.ACTIVITY_NAME, MyApps.COUNT_ACCESS};

	public static final String AUTHORITY = "com.dmobile.provider.myappbean";
	private long id;
	private String name;
	private String name_no_accent;
	private byte[] image;
	private String pkg;
	private String activityName;
	private int countAccess;
	private Date date_installed;

	public MyApp() {
	}

	public MyApp(String name, String name_no_accent, byte[] image, String pkg,
			String activityName){
		super();
		this.name = name;
		this.name_no_accent = name_no_accent;
		this.image = image;
		this.pkg = pkg;
		this.activityName = activityName;
	}

	public MyApp(long id, String name, String name_no_accent, byte[] image, String pkg,
			String activityName) {
		super();
		this.id = id;
		this.name = name;
		this.name_no_accent = name_no_accent;
		this.image = image;
		this.pkg = pkg;
		this.activityName = activityName;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName_no_accent() {
		return name_no_accent;
	}

	public void setName_no_accent(String name_no_accent) {
		this.name_no_accent = name_no_accent;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public String getPkg() {
		return pkg;
	}

	public void setPkg(String pkg) {
		this.pkg = pkg;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public int getCountAccess() {
		return countAccess;
	}

	public void setCountAccess(int countAccess) {
		this.countAccess = countAccess;
	}

	public Date getDate_installed() {
		return date_installed;
	}

	public void setDate_installed(Date date_installed) {
		this.date_installed = date_installed;
	}

	public static final class MyApps implements BaseColumns{
		private MyApps(){			
		}
	
	public static final String NAME = "name";
	public static final String NAME_NO_ACCENT = "name_no_accent";
	public static final String IMAGE = "image";
	public static final String PKG = "pkg";
	public static final String ACTIVITY_NAME = "activity_name";
	public static final String COUNT_ACCESS = "count_access"; 
	public static final String DATE_INSTALLED = "date_installed";


	}
}
