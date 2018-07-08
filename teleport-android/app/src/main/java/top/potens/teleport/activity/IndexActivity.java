package top.potens.teleport.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;

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
import top.potens.teleport.constant.HeadMapping;
import top.potens.teleport.data.FriendData;
import top.potens.teleport.util.XBossUtil;
import top.potens.teleport.util.XGlobalDataUtil;

/**
 * Created by wenshao on 2018/4/29.
 */
public class IndexActivity extends AppCompatActivity {
    private static final Logger logger = LoggerFactory.getLogger(IndexActivity.class);

    private Context mContext;
    private ExpandableListView elv_user_list;
    private List<FriendGroupBean> mFriends;
    private FriendAdapter mFriendAdapter;


    private final MyHandler mHandler = new MyHandler(this);

    /**
     * 耗时操作
     */
    private final Runnable sRunnable = new Runnable() {
        @Override
        public void run() {

            RPCHeader rpcHeader = new RPCHeader("getClients", new HashMap<String, String>());
            XBossUtil.sendRPC(rpcHeader, new RPCCallback<List<Client>>() {
                @Override
                public void succeed(List<Client> clients) {
                    XGlobalDataUtil.cleanFriendUserBean();
                    for (Client client : clients) {
                        FriendUserBean friendUserBean = new FriendUserBean(client.getChannelId(), client.getShowName(), HeadMapping.getHead(client.getImage()));
                        XGlobalDataUtil.addFriendUserBean(friendUserBean);
                    }
                    XGlobalDataUtil.addFriendUserBeansListener(mHandler);
                    mFriends = FriendData.getFixationFriendGroupData(XGlobalDataUtil.getFriendUserBeans());
                    mHandler.sendEmptyMessage(HandlerCode.RPC_QUERY_SUC);
                }

                @Override
                public void error(String s) {
                    logger.error("rpc error "+s);
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
                if (msg.what == HandlerCode.RPC_QUERY_SUC) {
                    activity.mFriendAdapter = new FriendAdapter(activity.getApplication(), activity.mFriends);
                    activity.elv_user_list.setAdapter(activity.mFriendAdapter);
                    activity.setClick();
                    logger.info(activity.mFriends.toString());
                } else if (msg.what == HandlerCode.DATA_CHANGE && activity.elv_user_list.getAdapter() !=null) {
                    activity.mFriendAdapter.notifyDataSetChanged();
                }

            }
        }
    }
    private void setClick() {
        elv_user_list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(IndexActivity.this.mContext, ChatWindowActivity.class);
                FriendUserBean friendUserBean = mFriends.get(groupPosition).getFriendUserBeans().get(childPosition);
                Bundle bundle=new Bundle();
                bundle.putSerializable("userInfo",friendUserBean);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            }
        });
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
