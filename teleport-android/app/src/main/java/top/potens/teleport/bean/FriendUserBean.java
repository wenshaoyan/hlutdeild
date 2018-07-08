package top.potens.teleport.bean;


import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;

import java.io.Serializable;

/**
 * Created by wenshao on 2017/3/16.
 * 好友用户对象
 */
public class FriendUserBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;

    private int head;

    public FriendUserBean() {
    }

    public FriendUserBean(String id, String name,  @RawRes @DrawableRes int head) {
        this.id = id;
        this.name = name;
        this.head = head;
    }
    public String  getId() {
        return id;
    }

    public void setId(String  id) {
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

}
