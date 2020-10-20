package com.wangy.myapplication

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.wangy.myapplication.adapater.CustomAdapater
import com.wangy.myapplication.adapater.viewhodel.CustomViewHodel
import com.wangy.myapplication.databinding.ActivityMainBinding
import com.wangy.new_lfilepicker.lfilepickerlibrary.LFilePicker
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private val REQUESTCODE_FROM_FRAGMENT = 1001
    var mBinding: ActivityMainBinding? = null
    var adapater: CustomAdapater<String>? = null
    private var showData = arrayListOf<String>("返回的路径是", "返回的文件是")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding?.root)
        initPro()
        initView()
        initListenter()


    }

    private fun initView() {
        mBinding?.rvLayoutContent?.layoutManager = LinearLayoutManager(this)

        adapater = object : CustomAdapater<String>(R.layout.iteam_show, showData, this) {
            override fun convert(holder: CustomViewHodel?, data: String?, position: Int) {
                holder?.setText(R.id.tv_content, data)
            }

        }
        mBinding?.rvLayoutContent?.adapter = adapater
    }

    private fun initListenter() {
        mBinding?.btnSkip?.setOnClickListener {
            LFilePicker()
                .withActivity(this)
                .withRequestCode(REQUESTCODE_FROM_FRAGMENT) //.withStartPath("/storage/emulated/0/Download")
                .withStartPath(
                    mBinding?.etPath?.text?.trim().toString()
                ) //.withFileFilter(new String[]{""})
                // 设置显示类型，默认全选
                .withEndPath(mBinding?.etShowType?.text?.trim().toString())
                // 返回的是文件还是文件夹
                .withChooseMode(mBinding?.cbMainType?.isChecked as Boolean)
                //  多选 or 单选
                .withMutilyMode(mBinding?.cbMainOnly?.isChecked as Boolean)
                // 是否是文件夹选择模式
                .withMutilyBoxMode(mBinding?.cbMainBox?.isChecked as Boolean)
                // 设置显示文件过滤  如果打开表示可以切换 list/ gride 模式
                .withShowFifter(mBinding?.cbMainLists?.isChecked as Boolean)
                // 对于文件的各种操作是否显示 创建 修改名称 删除 复制 移动
                .withSetCreate(mBinding?.cbMainCreate?.isChecked as Boolean)
                .withSetReName(mBinding?.cbMainRename?.isChecked as Boolean)
                .withSetDel(mBinding?.cbMainDel?.isChecked as Boolean)
                .withSetCopy(mBinding?.cbMainCopy?.isChecked as Boolean)
                .withSetMove(mBinding?.cbMainMove?.isChecked as Boolean)
                // 设置过滤模式
                //            .withIsGreater(true)
                //                // 过滤指定文件的大侠
                //            .withFileSize(500 * 1024.toLong())
                .start()
        }
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
                            com.wangy.new_lfilepicker.lfilepickerlibrary.utils.AlertDialogUtils.showDialog(this@MainActivity, "提示",
                                "请给您的应用授予权限，否则无法执行之后的操作！！！", null,
                                DialogInterface.OnClickListener { _, _ -> // 重新请求
                                    PermissionUtils.permission(PermissionConstants.STORAGE)
                                        .callback(
                                            object : PermissionUtils.FullCallback {
                                                override fun onGranted(granted: List<String?>) {

                                                }

                                                override fun onDenied(
                                                    deniedForever: List<String?>,
                                                    denied: List<String?>
                                                ) {
                                                    exitProcess(0)
                                                }
                                            }).request()
                                }, DialogInterface.OnClickListener { _, _ -> // 退出应用
                                    exitProcess(0)
                                })
                        }
                    }).request()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUESTCODE_FROM_FRAGMENT && resultCode == Activity.RESULT_OK) {
            // 获取intent
            if (data != null) {
                val paths = data.getStringExtra("path")
                val arrayPath = data.getStringArrayListExtra("paths")
                showData.clear()
                showData.add("返回的路径是")
                if (paths.isNotEmpty()) {
                    showData.add(paths)
                }
                showData.add("返回的文件是")
                if (arrayPath != null && arrayPath.isNotEmpty()) {
                    showData.addAll(arrayPath)
                }
                if (adapater != null) {
                    adapater?.notifyDataSetChanged()
                }


            }
        }
    }


}