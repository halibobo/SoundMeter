package me.daei.soundmeter;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import me.daei.soundmeter.widget.SoundDiscView;

public class MainActivity extends AppCompatActivity {

    float volume = 10000;
    private SoundDiscView soundDiscView;
    private MyMediaRecorder mRecorder;
    private static final int msgWhat = 0x1001;
    private static final int refreshTime = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecorder = new MyMediaRecorder();
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (this.hasMessages(msgWhat)) {
                return;
            }
            volume = mRecorder.getMaxAmplitude();  //获取声压值
            if(volume > 0 && volume < 1000000) {
                World.setDbCount(20 * (float)(Math.log10(volume)));  //将声压值转为分贝值
                soundDiscView.refresh();
            }
            handler.sendEmptyMessageDelayed(msgWhat, refreshTime);
        }
    };

    private void startListenAudio() {
        handler.sendEmptyMessageDelayed(msgWhat, refreshTime);
    }

    /**
     * 开始记录
     * @param fFile
     */
    public void startRecord(File fFile){
        try{
            mRecorder.setMyRecAudioFile(fFile);
            if (mRecorder.startRecorder()) {
                startListenAudio();
            }else{
                Toast.makeText(this, "启动录音失败", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            Toast.makeText(this, "录音机已被占用或录音权限被禁止", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        soundDiscView = (SoundDiscView) findViewById(R.id.soundDiscView);
        File file = FileUtil.createFile("temp.amr");
        if (file != null) {
            Log.v("file", "file =" + file.getAbsolutePath());
            startRecord(file);
        } else {
            Toast.makeText(getApplicationContext(), "创建文件失败", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 停止记录
     */
    @Override
    protected void onPause() {
        super.onPause();
        mRecorder.delete(); //停止记录并删除录音文件
        handler.removeMessages(msgWhat);
    }

    @Override
    protected void onDestroy() {
        handler.removeMessages(msgWhat);
        mRecorder.delete();
        super.onDestroy();
    }
}
