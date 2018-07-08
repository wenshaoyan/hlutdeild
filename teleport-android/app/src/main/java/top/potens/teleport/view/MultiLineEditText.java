package top.potens.teleport.view;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

/**
 * Created by wenshao on 2017/4/13.
 * 自定义输入框 解决textMultiLine多行设置导致imeOptions无效
 */

public class MultiLineEditText extends AppCompatEditText {
    private OnBackspacePressListener backspaceListener;
    public MultiLineEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiLineEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MultiLineEditText(Context context) {
        super(context);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection inputConnection = super.onCreateInputConnection(outAttrs);
        if(inputConnection != null){
            outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        }
        return inputConnection;
    }
    public interface OnBackspacePressListener {
        void onBackspacePressed();
    }
    public void setOnBackspacePressListener(OnBackspacePressListener backspaceListener) {
        this.backspaceListener = backspaceListener;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_DEL) {
            if(backspaceListener != null) {
                backspaceListener.onBackspacePressed();
                return true;
            }
        }
        return false;
    }


}
