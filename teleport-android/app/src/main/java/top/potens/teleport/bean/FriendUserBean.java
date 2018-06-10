package top.potens.teleport.bean;


import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;

/**
 * Created by wenshao on 2017/3/16.
 * 好友用户对象
 */
public class FriendUserBean {
    private int id;
    private String name;

    private int head;
    private int fg_id;

    public FriendUserBean() {
    }

    public FriendUserBean(int id, String name,  @RawRes @DrawableRes int head, int fg_id) {
        this.id = id;
        this.name = name;
        this.head = head;
        this.fg_id = fg_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHead() {
        return head;
    }

    public void setHead(@RawRes @DrawableRes int head) {
        this.head = head;
    }

    public int getFg_id() {
        return fg_id;
    }

    public void setFg_id(int fg_id) {
        this.fg_id = fg_id;
    }
}
