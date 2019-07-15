package com.bytedance.clockapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bytedance.clockapplication.widget.Clock;

public class MainActivity extends AppCompatActivity {

    private View mRootView;
    private Clock mClockView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRootView = findViewById(R.id.root);
        mClockView = findViewById(R.id.clock);

        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClockView.setShowAnalog(!mClockView.isShowAnalog());
            }
        });
        new StockhandlerThread("pbfzz").start();
    }

    public class StockhandlerThread extends HandlerThread implements Handler.Callback{
        public static final int MSG_QUERY_STOCK=100;
        private Handler mWorkerHandler;
        public StockhandlerThread(String name){
            super(name);
        }
        public StockhandlerThread(String name,int priority){
            super(name,priority);
        }

        @Override
        protected void onLooperPrepared(){
            mWorkerHandler=new Handler(getLooper(),this);
            mWorkerHandler.sendEmptyMessage(MSG_QUERY_STOCK);
        }

        @Override
        public boolean handleMessage(Message msg){
            switch (msg.what){
                case MSG_QUERY_STOCK:
                    mClockView.postInvalidate();
                    mWorkerHandler.sendEmptyMessageDelayed(MSG_QUERY_STOCK,1000);
                    break;
            }return true;
        }
    }
}
