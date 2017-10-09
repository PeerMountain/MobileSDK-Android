package com.peermountain.sdk.ui.register;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by Galeen on 10/9/17.
 */

public abstract class ToolbarFragments extends Fragment {
    public ToolbarEvents mToolbarListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ToolbarEvents) {
            mToolbarListener = (ToolbarEvents) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ToolbarEvents");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mToolbarListener = null;
    }

    public interface ToolbarEvents{
        void setToolbarTitle(int resTitle, String title);
        void setMenuButtonEvent(View.OnClickListener listener);
        void setMenuLeftIcon(int res);
    }
}
