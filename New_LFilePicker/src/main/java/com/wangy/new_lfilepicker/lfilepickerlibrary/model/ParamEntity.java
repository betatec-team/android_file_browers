package com.wangy.new_lfilepicker.lfilepickerlibrary.model;


import java.io.Serializable;
import java.util.Locale;

/**
 * 作者：Leon
 * 时间：2017/3/21 14:50
 */
public class ParamEntity implements Serializable {
    private String title;
    private String titleColor;
    private int titleStyle;
    private int theme;
    private String backgroundColor;
    private int backIcon;
    private boolean mutilyMode;
    private String addText;
    private int iconStyle;
    private String[] fileTypes;
    private String notFoundFiles;
    private int maxNum;
    private boolean chooseMode = true;
    private String path;
    private long fileSize;
    private boolean isGreater;
    private boolean create;
    private boolean reName;
    private boolean del;
    private boolean copy;
    private boolean move;
    private String defultPath;
    private boolean mutilyBoxMode;
    private String endPath;
    private boolean showFifter;
    private boolean listFifter;
    private int GrideFifterNum;
    private Locale locacalLanguage;
    private String currentPath;

    public String getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

    public Locale getLocacalLanguage() {
        return locacalLanguage;
    }

    public void setLocacalLanguage(Locale locacalLanguage) {
        this.locacalLanguage = locacalLanguage;
    }

    public int getGrideFifterNum() {
        return GrideFifterNum;
    }

    public void setGrideFifterNum(int grideFifterNum) {
        GrideFifterNum = grideFifterNum;
    }

    public boolean isListFifter() {
        return listFifter;
    }

    public void setListFifter(boolean listFifter) {
        this.listFifter = listFifter;
    }

    public boolean isShowFifter() {
        return showFifter;
    }

    public void setShowFifter(boolean showFifter) {
        this.showFifter = showFifter;
    }

    public String getEndPath() {
        return endPath;
    }

    public void setEndPath(String endPath) {
        this.endPath = endPath;
    }

    public boolean isMutilyBoxMode() {
        return mutilyBoxMode;
    }

    public void setMutilyBoxMode(boolean mutilyBoxMode) {
        this.mutilyBoxMode = mutilyBoxMode;
    }

    public String getDefultPath() {
        return defultPath;
    }

    public void setDefultPath(String defultPath) {
        this.defultPath = defultPath;
    }

    public boolean isCreate() {
        return create;
    }

    public void setCreate(boolean create) {
        this.create = create;
    }

    public boolean isReName() {
        return reName;
    }

    public void setReName(boolean reName) {
        this.reName = reName;
    }

    public boolean isDel() {
        return del;
    }

    public void setDel(boolean del) {
        this.del = del;
    }

    public boolean isCopy() {
        return copy;
    }

    public void setCopy(boolean copy) {
        this.copy = copy;
    }

    public boolean isMove() {
        return move;
    }

    public void setMove(boolean move) {
        this.move = move;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Deprecated
    public String getTitleColor() {
        return titleColor;
    }

    @Deprecated
    public void setTitleColor(String titleColor) {
        this.titleColor = titleColor;
    }

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }

    public int getTitleStyle() {
        return titleStyle;
    }

    public void setTitleStyle(int titleStyle) {
        this.titleStyle = titleStyle;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean isMutilyMode() {
        return mutilyMode;
    }

    public void setMutilyMode(boolean mutilyMode) {

        this.mutilyMode = mutilyMode;
    }

    public int getBackIcon() {
        return backIcon;
    }

    public void setBackIcon(int backIcon) {
        this.backIcon = backIcon;
    }

    public String getAddText() {
        return addText;
    }

    public void setAddText(String addText) {
        this.addText = addText;
    }

    public int getIconStyle() {
        return iconStyle;
    }

    public void setIconStyle(int iconStyle) {
        this.iconStyle = iconStyle;
    }

    public String[] getFileTypes() {
        return fileTypes;
    }

    public void setFileTypes(String[] fileTypes) {
        this.fileTypes = fileTypes;
    }

    public String getNotFoundFiles() {
        return notFoundFiles;
    }

    public void setNotFoundFiles(String notFoundFiles) {
        this.notFoundFiles = notFoundFiles;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    public boolean isChooseMode() {
        return chooseMode;
    }

    public void setChooseMode(boolean chooseMode) {
        this.chooseMode = chooseMode;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isGreater() {
        return isGreater;
    }

    public void setGreater(boolean greater) {
        isGreater = greater;
    }


}
