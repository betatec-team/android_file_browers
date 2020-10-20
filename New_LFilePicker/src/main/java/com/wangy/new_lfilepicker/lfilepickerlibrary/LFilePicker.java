package com.wangy.new_lfilepicker.lfilepickerlibrary;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.StyleRes;

import com.wangy.new_lfilepicker.R;
import com.wangy.new_lfilepicker.lfilepickerlibrary.model.ParamEntity;
import com.wangy.new_lfilepicker.lfilepickerlibrary.ui.LFilePickerActivity;


/**
 * 作者：Leon
 * 时间：2017/3/20 16:57
 */
public class LFilePicker {
    private Activity mActivity;
    private Fragment mFragment;
    private Fragment mSupportFragment;
    private String mTitle;
    private String mTitleColor;
    private int theme = R.style.LFileTheme;
    private int mTitleStyle = R.style.LFileToolbarTextStyle;
    private String mBackgroundColor;
    private int mBackStyle;
    private int mRequestCode;
    private boolean mMutilyMode = true;
    private boolean mChooseMode = true;
    private boolean mMutilyBoxMode = true;
    private String mAddText;
    private int mIconStyle;
    private String[] mFileTypes;
    private String mNotFoundFiles;
    private int mMaxNum;
    private String mStartPath, defultPath;
    private boolean mIsGreater = true;//是否大于
    private boolean mCreate = true, mDel = true, mMove = true, mReName = true, mCopy = true, mShowFifter = false, mListFifter = true;
    private long mFileSize;
    private String mEndPath = "ALL";
    private int mGrideFifterNum = 3;

    /**
     * 绑定Activity
     *
     * @param activity
     * @return
     */
    public LFilePicker withActivity(Activity activity) {
        this.mActivity = activity;
        return this;
    }

    /**
     * 绑定Fragment
     *
     * @param fragment
     * @return
     */
    public LFilePicker withFragment(Fragment fragment) {
        this.mFragment = fragment;
        return this;
    }

    /**
     * 绑定v4包Fragment
     *
     * @param supportFragment
     * @return
     */
    public LFilePicker withSupportFragment(Fragment supportFragment) {
        this.mSupportFragment = supportFragment;
        return this;
    }


    /**
     * 设置主标题
     *
     * @param title
     * @return
     */
    public LFilePicker withTitle(String title) {
        this.mTitle = title;
        return this;
    }

    /**
     * 设置辩题颜色
     *
     * @param color
     * @return
     */
    @Deprecated
    public LFilePicker withTitleColor(String color) {
        this.mTitleColor = color;
        return this;
    }

    /**
     * 设置主题
     *
     * @param theme
     * @return
     */
    public LFilePicker withTheme(@StyleRes int theme) {
        this.theme = theme;
        return this;
    }

    /**
     * 设置标题的颜色和字体大小
     *
     * @param style
     * @return
     */
    public LFilePicker withTitleStyle(@StyleRes int style) {
        this.mTitleStyle = style;
        return this;
    }

    /**
     * 设置背景色
     *
     * @param color
     * @return
     */
    public LFilePicker withBackgroundColor(String color) {
        this.mBackgroundColor = color;
        return this;
    }

    /**
     * 请求码
     *
     * @param requestCode
     * @return
     */
    public LFilePicker withRequestCode(int requestCode) {
        this.mRequestCode = requestCode;
        return this;
    }

    /**
     * 设置返回图标
     *
     * @param backStyle
     * @return
     */
    public LFilePicker withBackIcon(int backStyle) {
        this.mBackStyle = 0;//默认样式
        this.mBackStyle = backStyle;
        return this;
    }

    /**
     * 设置选择模式，默认为true,多选；false为单选
     *
     * @param isMutily
     * @return
     */
    public LFilePicker withMutilyMode(boolean isMutily) {
        this.mMutilyMode = isMutily;
        return this;
    }

    /**
     * 设置多选时按钮文字
     *
     * @param text
     * @return
     */
    public LFilePicker withAddText(String text) {
        this.mAddText = text;
        return this;
    }

    /**
     * 设置文件夹图标风格
     *
     * @param style
     * @return
     */
    public LFilePicker withIconStyle(int style) {
        this.mIconStyle = style;
        return this;
    }

    /**
     * @param arrs
     * @return
     */
    public LFilePicker withFileFilter(String[] arrs) {
        this.mFileTypes = arrs;
        return this;
    }

    /**
     * 没有选中文件时的提示信息
     *
     * @param notFoundFiles
     * @return
     */
    public LFilePicker withNotFoundBooks(String notFoundFiles) {
        this.mNotFoundFiles = notFoundFiles;
        return this;
    }

    /**
     * 设置最大选中数量
     *
     * @param num
     * @return
     */
    public LFilePicker withMaxNum(int num) {
        this.mMaxNum = num;
        return this;
    }

    /**
     * 设置初始显示路径
     *
     * @param path
     * @return
     */
    public LFilePicker withStartPath(String path) {
        this.mStartPath = path;
        return this;
    }

    /**
     * 设置默认路径
     *
     * @param path
     * @return
     */
    public LFilePicker withDefultPath(String path) {
        this.defultPath = path;
        return this;
    }

    /**
     * 设置选择模式，true为文件选择模式，false为文件夹选择模式，默认为true
     *
     * @param chooseMode
     * @return
     */
    public LFilePicker withChooseMode(boolean chooseMode) {
        this.mChooseMode = chooseMode;
        return this;
    }

    /**
     *  设置 是否是文件夹选项模式，true 表示的是可以多选文件夹，false 表示不可以
     * @param mutilyBoxMode
     * @return
     */

    public LFilePicker withMutilyBoxMode(boolean mutilyBoxMode) {
        this.mMutilyBoxMode = mutilyBoxMode;
        return this;
    }

    /**
     * 设置文件大小过滤方式：大于指定大小或者小于指定大小
     *
     * @param isGreater true：大于 ；false：小于，同时包含指定大小在内
     * @return
     */
    public LFilePicker withIsGreater(boolean isGreater) {
        this.mIsGreater = isGreater;
        return this;
    }

    /**
     * 如果想要进行删除，修改，复制，粘贴的操作；需要设置多选模式
     * 创建文件的/文件夹的方法
     *
     * @param create
     * @return
     */

    public LFilePicker withSetCreate(boolean create) {
        this.mCreate = create;
        return this;
    }

    /**
     * 如果想要进行删除，修改，复制，粘贴的操作；需要设置多选模式
     * 重命名的方法
     */

    public LFilePicker withSetReName(boolean reName) {
        this.mReName = reName;
        return this;
    }

    /**
     * 如果想要进行删除，修改，复制，粘贴的操作；需要设置多选模式
     * 删除的方法
     */

    public LFilePicker withSetDel(boolean del) {
        this.mDel = del;
        return this;
    }

    /**
     * 如果想要进行删除，修改，复制，粘贴的操作；需要设置多选模式
     * 拷贝的方法
     */

    public LFilePicker withSetCopy(boolean copy) {
        this.mCopy = copy;
        return this;
    }

    /**
     * 如果想要进行删除，修改，复制，粘贴的操作；需要设置多选模式
     * 移动的方法
     */

    public LFilePicker withSetMove(boolean move) {
        this.mMove = move;
        return this;
    }

    /**
     * 设置过滤文件大小
     *
     * @param fileSize
     * @return
     */
    public LFilePicker withFileSize(long fileSize) {
        this.mFileSize = fileSize;
        return this;
    }

    /**
     * 设置文件的后缀名称 ，默认显示为All 表示所有文件都进行显示
     * @param endPath
     * @return
     */

    public LFilePicker withEndPath(String endPath) {
        this.mEndPath = endPath;
        return this;
    }

    /**
     * 设置显示过滤  如果打开表示可以切换 list/ gride 模式
     * @param showFifter
     * @return
     */

    public LFilePicker withShowFifter(boolean showFifter) {
        this.mShowFifter = showFifter;
        return this;
    }

    /**
     * 设置 list 显示模式
     *
     * @param listFifter
     * @return
     */

    public LFilePicker withListFifter(boolean listFifter) {
        this.mListFifter = listFifter;
        return this;
    }

    /**
     * 设置 gride 显示模式
     *
     * @param grideFifterNum
     * @return
     * @throws Exception
     */

    public LFilePicker withGrideFifterNum(int grideFifterNum) throws Exception {
        if (mGrideFifterNum <= 1) {
            throw new Exception(" Please set it more than once, or this exception will be thrown! ");
        }
        this.mGrideFifterNum = grideFifterNum;
        return this;
    }

    public void start() {

        if (mActivity == null && mFragment == null && mSupportFragment == null) {
            throw new RuntimeException("You must pass Activity or Fragment by withActivity or withFragment or withSupportFragment method");
        }
        Intent intent = initIntent();
        Bundle bundle = getBundle();
        intent.putExtras(bundle);

        if (mActivity != null) {
            mActivity.startActivityForResult(intent, mRequestCode);
        } else if (mFragment != null) {
            mFragment.startActivityForResult(intent, mRequestCode);
        } else {
            mSupportFragment.startActivityForResult(intent, mRequestCode);
        }
    }


    private Intent initIntent() {
        Intent intent;
        if (mActivity != null) {
            intent = new Intent(mActivity, LFilePickerActivity.class);
        } else if (mFragment != null) {
            intent = new Intent(mFragment.getActivity(), LFilePickerActivity.class);
        } else {
            intent = new Intent(mSupportFragment.getActivity(), LFilePickerActivity.class);
        }
        return intent;
    }

    //    @NonNull
    private Bundle getBundle() {
        ParamEntity paramEntity = new ParamEntity();
        paramEntity.setTitle(mTitle);
        paramEntity.setTheme(theme);
        paramEntity.setTitleColor(mTitleColor);
        paramEntity.setTitleStyle(mTitleStyle);
        paramEntity.setBackgroundColor(mBackgroundColor);
        paramEntity.setBackIcon(mBackStyle);
        paramEntity.setMutilyMode(mMutilyMode);
        paramEntity.setAddText(mAddText);
        paramEntity.setIconStyle(mIconStyle);
        paramEntity.setFileTypes(mFileTypes);
        paramEntity.setNotFoundFiles(mNotFoundFiles);
        paramEntity.setMaxNum(mMaxNum);
        paramEntity.setChooseMode(mChooseMode);
        paramEntity.setMutilyBoxMode(mMutilyBoxMode);
        paramEntity.setPath(mStartPath);
        paramEntity.setFileSize(mFileSize);
        paramEntity.setGreater(mIsGreater);
        paramEntity.setCreate(mCreate);
        paramEntity.setCopy(mCopy);
        paramEntity.setMove(mMove);
        paramEntity.setDel(mDel);
        paramEntity.setReName(mReName);
        paramEntity.setDefultPath(defultPath);
        paramEntity.setEndPath(mEndPath);
        paramEntity.setShowFifter(mShowFifter);
        paramEntity.setListFifter(mListFifter);
        paramEntity.setGrideFifterNum(mGrideFifterNum);
        Bundle bundle = new Bundle();
        bundle.putSerializable("param", paramEntity);
        return bundle;
    }


}
