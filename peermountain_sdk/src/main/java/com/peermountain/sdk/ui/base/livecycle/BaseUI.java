package com.peermountain.sdk.ui.base.livecycle;

import android.app.ProgressDialog;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.peermountain.core.model.guarded.InfoMessage;
import com.peermountain.core.model.guarded.ProgressData;
import com.peermountain.core.network.NetworkResponse;
import com.peermountain.sdk.R;
import com.peermountain.sdk.utils.DialogUtils;


/**
 * Created by Galeen on 12/22/2017.
 * Class to implement base logic for show/hide progress , show errors and show info messages
 * It is reused in BaseActivity and BaseFragment
 */

public class BaseUI {
    private ProgressDialog progressDialog;
    private View progressBar;
    private LifecycleOwner lifecycleOwner;
    private View parentView;
    private Context context;

    BaseUI(LifecycleOwner lifecycleOwner, View parentView, Context context) {
        this.lifecycleOwner = lifecycleOwner;
        this.parentView = parentView;
        this.context = context;
    }

    BaseUI(LifecycleOwner lifecycleOwner, Context context) {
        this.lifecycleOwner = lifecycleOwner;
        this.context = context;
    }

    void setParentView(View parentView) {
        this.parentView = parentView;
    }

    public void setBaseViewModel(BaseViewModel model) {
        model.shouldShowProgressDialog().observe(lifecycleOwner, new Observer<ProgressData>() {
            @Override
            public void onChanged(@Nullable ProgressData progressData) {
                if (progressData == null) {
                    hideProgressDialog();
                } else {
                    showProgressDialog(progressData.getLoadingMsg(), progressData.getOnCancelListener());
                }
            }
        });
        model.shouldShowError().observe(lifecycleOwner, new Observer<NetworkResponse>() {
            @Override
            public void onChanged(@Nullable NetworkResponse networkResponse) {
                showError(networkResponse != null ? networkResponse.error : null, networkResponse);
            }
        });
        model.shouldShowProgressBar().observe(lifecycleOwner, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean != null && aBoolean) {
                    showProgressBar();
                } else {
                    hideProgressBar();
                }
            }
        });
        model.shouldShowNoNetwork().observe(lifecycleOwner, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean != null && aBoolean) {
                    showNoNetwork();
                }
            }
        });

        model.shouldShowMessage().observe(lifecycleOwner, new Observer<InfoMessage>() {
            @Override
            public void onChanged(@Nullable InfoMessage infoMessage) {
                showMessage(infoMessage);
            }
        });
        model.showErrorDialog().observe(lifecycleOwner,
                new Observer<NetworkResponse>() {
                    @Override
                    public void onChanged(@Nullable NetworkResponse networkResponse) {
                        showErrorDialog(networkResponse != null ? networkResponse.error : null, networkResponse);
                    }
                });
    }

    public void setProgressBar(View progressBar) {
        this.progressBar = progressBar;
    }

    private void showNoNetwork() {
        DialogUtils.showError(parentView, R.string.pm_msg_no_network);
    }

    private void showError(String msg, NetworkResponse networkResponse) {
        if (networkResponse != null && networkResponse.errorMsg != 0) {
            DialogUtils.showError(parentView, networkResponse.errorMsg);
        } else if (msg != null) {
            DialogUtils.showError(parentView, msg);
        } else {
            DialogUtils.showError(parentView, R.string.pm_msg_main_server_error);
        }
    }

    private void showErrorDialog(String msg, NetworkResponse networkResponse) {
        if (networkResponse != null && networkResponse.errorMsg != 0) {
            DialogUtils.showSimpleDialog(context, networkResponse.errorMsg, null);
        } else if (msg != null) {
            DialogUtils.showSimpleDialog(context, msg, null);
        } else {
            DialogUtils.showSimpleDialog(context, R.string.pm_msg_main_server_error, null);
        }
    }

    private void showMessage(InfoMessage infoMessage) {
        if (infoMessage == null) return;
        if (infoMessage.getMessageResource() != 0) {
            DialogUtils.showInfoSnackbar(parentView,infoMessage.getMessageResource());
//            Snackbar.make(parentView, infoMessage.getMessageResource(), Snackbar.LENGTH_LONG).show();
        } else if (infoMessage.getMessage() != null) {
            DialogUtils.showInfoSnackbar(parentView,infoMessage.getMessage());
//            Snackbar.make(parentView, infoMessage.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    public void showProgressDialog(String loadingMsg, DialogInterface.OnCancelListener onCancelListener) {
        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(loadingMsg == null ? context.getString(R.string.pm_msg_loading) : loadingMsg);
            progressDialog.setCancelable(onCancelListener != null);
            progressDialog.setOnCancelListener(onCancelListener);
        }
        progressDialog.show();
    }

    public void showProgressDialog() {
        showProgressDialog(null,null);
    }


    private void showProgressBar() {
        if (progressBar != null) {
            if (progressBar instanceof SwipeRefreshLayout) {
                ((SwipeRefreshLayout) progressBar).setRefreshing(true);
            } else {
                progressBar.setVisibility(View.VISIBLE);
            }
        }
    }

    void hideProgresses() {
        hideProgressDialog();
        hideProgressBar();
    }

    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void hideProgressBar() {
        if (progressBar != null) {
            if (progressBar instanceof SwipeRefreshLayout) {
                ((SwipeRefreshLayout) progressBar).setRefreshing(false);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    public LifecycleOwner getLifecycleOwner() {
        return lifecycleOwner;
    }
}
