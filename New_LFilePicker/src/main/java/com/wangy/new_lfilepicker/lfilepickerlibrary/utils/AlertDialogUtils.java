package com.wangy.new_lfilepicker.lfilepickerlibrary.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

public class AlertDialogUtils {
    public synchronized static void showDialog(Activity activiity, String title, String message, View view, DialogInterface.OnClickListener... dialogListener) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activiity);
        dialog.setTitle(title);
        dialog.setMessage(message);
        if (view!=null){
            dialog.setView(view);
        }
        dialog.setPositiveButton("确定", dialogListener[0]);
        if (dialogListener.length <= 1 ){
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }else {
            dialog.setNegativeButton("取消", dialogListener[1]);

        }
        dialog.show();
    }
}
