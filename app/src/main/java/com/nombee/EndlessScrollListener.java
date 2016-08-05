package com.nombee;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * Created by eriko on 2016/04/24.
 */
public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {

    int firstVisibleItem, visibleItemCount, totalItemCount;
    private int visibleThreshold = 4;
    private int previousTotal = 0;
    private boolean loading = true;
    private int current_page = 1;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (loading) {
            //Log.d("Scrolled:", "totalItemCount: "+totalItemCount+ " previouseTotal: "+previousTotal);
            if (totalItemCount > previousTotal) {
                loading = false;
                Log.d("Scrolled:", "loadingをfalseにした！");
                previousTotal = totalItemCount;
            }
        } else {
            //Log.d("Scrolled:", "loadingがfalse " + totalItemCount + " / " + visibleItemCount + " / " + firstVisibleItem + " / " + visibleThreshold);
        }

        //if(!loading && (totalItemCount == (firstVisibleItem + visibleItemCount))){
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            //Log.d("Scrolled:", "loadingがfalse " + totalItemCount + " / " + visibleItemCount + " / " + firstVisibleItem + " / " + visibleThreshold);
            current_page++;
            //Log.d("Scrolled:", "current_page++ " + current_page);
            onLoadMore(current_page);

            loading = true;
        }
    }

    public abstract void onLoadMore(int current_page);
}