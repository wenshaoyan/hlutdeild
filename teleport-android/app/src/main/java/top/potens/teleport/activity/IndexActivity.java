package top.potens.teleport.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;

import java.net.MulticastSocket;
import java.util.List;

import top.potens.jnet.bootstrap.BossServer;
import top.potens.jnet.listener.FileCallback;
import top.potens.teleport.R;
import top.potens.teleport.adapter.FriendAdapter;
import top.potens.teleport.bean.FriendGroupBean;
import top.potens.teleport.constant.HandlerCode;
import top.potens.teleport.data.FriendData;

/**
 * Created by wenshao on 2018/4/29.
 */
public class IndexActivity extends AppCompatActivity {
    private static final Logger logger = LoggerFactory.getLogger(IndexActivity.class);

    private Context mContext;
    private ExpandableListView elv_user_list;
    private List<FriendGroupBean> mFriends;
    private MulticastSocket multicastSocket;


    private final MyHandler mHandler = new MyHandler(this);

    /**
     * 耗时操作
     */
    private final Runnable sRunnable = new Runnable() {
        @Override
        public void run() {
            mFriends = FriendData.getFriendGroupData();
            mHandler.sendEmptyMessageDelayed(HandlerCode.LOCAL_QUERY_SUC, 10);

        }
    };

    /**
     * 更新ui
     * 声明一个静态的Handler内部类，并持有外部类的弱引用
     */
    private static class MyHandler extends Handler {

        private final WeakReference<IndexActivity> mActivity;

        private MyHandler(IndexActivity activity) {
            this.mActivity = new WeakReference<IndexActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            IndexActivity activity = mActivity.get();
            if (activity != null) {
                if (msg.what == HandlerCode.LOCAL_QUERY_SUC) {
                    FriendAdapter friendAdapter = new FriendAdapter(activity.getApplication(), activity.mFriends);
                    activity.elv_user_list.setAdapter(friendAdapter);

                }

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        mContext = getApplicationContext();
        elv_user_list = findViewById(R.id.elv_user_list);
        mHandler.post(sRunnable);
//        final BossClient bossClient = new BossClient();
        boot();

    }
    private void boot() {
        final BossServer bossServer = new BossServer();
        bossServer.fileUpSaveDir("d:\\tmp");
        logger.error("===================");
        bossServer.receiveFile(new FileCallback() {
            @Override
            public void start(int id, String path, long size) {

                System.out.println("r:start:id" + id + ",path:" + path);
            }
            @Override
            public void process(int id, long size, long process) {
                System.out.println("r:process:id:" + id + ",size:" + size + ",process:" + process);
            }

            @Override
            public void end(int id, long size) {
                System.out.println("r:end:id:" + id + ",size:" + size);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                bossServer.listenerPort(31416).start();
            }
        }).start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
