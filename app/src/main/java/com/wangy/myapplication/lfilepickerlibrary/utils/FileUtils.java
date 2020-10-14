package com.wangy.myapplication.lfilepickerlibrary.utils;

import android.util.Log;

import com.blankj.utilcode.util.ToastUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Dimorinny on 24.10.15.
 */
public class FileUtils {
    public static List<File> getFileListByDirPath(String path, FileFilter filter) {
        File directory = new File(path);
        File[] files = directory.listFiles(filter);
        List<File> result = new ArrayList<>();
        if (files == null) {
            return new ArrayList<>();
        }

        for (int i = 0; i < files.length; i++) {
            result.add(files[i]);
        }
        Collections.sort(result, new FileComparator());
        return result;
    }

    public static String cutLastSegmentOfPath(String path) {
        return path.substring(0, path.lastIndexOf("/"));
    }

    public static String getReadableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * 获取文件长度
     *
     * @param file 文件
     * @return 文件长度
     */
    public static long getFileLength(final File file) {
        if (!isFile(file)) return -1;
        return file.length();
    }

    /**
     * 判断是否是文件
     *
     * @param file 文件
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isFile(final File file) {
        return file != null && file.exists() && file.isFile();
    }

    /**
     * 根据地址获取当前地址下的所有目录和文件，并且排序,同时过滤掉不符合大小要求的文件
     *
     * @param path
     * @return List<File>
     */
    public static List<File> getFileList(String path, FileFilter filter, boolean isGreater, long targetSize) {
        List<File> list = FileUtils.getFileListByDirPath(path, filter);
        //进行过滤文件大小
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            File f = (File) iterator.next();
            if (f.isFile()) {
                //获取当前文件大小
                long size = FileUtils.getFileLength(f);
                if (isGreater) {
                    //当前想要留下大于指定大小的文件，所以过滤掉小于指定大小的文件
                    if (size < targetSize) {
                        iterator.remove();
                    }
                } else {
                    //当前想要留下小于指定大小的文件，所以过滤掉大于指定大小的文件
                    if (size > targetSize) {
                        iterator.remove();
                    }
                }
            }
        }
        return list;
    }

    /**
     * 创建文件/文件夹
     */
    public static void createFile(String path, String name, int types) {
        File file = new File(path, name);
        if (1 == types) {
            // 判断文件是否存在
            if (file.exists()) {
                ToastUtils.showShort("您要创建的文件已经存在！");
            } else {
                try {
                    file.createNewFile();
                    ToastUtils.showShort("文件创建成功！");
                } catch (IOException e) {
                    e.printStackTrace();
                    ToastUtils.showShort("文件创建失败！");
                }
            }
        } else {
            ToastUtils.showShort(file.exists() ? "您要创建的文件夹已经存在！" : file.mkdirs() ? "文件夹创建成功！" : "文件创建失败！");
        }
    }

    /**
     * 重新命名文件
     */
    public static void reName(File oldFile, File newFile) {
        oldFile.renameTo(newFile);
    }
    /**
     * 复制/移动 文件/文件夹
     */

    /**
     * 复制文件夹
     *
     * @param resource 源路径
     * @param target   目标路径
     */
    public static void copyFolder(String resource, String target) throws Exception {

        File resourceFile = new File(resource);
        if (!resourceFile.exists()) throw new Exception("源目标路径：[" + resource + "] 不存在...");
        File targetFile = new File(target);
        if (!targetFile.exists()) throw new Exception("存放的目标路径：[" + target + "] 不存在...");

        // 获取源文件夹下的文件夹或文件
        File[] resourceFiles = resourceFile.listFiles();

        if (resourceFiles != null && resourceFiles.length > 0) {
            for (File file : resourceFiles) {

                File file1 = new File(targetFile.getAbsolutePath() + File.separator + resourceFile.getName());
                // 复制文件
                if (file.isFile()) {
                    System.out.println("文件" + file.getName());
                    // 在 目标文件夹（B） 中 新建 源文件夹（A），然后将文件复制到 A 中
                    // 这样 在 B 中 就存在 A
                    if (!file1.exists()) {
                        file1.mkdirs();
                    }
                    File targetFile1 = new File(file1.getAbsolutePath() + File.separator + file.getName());
                    copyFile(file, targetFile1);
                } else if (file.isDirectory()) {// 复制源文件夹
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    // 复制文件夹
                    String dir1 = file.getAbsolutePath();
                    // 目的文件夹
                    String dir2 = file1.getAbsolutePath();
                    copyFolder(dir1, dir2);
                }
            }
        } else {
            File file = new File(target, resourceFile.getName());
            if (file.exists()){
                ToastUtils.showShort("请不要将同名称的文件放置到该目录下！");
            }else {
                file.mkdirs();
            }

        }


    }

    /**
     * 复制文件
     *
     * @param resource 要复制的文件的路径
     * @param target   要复制的目标
     */
    public static void copyFile(File resource, File target) throws Exception {
        // 输入流 --> 从一个目标读取数据
        // 输出流 --> 向一个目标写入数据
        long start = System.currentTimeMillis();
        // 文件输入流并进行缓冲
        FileInputStream inputStream = new FileInputStream(resource);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        // 文件输出流并进行缓冲
        FileOutputStream outputStream = new FileOutputStream(target);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);

        // 缓冲数组
        // 大文件 可将 1024 * 2 改大一些，但是 并不是越大就越快
        byte[] bytes = new byte[1024 * 2];
        int len;
        while ((len = inputStream.read(bytes)) != -1) bufferedOutputStream.write(bytes, 0, len);
        // 刷新输出缓冲流
        bufferedOutputStream.flush();
        //关闭流
        bufferedInputStream.close();
        bufferedOutputStream.close();
        inputStream.close();
        outputStream.close();
        long end = System.currentTimeMillis();
        System.out.println("耗时：" + (end - start) / 1000 + " s");

    }

    /**
     * 删除文件
     */
    public static void deleteDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.isFile()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File value : files) {
                    deleteDir(value.getAbsolutePath());
                }
            }
        }
        file.delete();
    }
}
