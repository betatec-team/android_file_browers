package com.leon.lfilepickerlibrary.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wangy.new_lfilepicker.R;
import com.leon.lfilepickerlibrary.adapter.PathAdapter;
import com.leon.lfilepickerlibrary.filter.LFileFilter;
import com.leon.lfilepickerlibrary.model.ParamEntity;
import com.leon.lfilepickerlibrary.utils.AlertDialogUtils;
import com.leon.lfilepickerlibrary.utils.Constant;
import com.leon.lfilepickerlibrary.utils.FileUtils;
import com.leon.lfilepickerlibrary.utils.StringUtils;
import com.leon.lfilepickerlibrary.widget.EmptyRecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;


public class LFilePickerActivity extends AppCompatActivity {
    private EmptyRecyclerView mRecylerView;
    private View mEmptyView;
    private TextView mTvPath, mTvBack;
    public Button mBtnAddBook;
    private String mPath;
    private List<File> mListFiles;
    public ArrayList<String> mListNumbers = new ArrayList<String>();//存放选中 文件条目的数据地址
    public ArrayList<String> mListBoxNumbers = new ArrayList<String>();//存放选中文件夹条目的数据地址
    private PathAdapter mPathAdapter;
    private Toolbar mToolbar;
    public ParamEntity mParamEntity;
    private LFileFilter mFilter;
    public boolean mIsAllSelected = false;
    private boolean mClearData = true;
    private Menu mMenu;
    private PopupWindow pw;
    private String defultBackPath;
    private boolean LIST_SHOW = true;
    private int currentPostion = 0;
    private File currentFiles;
    private RelativeLayout rlCopy;
    private TextView tvTags, tvCancel, tvCopy;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mParamEntity = (ParamEntity) Objects.requireNonNull(getIntent().getExtras()).getSerializable("param");
        assert mParamEntity != null;
        setTheme(mParamEntity.getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lfile_picker);
        defultBackPath = mParamEntity.getDefultPath() ;
        initView();

        initToolbar();
        updateAddButton();
        if (!checkSDState()) {
            Toast.makeText(this, R.string.lfile_NotFoundPath, Toast.LENGTH_SHORT).show();
            return;
        }
        initData();
        mTvPath.setText(mPath);
        mFilter = new LFileFilter(mParamEntity.getFileTypes());
        mListFiles = FileUtils.getFileList(mPath, mFilter, mParamEntity.isGreater(), mParamEntity.getFileSize(), mParamEntity.getEndPath());
        getCurrentPostion();
        mPathAdapter = new PathAdapter(mListFiles, this, mFilter, mParamEntity.isMutilyMode(), mParamEntity.isMutilyBoxMode(), mParamEntity.isGreater(), mParamEntity.getFileSize());
        if (mParamEntity.isListFifter()) {
            mRecylerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            mRecylerView.setLayoutManager(new GridLayoutManager(this, mParamEntity.getGrideFifterNum()));
        }
        mPathAdapter.setmIconStyle(mParamEntity.getIconStyle());
        mRecylerView.setAdapter(mPathAdapter);
        moveToPosition(currentPostion, mRecylerView, new LinearLayoutManager(this));
        mRecylerView.setmEmptyView(mEmptyView);
        initListener();
    }

    private void moveToPosition(int currentPostion, RecyclerView mRecyclerVie, LinearLayoutManager manager) {
        int firstItem = manager.findFirstVisibleItemPosition();
        int lastItem = manager.findLastVisibleItemPosition();
        if (currentPostion <= firstItem) {
            mRecyclerVie.scrollToPosition(currentPostion);
        } else if (currentPostion <= lastItem) {
            int top = mRecyclerVie.getChildAt(currentPostion - firstItem).getTop();
            mRecyclerVie.scrollBy(0, top);
        } else {
            mRecyclerVie.scrollToPosition(currentPostion);
        }
    }

    private void getCurrentPostion() {
        if (currentFiles != null && currentFiles.exists()) {
            for (int i = 0; i < mListFiles.size(); i++) {
                File file = mListFiles.get(i);
                if (file.getPath().equals(currentFiles.getPath())) {
                    // 判断路径
                    currentPostion = i;
                }

            }
        }

    }

    /**
     * 设置默认的位置
     */
    private void initData() {
        String currentPath = mParamEntity.getCurrentPath();
        if (currentPath != null) {

            currentFiles = new File(currentPath);
            if (currentFiles.exists()) {
                String parent = currentFiles.getParent();
                // 获取当前的路径
                String path = mParamEntity.getPath();
                if (parent.equals(path)) {
                    // 加载当前的路径
                    mPath = "/" + path;
                } else {
                    mPath = "/" + parent;
                }
            } else {
                mPath = "/" + mParamEntity.getPath();
                if (StringUtils.isEmpty(mPath)) {
                    //如果没有指定路径，则使用默认路径
                    mPath = "/" + Environment.getExternalStorageDirectory().getAbsolutePath();
                }
            }
        } else {
            mPath = "/" + mParamEntity.getPath();
            if (StringUtils.isEmpty(mPath)) {
                //如果没有指定路径，则使用默认路径
                mPath = "/" + Environment.getExternalStorageDirectory().getAbsolutePath();
            }
        }

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
        switch (mParamEntity.getBackIcon()) {
            case Constant.BACKICON_STYLEONE:
                mToolbar.setNavigationIcon(R.mipmap.lfile_back1);
                break;
            case Constant.BACKICON_STYLETWO:
                mToolbar.setNavigationIcon(R.mipmap.lfile_back2);
                break;
            case Constant.BACKICON_STYLETHREE:
                //默认风格
                break;
        }
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(v -> finish());
    }

    private void updateAddButton() {
        if (!mParamEntity.isMutilyMode() && !mParamEntity.isMutilyBoxMode()) {
            mBtnAddBook.setVisibility(View.GONE);
        }
        if (!mParamEntity.isChooseMode()) {
            mBtnAddBook.setVisibility(View.VISIBLE);
            mBtnAddBook.setText(getString(R.string.lfile_OK));
            //文件夹模式默认为单选模式
//            mParamEntity.setMutilyMode(false);
        }
    }


    /**
     * 添加点击事件处理
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("SetTextI18n")
    private void initListener() {
        Resources rescouse = getRes();
        // 返回目录上一级
        mTvBack.setOnClickListener(v -> {
            if (defultBackPath == null) {
                back();
            } else {
                if (!defultBackPath.equals(mPath) && !mPath.equals("/" + defultBackPath)) {
                    back();
                }
            }

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
//                    closeCopy();
//              todo
//                    if (falage)
//                        clearData();

                } else {
                    //如果已经选择则取消，否则添加进来
                    if (mListFiles.get(position).isFile()) {
                        if (mListNumbers.contains(mListFiles.get(position).getAbsolutePath())) {
                            mListNumbers.remove(mListFiles.get(position).getAbsolutePath());
                        } else {
                            mListNumbers.add(mListFiles.get(position).getAbsolutePath());
                        }
                    }

                    getBoxData();
                    mIsAllSelected = (mListNumbers.size() + mListBoxNumbers.size()) == mListFiles.size();
                    if (mPathAdapter != null) {
                        if ((mListNumbers.size() + mListBoxNumbers.size()) == mListFiles.size()) {
                            mPathAdapter.updateAllSelelcted(true);
                        } else if (mListNumbers.size() <= 0 && mListBoxNumbers.size() <= 0) {
                            mPathAdapter.updateAllSelelcted(false);
                        }

                    }
                    updateMenuTitle();
                    //先判断是否达到最大数量，如果数量达到上限提示，否则继续添加
                    if (mParamEntity.getMaxNum() > 0 && mListNumbers.size() > mParamEntity.getMaxNum()) {
                        Toast.makeText(LFilePickerActivity.this, R.string.lfile_OutSize, Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (mParamEntity.isMutilyBoxMode()) {
                if (mListFiles.get(position).isDirectory()) {
                    //如果当前是目录，则进入继续查看目录
                    chekInDirectory(position);
                    mPathAdapter.updateAllSelelcted(false);
                    mIsAllSelected = false;
                    updateMenuTitle();
                    mBtnAddBook.setText(getString(R.string.lfile_Selected));
//                    closeCopy();
//                todo
//                    if (falage)
//                        clearData();
                } else {
                    // 单选模式 文件
                    mListNumbers.add(mListFiles.get(position).getAbsolutePath());
                    chooseDone();

//                    // todo
//                    //如果已经选择则取消，否则添加进来
//                    if (mListNumbers.contains(mListFiles.get(position).getAbsolutePath())) {
//                        mListNumbers.remove(mListFiles.get(position).getAbsolutePath());
//                    } else {
//                        mListNumbers.add(mListFiles.get(position).getAbsolutePath());
//                    }
//                    mIsAllSelected = mListNumbers.size() == mListFiles.size();
//                    updateMenuTitle();
//                    if (mPathAdapter != null) {
//                        if (mListNumbers.size() == mListFiles.size()) {
//                            mPathAdapter.updateAllSelelcted(true);
//                        } else if (mListNumbers.size() <= 0) {
//                            mPathAdapter.updateAllSelelcted(false);
//                        }
//
//                    }
//                    updateMenuTitle();
//                    //先判断是否达到最大数量，如果数量达到上限提示，否则继续添加
//                    if (mParamEntity.getMaxNum() > 0 && mListNumbers.size() > mParamEntity.getMaxNum()) {
//                        Toast.makeText(LFilePickerActivity.this, R.string.lfile_OutSize, Toast.LENGTH_SHORT).show();
//                    }
                }
            } else {
                //单选模式直接返回
                if (mListFiles.get(position).isDirectory()) {
                    clearData();
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
            getBoxData();
            if (mParamEntity.isChooseMode() && mListNumbers.size() < 1) {
                String info = mParamEntity.getNotFoundFiles();
                if (TextUtils.isEmpty(info)) {
                    Toast.makeText(LFilePickerActivity.this, R.string.lfile_NotFoundBooks, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LFilePickerActivity.this, info, Toast.LENGTH_SHORT).show();
                }
            } else if (mParamEntity.isChooseMode() && mListBoxNumbers.size() >= 1) {
                String string = rescouse.getString(R.string.not_filebox);
                Toast.makeText(LFilePickerActivity.this, string, Toast.LENGTH_SHORT).show();
            } else {
                //返回
                chooseDone();
            }
        });
    }

    private void clearData() {
        mListNumbers.clear();
        mListBoxNumbers.clear();
    }

    private void back() {
//        closeCopy();
        String tempPath = new File(mPath).getParent();
        if (tempPath == null) {
            return;
        }
        mPath = tempPath;
        mListFiles = FileUtils.getFileList(mPath, mFilter, mParamEntity.isGreater(), mParamEntity.getFileSize(), mParamEntity.getEndPath());
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

    private void getBoxData() {
        if (mPathAdapter != null) {
            LinkedList<Integer> fileBoxList = mPathAdapter.fileBoxList;
            mListBoxNumbers.clear();
            for (int i = 0; i < fileBoxList.size(); i++) {
                File file = mListFiles.get(fileBoxList.get(i));
                if (file.isDirectory()) {
                    mListBoxNumbers.add(file.getPath());
                }

            }
        }
    }


    /**
     * 点击进入目录*
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
        mListFiles = FileUtils.getFileList(mPath, mFilter, mParamEntity.isGreater(), mParamEntity.getFileSize(), mParamEntity.getEndPath());
        mPathAdapter.setmListData(mListFiles);
        mPathAdapter.notifyDataSetChanged();
    }

    /**
     * 完成提交
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void chooseDone() {
        Resources rescouse = getRes();
        //判断是否数量符合要求
        if (mParamEntity.isChooseMode()) {
            if (mParamEntity.getMaxNum() > 0 && mListNumbers.size() > mParamEntity.getMaxNum()) {
                Toast.makeText(LFilePickerActivity.this, R.string.lfile_OutSize, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (mParamEntity.isChooseMode()) {
            if (mListNumbers.isEmpty()) {
                String string = rescouse.getString(R.string.lfile_NotFoundBooks);
                Toast.makeText(LFilePickerActivity.this, string, Toast.LENGTH_SHORT).show();
                return;
            } else {
                // 判断是单选还是多选
                if (!mParamEntity.isMutilyMode()) {
                    // 单选
                    if (mListNumbers.size() != 1) {
                        String string = rescouse.getString(R.string.selector_once);
                        Toast.makeText(LFilePickerActivity.this, string, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        } else {
            if (mListBoxNumbers.isEmpty()) {
                String string = rescouse.getString(R.string.selector_box);
                Toast.makeText(LFilePickerActivity.this, string, Toast.LENGTH_SHORT).show();
                return;
            } else {
                // 判断是单选还是多选
                if (!mParamEntity.isMutilyMode()) {

                    if (mListBoxNumbers.size() != 1) {
                        String string = rescouse.getString(R.string.no_selector_box);
                        Toast.makeText(LFilePickerActivity.this, string, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        }
        Intent intent = new Intent();
        if (mParamEntity.isChooseMode()) {
            for (int i = 0; i < mListNumbers.size(); i++) {
                String path = mListNumbers.get(i);
                String parent = new File(path).getParent();
                if (!parent.equals(mTvPath.getText())) {
                    mListNumbers.remove(i);
                }

            }
            intent.putStringArrayListExtra("paths", mListNumbers);
        } else {
            for (int i = 0; i < mListBoxNumbers.size(); i++) {
                String path = mListBoxNumbers.get(i);
                String parent = new File(path).getParent();
                if (!parent.equals(mTvPath.getText())) {
                    mListBoxNumbers.remove(i);
                }

            }
            intent.putStringArrayListExtra("paths", mListBoxNumbers);
        }
        intent.putExtra("path", mTvPath.getText().toString().trim());
        setResult(RESULT_OK, intent);
        this.finish();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mRecylerView = findViewById(R.id.recylerview);

        rlCopy = findViewById(R.id.rl_life_copy);
        tvTags = findViewById(R.id.tv_life_tags);
        tvCancel = findViewById(R.id.tv_life_cancel);
        tvCopy = findViewById(R.id.tv_life_copy);
        mTvPath = findViewById(R.id.tv_path);
        mTvBack = findViewById(R.id.tv_back);
        mBtnAddBook = findViewById(R.id.btn_addbook);
        mEmptyView = findViewById(R.id.empty_view);
        mToolbar = findViewById(R.id.toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
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
        updateOptionsMenu();
        return true;
    }

    /**
     * 更新选项菜单展示，如果是单选模式，不显示全选操作
     */
    private void updateOptionsMenu() {
        mMenu.findItem(R.id.action_selecteall_cancel).setVisible(mParamEntity.isMutilyMode() && mParamEntity.isMutilyBoxMode());
        mMenu.findItem(R.id.action_selecte_create).setVisible(mParamEntity.isCreate());
        mMenu.findItem(R.id.action_selecte_rename).setVisible(mParamEntity.isMutilyMode() && mParamEntity.isReName());
        mMenu.findItem(R.id.action_selecte_del).setVisible(mParamEntity.isMutilyMode() && mParamEntity.isDel());
        mMenu.findItem(R.id.action_selecte_copy).setVisible(mParamEntity.isMutilyMode() && mParamEntity.isCopy());
        mMenu.findItem(R.id.action_selecte_move).setVisible(mParamEntity.isMutilyMode() && mParamEntity.isMove());
        mMenu.findItem(R.id.action_list).setVisible(mParamEntity.isShowFifter());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("SetTextI18n")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_selecteall_cancel) {
            //将当前目录下所有文件选中或者取消
            mPathAdapter.updateAllSelelcted(!mIsAllSelected);
            mIsAllSelected = !mIsAllSelected;
//            clearDataAll();
            // 为了防止之前有复制的操作
            mListNumbers.clear();
            mListBoxNumbers.clear();
            if (mIsAllSelected) {
                // 全选
                item.setIcon(R.drawable.check_selector);
                for (File mListFile : mListFiles) {
                    //不包含再添加，避免重复添加
                    if (!mListFile.isDirectory() && !mListNumbers.contains(mListFile.getAbsolutePath())) {
                        // 是文件
                        mListNumbers.add(mListFile.getAbsolutePath());
                    } else if (mListFile.isDirectory() && !mListBoxNumbers.contains(mListFile.getAbsolutePath())) {
                        // 是文件夹
                        mListBoxNumbers.add(mListFile.getAbsolutePath());
                    }
                }
                if (mParamEntity.getAddText() != null) {
                    mBtnAddBook.setText(mParamEntity.getAddText() + "( " + (mListNumbers.size() + mListBoxNumbers.size()) + " )");
                } else {
                    mBtnAddBook.setText(getString(R.string.lfile_Selected) + "( " + (mListNumbers.size() + mListBoxNumbers.size()) + " )");
                }
            } else {
                // 去除全选
                item.setIcon(R.drawable.check_normal);
                mListNumbers.clear();
                mBtnAddBook.setText(getString(R.string.lfile_Selected));
            }
            updateMenuTitle();
            closeCopy();
        } else if (item.getItemId() == R.id.action_selecte_create) {
            closeCopy();
            create();
        } else if (item.getItemId() == R.id.action_selecte_rename) {
            closeCopy();
            reName();
        } else if (item.getItemId() == R.id.action_selecte_del) {
            closeCopy();
            delFile();

        } else if (item.getItemId() == R.id.action_selecte_copy) {
            copyOrMoveFile(0);
        } else if (item.getItemId() == R.id.action_selecte_move) {
            copyOrMoveFile(1);

        } else if (item.getItemId() == R.id.action_list) {
            LIST_SHOW = !LIST_SHOW;
            if (LIST_SHOW) {
                item.setIcon(R.drawable.list);
                mRecylerView.setLayoutManager(new LinearLayoutManager(this));
            } else {
                item.setIcon(R.drawable.tab);
                mRecylerView.setLayoutManager(new GridLayoutManager(this, mParamEntity.getGrideFifterNum()));
            }
        }
        return true;
    }

    private void closeCopy() {
        rlCopy.setVisibility(View.GONE);
        mBtnAddBook.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void copyOrMoveFile(int mode) {
        Resources rescouse = getRes();
        getBoxData();
        if (mListNumbers != null | mListBoxNumbers != null) {
            mClearData = false;
            copyOrMoveDialog(mode);
        } else {
            // 提示

            String youCheck = rescouse.getString(R.string.you_check);
            String youCheckEnd = rescouse.getString(R.string.you_check_end);
            Toast.makeText(LFilePickerActivity.this, youCheck + (mListNumbers == null ? 0 : mListNumbers.size()) + youCheckEnd, Toast.LENGTH_SHORT).show();
        }
    }

    ArrayList<String> mNewListBoxNumbers;
    ArrayList<String> mNewListNumbers;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("SetTextI18n")
    private void copyOrMoveDialog(int mode) {
        Resources rescouse = getRes();
        clearLastFileData(mListBoxNumbers);
        clearLastFileData(mListNumbers);
        mNewListBoxNumbers = mListBoxNumbers;
        mNewListNumbers = mListNumbers;
        String copy = rescouse.getString(R.string.copy);
        String cancel = rescouse.getString(R.string.cancel);
        String move = rescouse.getString(R.string.move);
//        mBtnAddBook.setText(getString(R.string.lfile_Selected));
//        Dialog dialog = new Dialog(this);
//        LayoutInflater inflater = LayoutInflater.from(this);
//        View view = inflater.inflate(R.layout.copy_dialog, null);
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 40);
//        dialog.setContentView(view, layoutParams);
//        TextView tvContent = view.findViewById(R.id.tv_copy_num);
//        TextView tvCacncel = view.findViewById(R.id.tv_copy_close);
//        TextView tvOK = view.findViewById(R.id.tv_copy);
//        tvContent.setText((mode == 0 ? copy + "：" : move + "：") + mNewListBoxNumbers.size() +
//                rescouse.getString(R.string.file_box_num) + "，" + mNewListNumbers.size() + rescouse.getString(R.string.file_num));
//        dialog.show();
        mBtnAddBook.setVisibility(View.GONE);
        rlCopy.setVisibility(View.VISIBLE);
        tvTags.setText((mode == 0 ? copy : move) + "：" + mNewListBoxNumbers.size() + rescouse.getString(R.string.file_box_num)
                + "，" + mNewListNumbers.size() + rescouse.getString(R.string.file_num));
        tvCancel.setText(cancel);
        tvCopy.setText(copy);
        tvCancel.setOnClickListener((v) -> {
            // 清空选择
            clearDataAll();
            closeCopy();
        });
        tvCopy.setOnClickListener((v) -> {
            if (mNewListNumbers != null) {
                //1.判断复制的文件是否是当前的路径
                for (String path : mNewListNumbers) {
                    if (mPath.equals(new File(path).getAbsolutePath())) {
                        Toast.makeText(LFilePickerActivity.this, rescouse.getString(R.string.donot_worry), Toast.LENGTH_LONG).show();
                        return;
                    }

                }
                // 将文件/文件夹 复制到当前的位置
                for (String path : mNewListNumbers) {
                    try {
                        File file = new File(path);
                        FileUtils.copyFile(file, new File(mPath, file.getName()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // 将文件夹复制到当前的位置
                for (String path : mNewListBoxNumbers) {
                    try {
                        FileUtils.copyFolder(this, path, new File(mPath).getPath(), mParamEntity.getLocacalLanguage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (mode == 1) {
                    for (String path : mNewListNumbers) {
                        try {
                            FileUtils.deleteDir(path);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    for (String path : mNewListBoxNumbers) {
                        try {
                            FileUtils.deleteDir(path);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                notityUI();
            }
            clearData();
            closeCopy();
        });
//        Snackbar snackbar =
//                Snackbar.make(findViewById(R.id.cool_layout), (mode == 0 ? copy + "：" : move + "：") + mNewListBoxNumbers.size() +
//                                rescouse.getString(R.string.file_box_num) + "，" + mNewListNumbers.size() + rescouse.getString(R.string.file_num),
//                        BaseTransientBottomBar.LENGTH_INDEFINITE)
//                        .setAction(copy, v -> {
//                            if (mNewListNumbers != null) {
//                                //1.判断复制的文件是否是当前的路径
//                                for (String path : mNewListNumbers) {
//                                    if (mPath.equals(new File(path).getAbsolutePath())) {
//                                        ToastUtils.showShort(rescouse.getString(R.string.donot_worry));
//                                        return;
//                                    }
//
//                                }
//                                // 将文件/文件夹 复制到当前的位置
//                                for (String path : mNewListNumbers) {
//                                    try {
//                                        File file = new File(path);
//                                        FileUtils.copyFile(file, new File(mPath, file.getName()));
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                                // 将文件夹复制到当前的位置
//                                for (String path : mNewListBoxNumbers) {
//                                    try {
//                                        FileUtils.copyFolder(this, path, new File(mPath).getPath(), mParamEntity.getLocacalLanguage());
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                                if (mode == 1) {
//                                    for (String path : mNewListNumbers) {
//                                        try {
//                                            FileUtils.deleteDir(path);
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                    for (String path : mNewListBoxNumbers) {
//                                        try {
//                                            FileUtils.deleteDir(path);
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                }
//                                notityUI();
//                            }
//                            clearData();
//                        });
//        snackbar.getView()
//                .setBackgroundColor(rescouse.getColor(android.R.color.white));
//        snackbar.setTextColor(rescouse.getColor(android.R.color.black));
//        snackbar.show();

    }

    private void clearLastFileData(ArrayList<String> fileList) {
        for (int i = 0; i < fileList.size(); i++) {
            String path = fileList.get(i);
            File file = new File(path);
            if (!file.exists() | !file.getParent().equals(mTvPath.getText().toString().trim()))
                fileList.remove(file);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("SetTextI18n")
    private void delFile() {
        Resources rescouse = getRes();
        getBoxData();
        if (mListNumbers.size() > 0 | mListBoxNumbers.size() > 0) {
            //todo 关闭
//            clearDataAll();
            clearLastFileData(mListBoxNumbers);
            clearLastFileData(mListNumbers);
            AlertDialogUtils.showDialog(this, null, rescouse.getString(R.string.delete_all), null, (dialog, which) -> {
                // 删除文件夹
                if (mListBoxNumbers != null) {
                    for (String path : mListBoxNumbers) {
                        FileUtils.deleteDir(path);
                    }
                    mListBoxNumbers.clear();
                }
                if (mListNumbers != null) {
                    for (String path : mListNumbers) {
                        FileUtils.deleteDir(path);
                    }
                    mListNumbers.clear();
                }
                if (mPathAdapter != null) {
                    mPathAdapter.updateAllSelelcted(false);
                }
                // 刷新界面
                notityUI();
                if (mParamEntity.getAddText() != null) {
                    mBtnAddBook.setText(mParamEntity.getAddText() + "( 0 )");
                } else {
                    mBtnAddBook.setText(getString(R.string.lfile_Selected) + "( 0 )");
                }
                dialog.dismiss();
            });
        } else {
            Toast.makeText(LFilePickerActivity.this, rescouse.getString(R.string.check_zero), Toast.LENGTH_LONG).show();
        }


    }

    private void clearDataAll() {
        mListNumbers.clear();
        mListBoxNumbers.clear();
        mIsAllSelected = false;
        if (mPathAdapter != null) {
            mPathAdapter.notifyDataSetChanged();
        }
        updateMenuTitle();
    }

    /**
     * 重命名的方法
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void reName() {
        Resources rescouse = getRes();
        getBoxData();
        // todo
//        mListNumbers.clear();
//        mListBoxNumbers.clear();
//        mIsAllSelected = false;
//        if (mPathAdapter!=null){
//            mPathAdapter.notifyDataSetChanged();
//        }
//        updateMenuTitle();

        if (mListNumbers != null && mListNumbers.size() == 1 && (mListBoxNumbers == null | mListBoxNumbers.size() <= 0)) {
            File file = new File(mListNumbers.get(0));
            retNameAlertDialog(file.getName(), file.getParent());
        } else {
            assert mListNumbers != null;
            if (mListBoxNumbers != null && mListBoxNumbers.size() == 1 && mListNumbers.size() <= 0) {
                clearLastFileData(mListBoxNumbers);
                clearLastFileData(mListNumbers);
                File file = new File(mListBoxNumbers.get(0));
                retNameAlertDialog(file.getName(), file.getParent());
            } else {
                // 提示
                assert mListBoxNumbers != null;
                Toast.makeText(LFilePickerActivity.this, rescouse.getString(R.string.you_check) + (mListNumbers.size() + mListBoxNumbers.size()) + rescouse.getString(R.string.update_name_end), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 重命名的提示框
     *
     * @param texts 名称
     * @param path  路径
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("SetTextI18n")
    private void retNameAlertDialog(final String texts, final String path) {
        @SuppressLint("InflateParams")
        Resources rescouse = getRes();
        View view = LayoutInflater.from(this).inflate(R.layout.itaam_rename, null);
        final EditText etReName = view.findViewById(R.id.et_iteam_rename);
        etReName.setText(texts);
        AlertDialogUtils.showDialog(this, null, null, view, (dialog, which) -> {
            String reName = etReName.getText().toString().trim();
            if (texts.equals(reName)) {
                Log.d("Tag", rescouse
                        .getString(R.string.update_name_error));
            } else {
                // 更改名称
                FileUtils.reName(new File(path, texts), new File(path, reName));
                // 判断当前的文件是否存在
                Log.d("Tag", rescouse
                        .getString(R.string.update_name_success));
                if (mParamEntity.getAddText() != null) {
                    mBtnAddBook.setText(mParamEntity.getAddText() + "( " + 0 + " )");
                } else {
                    mBtnAddBook.setText(getString(R.string.lfile_Selected) + "( " + 0 + " )");
                }
                // 刷新界面
                notityUI();
                if (mPathAdapter != null) {
                    mPathAdapter.updateAllSelelcted(false);
                }
                dialog.dismiss();

            }

        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint({"InflateParams", "RtlHardcoded"})
    private void create() {
        if (pw != null && pw.isShowing()) {
            pw.dismiss();
        } else {
            // 按钮的点击事件
            pw = new PopupWindow(LFilePickerActivity.this);
            View view = LayoutInflater.from(this).inflate(R.layout.iteam_checks, null);
            TextView tvCreateFile = view.findViewById(R.id.tv_checks_create);
            TextView tvCreateFileBox = view.findViewById(R.id.tv_checks_creates);
            pw.setWidth(300);
            pw.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            // 创建文件的操作
            tvCreateFile.setOnClickListener(v -> showCorrespondingDialog("file"));
            // 创建文件夹的操作
            tvCreateFileBox.setOnClickListener(v -> showCorrespondingDialog("fileBox"));
            pw.setContentView(view);
            // 点击外部可以消失
            pw.setOutsideTouchable(true);
            pw.setFocusable(true);
            // 点击外部不可以消失
//            pw.setTouchable(true);
            pw.showAtLocation(LayoutInflater.from(LFilePickerActivity.this).inflate(R.layout.activity_lfile_picker, null, false), Gravity.RIGHT, 0, -125);
//            mPathAdapter.
            // 去除所有的选中
            //todo
//            mListNumbers.clear();
//            mListBoxNumbers.clear();
//            mIsAllSelected = false;
//            if (mPathAdapter!=null){
//                mPathAdapter.notifyDataSetChanged();
//            }
//            updateMenuTitle();
//            mBtnAddBook.setText(getString(R.string.lfile_Selected));

        }
    }

    /**
     * 创建文件/文件夹的方法
     *
     * @param type 传递文件（“file”）或者是文件夹（“fileBox”），弹窗显示
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showCorrespondingDialog(final String type) {
        Resources rescouse = getRes();
        if (pw != null && pw.isShowing()) pw.dismiss();
        final int types = type.equals("file") ? 1 : 2;
        AlertDialog.Builder dialog = new AlertDialog.Builder(LFilePickerActivity.this);
        dialog.setTitle(rescouse.getString(R.string.tips));
        View view = LayoutInflater.from(this).inflate(R.layout.iteam_create_file_filebox, null);
        dialog.setView(view);
        EditText etCreate = view.findViewById(R.id.et_iteam_create);
        etCreate.setHint(types == 1 ? rescouse.getString(R.string.create_file) : rescouse.getString(R.string.create_file));
        dialog.setCancelable(false);
        dialog.setNegativeButton(rescouse.getString(R.string.cancel), (dialog12, which) -> dialog12.dismiss());
        dialog.setPositiveButton(rescouse.getString(R.string.lfile_OK), (dialog1, which) -> {
            // 创建文件/ 文件夹的方法
            String fileOrBoxName = etCreate.getText().toString().trim();
            if (fileOrBoxName.isEmpty()) {
                Toast.makeText(LFilePickerActivity.this, rescouse.getString(R.string.inout_str), Toast.LENGTH_LONG).show();
            } else {
                dialog1.dismiss();
                FileUtils.createFile(this, mPath, fileOrBoxName, types, mParamEntity.getLocacalLanguage());
                //todo  清空选中

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
            mMenu.findItem(R.id.action_selecteall_cancel).setIcon(R.drawable.check_selector);
            mMenu.getItem(0).setTitle(getString(R.string.lfile_Cancel));
        } else {
            mMenu.findItem(R.id.action_selecteall_cancel).setIcon(R.drawable.check_normal);
            mMenu.getItem(0).setTitle(getString(R.string.lfile_SelectAll));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (defultBackPath == null) {
                back();
            } else {
                if (!defultBackPath.equals(mPath) && !mPath.equals("/" + defultBackPath)) {
                    back();
                }
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public Resources getRes() {
        Resources rescouse = getResources();
//        if (mParamEntity.getLocacalLanguage() !=null){
//            Configuration config = rescouse.getConfiguration();
//            DisplayMetrics dm = rescouse.getDisplayMetrics();
//            config.setLocale(mParamEntity.getLocacalLanguage());
//            rescouse.updateConfiguration(config, dm);
//        }
        return rescouse;

    }
}
