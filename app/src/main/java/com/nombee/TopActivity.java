package com.nombee;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TopActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

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
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        // セットするデータの数だけカードが作られる
        String[] myDataset = {"a","b","c","d"};
        mAdapter = new TopActivity.MyAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
        int viewHeight = 800 * myDataset.length;
        mRecyclerView.getLayoutParams().height = viewHeight;
        /*
        for (int i = 0; i < 5; i++) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.post_card, null);
            CardView cardView = (CardView) linearLayout.findViewById(R.id.cardView);
            //cardView.setLayoutParams(new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT,CardView.LayoutParams.WRAP_CONTENT));
            //cardView.setLayoutParams(new LinearLayout.LayoutParams(CardView.LayoutParams.MATCH_PARENT,CardView.LayoutParams.WRAP_CONTENT));
            //CardView.LayoutParams layoutParams = (CardView.LayoutParams)cardView.getLayoutParams();
            //CardView.LayoutParams layoutParams = (CardView.LayoutParams) cardView.getLayoutParams();
            //layoutParams.height = 300;

            cardView.setTag(i);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(TopActivity.this, String.valueOf(v.getTag()) + "番目のCardViewがClickされました", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.setClassName("com.nombee", "com.nombee.TestActivity");
                    startActivity(intent);
                }
            });
            cardLinear.addView(linearLayout, i);
        }
        */


    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
        private String[] mDataset;

        public class ViewHolder extends RecyclerView.ViewHolder{
            public CardView mCardView;
            public TextView vName;
            public TextView vSakeName;
            public TextView vComment;

            public ViewHolder(View v){
                super(v);
                mCardView = (CardView)v.findViewById(R.id.cardView);
                vName = (TextView) v.findViewById(R.id.username);
                vComment = (TextView) v.findViewById(R.id.comment);
                vSakeName = (TextView) v.findViewById(R.id.sakename);
            }
        }

        public MyAdapter(String[] myDataset){
            mDataset = myDataset;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.post_card, parent, false);

            // set the view's size, margins, paddings and layout parameters here

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position){
            // get element from my dataset at this position
            // replace the contents of the view with that element
            holder.vName.setText("hanako-san");

            // 画像の処理
            Bitmap _bm = BitmapFactory.decodeResource(getResources(), R.drawable.samplepic);

            int cardW = holder.mCardView.getWidth();
            int w = _bm.getWidth();
            int h = _bm.getHeight();
            float scale = Math.min((float) cardW / w, (float) 300 / h);
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            int size = Math.min(w, h);
            ((ImageView) findViewById(R.id.sakeImage)).setImageBitmap(Bitmap
                    .createBitmap(_bm, 0, 0, size, size, matrix, true));
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
            return mDataset.length;
        }
    }
}
