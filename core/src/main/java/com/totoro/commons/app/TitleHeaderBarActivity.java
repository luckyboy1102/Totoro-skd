package com.totoro.commons.app;

import android.os.Bundle;
import android.view.View;

/**
 * Created by Chen on 2015/4/22.
 */
public abstract class TitleHeaderBarActivity extends ActionBarActivity {

    private TitleHeaderBar mTitleHeaderBar;
    private View.OnClickListener leftClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        mTitleHeaderBar = new TitleHeaderBar(this);
        setActionBarView(mTitleHeaderBar);
    }

    @Override
    protected void initActionBarView(View v) {
        mTitleHeaderBar.getTitleTextView().setText(getTitleId());

        if (leftClickListener != null) {
            mTitleHeaderBar.setLeftOnClickListener(leftClickListener);
        } else {
            mTitleHeaderBar.setLeftOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    protected abstract int getLayoutId();

    protected abstract int getTitleId();

    protected TitleHeaderBar getTitleHeaderBar() {
        return mTitleHeaderBar;
    }

    public void setLeftClickListener(View.OnClickListener leftClickListener) {
        this.leftClickListener = leftClickListener;
    }
}
