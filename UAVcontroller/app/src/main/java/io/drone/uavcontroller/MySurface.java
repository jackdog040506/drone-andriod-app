package io.drone.uavcontroller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.Date;

/**
 * Created by james-NB on 2016/12/13.
 */

//自建內部類別必須繼承SurfaceView以及實作SurfaceHolder.Callback
//並實作其抽象Callback方法
public class MySurface extends SurfaceView implements
        SurfaceHolder.Callback,View.OnTouchListener {
    //建立以及宣告Handler來管理以及調派執行緒
    final private Handler handler = new Handler();
    //宣告SurfaceHolder，之後用來取得Canvas物件
    SurfaceHolder holder;

    String streamUrl;
    static Bitmap bitmap;
    boolean asyncStreamIO;
    long asyncStreamDuration;
    long fps;
    boolean flag;
    //宣告畫筆變數以及建立物件
    Paint mPaint = new Paint();

    public MySurface(Context context) {
        super(context);
        //取得SurfaceHolder物件
        this.holder = this.getHolder();
        //addCallback()設定callback函式
        this.holder.addCallback(this);
        this.flag = true;
        this.asyncStreamIO=false;
        this.asyncStreamDuration=40L;
        this.fps=30;
        this.streamUrl="192.168.0.198";
    }
    public boolean isAsyncStreamIO() {
        return asyncStreamIO;
    }

    public void setAsyncStreamIO(boolean asyncStreamIO) {
        this.asyncStreamIO = asyncStreamIO;
    }

    public long getAsyncStreamDuration() {
        return asyncStreamDuration;
    }

    public void setAsyncStreamDuration(long asyncStreamDuration) {
        this.asyncStreamDuration = asyncStreamDuration;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    //繪圖的工作在自建執行緒t中執行
    Thread drawProccess = new Thread(new Runnable() {
        public void run() {
            drawPicture();
            handler.postDelayed(drawProccess, 1000/fps);
        }
    });
    Thread asyncStream = new Thread(new Runnable() {
        public void run() {
            while (true){
                if (asyncStreamIO){
                    try {
                        bitmap=Utility.getBitmapFromURL("http://"+streamUrl+"/html/cam_pic.php?time="+String.valueOf(new Date().getTime())+"&pDelay=40000");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(asyncStreamDuration);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    });

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Utility.disableNetworkPolicy();
        drawProccess.start();
        asyncStream.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        handler.removeCallbacks(drawProccess);
        handler.removeCallbacks(asyncStream);
    }

    protected void drawPicture() {
        //鎖定畫布，並利用SurfaceHolder取得canvas物件
        Canvas c = holder.lockCanvas();

        if (flag) {
//                mPaint.setColor(Color.RED);
            c.drawBitmap(bitmap, 0, 0, null);
            //用Canvas繪圖
            //RectF(原點X軸,原點Y軸,寬,高)
//               c.drawRect(new RectF(0, 100, 1200, 600), mPaint);
//                flag = false;
        } else {
//                mPaint.setColor(Color.BLUE);
//                c.drawRect(new RectF(380, 380, 760, 760), mPaint);
//                flag = true;
        }
        //解鎖畫布
        holder.unlockCanvasAndPost(c);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }
}
