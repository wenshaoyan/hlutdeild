package top.potens.teleport.data;

import java.util.ArrayList;
import java.util.List;

import top.potens.jnet.bean.Client;
import top.potens.teleport.R;
import top.potens.teleport.bean.FriendGroupBean;
import top.potens.teleport.bean.FriendUserBean;

/**
 * Created by wenshao on 2018/4/30.
 *
 */
public class FriendData {
    // 获取固定的列表数据
    public static List<FriendGroupBean>  getFixationFriendGroupData(List<FriendUserBean> intranetList) {
        List<FriendGroupBean> fgs = new ArrayList<FriendGroupBean>();
        FriendGroupBean groupSource = new FriendGroupBean("1","我的资源");
        FriendGroupBean groupIntranet = new FriendGroupBean("2","内网设备");

        ArrayList<FriendUserBean> sourceList = new ArrayList<>();
        sourceList.add(new FriendUserBean("","我的共享", R.mipmap.share));
        sourceList.add(new FriendUserBean("","收到的共享",R.mipmap.share));
        groupSource.setFriendUserBeans(sourceList);

        groupIntranet.setFriendUserBeans(intranetList);
        fgs.add(groupSource);
        fgs.add(groupIntranet);
        return fgs;
    }

}
