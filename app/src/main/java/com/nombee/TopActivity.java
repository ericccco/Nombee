package com.nombee;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TopActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int viewHeight = 0;

    // Test用
    //private String[] myDataset = {"a","b","c","d"};
    private List<String> myDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);
        //LinearLayout cardLinear = (LinearLayout) this.findViewById(R.id.cardLinear);
        //cardLinear.removeAllViews();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mRecyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);

        // won't change size
        //mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        // セットするデータの数だけカードが作られる
        myDataset = new ArrayList<String>();
        myDataset.add("a");
        myDataset.add("b");
        myDataset.add("c");
        myDataset.add("e");
        mAdapter = new TopActivity.MyAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
        //viewHeight = 1200 * myDataset.size();
        //mRecyclerView.getLayoutParams().height = viewHeight;

        // set Scroll Listner
        mRecyclerView.addOnScrollListener(new EndlessScrollListener((LinearLayoutManager) mRecyclerView.getLayoutManager()) {
            @Override
            public void onLoadMore(int current_page) {
                //Load
                myDataset.add("e");
                myDataset.add("f");
                myDataset.add("g");
                myDataset.add("h");
                //viewHeight = 1200 * myDataset.size();
                //mRecyclerView.getLayoutParams().height = viewHeight;
            }
        });



    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
        private List<String> mDataset;
        View v;
        Bitmap _bm = null;

        public class ViewHolder extends RecyclerView.ViewHolder{
            public CardView mCardView;
            public TextView vName;
            public TextView vSakeName;
            public TextView vComment;
            public ImageView vSakeImage;

            public ViewHolder(View v){
                super(v);
                mCardView = (CardView)v.findViewById(R.id.cardView);
                vName = (TextView) v.findViewById(R.id.username);
                vComment = (TextView) v.findViewById(R.id.comment);
                vSakeName = (TextView) v.findViewById(R.id.sakename);
                vSakeImage = (ImageView) v.findViewById(R.id.sakeImage);
            }
        }

        public MyAdapter(List<String> myDataset) {
            mDataset = myDataset;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.post_card, parent, false);

            // set the view's size, margins, paddings and layout parameters here


            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position){
            // get element from my dataset at this position
            // replace the contents of the view with that element
            holder.vName.setText("hanako-san : " + position);

            int displayWidth = getDisplaySize(getApplicationContext());
            // 画像の処理
            _bm = BitmapFactory.decodeResource(getResources(), R.drawable.kuheiji);
            int w = _bm.getWidth();
            int h = _bm.getHeight();
            float scale = (float) displayWidth / w;
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            //int size = Math.max(w, h);
            holder.vSakeImage.setImageBitmap(Bitmap
                    .createBitmap(_bm, 0, 0, w, h, matrix, true));
            _bm.recycle();
            _bm = null;

            holder.mCardView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    //on Click
                    //holder.mCardView.setElevation(500);
                    //holder.mCardView.setCardElevation(700);
                    //holder.mCardView.setTranslationZ(700);

                    //Expand height
                    ViewGroup.LayoutParams layoutParams = holder.mCardView.getLayoutParams();
                    //layoutParams.height = 1200;
                    layoutParams.height = CardView.LayoutParams.WRAP_CONTENT;
                    layoutParams.width = CardView.LayoutParams.MATCH_PARENT;
                    holder.mCardView.setLayoutParams(layoutParams);
                    Toast.makeText(TopActivity.this, String.valueOf(holder.getAdapterPosition()) + "番目のCardViewがClickされました", Toast.LENGTH_SHORT).show();
                }
            });

        }

        // セットされたデータの数を数え、作るべきカードの枚数を決める
        @Override
        public int getItemCount(){
            return mDataset.size();
        }

        /**
         * get Display Size
         *
         * @param context
         * @return int displayWidth
         */
        public int getDisplaySize(Context context) {
            int displayWidth = 0;

            //画面サイズ取得の準備
            WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            Display disp = wm.getDefaultDisplay();

            // AndroidのAPIレベルによって画面サイズ取得方法が異なるので条件分岐
            if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) < 13) {
                Log.d("TEST", "12までのが来てる!");
                displayWidth = disp.getWidth();

            } else {
                Log.d("TEST", "13以降が来てる!");
                Point size = new Point();
                disp.getSize(size);
                displayWidth = size.x;

            }
            return displayWidth;
        }
    }
}

