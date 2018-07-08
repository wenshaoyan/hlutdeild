package top.potens.teleport.helper;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.TextView;



import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.potens.teleport.GlobalApplication;
import top.potens.teleport.gif.AnimatedGifDrawable;
import top.potens.teleport.gif.AnimatedImageSpan;

/**
 * Created by wenshao on 2017/4/14.
 * 表情帮助类
 */

public class FaceHelper {
    private static List<String> staticFacesList;
    private static final String fileNameTemp = "#[face/png/f_static_000.png]#";

    private static final String smallRegex = "(\\#\\[face/png/f_static_)\\d{3}(.png\\]\\#)";

    private int columns;
    private int rows;

    static {
        initStaticFaces();
    }

    public FaceHelper(int columns) {

        this.columns = columns;
        this.rows = staticFacesList.size() / this.columns;
    }

    public int getColumns() {
        return columns;
    }


    /**
     * 获取表情转文字后的内容
     *
     * @param path 表情图片路径
     * @return
     */
    public SpannableStringBuilder getFace(String path) {
        Context mContent = GlobalApplication.getAppContext();
        SpannableStringBuilder sb = new SpannableStringBuilder();
        try {
            /**
             * 经过测试，虽然这里tempText被替换为png显示，但是但我单击发送按钮时，获取到輸入框的内容是tempText的值而不是png
             * 所以这里对这个tempText值做特殊处理
             * 格式：#[face/png/f_static_000.png]#，以方便判斷當前圖片是哪一個
             * */
            String tempText = "#[" + path + "]#";
            sb.append(tempText);
            sb.setSpan(
                    new ImageSpan(mContent, BitmapFactory
                            .decodeStream(mContent.getAssets().open(path))), sb.length()
                            - tempText.length(), sb.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb;
    }


    /**
     * 初始化表情列表staticFacesList
     */
    private static void initStaticFaces() {
        Context content = GlobalApplication.getAppContext();

        try {
            staticFacesList = new ArrayList<String>();
            String[] faces = content.getAssets().list("face/png");
            //将Assets中的表情名称转为字符串一一添加进staticFacesList
            for (int i = 0; i < faces.length; i++) {
                staticFacesList.add(faces[i]);
            }
            //去掉删除图片
            staticFacesList.remove("emotion_del_normal.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断即将删除的字符串是否是图片占位字符串tempText 如果是：则讲删除整个tempText
     **/
    private boolean isDeletePng(String inputStr, int cursor) {
        String contentString = inputStr.substring(0, cursor);
        if (contentString.length() >= fileNameTemp.length()) {
            String checkStr = contentString.substring(contentString.length() - fileNameTemp.length(),
                    contentString.length());

            Pattern p = Pattern.compile(smallRegex);
            Matcher m = p.matcher(checkStr);
            return m.matches();
        }
        return false;
    }

    /**
     * 根据表情数量以及GridView设置的行数和列数计算Pager数量
     *
     * @return Pager数量
     */
    public int getPagerCount() {
        int count = staticFacesList.size();
        return count % (columns * rows - 1) == 0 ? count / (columns * rows - 1)
                : count / (columns * rows - 1) + 1;
    }

    /**
     * 删除图标执行事件
     * 注：如果删除的是表情，在删除时实际删除的是tempText即图片占位的字符串，所以必需一次性删除掉tempText，才能将图片删除
     */
    public void delete(Editable editable) {
        if (editable.length() != 0) {
            int iCursorEnd = Selection.getSelectionEnd(editable);  // 光标结束位置
            int iCursorStart = Selection.getSelectionStart(editable);// 光标起始位置
            Log.i("test", "delete: " + iCursorStart + " " + iCursorEnd);
            if (iCursorEnd > 0) {
                if (iCursorEnd == iCursorStart) {
                    if (isDeletePng(editable.toString(), iCursorEnd)) {
                        editable.delete(iCursorEnd - fileNameTemp.length(), iCursorEnd);
                    } else {
                        editable.delete(iCursorEnd - 1, iCursorEnd);
                    }
                } else {
                    editable.delete(iCursorStart, iCursorEnd);
                }
            }
        }
    }


    /**
     * 获取每页的表情文名称
     *
     * @param page 页码
     * @return
     */
    public List<String> pagePath(int page) {

        List<String> subList = new ArrayList<String>();
        subList.addAll(staticFacesList
                .subList(page * (columns * rows - 1),
                        (columns * rows - 1) * (page + 1) > staticFacesList
                                .size() ? staticFacesList.size() : (columns
                                * rows - 1)
                                * (page + 1)));
        /**
         * 末尾添加删除图标
         * */
        subList.add("emotion_del_normal.png");

        return subList;
    }

    /**
     * 向指定Editable控件添加表情
     */
    public void insertFace(Editable editable, String text) {
        SpannableStringBuilder face = getFace(text);

        int iCursorStart = Selection.getSelectionStart(editable);
        int iCursorEnd = Selection.getSelectionEnd(editable);
        if (iCursorStart != iCursorEnd) {
            editable.replace(iCursorStart, iCursorEnd, "");
        }
        int iCursor = Selection.getSelectionEnd(editable);
        editable.insert(iCursor, face);
    }

    /**
     * 获取所有表情列表
     */
    public List<String> getAllSmallFace() {
        return staticFacesList;
    }

    // 把对应的图片显示为gif图
    public static SpannableStringBuilder imageToGif(final TextView gifTextView, String content) {
        Context mContext = GlobalApplication.getAppContext();
        SpannableStringBuilder sb = new SpannableStringBuilder(content);
        Pattern p = Pattern.compile(smallRegex);
        Matcher m = p.matcher(content);
        while (m.find()) {
            String tempText = m.group();
            try {
                String num = tempText.substring("#[face/png/f_static_".length(), tempText.length() - ".png]#".length());
                String gif = "face/gif/f" + num + ".gif";
                /**
                 * 如果open这里不抛异常说明存在gif，则显示对应的gif
                 * 否则说明gif找不到，则显示png
                 * */
                InputStream is = mContext.getAssets().open(gif);
                sb.setSpan(new AnimatedImageSpan(new AnimatedGifDrawable(is, new AnimatedGifDrawable.UpdateListener() {
                            @Override
                            public void update() {
                                gifTextView.postInvalidate();
                            }
                        })), m.start(), m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                is.close();
            } catch (Exception e) {
                String png = tempText.substring("#[".length(), tempText.length() - "]#".length());
                try {
                    sb.setSpan(new ImageSpan(mContext, BitmapFactory.decodeStream(mContext.getAssets().open(png))), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    //e1.printStackTrace();
                }
                //e.printStackTrace();
            }
        }
        return sb;
    }
}
