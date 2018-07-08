package top.potens.teleport.util;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;


import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import top.potens.teleport.view.CirclePlayProgress;

/**
 * Created by wenshao on 2017/4/21.
 * 录音播放类
 */

public class AudioRecordUtil {
    private static final String TAG = "AudioRecordUtil";
    private MediaRecorder mRecorder;
    private String filePath;
    private MediaPlayer mPlayer;
    private int status;
    private Timer timer;
    private TextView showTimer;
    private OnPlayEventListener playEventListener;
    private CirclePlayProgress progress;
    private int mDuration=0;

    public final static int STATUS_RECORD_FREE = 0;   // 录音空闲状态
    public final static int STATUS_RECORDING = 1;  // 录音中
    public final static int STATUS_PLAY_FREE = 2;  // 播放空闲
    public final static int STATUS_PLAYING = 3;  // 播放中


    private int playerSecondsElapsed = 0;
    private int recorderSecondsElapsed = 0;
    private int mCurrentProgress=0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            updateTimer();
        }
    };


    public AudioRecordUtil(String filePath, TextView textView, CirclePlayProgress progress) {
        this.filePath = filePath;
        status = STATUS_RECORD_FREE;
        this.showTimer = textView;
        this.progress = progress;
    }

    public AudioRecordUtil(String filePath){
        this.filePath = filePath;
    }

    public void reset() {
        if (status == STATUS_RECORDING) {
            endRecord();
        } else if (status == STATUS_PLAYING) {
            endPlay();
        }
        status = STATUS_RECORD_FREE;
        stopTimer();
        playerSecondsElapsed = 0;
        recorderSecondsElapsed = 0;
        mCurrentProgress = 0;
    }


    public void startRecord() {

        reset();
        mDuration=0;
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mRecorder.setOutputFile(filePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mRecorder.start();
        progress.setProgress(0);
        status = STATUS_RECORDING;
        showTimer.setText("00:00");
        startTimer();

    }

    public void endRecord() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            progress.setMax(recorderSecondsElapsed*10);
        }
        status = STATUS_PLAY_FREE;
        stopTimer();


    }

    public void startPlay() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(filePath);
            mPlayer.prepare();
            mPlayer.start();
            status = STATUS_PLAYING;
            showTimer.setText("00:00");
            startTimer();
            execProgressAnim();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    public void endPlay() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        playerSecondsElapsed = 0;
        status = STATUS_PLAY_FREE;
        stopTimer();
    }

    public int getStatus() {
        return status;
    }

    /**
     * 开始计时
     */
    private void startTimer() {
        stopTimer();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);

            }
        }, 0, 1000);
    }

    /**
     * 停止计算
     */
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    /**
     * 更新计时器的显示
     */
    private void updateTimer() {
        if (status == STATUS_RECORDING) {
            recorderSecondsElapsed++;
            mDuration++;
            showTimer.setText(formatSeconds(recorderSecondsElapsed));
        } else if (status == STATUS_PLAYING) {
            playerSecondsElapsed++;
            if (playerSecondsElapsed > recorderSecondsElapsed) {
                endPlay();
                playEventListener.onPlayAutoEnd();
            } else {
                showTimer.setText(formatSeconds(playerSecondsElapsed));
            }


        }
    }
    private void execProgressAnim() {
        new Thread(new ProgressRunnable()).start();
    }
    class ProgressRunnable implements Runnable {

        @Override
        public void run() {

            while (mCurrentProgress < recorderSecondsElapsed*10 && status==STATUS_PLAYING) {
                mCurrentProgress += 1;


                progress.setProgress(mCurrentProgress);


                try {
                    Thread.sleep(100);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            progress.setProgress(0);   // 复位初始播放状态
            mCurrentProgress=0;

        }

    }

    // 把秒转为00:00格式
    private static String formatSeconds(int seconds) {
        return getTwoDecimalsValue(seconds / 60) + ":"
                + getTwoDecimalsValue(seconds % 60);
    }

    private static String getTwoDecimalsValue(int value) {
        if (value >= 0 && value <= 9) {
            return "0" + value;
        } else {
            return value + "";
        }
    }

    /**
     * 获取音频的时长
     * @return  时长 单位为s
     */
    public int getDuration(){
        return mDuration;
    }

    public void setOnPlayPressListener(OnPlayEventListener listener) {
        this.playEventListener = listener;
    }


    public interface OnPlayEventListener {
        void onPlayAutoEnd();
    }

}
