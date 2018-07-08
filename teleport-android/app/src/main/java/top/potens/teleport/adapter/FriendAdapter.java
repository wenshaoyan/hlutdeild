package top.potens.teleport.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import top.potens.teleport.R;
import top.potens.teleport.bean.FriendGroupBean;
import top.potens.teleport.bean.FriendUserBean;
import top.potens.teleport.image.GlideApp;

/**
 * Created by wenshao on 2018/4/30.
 */
public class FriendAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private List<FriendGroupBean> mFriends;


    public FriendAdapter(Context context, List<FriendGroupBean> list) {
        mContext = context;
        mFriends = list;

    }



    @Override
    public int getGroupCount() {
        return mFriends.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mFriends.get(groupPosition).getFriendUserBeans().size();
    }

    @Override
    public FriendGroupBean getGroup(int groupPosition) {
        return mFriends.get(groupPosition);
    }

    @Override
    public FriendUserBean getChild(int groupPosition, int childPosition) {
        return mFriends.get(groupPosition).getFriendUserBeans().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.user_group_item_view, null);
        }
        TextView tv_group_name =  convertView.findViewById(R.id.tv_group_name);
        TextView tv_group_number =  convertView.findViewById(R.id.tv_group_number);
        ImageView iv_triangle =  convertView.findViewById(R.id.iv_triangle);
        tv_group_name.setText(getGroup(groupPosition).getName());
        tv_group_number.setText(String.valueOf(getChildrenCount(groupPosition)));

        if (!isExpanded) {
            iv_triangle.setImageResource(R.mipmap.ic_right_triangle);
        } else {
            iv_triangle.setImageResource(R.mipmap.ic_down_triangle);
        }


        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.contact_item_view, null);
        }
        ViewHolder holder = ViewHolder.getHolder(convertView);

        FriendUserBean child = getChild(groupPosition, childPosition);
        holder.tv_name.setText(child.getName());
        GlideApp.with(mContext)
                .load(child.getHead())
                .into(holder.rw_head);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

    }

    @Override
    public void onGroupCollapsed(int groupPosition) {

    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return 0;
    }


    private static class ViewHolder {

        private ImageView rw_head;
        private TextView tv_name;
        private TextView tv_signature;


        ViewHolder(View convertView) {
            rw_head =  convertView.findViewById(R.id.rw_head);
            tv_name =  convertView.findViewById(R.id.tv_name);
            tv_signature =  convertView.findViewById(R.id.tv_signature);

        }

        static FriendAdapter.ViewHolder getHolder(View convertView) {
            FriendAdapter.ViewHolder holder = (FriendAdapter.ViewHolder) convertView.getTag();
            if (holder == null) {
                holder = new FriendAdapter.ViewHolder(convertView);
                convertView.setTag(holder);
            }
            return holder;
        }
    }
}
