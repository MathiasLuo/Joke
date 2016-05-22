package com.mathiasluo.joke.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mathiasluo.joke.R;
import com.mathiasluo.joke.model.JokeEntry;
import com.mathiasluo.joke.view.JokeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mathiasluo on 16-5-3.
 */
public class JokeAdapter extends RecyclerView.Adapter<JokeAdapter.ViewHolder> {


    private JokeEntry mJokeEntry;
    private Context mContext;


    public JokeAdapter(JokeEntry mJokeEntry, Context mContext) {
        this.mJokeEntry = mJokeEntry;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item_joke_setup, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mTextView.setText(mJokeEntry.setups.get(mJokeEntry.size - position - 1));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, JokeActivity.class);
                intent.putExtra("index", mJokeEntry.size - position - 1);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mJokeEntry.size;
    }

    public void replaceDate(JokeEntry jokeEntry) {
        this.mJokeEntry = jokeEntry;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textView)
        TextView mTextView;

        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.view = itemView;

        }
    }
}
