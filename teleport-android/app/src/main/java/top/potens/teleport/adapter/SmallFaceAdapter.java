package top.potens.teleport.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.IOException;
import java.util.List;

import top.potens.teleport.R;

/**
 * Created by wenshao on 2017/4/15.
 * qq默认小表情适配器
 */

public class SmallFaceAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mFaceList;
    private String facePath = "face/png/";

    public SmallFaceAdapter(Context context, List<String> list) {
        this.mContext = context;
        this.mFaceList = list;

    }

    @Override
    public int getCount() {
        return mFaceList.size();
    }

    @Override
    public String getItem(int position) {
        return mFaceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SmallFaceAdapter.ViewHolder holder;
        if (convertView == null) {
            holder = new SmallFaceAdapter.ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.face_vertical, null);
            holder.iv = (ImageView) convertView.findViewById(R.id.face_img);
            holder.tv = (TextView) convertView.findViewById(R.id.face_text);
            convertView.setTag(holder);
        } else {
            holder = (SmallFaceAdapter.ViewHolder) convertView.getTag();
        }
        try {
            Bitmap mBitmap = BitmapFactory.decodeStream(mContext.getAssets().open(facePath + mFaceList.get(position)));
            holder.iv.setImageBitmap(mBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.tv.setText(String.valueOf(facePath + mFaceList.get(position)));

        return convertView;
    }
    private class ViewHolder {
        ImageView iv;
        TextView tv;
    }

    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
