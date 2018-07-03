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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import top.potens.jnet.bean.Client;
import top.potens.jnet.bean.RPCHeader;
import top.potens.jnet.listener.RPCCallback;
import top.potens.teleport.R;
import top.potens.teleport.adapter.FriendAdapter;
import top.potens.teleport.bean.FriendGroupBean;
import top.potens.teleport.bean.FriendUserBean;
import top.potens.teleport.constant.HandlerCode;
import top.potens.teleport.data.FriendData;
import top.potens.teleport.util.DeviceUtil;
import top.potens.teleport.util.XBossUtil;

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

            RPCHeader rpcHeader = new RPCHeader("getClients", new HashMap<String, String>());
            XBossUtil.sendRPC(rpcHeader, new RPCCallback<String>() {
                @Override
                public void succeed(String clients) {
                    List<FriendUserBean> intranetList = new ArrayList<>();
                    logger.debug(clients);
                    logger.debug("===============");
//                    for (Client client : clients) {
//                        new FriendUserBean(client.getChannelId(), client.getShowName(),R.mipmap.head3);
//                    }
                    mFriends = FriendData.getFixationFriendGroupData(intranetList);
                    mHandler.sendEmptyMessage(HandlerCode.LOCAL_QUERY_SUC);
                    logger.info("rpc suc");
                }

                @Override
                public void error(String s) {
                    logger.error("lalalallalalalal"+s);
                }
            });

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
    }

}
