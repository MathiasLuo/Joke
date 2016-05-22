package com.mathiasluo.joke.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mathiasluo.joke.R;
import com.mathiasluo.joke.adapter.JokeAdapter;
import com.mathiasluo.joke.model.JokeEntry;
import com.mathiasluo.joke.utils.DialogUtil;
import com.mathiasluo.joke.utils.SocketsUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout mRefresh;
    @BindView(R.id.loading_progress)
    ProgressBar mProgressBar;

    private JokeEntry mJokeEntry;

    private MyHandler mMyHandler = new MyHandler(this);

    private JokeAdapter mJokeAdapter;


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
                    if (mJokeEntry.size == 0) {
                        Toast.makeText(MainActivity.this, "No data , click Upload it", Toast.LENGTH_SHORT).show();
                    }
                    if (mJokeAdapter == null) {
                        mJokeAdapter = new JokeAdapter(mJokeEntry, mActivityWeakReference.get());
                        mRecyclerView.setAdapter(mJokeAdapter);
                    } else
                        mJokeAdapter.replaceDate(mJokeEntry);
                    closeLoading();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void init() {
        setSupportActionBar(mToolbar);
        mFab.setOnClickListener(this);

        mToolbar.setTitle(getString(R.string.joke));
        setSupportActionBar(mToolbar);
        mRefresh.setColorSchemeColors(R.color.colorAccent);
        mRefresh.setOnRefreshListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mJokeEntry = SocketsUtils.getJokes();
                    Message message = mMyHandler.obtainMessage();
                    message.arg1 = SocketsUtils.SEND_SETUPS;
                    mMyHandler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                    mMyHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            closeLoading();
                            showToast("Network problems");
                        }
                    });
                }
            }
        }).start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refresh();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refresh() {
        mRefresh.setRefreshing(true);
        getData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onRefresh() {
        getData();
    }


    private void closeLoading() {
        mRefresh.setRefreshing(false);
        mProgressBar.setVisibility(View.GONE);
    }

    public void showToast(final String reason) {
        Toast.makeText(this, reason, Toast.LENGTH_SHORT).show();
        mMyHandler.post(new Runnable() {
            @Override
            public void run() {
                DialogUtil.showLoadSucess(MainActivity.this, "notification", "Post joke failed \n" + reason, "Republished", "back", new DialogUtil.DialogListener() {
                    @Override
                    public void onPositive() {
                        closeLoadingProgress();
                    }

                    @Override
                    public void onNegative() {
                        closeLoadingProgress();
                    }
                });
            }
        });
    }

    private void closeLoadingProgress() {
        DialogUtil.dismissDialog();
    }

}
