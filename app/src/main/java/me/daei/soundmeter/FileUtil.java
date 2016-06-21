package me.daei.soundmeter;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by su on 2016/6/4.
 */
public class FileUtil {

    private static final String TAG = "FileUtil";

    public static final String LOCAL = "SoundMeter";

    public static final String LOCAL_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator;

    /**
     * 录音文件目录
     */
    public static final String REC_PATH = LOCAL_PATH + LOCAL + File.separator;



    /**
     * 自动在SD卡创建相关的目录
     */
    static {
        File dirRootFile = new File(LOCAL_PATH);
        if (!dirRootFile.exists()) {
            dirRootFile.mkdirs();
        }
        File recFile = new File(REC_PATH);
        if (!recFile.exists()) {
            recFile.mkdirs();
        }
    }

    private FileUtil() {
    }

    /**
     * 判断是否存在存储空间	 *
     *
     * @return
     */
    public static boolean isExitSDCard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    private static boolean hasFile(String fileName) {
        File f = createFile(fileName);
        return null != f && f.exists();
    }

    public static File createFile(String fileName) {

        File myCaptureFile = new File(REC_PATH + fileName);
        if (myCaptureFile.exists()) {
            myCaptureFile.delete();
        }
        try {
            myCaptureFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myCaptureFile;
    }


}
