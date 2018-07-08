package top.potens.teleport.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import java.util.Timer;
import java.util.TimerTask;

import top.potens.teleport.R;


/**
 * Created by wenshao on 2017/4/26.
 * 自定义语音播放类
 */

public class VoicePlayingView extends View {
    private final static String TAG = "VoicePlayingView";
    private Paint paint;
    private Paint textPaint;
    private boolean isPlay = false;
    private Timer timer;
    private int playTime = 3;
    private int playDuration=0;


    // 自定义属性


    /**
     * 圆弧的颜色
     */
    private int arcColor;

    /**
     * 圆弧的宽度
     */
    private int arcStrokeWidth;

    /**
     * 起始角度
     */
    private int startAngle;

    /**
     * 总的弧度
     */
    private int arc;



    /**
     * 语音时长
     */
    private int duration;

    /**
     * 对齐方式
     */
    private int direction;

    /**
     * 显示语音长度的字体颜色
     */
    private int textColor;

    /**
     * 显示语音长度的字体大小
     */
    private float textSize;

    public final static int DIRECTION_LEFT = 1;
    public final static int DIRECTION_RIGHT = 2;
    public final static int DIRECTION_ABOVE = 3;
    public final static int DIRECTION_BELOW = 4;
    private Rect backRect;
    private String audioUrl;
    private static MediaPlayer runningMediaPlayer;


    public VoicePlayingView(Context context) {
        this(context, null);

    }

    public VoicePlayingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoicePlayingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();

        textPaint = new Paint();

        Drawable background = getBackground();

        backRect = new Rect();
        if (background!=null){

            background.getPadding(backRect);
        }
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.VoicePlayingView);

        arcColor = mTypedArray.getColor(R.styleable.VoicePlayingView_arcColor, Color.RED);
        arcStrokeWidth = mTypedArray.getInteger(R.styleable.VoicePlayingView_arcStrokeWidth, 3);

        direction = mTypedArray.getInt(R.styleable.VoicePlayingView_direction, DIRECTION_LEFT);
        duration = mTypedArray.getInt(R.styleable.VoicePlayingView_duration, 1);
        //文字的颜色
        textColor = mTypedArray.getColor(R.styleable.VoicePlayingView_textColor, Color.BLACK);
        //文字的大小
        textSize =  mTypedArray.getDimensionPixelSize(R.styleable.VoicePlayingView_textSize, 15);


        Log.i(TAG, "textSize: "+textSize);

        if (direction == DIRECTION_LEFT) {
            startAngle = -30;
            arc = 60;
        } else {
            startAngle = -210;
            arc = 60;
        }

        mTypedArray.recycle();

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlay()){
                    startPlay();

                }
            }
        });
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(arcColor);   // 设置画笔颜色
        paint.setStyle(Paint.Style.STROKE);   //设置空心
        paint.setStrokeWidth(arcStrokeWidth);
        paint.setAntiAlias(true);
        int height = getHeight();
        int width = getWidth();
        Log.i(TAG, "onDraw: "+height);
        int space = height / 5;
        float arc1 = space;
        float arc2 = space * 2;
        float arc3 = space * 3;





        if (!isPlay || playTime % 3 >= 0) {
            float xLeft;
            float yTop;
            float xRight;
            float yBottom;

            if (direction == DIRECTION_LEFT) {
                xLeft =(arc3 / 2 - arc1 / 2);
                yTop = (height - arc1) / 2;
                xRight = xLeft + arc1;
                yBottom = yTop + arc1;
            } else {
                xLeft = width - arc1 - (arc3 / 2 - arc1 / 2);
                yTop = (height - arc1) / 2;
                xRight = xLeft + arc1;
                yBottom = yTop + arc1;
            }

            RectF oval = new RectF(xLeft, yTop, xRight, yBottom);
            canvas.drawArc(oval, startAngle, arc, false, paint);
            //canvas.drawRect(oval, paint);

        }

        if (!isPlay || playTime % 3 >= 1) {

            float xLeft;
            float yTop;
            float xRight;
            float yBottom;

            if (direction == DIRECTION_LEFT) {
                xLeft =  (arc3 / 2 - arc2 / 2);
                yTop = (height - arc2) / 2;
                xRight = xLeft + arc2;
                yBottom = yTop + arc2;
            } else {
                xLeft = width  - arc2 - (arc3 / 2 - arc2 / 2);
                yTop = (height - arc2) / 2;
                xRight = xLeft + arc2;
                yBottom = yTop + arc2;
            }

            RectF oval = new RectF(xLeft, yTop, xRight, yBottom);
            canvas.drawArc(oval, startAngle, arc, false, paint);
             //canvas.drawRect(oval, paint);

        }

        if (!isPlay || playTime % 3 >= 2) {
            float xLeft;
            float yTop;
            float xRight;
            float yBottom;

            if (direction == DIRECTION_LEFT) {
                xLeft = 0;
                yTop = (height - arc3) / 2;
                xRight = xLeft + arc3;
                yBottom = yTop + arc3;
            } else {
                xLeft = width  - arc3;
                yTop = (height - arc3) / 2;
                xRight = xLeft + arc3;
                yBottom = yTop + arc3;
                //Log.i(TAG, "onDraw:  "+xLeft+" "+xRight+" width="+width+"  arc3="+arc3);
            }

            RectF oval = new RectF(xLeft, yTop, xRight, yBottom);
            canvas.drawArc(oval, startAngle, arc, false, paint);
            //canvas.drawRect(oval, paint);
        }

        RectF rect;//画一个矩形

        if (direction == DIRECTION_LEFT) {
            float v = arc3 ;
            rect = new RectF(v,0,v+100,getHeight());
        }else{
            float v = width-arc3-100;
            rect = new RectF(v,0,width-arc3,getHeight());
            //Log.i(TAG, "onDraw:========= "+rect.toString()+" width="+width+"  arc3="+arc3);

        }

        //canvas.drawRect(rect, paint);

        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setStyle(Paint.Style.FILL);
        //该方法即为设置基线上那个点究竟是left,center,还是right  这里我设置为center
        textPaint.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom

        int baseLineY = (int) (rect.centerY() - top/2 - bottom/2);//基线中间点的y轴计算公式

        int time;
        if (isPlay()){
            time = playDuration/2;
        }else{
            time=duration;
        }
        canvas.drawText(time+"''",rect.centerX(),baseLineY,textPaint);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int defaultWidth=50;
        int defaultHeight=50;
        // 声明一个临时变量来存储计算出的测量值
        int resultWidth = 0;

        // 获取宽度测量规格中的mode
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);

        // 获取宽度测量规格中的size
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        /*
         * 如果爹心里有数
         */
        if (modeWidth == MeasureSpec.EXACTLY) {
            // 那么儿子也不要让爹难做就取爹给的大小吧
            //resultWidth = defaultWidth + getPaddingLeft() + getPaddingRight();
            resultWidth = defaultWidth + backRect.left+backRect.right;
        }
        /*
         * 如果爹心里没数
         */
        else {
            // 那么儿子可要自己看看自己需要多大了
            //resultWidth = defaultWidth + getPaddingLeft() + getPaddingRight();
            resultWidth = defaultWidth + backRect.left+backRect.right;;

            /*
             * 如果爹给儿子的是一个限制值
             */
            if (modeWidth == MeasureSpec.AT_MOST) {
                // 那么儿子自己的需求就要跟爹的限制比比看谁小要谁
                resultWidth = Math.min(resultWidth, sizeWidth);
            }
        }

        int resultHeight = 0;
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (modeHeight == MeasureSpec.EXACTLY) {
            resultHeight = sizeHeight;
        } else {
            //resultHeight = defaultHeight + getPaddingTop() + getPaddingBottom();
            resultHeight = defaultHeight + backRect.top+backRect.bottom;;
            if (modeHeight == MeasureSpec.AT_MOST) {
                //resultHeight = defaultHeight + getPaddingTop() + getPaddingBottom();
                resultHeight = defaultHeight + backRect.top+backRect.bottom;;
            }
        }
        //Log.i(TAG, "onMeasure: "+resultWidth+"   "+resultHeight+backRect.toString());

        resultWidth = getCustomWidth(resultWidth, sizeWidth);

        // 设置测量尺寸
        setMeasuredDimension(resultWidth, resultHeight);
    }


    public int getCustomWidth(int minWidth,int maxWidth) {
        minWidth=minWidth+50;
        if (duration>120){
            return maxWidth;
        } else if (duration<=0) {
            return minWidth;
        } else{
            int i = (maxWidth-minWidth) / 120;
            return minWidth+i*duration;
        }

    }

    public void startPlay() {

        if (audioUrl!=null){
            try {
                MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), Uri.parse(audioUrl));
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopPlay(mp);
                    }
                });
                isPlay = true;
                playTime = -1;
                playDuration=1;
                startTimer();
                mediaPlayer.start();
            }catch (Exception e){
                e.printStackTrace();

            }

        }

    }

    public void stopPlay(MediaPlayer mp) {
        if (mp != null ) {
            isPlay = false;
            stopTimer();
            postInvalidate();
            mp.stop();
            mp.release();
        }

    }

    private synchronized void refresh() {
        playTime++;
        playDuration++;
        postInvalidate();
        Log.i(TAG, "refresh: ");

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
                refresh();
            }
        }, 0, 500);
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

    public boolean isPlay() {
        return isPlay;
    }


    public void setMediaPlay(String url) {
        this.audioUrl=url;
    }


    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }



    public int getArc() {
        return arc;
    }

    public void setArc(int arc) {
        this.arc = arc;
    }

    public int getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(int startAngle) {
        this.startAngle = startAngle;
    }

    public int getArcStrokeWidth() {
        return arcStrokeWidth;
    }

    public void setArcStrokeWidth(int arcStrokeWidth) {
        this.arcStrokeWidth = arcStrokeWidth;
    }

    public int getArcColor() {
        return arcColor;
    }

    public void setArcColor(int arcColor) {
        this.arcColor = arcColor;
    }
}
