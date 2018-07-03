package top.potens.teleport.bean;


import java.util.List;

/**
 * Created by wenshao on 2017/3/16.
 * 好友分组对象
 */
public class FriendGroupBean {

    private String id;
    private String name;
    private List<FriendUserBean> friendUserBeans;


    public FriendGroupBean(){

    }

    public FriendGroupBean(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FriendUserBean> getFriendUserBeans() {
        return friendUserBeans;
    }

    public void setFriendUserBeans(List<FriendUserBean> friendUserBeans) {
        this.friendUserBeans = friendUserBeans;
    }
}
