package me.daei.soundmeter;

import android.os.Environment;
import android.widget.TextView;


public class World {
	public static float dbCount = 40;

	private static float lastDbCount = dbCount;
	public static void setDbCount(float dbValue) {
		dbCount = lastDbCount + (dbValue - lastDbCount) * 0.2f;
		lastDbCount = dbCount;
	}
	
}
