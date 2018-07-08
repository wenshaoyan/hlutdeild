package top.potens.teleport.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import top.potens.teleport.R;
import top.potens.teleport.bean.MessageBean;
import top.potens.teleport.image.GlideApp;
import top.potens.teleport.view.VoicePlayingView;


/**
 * Created by wenshao on 2017/4/10.
 * 聊天室聊天记录适配器
 */

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.BaseAdapter> {
    private List<MessageBean> dataList = new ArrayList<>();
    private Drawable soundDrawable;
    private OnMessageItemClickListener mOnMessageItemClickListener;
    private Context mContext;
    private final static String fileRegex = "^file://.*";


    public ChatMessageAdapter(Context context) {
        this.mContext=context;
        soundDrawable = ContextCompat.getDrawable(context, R.mipmap.ic_action_sound);
        soundDrawable.setBounds(0,0,soundDrawable.getMinimumWidth(),soundDrawable.getMinimumHeight());


    }

    public void replaceAll(List<MessageBean> list) {
        dataList.clear();
        if (list != null && list.size() > 0) {
            dataList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void addAll(List<MessageBean> list) {
        if (dataList != null && list != null) {
            dataList.addAll(list);
            //notifyItemRangeChanged(dataList.size(), list.size());
        }
        notifyDataSetChanged();
    }

    public void add(MessageBean messageBean) {
        if (messageBean != null) {
            dataList.add(messageBean);
            //notifyItemRangeChanged(dataList.size(), 1);
        }
        notifyDataSetChanged();
    }

    public List<MessageBean> getDataList() {
        return dataList;
    }

    @Override
    public BaseAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case MessageBean.LOCATION_LEFT:
                return new ChatLeftViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_left, parent, false),mOnMessageItemClickListener);
            case MessageBean.LOCATION_RIGHT:
                return new ChatRightViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_right, parent, false),mOnMessageItemClickListener);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(BaseAdapter holder, int position) {
        holder.setData(dataList.get(position));

    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).getLocation();
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    public class BaseAdapter extends RecyclerView.ViewHolder {

        public BaseAdapter(View itemView) {
            super(itemView);
        }

        void setData(MessageBean message) {

        }


    }

    private class ChatLeftViewHolder extends BaseAdapter implements View.OnClickListener {
        private ImageView rw_head;
        private TextView tv_content;
        private ImageView rw_content;
        private VoicePlayingView vpv_audio;

        private OnMessageItemClickListener mListener;


        public ChatLeftViewHolder(View view, OnMessageItemClickListener listener) {
            super(view);
            rw_head = view.findViewById(R.id.rw_head);
            tv_content = view.findViewById(R.id.tv_content);
            rw_content =  view.findViewById(R.id.rw_content);
            vpv_audio = view.findViewById(R.id.vpv_audio);
            mListener=listener;
            view.setOnClickListener(this);

        }

        @Override
        void setData(MessageBean messageBean) {
            super.setData(messageBean);
            int head = messageBean.getFriendUserBean().getHead();
            GlideApp.with(mContext)
                    .load(head)
                    .into(rw_head);
            /*if (messageBean.getType().equals(MessageBean.TYPE_TEXT)) {
                SpannableStringBuilder sb = FaceHelper.imageToGif(tv_content, messageBean.getContent());

                rw_content.setVisibility(View.GONE);
                vpv_audio.setVisibility(View.GONE);
                tv_content.setVisibility(View.VISIBLE);
                tv_content.setText(sb);
            } else if (messageBean.getType().equals(MessageBean.TYPE_IMAGE)) {
                tv_content.setVisibility(View.GONE);
                vpv_audio.setVisibility(View.GONE);

                rw_content.setVisibility(View.VISIBLE);

                mImageLoader.displayImage(messageBean.getContent(), rw_content, mOptions);
            } else if (messageBean.getType().equals(MessageBean.TYPE_AUDIO)) {
                tv_content.setVisibility(View.GONE);
                rw_content.setVisibility(View.GONE);
                vpv_audio.setVisibility(View.VISIBLE);
                vpv_audio.setDuration(messageBean.getDuration());
                String proxyUrl = mProxy.getProxyUrl(messageBean.getContent());

                vpv_audio.setMediaPlay(proxyUrl);
            }*/

        }

        @Override
        public void onClick(View v) {
            if (mListener!=null){
                mListener.onMessageItemClick(v,getAdapterPosition());
            }
        }
    }

    private class ChatRightViewHolder extends BaseAdapter implements View.OnClickListener {
        private ImageView rw_head;
        private TextView tv_content;
        private ImageView rw_content;
        private VoicePlayingView vpv_audio;
        private OnMessageItemClickListener mListener;

        public ChatRightViewHolder(View view, OnMessageItemClickListener listener) {
            super(view);
            RelativeLayout ry_message_main =  view.findViewById(R.id.ry_message_main);


            rw_head =  view.findViewById(R.id.rw_head);

            tv_content =  view.findViewById(R.id.tv_content);
            rw_content =  view.findViewById(R.id.rw_content);


            vpv_audio =  view.findViewById(R.id.vpv_audio);

            mListener=listener;
            view.setOnClickListener(this);

        }

        @Override
        void setData(MessageBean messageBean) {
            super.setData(messageBean);
            int head = messageBean.getFriendUserBean().getHead();

            GlideApp.with(mContext)
                    .load(head)
                    .into(rw_head);
            /*if (messageBean.getType().equals(MessageBean.TYPE_TEXT)) {
                SpannableStringBuilder sb = FaceHelper.imageToGif(tv_content, messageBean.getContent());

                rw_content.setVisibility(View.GONE);
                vpv_audio.setVisibility(View.GONE);
                tv_content.setVisibility(View.VISIBLE);
                tv_content.setText(sb);
            } else if (messageBean.getType().equals(MessageBean.TYPE_IMAGE)) {
                tv_content.setVisibility(View.GONE);
                vpv_audio.setVisibility(View.GONE);
                rw_content.setVisibility(View.VISIBLE);
                mImageLoader.displayImage(messageBean.getContent(), rw_content, mOptions);

            } else if (messageBean.getType().equals(MessageBean.TYPE_AUDIO)){
                tv_content.setVisibility(View.GONE);
                rw_content.setVisibility(View.GONE);
                vpv_audio.setVisibility(View.VISIBLE);
                vpv_audio.setDuration(messageBean.getDuration());
                String proxyUrl;
                if (messageBean.getContent().matches(fileRegex)){
                    proxyUrl=messageBean.getContent();
                }else{
                    proxyUrl=mProxy.getProxyUrl(messageBean.getContent());
                }


                vpv_audio.setMediaPlay(proxyUrl);
            }*/

        }

        @Override
        public void onClick(View v) {
            if (mListener!=null){
                mListener.onMessageItemClick(v,getAdapterPosition());
            }
        }
    }

    public void setOnItemClickListener(OnMessageItemClickListener listener){
        this.mOnMessageItemClickListener=listener;
    }
    public interface OnMessageItemClickListener{
        public void onMessageItemClick(View view, int position);
    }

}
