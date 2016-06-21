package me.daei.soundmeter;

import java.io.File;
import java.io.IOException;
import android.media.MediaRecorder;

public class MyMediaRecorder {
	
	public  File myRecAudioFile ;
	private MediaRecorder mMediaRecorder ;
	public boolean isRecording = false ;
	
	 public float getMaxAmplitude() {
		 if (mMediaRecorder != null) {
			 try {
				 return mMediaRecorder.getMaxAmplitude();
			 } catch (IllegalArgumentException e) {
				 e.printStackTrace();
				 return 0;
			 }
		 } else {
			 return 5;
		 }
	 }

	public File getMyRecAudioFile() {
		return myRecAudioFile;
	}

	public void setMyRecAudioFile(File myRecAudioFile) {
		this.myRecAudioFile = myRecAudioFile;
	}

	/**
	 * 录音
	 * @return 是否成功开始录音
     */
	public boolean startRecorder(){
		if (myRecAudioFile == null) {
			return false;
		}
        try {
			mMediaRecorder = new MediaRecorder();

			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mMediaRecorder.setOutputFile(myRecAudioFile.getAbsolutePath());

			mMediaRecorder.prepare();
			mMediaRecorder.start();
			isRecording = true;
			return true;
        } catch(IOException exception) {
        	mMediaRecorder.reset();
        	mMediaRecorder.release();
        	mMediaRecorder = null;
        	isRecording = false ;
			exception.printStackTrace();
        }catch(IllegalStateException e){
        	stopRecording();
			e.printStackTrace();
			isRecording = false ;
        }
		return false;
	}




	 public void stopRecording() {
	      if (mMediaRecorder != null){
			if(isRecording){
				try{
					mMediaRecorder.stop();
					mMediaRecorder.release();
				}catch(Exception e){
					e.printStackTrace();
				}
		   }
	        mMediaRecorder = null;
	        isRecording = false ;
	     }
	 }




	  public void delete() {
	        stopRecording();
			if (myRecAudioFile != null) {
				myRecAudioFile.delete();
				myRecAudioFile = null;
			}
	   }
}
