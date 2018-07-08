package top.potens.teleport.constant;

/**
 * Created by wenshao on 2017/5/7.
 * Handler返回码
 */

public class HandlerCode {
    // 常用返回码
    /**
     * http请求成功
     */
    public final static int HTTP_REQUEST_SUC = 0x001;

    /**
     * http请求成功
     */
    public final static int HTTP_REQUEST_FAIL = 0x002;


    /**
     * 本地加载成功
     */
    public final static int LOCAL_QUERY_SUC = 0x003;
    /**
     * 本地加载失败
     */
    public final static int LOCAL_QUERY_FAIL = 0x004;

    /**
     * rpc加载成功
     */
    public final static int RPC_QUERY_SUC = 0x005;
    /**
     * rpc加载成功
     */
    public final static int RPC_QUERY_FAIL = 0x006;


    /**
     * 数据变化
     */
    public final static int DATA_CHANGE = 0x007;



    // 特定返回码
    /**
     * 请求登录成功
     */
    public final static int HTTP_LOGIN_SUC = 0x100;
    /**
     * 请求登录失败
     */
    public final static int HTTP_LOGIN_FAIL = 0x101;

    /**
     * 初始化成功
     */
    public final static int HTTP_INIT_SUC = 0x102;





}
