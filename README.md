# SoundMeter(分贝仪)

### [我的个人主页](http://www.dahei.me)
## 这是什么？
### [详细介绍](http://dahei.me/2016/05/26/android%E5%A3%B0%E9%9F%B3%E6%A3%80%E6%B5%8B%E4%BB%AA---%E5%88%86%E8%B4%9D%E4%BB%AA/)
### 如果喜欢，请给我一个star
android端的声音检测程序，实时获取当前周围环境的声压级，也就是平常所说的分贝值

![enter image description here](https://raw.githubusercontent.com/halibobo/BlogImage/master/blog/sound_meter/sound.gif)


### 源码
声音采集利用系统的MediaRecorder

<!-- more -->

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
      /**
      * 获取声压值
      */
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




自定义显示分贝值得大圆盘View  取名SoundDiscView

    private float scaleWidth, scaleHeight;
    private int newWidth, newHeight;
    private Matrix mMatrix = new Matrix();
    private Bitmap indicatorBitmap;
    private Paint paint = new Paint();
    static final long  ANIMATION_INTERVAL = 100;


	private void init() {
        Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.noise_index);
        int bitmapWidth = myBitmap.getWidth();
        int bitmapHeight = myBitmap.getHeight();
        newWidth = getWidth();
        newHeight = getHeight();
        scaleWidth = ((float) newWidth) /(float) bitmapWidth;  // 获取缩放比例
        scaleHeight = ((float) newHeight) /(float) bitmapHeight;  //获取缩放比例
        mMatrix.postScale(scaleWidth, scaleHeight);   //设置mMatrix的缩放比例
        indicatorBitmap = Bitmap.createBitmap(myBitmap, 0, 0, bitmapWidth, bitmapHeight, mMatrix,true);  //获取同等和背景宽高的指针图的bitmap

        paint = new Paint();
        paint.setTextSize(55);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);  //抗锯齿
        paint.setColor(Color.WHITE);
    }
    
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        currentAngle = getAngle(World.dbCount); //获取指针应该显示的角度
        mMatrix.setRotate(getAngle(World.dbCount), newWidth / 2, newHeight * 215 / 460);   //片相对位置
        canvas.drawBitmap(indicatorBitmap, mMatrix, paint);
        postInvalidateDelayed(ANIMATION_INTERVAL);
        canvas.drawText((int)World.dbCount+" DB", newWidth/2,newHeight*36/46, paint); //图片相对位置
    }




### 启动、暂停与关闭
控制的所有操作全部在MainActivity里进行的，当启动或者重新进入activity时启动分贝仪，启动代码如下：

     @Override
    protected void onResume() {
        super.onResume();
        bListener = true;
        File file = FileUtil.createFile("temp.amr"); //创建录音文件
        if (file != null) {
            Log.v("file", "file =" + file.getAbsolutePath());
            startRecord(file);
        } else {
            Toast.makeText(getApplicationContext(), "创建文件失败", Toast.LENGTH_LONG).show();
        }
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
            Toast.makeText(this, "录音机已被占用", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }



    private void startListenAudio() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isThreadRun) {
                    try {
                        if(bListener) {
                            volume = mRecorder.getMaxAmplitude();  //获取声压值
                            if(volume > 0 && volume < 1000000) {
                                World.setDbCount(20 * (float)(Math.log10(volume)));  //将声压值转为分贝值
                                soundDiscView.refresh(); //子线程刷新view，调用了postInvalidate
                            }
                        }
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        bListener = false;
                    }
                }
            }
        });
        thread.start();
    }
startListenAudio的执行过程就是开启一个线程，每隔一段时间获取分贝值然后刷新大圆盘，这里要注意的是在子线程操作刷新view的时候要用postInvalidate，查看postInvalidate的源码发现其原理很简单，它就是v用了一个handler机制，最终刷新肯定是在主线程的。


当activity被onStop时我们要停止与关闭录音采集，然后删除采集的文件，操作如下：

    
    /**
     * 停止记录
     */
    @Override
    protected void onStop() {
        super.onStop();
        bListener = false;
        mRecorder.delete(); //停止记录并删除录音文件
    }
    
     public void delete() {
	        stopRecording();
			if (myRecAudioFile != null) {
				myRecAudioFile.delete();
				myRecAudioFile = null;
			}
	   }

### 执行
整个效果还行，但是运行发现指针滑动的太突兀，做个缓慢过度

    public static float dbCount = 40;

	private static float lastDbCount = dbCount;
	private static float min = 0.5f;
	private static float value = 0;
	public static void setDbCount(float dbValue) {
		if (dbValue > lastDbCount) {
			value = dbValue - lastDbCount > min ? dbValue - lastDbCount : min;
		}else{
			value = dbValue - lastDbCount < -min ? dbValue - lastDbCount : -min;
		}
		dbCount = lastDbCount + value * 0.2f ;
		lastDbCount = dbCount;
	}
	
	
再次执行，perfect !