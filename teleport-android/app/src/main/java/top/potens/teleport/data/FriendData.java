package top.potens.teleport.data;

import java.util.ArrayList;
import java.util.List;

import top.potens.teleport.R;
import top.potens.teleport.bean.FriendGroupBean;
import top.potens.teleport.bean.FriendUserBean;

/**
 * Created by wenshao on 2018/4/30.
 */
public class FriendData {
    public static List<FriendGroupBean>  getFriendGroupData() {
        List<FriendGroupBean> fgs = new ArrayList<FriendGroupBean>();
        FriendGroupBean groupSource = new FriendGroupBean(1,"我的资源");
        FriendGroupBean groupIntranet = new FriendGroupBean(2,"内网设备");

        ArrayList<FriendUserBean> sourceList = new ArrayList<>();
        sourceList.add(new FriendUserBean(1,"我的共享", R.mipmap.head1, 1));
        sourceList.add(new FriendUserBean(2,"收到的共享",R.mipmap.head1, 1));
        groupSource.setFriendUserBeans(sourceList);

        ArrayList<FriendUserBean> IntranetList = new ArrayList<>();

        IntranetList.add(new FriendUserBean(3, "小米4", R.mipmap.head2, 2));
        IntranetList.add(new FriendUserBean(4, "小米5", R.mipmap.head3, 2));
        groupIntranet.setFriendUserBeans(IntranetList);
        fgs.add(groupSource);
        fgs.add(groupIntranet);
        return fgs;
    }

}
