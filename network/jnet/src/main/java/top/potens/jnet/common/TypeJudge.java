package top.potens.jnet.common;

/**
 * Created by wenshao on 2018/6/23.
 * 类型判断
 *  java基础类型包括
 *      1 四种整数类型(byte、short、int、long)
 *      2 两种浮点数类型(float、double)
 *      3 一种字符类型(char)
 *      4 一种布尔类型(boolean)
 * 每种类型都对应八中包装类
 *       1 四种整数类型(Byte、Short、Integer、Long)
 *       2 两种浮点数类型(Float、Double)
 *       3 一种字符类型(Character)
 *       4 一种布尔类型(Boolean)
 *
 */
public class TypeJudge {

    // 是否为基础类型的包装类
    public static boolean isWrapClass(Class clazz) {
        try {
            return ((Class) clazz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }
    // 是否为String类型
    public static boolean isStringClass(Class clazz) {
        return clazz.getName().equals("java.lang.String");
    }
}
