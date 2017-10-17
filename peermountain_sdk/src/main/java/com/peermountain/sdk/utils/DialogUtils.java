package com.peermountain.sdk.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.peermountain.sdk.R;


/**
 * Created by Galeen on 7.1.2016 г..
 */
public class DialogUtils {
//    public static void showInfoDialog(Context context, String message, String title) {
//        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//        if (title != null)
//            dialog.setTitle(title);
//        if (message != null)
//            dialog.setShowActivity(message);
//        dialog.setPositiveButton(R.string.btn_close,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface di, int i) {
//                        di.dismiss();
//                    }
//                });
////        dialog.setNeutralButton(R.string.btn_change_settings_wifi, new DialogInterface.OnClickListener() {
////
////            @Override
////            public void onClick(DialogInterface di, int i) {
////                context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
////            }
////        });
////        dialog.setNegativeButton(R.string.btn_close, null);
//        dialog.show();
//    }

//    public static void openWiFiSettingsDialog(final Context context, String message) {
//        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//        dialog.setTitle(R.string.msg_no_network);
//        if (message == null)
//            dialog.setShowActivity(R.string.msg_need_network);
//        else
//            dialog.setShowActivity(message);
//        dialog.setPositiveButton(R.string.btn_change_settings_3g,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface di, int i) {
//                        context.startActivity(new Intent(
//                                Settings.ACTION_WIRELESS_SETTINGS));
//                    }
//                });
//        dialog.setNeutralButton(R.string.btn_change_settings_wifi, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface di, int i) {
//                context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
//            }
//        });
//        dialog.setNegativeButton(R.string.btn_close, null);
//        dialog.show();
//    }


    public static void showNoNetworkMsg(View v) {
        Snackbar.make(v, R.string.pm_msg_no_network, Snackbar.LENGTH_LONG).show();
    }

    public static void showError(Activity activity, int message) {
        if (activity != null)
            showInfoSnackbar(activity, message, 0, Color.WHITE, 0, null);
    }

    public static void showError(Activity activity, String message) {
        if (activity != null)
            showInfoSnackbar(activity, message);
    }

    public static void showErrorToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showInfoSnackbar(View v, int message) {
        Snackbar.make(v, message, Snackbar.LENGTH_LONG).show();
    }

    public static void showInfoSnackbar(View v, String message) {
        Snackbar.make(v, message, Snackbar.LENGTH_LONG).show();
//                .setAction(Application.getInstance().getUser().getLang().equalsIgnoreCase(Constants.FR_USER_LANGUAGE)?R.string.english:R.string.french,
//                        new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                User user = Application.getInstance().getUser();
//                                if (user.getLang().equalsIgnoreCase(Constants.FR_USER_LANGUAGE)) {
//                                    user.setLang(Constants.EN_USER_LANGUAGE);
//                                    tvLang.setText(R.string.english);
//                                    tv2Lang.setText(R.string.french);
//                                    lang = Constants.EN_APP_LANGUAGE;
//                                } else {
//                                    user.setLang(Constants.FR_USER_LANGUAGE);
//                                    tvLang.setText(R.string.french);
//                                    tv2Lang.setText(R.string.english);
//                                    lang = Constants.FR_APP_LANGUAGE;
//                                }
//                                tv2Lang.setVisibility(View.GONE);
//                                isLanguageUpdated = true;
//                                if(!PostOperationToServer.updateUser(getParentActivity(), updateCallback))
//                                    notifyLanguageChange();
//
//                            }
//                        }
//                ).show();
    }

    public static void showInfoSnackbar(Activity activity, String message) {
        if (activity != null && message != null)
            Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    public static void showInfoSnackbar(Activity activity, int message, int colBtn, int colMsg, int colBkg, View.OnClickListener listener) {
        if (activity != null && message != 0) {
            Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
            // Set the Snackbar action button default text color
            if (colBtn != 0)
                snackbar.setActionTextColor(colBtn);

            // Change the Snackbar default text color
            View snackbarView = snackbar.getView();
            if (colMsg != 0) {
                TextView tv = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(colMsg);
            }
            // Change the Snackbar default background color
            if (colBkg != 0)
                snackbarView.setBackgroundColor(colBkg);

            if (listener == null)
                snackbar.setAction(R.string.pm_btn_ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        snackbar.dismiss();
                    }
                });
            else
                snackbar.setAction(R.string.pm_btn_ok, listener);
            // Display the Snackbar
            snackbar.show();
        }
    }

    public static void showInfoSnackbar(Activity activity, int message) {
        if (activity != null && message != 0)
            showInfoSnackbar(activity, message, 0, Color.WHITE, 0, null);
//            Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

//    public static void showSaveAndExitDialog(Activity activity,DialogInterface.OnClickListener listenerYes,
//                                             DialogInterface.OnClickListener listenerNO){
//        if (activity != null)
//            showChoiceDialog(activity, R.string.title_save_and_exit_dialog,R.string.msg_save_and_exit_dialog,listenerYes,
//                    listenerNO, activity.getString(R.string.btn_yes_save_and_exit_dialog),
//                    activity.getString(R.string.btn_no_save_and_exit_dialog));
//    }

    public static void showSimpleDialog(Context ctx, int msg, DialogInterface.OnClickListener listener) {
        showSimpleDialog(ctx, -1, msg, null, listener);
    }

    public static void showSimpleDialog(Context ctx, String msg, DialogInterface.OnClickListener listener) {
        showSimpleDialog(ctx, -1, -1, msg, listener);
    }

    public static void showSimpleDialog(Context ctx, int title, int msgRes, String msg, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx, getDialogStyle());
        dialog.setCancelable(false);
        if (title != -1)
            dialog.setTitle(title);
        if (msgRes != -1) {
            dialog.setMessage(msgRes);
        } else {
            dialog.setMessage(msg);
        }
        if (listener == null)
            dialog.setPositiveButton(R.string.pm_btn_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        else
            dialog.setPositiveButton(R.string.pm_btn_ok, listener);
        AlertDialog alertDialog = dialog.create();
//        alertDialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
        alertDialog.show();
    }

    private static int getDialogStyle() {
        return Build.VERSION.SDK_INT >= 22 ? android.R.style.Theme_DeviceDefault_Dialog_Alert :
                AlertDialog.THEME_DEVICE_DEFAULT_DARK;
    }

    public static void showChoiceDialog(Context ctx, int title, int msg, DialogInterface.OnClickListener listener1
            , DialogInterface.OnClickListener listener2, int btnYes, int btnNo) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx, getDialogStyle());
        dialog.setCancelable(false);
        if (title != -1)
            dialog.setTitle(title);
        if (msg != -1)
            dialog.setMessage(msg);
        if (listener1 == null)
            dialog.setPositiveButton(btnYes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        else
            dialog.setPositiveButton(btnYes, listener1);
        if (listener2 == null)
            dialog.setNegativeButton(btnNo, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        else
            dialog.setNegativeButton(btnNo, listener2);
        AlertDialog alertDialog = dialog.create();
//        alertDialog.getWindow().setBackgroundDrawableResource(R.color.colorPrimary);
        alertDialog.show();
    }

//    public static AlertDialog openGPSSettingsDialog(final Context context, final SettingsDialogListener listener) {
//        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//        dialog.setCancelable(false);
//        dialog.setTitle(R.string.msg_no_gps);
//        dialog.setShowActivity(R.string.msg_need_gps);
//        dialog.setPositiveButton(R.string.btn_change_settings,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface di, int i) {
//                        if (listener != null)
//                            listener.onGoingToSettings(true);
//                        context.startActivity(new Intent(
//                                Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                        di.dismiss();
//                    }
//                });
//        dialog.setNegativeButton(R.string.btn_close,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface di, int i) {
//                        if (listener != null)
//                            listener.onGoingToSettings(false);
//                        di.dismiss();
//
//                    }
//                });
//        return dialog.show();
//    }

//    public static void showInfoDialog(Context ctx, int msgRes, int btnYesRes, int btnNoRes,
//                                      final View.OnClickListener listenerYes, final View.OnClickListener listenerNo) {
//        final Dialog dialog = new Dialog(ctx);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = vi.inflate(R.layout.info_dialog, null);
//        TextView msg = (TextView) view.findViewById(R.id.tvDialogMessage);
//        TextView btnYes = (TextView) view.findViewById(R.id.btnYes);
//        TextView btnNo = (TextView) view.findViewById(R.id.btnNo);
//        msg.setText(msgRes);
//        if (btnYesRes != 0) {
//            btnYes.setText(btnYesRes);
//            btnYes.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dialog.dismiss();
//                    if (listenerYes != null)
//                        listenerYes.onClick(view);
//                }
//            });
//            RippleUtils.setRippleEffectLessRounded(btnYes);
//        } else {
//            btnYes.setVisibility(View.GONE);
//        }
//        if (btnNoRes != 0) {
//            btnNo.setText(btnNoRes);
//            btnNo.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dialog.dismiss();
//                    if (listenerNo != null) {
//                        listenerNo.onClick(view);
//                    }
//                }
//            });
//            RippleUtils.setRippleEffectLessRounded(btnNo);
//        } else {
//            btnNo.setVisibility(View.GONE);
//        }
//        dialog.setContentView(view);
//        dialog.show();
//    }

//    public interface SettingsDialogListener {
//        void onGoingToSettings(boolean flag);
//    }
}
