package me.daei.soundmeter;



public class World {

	public static float dbCount = 40;

	private static float lastDbCount = dbCount;
	private static float min = 0.5f;  //设置声音最低变化
	private static float value = 0;   // 声音分贝值
	public static void setDbCount(float dbValue) {
		if (dbValue > lastDbCount) {
			value = dbValue - lastDbCount > min ? dbValue - lastDbCount : min;
		}else{
			value = dbValue - lastDbCount < -min ? dbValue - lastDbCount : -min;
		}
		dbCount = lastDbCount + value * 0.2f ; //防止声音变化太快
		lastDbCount = dbCount;
	}
	
}
