package io.drone.uavcontroller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.webkit.WebView;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
//    private SurfaceView sufaceView;
   // private String url=
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        sufaceView = (SurfaceView) findViewById(R.id.surfaceView);
        setContentView(new MySurface(this));
        //顯示網路圖片的ImageView與進行讀取網路圖片的Button


    }

    //自建內部類別必須繼承SurfaceView以及實作SurfaceHolder.Callback
    //並實作其抽象Callback方法
    public class MySurface extends SurfaceView implements
            SurfaceHolder.Callback {
        //建立以及宣告Handler來管理以及調派執行緒
        final private Handler handler = new Handler();
        //宣告SurfaceHolder，之後用來取得Canvas物件
        SurfaceHolder holder;
        boolean flag;
        //宣告畫筆變數以及建立物件
        Paint mPaint = new Paint();
        //建構式
        public MySurface(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
            //取得SurfaceHolder物件
            holder = this.getHolder();
            //addCallback()設定callback函式
            holder.addCallback(this);
            flag = true;
        }

        //繪圖的工作在自建執行緒t中執行
        Thread t = new Thread(new Runnable() {

            public void run() {
                // TODO Auto-generated method stub
                drawPicture();
            }

        });
        //Callback
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            // TODO Auto-generated method stub
        }
        //Callback
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8)
            {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                //your codes here

            }
            if (android.os.Build.VERSION.SDK_INT > 9)
            {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            //啟動執行緒
            t.start();
        }
        //Callback
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            handler.removeCallbacks(t);
        }

        protected void drawPicture() {
            // TODO Auto-generated method stub
            long l=new Date().getTime();
             Bitmap bitmap=getBitmapFromURL("http://192.168.0.198/html/cam_pic.php?time="+String.valueOf(l)+"&pDelay=40000");
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
            //執行緒t暫停1秒後再跑一次
            handler.postDelayed(t, 50);

        }

    }

    //讀取網路圖片，型態為Bitmap
    private Bitmap getBitmapFromURL(String imageUrl){
        try
        {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
