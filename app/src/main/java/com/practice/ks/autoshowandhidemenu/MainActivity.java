package com.practice.ks.autoshowandhidemenu;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.header)
    View header;
    private boolean isTop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new NormalRecyclerViewAdapter(this));
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int down = 0;
            private int up = 0;
            private int last = 0;
            private int distance = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                down = up = 0;
                if (newState == 1) {
                    distance = getScollYDistance(mRecyclerView);
                }
                Log.d("KS_TEST", "***********state change*************" + newState);
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                down = dy < 0 ? down - dy : 0;
                up = dy > 0 ? up + dy : 0;
                last = dy;
                if (isTop && down > distance && last < -30) {
                    isTop = false;
                    down = 0;
                    changeView(header, -header.getHeight() / 2, 0);
                } else if (!isTop && up > distance) {
                    isTop = true;
                    up = 0;
                    changeView(header, 0, -header.getHeight() / 2);
                }
                super.onScrolled(recyclerView, dx, dy);
                Log.d("KS_TEST", "first item " + getScollYDistance(mRecyclerView) + "");
            }
        });


    }

    private void changeView(View view, int start, int end) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                RelativeLayout.LayoutParams ll2 = (RelativeLayout.LayoutParams) header.getLayoutParams();
                ll2.topMargin = value;
                header.setLayoutParams(ll2);
            }
        });
        valueAnimator.setDuration(300);//动画时间
        valueAnimator.start();//启动动画
    }

    public int getScollYDistance(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int position = layoutManager.findFirstVisibleItemPosition();
        View firstVisiableChildView = layoutManager.findViewByPosition(position);
        int itemHeight = firstVisiableChildView.getBottom();
        return itemHeight;
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    class NormalRecyclerViewAdapter extends RecyclerView.Adapter<NormalRecyclerViewAdapter.NormalTextViewHolder> {
        private final LayoutInflater mLayoutInflater;
        private final Context mContext;
        private String[] mTitles;

        public NormalRecyclerViewAdapter(Context context) {
            mTitles = new String[30];
            for (int i = 0; i < 30; i++) {
                mTitles[i] = "this is a  test, NO." + i;
            }
            mContext = context;
            mLayoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public NormalTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.recycler_item, parent, false));
        }

        @Override
        public void onBindViewHolder(NormalTextViewHolder holder, int position) {
            holder.mTextView.setText(mTitles[position]);
        }

        @Override
        public int getItemCount() {
            return mTitles == null ? 0 : mTitles.length;
        }

        class NormalTextViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.textView4)
            TextView mTextView;

            NormalTextViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("NormalTextViewHolder", "onClick--> position = " + getPosition());
                    }
                });
            }
        }
    }
}
