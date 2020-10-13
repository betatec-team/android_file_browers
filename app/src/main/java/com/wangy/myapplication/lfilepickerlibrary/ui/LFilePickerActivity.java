package com.wangy.myapplication.lfilepickerlibrary.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.wangy.myapplication.AlertDialogUtils;
import com.wangy.myapplication.R;
import com.wangy.myapplication.databinding.IteamChecksBinding;
import com.wangy.myapplication.databinding.IteamCreateFileFileboxBinding;
import com.wangy.myapplication.lfilepickerlibrary.adapter.PathAdapter;
import com.wangy.myapplication.lfilepickerlibrary.filter.LFileFilter;
import com.wangy.myapplication.lfilepickerlibrary.model.ParamEntity;
import com.wangy.myapplication.lfilepickerlibrary.utils.FileUtils;
import com.wangy.myapplication.lfilepickerlibrary.utils.StringUtils;
import com.wangy.myapplication.lfilepickerlibrary.widget.EmptyRecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class LFilePickerActivity extends AppCompatActivity {

    private final String TAG = "FilePickerLeon";
    private EmptyRecyclerView mRecylerView;
    private View mEmptyView;
    private TextView mTvPath, mTvBack;
    private Button mBtnAddBook;
    private String mPath;
    private List<File> mListFiles;
    private ArrayList<String> mListNumbers = new ArrayList<String>();//存放选中条目的数据地址
    private PathAdapter mPathAdapter;
    private Toolbar mToolbar;
    private ParamEntity mParamEntity;
    private LFileFilter mFilter;
    private boolean mIsAllSelected = false;
    private boolean mClearData = true;
    private Menu mMenu;
    private PopupWindow pw;
    private String defultBackPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mParamEntity = (ParamEntity) getIntent().getExtras().getSerializable("param");
        setTheme(mParamEntity.getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lfile_picker);

        defultBackPath = mParamEntity.getPath() == null ? mParamEntity.getDefultPath() == null ? Environment.getExternalStorageDirectory().getPath() : mParamEntity.getDefultPath() : mParamEntity.getPath();

        initView();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initToolbar();
        updateAddButton();
        if (!checkSDState()) {
            Toast.makeText(this, R.string.lfile_NotFoundPath, Toast.LENGTH_SHORT).show();
            return;
        }
        mPath = mParamEntity.getPath();
        if (StringUtils.isEmpty(mPath)) {
            //如果没有指定路径，则使用默认路径
            mPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        mTvPath.setText(mPath);
        mFilter = new LFileFilter(mParamEntity.getFileTypes());
        mListFiles = FileUtils.getFileList(mPath, mFilter, mParamEntity.isGreater(), mParamEntity.getFileSize());
        mPathAdapter = new PathAdapter(mListFiles, this, mFilter, mParamEntity.isMutilyMode(), mParamEntity.isGreater(), mParamEntity.getFileSize());
        mRecylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mPathAdapter.setmIconStyle(mParamEntity.getIconStyle());
        mRecylerView.setAdapter(mPathAdapter);
        mRecylerView.setmEmptyView(mEmptyView);
        initListener();
    }

    /**
     * 更新Toolbar展示
     */
    private void initToolbar() {
        if (mParamEntity.getTitle() != null) {
            mToolbar.setTitle(mParamEntity.getTitle());
        }
        if (mParamEntity.getTitleStyle() != 0) {
            mToolbar.setTitleTextAppearance(this, mParamEntity.getTitleStyle());
        }
        if (mParamEntity.getTitleColor() != null) {
            mToolbar.setTitleTextColor(Color.parseColor(mParamEntity.getTitleColor())); //设置标题颜色
        }
        if (mParamEntity.getBackgroundColor() != null) {
            mToolbar.setBackgroundColor(Color.parseColor(mParamEntity.getBackgroundColor()));
        }
//        switch (mParamEntity.getBackIcon()) {
//            case Constant.BACKICON_STYLEONE:
//                mToolbar.setNavigationIcon(R.mipmap.lfile_back1);
//                break;
//            case Constant.BACKICON_STYLETWO:
//                mToolbar.setNavigationIcon(R.mipmap.lfile_back2);
//                break;
//            case Constant.BACKICON_STYLETHREE:
//                //默认风格
//                break;
//        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateAddButton() {
        if (!mParamEntity.isMutilyMode()) {
            mBtnAddBook.setVisibility(View.GONE);
        }
        if (!mParamEntity.isChooseMode()) {
            mBtnAddBook.setVisibility(View.VISIBLE);
            mBtnAddBook.setText(getString(R.string.lfile_OK));
            //文件夹模式默认为单选模式
            mParamEntity.setMutilyMode(false);
        }
    }

    /**
     * 添加点击事件处理
     */
    @SuppressLint("SetTextI18n")
    private void initListener() {
        // 返回目录上一级
        mTvBack.setOnClickListener(v -> {
            back();
        });
        mPathAdapter.setOnItemClickListener(position -> {
            if (mParamEntity.isMutilyMode()) {
                if (mListFiles.get(position).isDirectory()) {
                    //如果当前是目录，则进入继续查看目录
                    chekInDirectory(position);
                    mPathAdapter.updateAllSelelcted(false);
                    mIsAllSelected = false;
                    updateMenuTitle();
                    mBtnAddBook.setText(getString(R.string.lfile_Selected));
                } else {
                    //如果已经选择则取消，否则添加进来
                    if (mListNumbers.contains(mListFiles.get(position).getAbsolutePath())) {
                        mListNumbers.remove(mListFiles.get(position).getAbsolutePath());
                    } else {
                        mListNumbers.add(mListFiles.get(position).getAbsolutePath());
                    }
                    //todo 设置数量(暂时没有分割出文件/文件夹子)
                    mIsAllSelected = mListNumbers.size() == mListFiles.size();
                    updateMenuTitle();
                    if (mPathAdapter != null) {
                        if (mListNumbers.size() == mListFiles.size()) {
                            mPathAdapter.updateAllSelelcted(true);
                        } else if (mListNumbers.size() <= 0) {
                            mPathAdapter.updateAllSelelcted(false);
                        }

                    }
                    updateMenuTitle();
                    if (mParamEntity.getAddText() != null) {
                        mBtnAddBook.setText(mParamEntity.getAddText() + "( " + mListNumbers.size() + " )");
                    } else {
                        mBtnAddBook.setText(getString(R.string.lfile_Selected) + "( " + mListNumbers.size() + " )");
                    }
                    //先判断是否达到最大数量，如果数量达到上限提示，否则继续添加
                    if (mParamEntity.getMaxNum() > 0 && mListNumbers.size() > mParamEntity.getMaxNum()) {
                        Toast.makeText(LFilePickerActivity.this, R.string.lfile_OutSize, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            } else {
                //单选模式直接返回
                if (mListFiles.get(position).isDirectory()) {
                    chekInDirectory(position);
                    return;
                }
                if (mParamEntity.isChooseMode()) {
                    //选择文件模式,需要添加文件路径，否则为文件夹模式，直接返回当前路径
                    mListNumbers.add(mListFiles.get(position).getAbsolutePath());
                    chooseDone();
                } else {
                    Toast.makeText(LFilePickerActivity.this, R.string.lfile_ChooseTip, Toast.LENGTH_SHORT).show();
                }
            }

        });

        mBtnAddBook.setOnClickListener(v -> {
            if (mParamEntity.isChooseMode() && mListNumbers.size() < 1) {
                String info = mParamEntity.getNotFoundFiles();
                if (TextUtils.isEmpty(info)) {
                    Toast.makeText(LFilePickerActivity.this, R.string.lfile_NotFoundBooks, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LFilePickerActivity.this, info, Toast.LENGTH_SHORT).show();
                }
            } else {
                //返回
                chooseDone();
            }
        });
    }

    private void back() {
        if (defultBackPath.equals(mPath)) {
            this.finish();
        }
        String tempPath = new File(mPath).getParent();
        if (tempPath == null) {
            return;
        }
        mPath = tempPath;
        mListFiles = FileUtils.getFileList(mPath, mFilter, mParamEntity.isGreater(), mParamEntity.getFileSize());
        mPathAdapter.setmListData(mListFiles);
        mPathAdapter.updateAllSelelcted(false);
        mIsAllSelected = false;
        updateMenuTitle();
        mBtnAddBook.setText(getString(R.string.lfile_Selected));
        mRecylerView.scrollToPosition(0);
        setShowPath(mPath);
        //清除添加集合中数据
        if (mClearData) {
            mListNumbers.clear();
        }

        if (mParamEntity.getAddText() != null) {
            mBtnAddBook.setText(mParamEntity.getAddText());
        } else {
            mBtnAddBook.setText(R.string.lfile_Selected);
        }
    }


    /**
     * 点击进入目录
     *
     * @param position
     */
    private void chekInDirectory(int position) {
        mPath = mListFiles.get(position).getAbsolutePath();
        setShowPath(mPath);
        //更新数据源
        notityUI();
        mRecylerView.scrollToPosition(0);
    }

    private void notityUI() {
        mListFiles = FileUtils.getFileList(mPath, mFilter, mParamEntity.isGreater(), mParamEntity.getFileSize());
        mPathAdapter.setmListData(mListFiles);
        mPathAdapter.notifyDataSetChanged();
    }

    /**
     * 完成提交
     */
    private void chooseDone() {
        //判断是否数量符合要求
        if (mParamEntity.isChooseMode()) {
            if (mParamEntity.getMaxNum() > 0 && mListNumbers.size() > mParamEntity.getMaxNum()) {
                Toast.makeText(LFilePickerActivity.this, R.string.lfile_OutSize, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Intent intent = new Intent();
        intent.putStringArrayListExtra("paths", mListNumbers);
        intent.putExtra("path", mTvPath.getText().toString().trim());
        setResult(RESULT_OK, intent);
        this.finish();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mRecylerView = findViewById(R.id.recylerview);
        mTvPath = findViewById(R.id.tv_path);
        mTvBack = findViewById(R.id.tv_back);
        mBtnAddBook = findViewById(R.id.btn_addbook);
        mEmptyView = findViewById(R.id.empty_view);
        mToolbar = findViewById(R.id.toolbar);
        if (mParamEntity.getAddText() != null) {
            mBtnAddBook.setText(mParamEntity.getAddText());
        }
    }

    /**
     * 检测SD卡是否可用
     */
    private boolean checkSDState() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 显示顶部地址
     *
     * @param path
     */
    private void setShowPath(String path) {
        mTvPath.setText(path);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_toolbar, menu);
        this.mMenu = menu;
        updateOptionsMenu(menu);
        return true;
    }

    /**
     * 更新选项菜单展示，如果是单选模式，不显示全选操作
     *
     * @param menu
     */
    private void updateOptionsMenu(Menu menu) {
        mMenu.findItem(R.id.action_selecteall_cancel).setVisible(mParamEntity.isMutilyMode());
        mMenu.findItem(R.id.action_selecte_create).setVisible(mParamEntity.isCreate());
        mMenu.findItem(R.id.action_selecte_rename).setVisible(mParamEntity.isReName());
        mMenu.findItem(R.id.action_selecte_del).setVisible(mParamEntity.isDel());
        mMenu.findItem(R.id.action_selecte_copy).setVisible(mParamEntity.isCopy());
        mMenu.findItem(R.id.action_selecte_move).setVisible(mParamEntity.isMove());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_selecteall_cancel) {
            //将当前目录下所有文件选中或者取消
            mPathAdapter.updateAllSelelcted(!mIsAllSelected);
            mIsAllSelected = !mIsAllSelected;
            if (mIsAllSelected) {
                for (File mListFile : mListFiles) {
                    //不包含再添加，避免重复添加
                    if (!mListFile.isDirectory() && !mListNumbers.contains(mListFile.getAbsolutePath())) {
                        mListNumbers.add(mListFile.getAbsolutePath());
                    }
                    if (mParamEntity.getAddText() != null) {
                        mBtnAddBook.setText(mParamEntity.getAddText() + "( " + mListNumbers.size() + " )");
                    } else {
                        mBtnAddBook.setText(getString(R.string.lfile_Selected) + "( " + mListNumbers.size() + " )");
                    }
                }
            } else {
                mListNumbers.clear();
                mBtnAddBook.setText(getString(R.string.lfile_Selected));
            }
            updateMenuTitle();
        } else if (item.getItemId() == R.id.action_selecte_create) {
            create();
        } else if (item.getItemId() == R.id.action_selecte_rename) {
            reName();

        } else if (item.getItemId() == R.id.action_selecte_del) {
            delFile();

        } else if (item.getItemId() == R.id.action_selecte_copy) {
            //todo 复制的操作,暂时只有文件的复制，没有添加对文件夹的操作
            copyOrMoveFile(0);

        } else if (item.getItemId() == R.id.action_selecte_move) {
            copyOrMoveFile(1);

        }
        return true;
    }

    private void copyOrMoveFile(int mode) {
        if (mListNumbers != null) {
            //todo  表示不清除内容
            mClearData = false;
            copyOrMoveDialog(mode);


        } else {
            // 提示
            ToastUtils.showShort("您选中的条目有" + (mListNumbers == null ? 0 : mListNumbers.size()) + "条,不符合修改名称的条件！");
        }
    }

    @SuppressLint("SetTextI18n")
    private void copyOrMoveDialog(int mode) {
        Snackbar snackbar =
                Snackbar.make(findViewById(R.id.cool_layout), (mode == 0 ? "复制" : "移动") + "：0个文件夹，" + mListNumbers.size() + "个文件", BaseTransientBottomBar.LENGTH_INDEFINITE)
                        .setAction("粘贴", v -> {
                            if (mListNumbers != null) {
                                //1.判断复制的文件是否是当前的路径
                                for (String path : mListNumbers) {
                                    if (mPath.equals(new File(path).getAbsolutePath())) {
                                        ToastUtils.showShort("不可以将当前的文件/文件夹复制在当前的位置上！");
                                        return;
                                    }

                                }
                                // 将文件/文件夹 复制到当前的位置
                                for (String path : mListNumbers) {
                                    try {
                                        File file = new File(path);
                                        FileUtils.copyFile(file, new File(mPath, file.getName()));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (mode == 1) {
                                    for (String path : mListNumbers) {
                                        try {
                                            FileUtils.deleteDir(path);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                notityUI();
                            }
                            mListNumbers.clear();
                        });
        snackbar
                .getView()
                .setBackgroundColor(getResources().getColor(android.R.color.white));
        snackbar.setTextColor(getResources().getColor(android.R.color.black));
        snackbar.show();

    }

    private void delFile() {
        if (mListNumbers != null) {
            AlertDialogUtils.showDialog(this, null, "您确定要删除这些文件吗吗？", null, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for (String path : mListNumbers) {
                        FileUtils.deleteDir(path);
                    }
                    // 刷新界面
                    notityUI();
                    dialog.dismiss();
                }
            });
        } else {
            // 提示
            ToastUtils.showShort("您选中的条目有" + (mListNumbers == null ? 0 : mListNumbers.size()) + "条,不符合修改名称的条件！");
        }

    }

    /**
     * 重命名的方法
     */
    private void reName() {
        if (mListNumbers != null && mListNumbers.size() == 1) {
            File file = new File(mListNumbers.get(0));
            retNameAlertDialog(file.getName(), file.getParent());
        } else {
            // 提示
            ToastUtils.showShort("您选中的条目有" + (mListNumbers == null ? 0 : mListNumbers.size()) + "条,不符合修改名称的条件！");
        }
    }

    /**
     * 重命名的提示框
     *
     * @param texts 名称
     * @param path  路径
     */
    private void retNameAlertDialog(final String texts, final String path) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.itaam_rename, null);
        final EditText etReName = view.findViewById(R.id.et_iteam_rename);
        etReName.setText(texts);
        AlertDialogUtils.showDialog(this, null, null, view, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String reName = etReName.getText().toString().trim();
                if (texts.equals(reName)) {
                    LogUtils.d("当前文件名称没有修改");
                } else {
                    // 更改名称
                    FileUtils.reName(new File(path, texts), new File(path, reName));
                    // 判断当前的文件是否存在
                    LogUtils.d("当前文件名称已经修改");
                    // 刷新界面
                    notityUI();
                    dialog.dismiss();

                }

            }
        });
    }

    private void create() {
        if (pw != null && pw.isShowing()) {
            pw.dismiss();
        } else {
            // 按钮的点击事件
            pw = new PopupWindow(LFilePickerActivity.this);
            IteamChecksBinding mCheckBinding = IteamChecksBinding.inflate(getLayoutInflater());
            pw.setWidth(300);
            pw.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            // 创建文件的操作
            mCheckBinding.tvChecksCreate.setOnClickListener(v -> showCorrespondingDialog("file"));
            // 创建文件夹的操作
            mCheckBinding.tvChecksCreates.setOnClickListener(v -> showCorrespondingDialog("fileBox"));
            pw.setContentView(mCheckBinding.getRoot());
            pw.setTouchable(true);
            pw.showAtLocation(LayoutInflater.from(LFilePickerActivity.this).inflate(R.layout.activity_lfile_picker, null, false), Gravity.RIGHT, 0, -125);

        }
    }

    /**
     * 创建文件/文件夹的方法
     *
     * @param type 传递文件（“file”）或者是文件夹（“fileBox”），弹窗显示
     */
    private void showCorrespondingDialog(final String type) {
        if (pw != null && pw.isShowing()) pw.dismiss();
        final int types = type.equals("file") ? 1 : 2;
        AlertDialog.Builder dialog = new AlertDialog.Builder(LFilePickerActivity.this);
        dialog.setTitle("提示");
        final IteamCreateFileFileboxBinding binding = IteamCreateFileFileboxBinding.inflate(getLayoutInflater());
        dialog.setView(binding.getRoot());
        binding.etIteamCreate.setHint(types == 1 ? "请输入您要创建的文件名称" : "请输入您要创建的文件夹名称");
        dialog.setCancelable(false);
        dialog.setNegativeButton("取消", (dialog12, which) -> dialog12.dismiss());
        dialog.setPositiveButton("确定", (dialog1, which) -> {
            // 创建文件/ 文件夹的方法
            String fileOrBoxName = binding.etIteamCreate.getText().toString().trim();
            if (fileOrBoxName.isEmpty()) {
                ToastUtils.showShort("请重新确认输入内容！");
            } else {
                dialog1.dismiss();
                FileUtils.createFile(mPath, fileOrBoxName, types);
                notityUI();
            }
        });
        dialog.show();
    }


    /**
     * 更新选项菜单文字
     */
    public void updateMenuTitle() {

        if (mIsAllSelected) {
            mMenu.getItem(0).setTitle(getString(R.string.lfile_Cancel));
        } else {
            mMenu.getItem(0).setTitle(getString(R.string.lfile_SelectAll));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            back();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
