package com.mathiasluo.joke.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mathiasluo.joke.R;
import com.mathiasluo.joke.model.Joke;
import com.mathiasluo.joke.utils.DialogUtil;
import com.mathiasluo.joke.utils.SocketsUtils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mathiasluo on 16-5-3.
 */
public class JokeActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.setup)
    TextView setup;
    @BindView(R.id.punchline)
    TextView punchline;

    private int index;
    private Joke joke;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (joke != null) setData(joke);
            closeLoadingProgress();
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joke);
        index = getIntent().getIntExtra("index", 0);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JokeActivity.this.finish();
            }
        });

        getData();
    }


    private void setData(Joke joke) {
        setup.setText(joke.setup);
        punchline.setText(joke.punchline);
    }

    private void getData() {
        showLoadingProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    joke = SocketsUtils.getJokeByIndex(index);
                    handler.sendMessage(handler.obtainMessage());
                } catch (final IOException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            showUploadFail(e.toString());
                        }
                    });
                }
            }
        }).start();

    }


    private void showLoadingProgress() {
        DialogUtil.showLoadingDiaolog(this, "downloading");
    }

    private void closeLoadingProgress() {
        DialogUtil.dismissDialog();
    }

    private void showUploadFail(final String reason) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                DialogUtil.showLoadSucess(JokeActivity.this, "notification", "Get joke failed \n" + reason, "Republished", "back", new DialogUtil.DialogListener() {
                    @Override
                    public void onPositive() {
                        closeLoadingProgress();
                    }

                    @Override
                    public void onNegative() {
                        closeLoadingProgress();
                        JokeActivity.this.finish();
                    }
                });
            }
        });


    }
}
