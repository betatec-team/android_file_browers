package com.wangy.myapplication

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.wangy.myapplication.lfilepickerlibrary.LFilePicker

class MainActivity : AppCompatActivity() {
    private val REQUESTCODE_FROM_FRAGMENT = 1001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initPro()
    }

    /**
     * 给应用授权的方法
     */
    private fun initPro() {
        // 继续执行之后的操作
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!PermissionUtils.isGranted(PermissionConstants.STORAGE)) {
                PermissionUtils.permission(PermissionConstants.STORAGE)
                    .callback(object : PermissionUtils.FullCallback {
                        override fun onGranted(granted: List<String?>) {

                        }

                        override fun onDenied(
                            deniedForever: List<String?>,
                            denied: List<String?>
                        ) {
                            //用户选择了禁止不再询问
                            AlertDialogUtils.showDialog(this@MainActivity, "提示",
                                "请给您的应用授予权限，否则无法执行之后的操作！！！", null,
                                DialogInterface.OnClickListener { dialog, which -> // 重新请求
                                    PermissionUtils.permission(PermissionConstants.STORAGE)
                                        .callback(
                                            object : PermissionUtils.FullCallback {
                                                override fun onGranted(granted: List<String?>) {

                                                }

                                                override fun onDenied(
                                                    deniedForever: List<String?>,
                                                    denied: List<String?>
                                                ) {
                                                    System.exit(0)
                                                }
                                            }).request()
                                }, DialogInterface.OnClickListener { dialog, which -> // 退出应用
                                    System.exit(0)
                                })
                        }
                    }).request()
            }
        }
    }
    fun open(view: View) {
        // 授权

        LFilePicker()
            .withActivity(this)
            .withRequestCode(REQUESTCODE_FROM_FRAGMENT) //.withStartPath("/storage/emulated/0/Download")
            .withStartPath("/mnt/sdcard") //.withFileFilter(new String[]{""})
            .withIsGreater(false)
            .withChooseMode(true)
            .withMutilyBoxMode(true)
            .withMutilyMode(true)
            .withSetCreate(true)
            .withSetReName(true)
            .withSetDel(true)
            .withSetCopy(true)
            .withSetMove(true)
            .withFileSize(500 * 1024.toLong())
            .start()
    }
}