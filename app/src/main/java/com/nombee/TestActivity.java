package com.nombee;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TestActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
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

        mRecyclerView = (RecyclerView)findViewById(R.id.my_recycler_view2);

        // won't change size
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        // セットするデータの数だけカードが作られる
        String[] myDataset = {"a","b","c","d"};
        mAdapter = new MyAdapter2(myDataset);
        mRecyclerView.setAdapter(mAdapter);

    }

    public class MyAdapter2 extends RecyclerView.Adapter<MyAdapter2.ViewHolder>{
        private String[] mDataset;

        public class ViewHolder extends RecyclerView.ViewHolder{
            //public CardView mCardView;
            public TextView vName;
            public ViewHolder(View v){
                super(v);
                //mCardView = (CardView)v;
                vName = (TextView) v.findViewById(R.id.username);
            }
        }

        public MyAdapter2(String[] myDataset){
            mDataset = myDataset;
        }

        @Override
        public MyAdapter2.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.post_card, parent, false);

            // set the view's size, margins, paddings and layout parameters here

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position){
            // get element from my dataset at this position
            // replace the contents of the view with that element
            holder.vName.setText("hanako-san");

        }

        // セットされたデータの数を数え、作るべきカードの枚数を決める
        @Override
        public int getItemCount(){
            return mDataset.length;
        }
    }
}
