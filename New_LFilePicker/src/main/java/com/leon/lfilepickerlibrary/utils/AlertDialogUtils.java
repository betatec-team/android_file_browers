package com.leon.lfilepickerlibrary.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;

import com.leon.lfilepickerlibrary.R;


public class AlertDialogUtils {
    public synchronized static void showDialog(Activity activiity, String title, String message, View view, DialogInterface.OnClickListener... dialogListener) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activiity);
        dialog.setTitle(title);
        dialog.setMessage(message);
        if (view!=null){
            dialog.setView(view);
        }
        dialog.setPositiveButton(activiity.getResources().getString(R.string.lfile_OK), dialogListener[0]);
        if (dialogListener.length <= 1 ){
            dialog.setNegativeButton(activiity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }else {
            dialog.setNegativeButton(activiity.getResources().getString(R.string.cancel), dialogListener[1]);

        }
        dialog.show();
    }
}
