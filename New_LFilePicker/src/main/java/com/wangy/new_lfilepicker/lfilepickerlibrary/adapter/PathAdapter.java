package com.wangy.new_lfilepicker.lfilepickerlibrary.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.wangy.new_lfilepicker.R;
import com.wangy.new_lfilepicker.lfilepickerlibrary.ui.LFilePickerActivity;
import com.wangy.new_lfilepicker.lfilepickerlibrary.utils.Constant;
import com.wangy.new_lfilepicker.lfilepickerlibrary.utils.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.blankj.utilcode.util.StringUtils.getString;

/**
 * 作者：Leon
 * 时间：2017/3/15 15:47
 */
public class PathAdapter extends RecyclerView.Adapter<PathAdapter.PathViewHolder> {
    public interface OnItemClickListener {
        void click(int position);
    }

//    public interface OnCancelChoosedListener {
//        void cancelChoosed(CheckBox checkBox);
//    }

    private final String TAG = "FilePickerLeon";
    private List<File> mListData;
    private LFilePickerActivity activity;
    public OnItemClickListener onItemClickListener;
    private FileFilter mFileFilter;
    private boolean[] mCheckedFlags;
    private boolean mMutilyMode;
    private int mIconStyle;
    private boolean mIsGreater;
    private long mFileSize;
    private boolean mMutilyBoxMode;
    public LinkedList<Integer> fileBoxList = new LinkedList();
    public ArrayList<String> mListNumbers = new ArrayList<String>();//存放选中文件条目的数据地址

    public PathAdapter(List<File> mListData, LFilePickerActivity activity, FileFilter mFileFilter, boolean mMutilyMode, boolean mMutilyBoxMode, boolean mIsGreater, long mFileSize) {
        this.mListData = mListData;
        this.activity = activity;
        this.mFileFilter = mFileFilter;
        this.mMutilyMode = mMutilyMode;
        this.mIsGreater = mIsGreater;
        this.mFileSize = mFileSize;
        this.mMutilyBoxMode = mMutilyBoxMode;
        mCheckedFlags = new boolean[mListData.size()];
    }

    @Override
    public PathViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(activity, R.layout.lfile_listitem, null);
        PathViewHolder pathViewHolder = new PathViewHolder(view);
        return pathViewHolder;
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final PathViewHolder holder, final int position) {
        final File file = mListData.get(position);
        if (file.isFile()) {
            updateFileIconStyle(holder.ivType);
            holder.tvName.setText(file.getName());
            holder.tvDetail.setText(activity.getString(R.string.lfile_FileSize) + " " + FileUtils.getReadableFileSize(file.length()));
            holder.cbChoose.setVisibility(mMutilyMode ? View.VISIBLE : View.GONE);
        } else {
            updateFloaderIconStyle(holder.ivType);
            holder.tvName.setText(file.getName());
            //文件大小过滤
            List files = FileUtils.getFileList(file.getAbsolutePath(), mFileFilter, mIsGreater, mFileSize, activity.mParamEntity.getEndPath());
            if (files == null) {
                holder.tvDetail.setText("0 " + activity.getString(R.string.lfile_LItem));
            } else {
                holder.tvDetail.setText(files.size() + " " + activity.getString(R.string.lfile_LItem));
            }
            holder.cbChoose.setVisibility(mMutilyBoxMode ? View.VISIBLE : View.GONE);
        }
        holder.layoutRoot.setOnClickListener(v -> {
            if (file.isFile() && mMutilyMode) {
                holder.cbChoose.setChecked(!holder.cbChoose.isChecked());
            }
            onItemClickListener.click(position);
        });
        holder.cbChoose.setOnClickListener(v -> {
            if (file.isFile() && mMutilyMode) {
                //同步复选框和外部布局点击的处理
                onItemClickListener.click(position);
            }

        });
        holder.cbChoose.setOnCheckedChangeListener(null);//先设置一次CheckBox的选中监听器，传入参数null
        holder.cbChoose.setChecked(mCheckedFlags[position]);//用数组中的值设置CheckBox的选中状态
        //再设置一次CheckBox的选中监听器，当CheckBox的选中状态发生改变时，把改变后的状态储存在数组中
        holder.cbChoose.setOnCheckedChangeListener((compoundButton, check) -> {
            if (file.isDirectory() && mMutilyBoxMode) {
                if (check) {
                    fileBoxList.add(position);
                } else {
                    for (int i = 0; i < fileBoxList.size(); i++) {
                        if (position == fileBoxList.get(i)) {
                            fileBoxList.remove(i);
                        }
                    }
                }
            } else if (file.isFile() && mMutilyMode) {
                if (mListNumbers.contains(mListData.get(position).getAbsolutePath())) {
                    mListNumbers.remove(mListData.get(position).getAbsolutePath());
                } else {
                    mListNumbers.add(mListData.get(position).getAbsolutePath());
                }
            }
            mCheckedFlags[position] = check;
            // 设置动态监听
            if (activity.mBtnAddBook != null) {
                // 获取Activitiy 的数量
                int num = 0;
                for (boolean mCheckedFlag : mCheckedFlags) {
                    if (mCheckedFlag) {
                        num += 1;
                    }
                }
                if (activity.mParamEntity.getAddText() != null) {
                    activity.mBtnAddBook.setText(activity.mParamEntity.getAddText() + "( " + num + " )");
                } else {
                    activity.mBtnAddBook.setText(getString(R.string.lfile_Selected) + "( " + num + " )");
                }
                // 条目的更新
                // 显示取消全选
                activity.mIsAllSelected = num == mCheckedFlags.length;
                activity.updateMenuTitle();

            }

        });
    }

    private void updateFloaderIconStyle(ImageView imageView) {
        switch (mIconStyle) {
            case Constant.ICON_STYLE_BLUE:
                imageView.setBackgroundResource(R.mipmap.lfile_folder_style_blue);
                break;
            case Constant.ICON_STYLE_GREEN:
                imageView.setBackgroundResource(R.mipmap.lfile_folder_style_green);
                break;
            case Constant.ICON_STYLE_YELLOW:
                imageView.setBackgroundResource(R.mipmap.lfile_folder_style_yellow);
                break;
        }
    }

    private void updateFileIconStyle(ImageView imageView) {
        switch (mIconStyle) {
            case Constant.ICON_STYLE_BLUE:
                imageView.setBackgroundResource(R.mipmap.lfile_file_style_blue);
                break;
            case Constant.ICON_STYLE_GREEN:
                imageView.setBackgroundResource(R.mipmap.lfile_file_style_green);
                break;
            case Constant.ICON_STYLE_YELLOW:
                imageView.setBackgroundResource(R.mipmap.lfile_file_style_yellow);
                break;
        }
    }

    /**
     * 设置监听器
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 设置数据源
     *
     * @param mListData
     */
    public void setmListData(List<File> mListData) {
        this.mListData = mListData;
        mCheckedFlags = new boolean[mListData.size()];
    }

    public void setmIconStyle(int mIconStyle) {
        this.mIconStyle = mIconStyle;
    }

    /**
     * 设置是否全选
     *
     * @param isAllSelected
     */
    public void updateAllSelelcted(boolean isAllSelected) {
        for (int i = 0; i < mCheckedFlags.length; i++) {
            mCheckedFlags[i] = isAllSelected;
        }
        if (isAllSelected) {
            for (int i = 0; i < mListData.size(); i++) {
                if (!fileBoxList.contains(i)) {
                    fileBoxList.add(i);
                }

            }
        } else {
            fileBoxList.clear();
        }

        notifyDataSetChanged();
    }

    class PathViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout layoutRoot;
        private ImageView ivType;
        private TextView tvName;
        private TextView tvDetail;
        private CheckBox cbChoose;

        public PathViewHolder(View itemView) {
            super(itemView);
            ivType = (ImageView) itemView.findViewById(R.id.iv_type);
            layoutRoot = (RelativeLayout) itemView.findViewById(R.id.layout_item_root);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvDetail = (TextView) itemView.findViewById(R.id.tv_detail);
            cbChoose = (CheckBox) itemView.findViewById(R.id.cb_choose);
        }
    }
}


