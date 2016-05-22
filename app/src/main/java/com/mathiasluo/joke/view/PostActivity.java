package com.mathiasluo.joke.view;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mathiasluo.joke.R;
import com.mathiasluo.joke.utils.DialogUtil;
import com.mathiasluo.joke.utils.SocketsUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mathiasluo on 16-5-3.
 */
public class PostActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.post)
    TextView mPostText;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.add_joke_setup)
    EditText mAddJokeSetup;
    @BindView(R.id.add_joke_Punchline)
    EditText mAddJokePunchline;


    private MyHandler myHandler = new MyHandler(this);

    class MyHandler extends Handler {

        WeakReference<Activity> mActivityWeakReference;

        MyHandler(Activity activity) {
            mActivityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case SocketsUtils.SEND_SETUPS:

                    break;
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mPostText.setOnClickListener(this);
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostActivity.this.finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.post:
                sendJoke();
                break;
        }
    }


    private void sendJoke() {
        if (!mAddJokePunchline.getText().toString().equals("") && !mAddJokeSetup.getText().toString().equals("")) {
            showLoadingProgress();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        boolean b = SocketsUtils.postJoke(mAddJokeSetup.getText().toString(), mAddJokePunchline.getText().toString());
                        if (b) {
                            closeLoadingProgress();
                            PostActivity.this.finish();
                        } else
                            showUploadFail("上传失败");
                    } catch (final IOException e) {
                        e.printStackTrace();
                        showUploadFail(e.toString());
                    }
                }
            }).start();
        } else
            showToast("你还没有输入完整的joke");
    }


    public void showToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }


    private void showLoadingProgress() {
        DialogUtil.showLoadingDiaolog(this, "uploading");
    }

    private void closeLoadingProgress() {
        DialogUtil.dismissDialog();
    }

    private void showUploadFail(final String reason) {
        myHandler.post(new Runnable() {
            @Override
            public void run() {
                DialogUtil.showLoadSucess(PostActivity.this,"notification", "Post joke failed \n" + reason, "Republished", "back", new DialogUtil.DialogListener() {
                    @Override
                    public void onPositive() {
                        closeLoadingProgress();
                        sendJoke();
                    }

                    @Override
                    public void onNegative() {
                        closeLoadingProgress();
                        PostActivity.this.finish();
                    }
                });
            }
        });


    }
}
