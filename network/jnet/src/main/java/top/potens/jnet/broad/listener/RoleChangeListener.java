package top.potens.jnet.broad.listener;

/**
 * Created by wenshao on 2018/5/28.
 *
 */
public interface RoleChangeListener {
    // server角色
    public static final byte ROlE_SERVER = 0x21;
    // client角色
    public static final byte ROLE_CLIENT = 0x22;
    // work角色
    public static final byte ROLE_WORK = 0x23;
    // role->client
    public void onWorkToClient();

    // role->server
    public void onWorkToServer();

    // client->role
    public void onClientToWork();

    // server->client
    public void onServerToClient();

    // client -> server
    public void onClientToServer();

}
