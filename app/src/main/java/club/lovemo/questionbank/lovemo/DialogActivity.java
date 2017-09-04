package club.lovemo.questionbank.lovemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import club.lovemo.questionbank.R;

/**
 * Created by John.
 */

public class DialogActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFinishOnTouchOutside(false);
        setContentView(R.layout.item_login);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }
}
