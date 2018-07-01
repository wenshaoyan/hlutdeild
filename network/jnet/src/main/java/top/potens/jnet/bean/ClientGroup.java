package top.potens.jnet.bean;

import java.util.List;

/**
 * Created by wenshao on 2018/6/19.
 * 客户端组
 */
public class ClientGroup {
    // 组id
    private String id;
    // 组名称
    private String name;
    // 组的所有client
    private List<Client> clients;

    public ClientGroup(String id) {
        this.id = id;
    }
    public ClientGroup(String id, String name, List<Client> clients) {
        this.id = id;
        this.name = name;
        this.clients = clients;
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

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    @Override
    public String toString() {
        return "ClientGroup{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", clients=" + clients +
                '}';
    }
}
