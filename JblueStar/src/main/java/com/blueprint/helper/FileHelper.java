package com.blueprint.helper;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.blueprint.LibApp;
import com.blueprint.rx.RxUtill;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;

import static com.blueprint.helper.LogHelper.slog_d;

/**
 * Created by _SOLID
 * Date:2016/4/20
 * Time:15:01
 */
public class FileHelper {

    private static String TAG = "FileHelper";
    private static String FILE_WRITING_ENCODING = "UTF-8";
    private static String FILE_READING_ENCODING = "UTF-8";

    public static void closeQuietly(Closeable closeable){
        closeIO(closeable);
    }


    public static void closeIO(Closeable... closes){
        for(Closeable close : closes) {
            if(close != null) {
                try {
                    close.close();
                }catch(IOException e) {
                    e.printStackTrace();
                    close = null;
                }
            }
        }
    }

    public static String readTextfromAsset(String fileName){
        try {
            InputStream inStream = LibApp.getContext().getAssets().open(fileName);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = inStream.read(bytes)) > 0) {
                outStream.write(bytes, 0, len);
            }
            return outStream.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readFile(String _sFileName, String _sEncoding) throws Exception{
        StringBuffer buffContent = null;
        String sLine;

        FileInputStream fis = null;
        BufferedReader buffReader = null;
        if(_sEncoding == null || "".equals(_sEncoding)) {
            _sEncoding = FILE_READING_ENCODING;
        }

        try {
            fis = new FileInputStream(_sFileName);
            buffReader = new BufferedReader(new InputStreamReader(fis, _sEncoding));
            boolean zFirstLine = "UTF-8".equalsIgnoreCase(_sEncoding);
            while(( sLine = buffReader.readLine() ) != null) {
                if(buffContent == null) {
                    buffContent = new StringBuffer();
                }else {
                    buffContent.append("\n");
                }
                if(zFirstLine) {
                    sLine = removeBomHeaderIfExists(sLine);
                    zFirstLine = false;
                }
                buffContent.append(sLine);
            }// end while
            return ( buffContent == null ? "" : buffContent.toString() );
        }catch(FileNotFoundException ex) {
            throw new Exception("要读取的文件没有找到!", ex);
        }catch(IOException ex) {
            throw new Exception("读取文件时错误!", ex);
        }finally {
            // 增加异常时资源的释放
            closeQuietly(buffReader);
            closeQuietly(fis);
        }
    }

    public static File writeFile(String path, String content, String encoding, boolean isOverride) throws Exception{
        if(TextUtils.isEmpty(encoding)) {
            encoding = FILE_WRITING_ENCODING;
        }
        InputStream is = new ByteArrayInputStream(content.getBytes(encoding));
        return writeFile(is, path, isOverride);
    }

    public static File writeFile(InputStream is, String path, boolean isOverride) throws Exception{
        String sPath = extractFilePath(path);
        if(!pathExists(sPath)) {
            makeDir(sPath, true);
        }

        if(!isOverride && fileExists(path)) {
            if(path.contains(".")) {
                String suffix = path.substring(path.lastIndexOf("."));
                String pre = path.substring(0, path.lastIndexOf("."));
                path = pre+"_"+System.currentTimeMillis()+suffix;
            }else {
                path = path+"_"+System.currentTimeMillis();
            }
        }

        FileOutputStream os = null;
        File file = null;

        try {
            file = new File(path);
            os = new FileOutputStream(file);
            int byteCount = 0;
            byte[] bytes = new byte[1024];

            while(( byteCount = is.read(bytes) ) != -1) {
                os.write(bytes, 0, byteCount);
            }
            os.flush();

            return file;
        }catch(Exception e) {
            e.printStackTrace();
            throw new Exception("写文件错误", e);
        }finally {
            closeQuietly(os);
            closeQuietly(is);
        }
    }

    /**
     * 移除字符串中的BOM前缀
     *
     * @param _sLine
     *         需要处理的字符串
     * @return 移除BOM后的字符串.
     */
    private static String removeBomHeaderIfExists(String _sLine){
        if(_sLine == null) {
            return null;
        }
        String line = _sLine;
        if(line.length()>0) {
            char ch = line.charAt(0);
            // 使用while是因为用一些工具看到过某些文件前几个字节都是0xfffe.
            // 0xfeff,0xfffe是字节序的不同处理.JVM中,一般是0xfeff
            while(( ch == 0xfeff || ch == 0xfffe )) {
                line = line.substring(1);
                if(line.length() == 0) {
                    break;
                }
                ch = line.charAt(0);
            }
        }
        return line;
    }

    /**
     * 从文件的完整路径名（路径+文件名）中提取 路径（包括：Drive+Directroy )
     *
     * @param _sFilePathName
     * @return
     */
    public static String extractFilePath(String _sFilePathName){
        int nPos = _sFilePathName.lastIndexOf('/');
        if(nPos<0) {
            nPos = _sFilePathName.lastIndexOf('\\');
        }

        return ( nPos>=0 ? _sFilePathName.substring(0, nPos+1) : "" );
    }

    /**
     * 检查指定文件的路径是否存在
     *
     * @param _sPathFileName
     *         文件名称(含路径）
     * @return 若存在，则返回true；否则，返回false
     */
    public static boolean pathExists(String _sPathFileName){
        String sPath = extractFilePath(_sPathFileName);
        return fileExists(sPath);
    }

    public static boolean fileExists(String _sPathFileName){
        File file = new File(_sPathFileName);
        return file.exists();
    }

    /**
     * 创建目录
     *
     * @param _sDir
     *         目录名称
     * @param _bCreateParentDir
     *         如果父目录不存在，是否创建父目录
     * @return
     */
    public static boolean makeDir(String _sDir, boolean _bCreateParentDir){
        boolean zResult = false;
        File file = new File(_sDir);
        if(_bCreateParentDir) {
            zResult = file.mkdirs(); // 如果父目录不存在，则创建所有必需的父目录
        }else {
            zResult = file.mkdir(); // 如果父目录不存在，不做处理
        }
        if(!zResult) {
            zResult = file.exists();
        }
        return zResult;
    }


    public static void moveRawToDir(String rawName, String dir){
        try {
            writeFile(LibApp.getContext().getAssets().open(rawName), dir, true);
        }catch(Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * 得到手机的缓存目录
     *
     * @return
     */
    public static File getCacheDir(){
        slog_d("getCacheDir", "cache sdcard state: "+Environment.getExternalStorageState());
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File cacheDir = LibApp.getContext().getExternalCacheDir();
            if(cacheDir != null && ( cacheDir.exists() || cacheDir.mkdirs() )) {
                Log.i("getCacheDir", "cache dir: "+cacheDir.getAbsolutePath());
                return cacheDir;
            }
        }

        File cacheDir = LibApp.getContext().getCacheDir();
        slog_d("getCacheDir", "cache dir: "+cacheDir.getAbsolutePath());

        return cacheDir;
    }

    public static String getSaveImagePath(){

        String path = getCacheDir().getAbsolutePath();
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            path = Environment.getExternalStorageDirectory()
                    .getAbsolutePath()+File.separator+Environment.DIRECTORY_DCIM;
        }else {
            path = path+File.separator+"Pictures";
        }
        File file = new File(path);
        if(!file.exists()) {
            file.mkdir();
        }
        return path;
    }

    public static String generateFileNameByTime(){
        return System.currentTimeMillis()+"";
    }

    public static String getFileName(String path){
        int index = path.lastIndexOf('/');
        return path.substring(index+1);
    }

    public static Single<Boolean> clearFile(final File... file){
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Boolean> e) throws Exception{
                for(File file1 : file) {
                    if(file1 != null) {
                        doClearFile(file1);
                    }
                }
                e.onSuccess(true);
            }
        }).compose(RxUtill.<Boolean>defaultSchedulers_single());
    }


    public static void doClearFile(File file){
        if(file == null || !file.exists()) {
            return;
        }
        if(file.isDirectory() && file.listFiles() != null) {
            for(File child : file.listFiles()) {
                doClearFile(child);
            }
        }else {
            file.delete();
        }
    }

    public static Single<Long> getDirSize(final File... dir){
        return Single.create(new SingleOnSubscribe<Long>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Long> e) throws Exception{
                long size = 0;
                for(File file : dir) {
                    if(file != null) {
                        size += calcureDirSize(file);
                    }
                }
                e.onSuccess(size);
            }
        }).compose(RxUtill.<Long>defaultSchedulers_single());
    }

    public static long calcureDirSize(File dir){
        if(dir == null) {
            return 0;
        }
        if(!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        if(files == null) {
            return 0;
        }
        for(File file : files) {
            if(file.isFile()) {
                dirSize += file.length();
            }else if(file.isDirectory()) {
                dirSize += file.length();
                dirSize += calcureDirSize(file); // 递归调用继续统计
            }
        }
        return dirSize;
    }

    public static String formatFileSize(long fileS){
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        String fileSizeString = "";
        if(fileS<1024) {
            fileSizeString = df.format((double)fileS)+"B";
        }else if(fileS<1048576) {
            fileSizeString = df.format((double)fileS/1024)+"KB";
        }else if(fileS<1073741824) {
            fileSizeString = df.format((double)fileS/1048576)+"MB";
        }else {
            fileSizeString = df.format((double)fileS/1073741824)+"G";
        }

        if(fileSizeString.startsWith(".")) {
            return "0B";
        }
        return fileSizeString;
    }

    /**
     * 重命名文件
     *
     * @param filePath
     *         文件路径
     * @param newName
     *         新名称
     * @return {@code true}: 重命名成功<br>{@code false}: 重命名失败
     */
    public static boolean rename(@NonNull String filePath, String newName){
        return rename(new File(filePath), newName);
    }

    /**
     * 重命名文件
     *
     * @param file
     *         文件
     * @param newName
     *         新名称
     * @return {@code true}: 重命名成功<br>{@code false}: 重命名失败
     */
    public static boolean rename(File file, String newName){
        // 文件为空返回false
        if(file == null) {
            return false;
        }
        // 文件不存在返回false
        if(!file.exists()) {
            return false;
        }
        // 新的文件名为空返回false
        if(TextUtils.isEmpty(newName)) {
            return false;
        }
        // 如果文件名没有改变返回true
        if(newName.equals(file.getName())) {
            return true;
        }
        File newFile = new File(file.getParent()+File.separator+newName);
        // 如果重命名的文件已存在返回false
        return !newFile.exists() && file.renameTo(newFile);
    }

    //Android/data/包名/file/download/filename
    public static String getFileDownloadPath(String fileName){
        return new File(LibApp.getAppFileDir(Environment.DIRECTORY_DOWNLOADS), fileName).getAbsolutePath();
    }

    //Android/data/包名/file/download/filename
    public static String getFileDownloadPath(String dir, String fileName){
        return new File(LibApp.getAppFileDir(dir), fileName).getAbsolutePath();
    }

    //Android/data/包名/file/download/filename
    public static File getFileDownloadPath_file(String fileName){
        return new File(LibApp.getAppFileDir(Environment.DIRECTORY_DOWNLOADS), fileName);
    }

    @NonNull
    public static String getNewAppName(String new_version){
        return LibApp.getPackageName()+"_"+new_version+".apk";
    }

    @NonNull
    public static File getNewAppFile(String new_version){
        return getFileDownloadPath_file(getNewAppName(new_version));
    }

    /**
     * 复制或移动目录
     *
     * @param srcDirPath
     *         源目录路径
     * @param destDirPath
     *         目标目录路径
     * @param isMove
     *         是否移动
     * @return {@code true}: 复制或移动成功<br>{@code false}: 复制或移动失败
     */
    public static boolean copyOrMoveDir(final String srcDirPath, final String destDirPath, final boolean isMove){
        return copyOrMoveDir(new File(srcDirPath), new File(destDirPath), isMove);
    }

    /**
     * 复制或移动目录
     *
     * @param srcDir
     *         源目录
     * @param destDir
     *         目标目录
     * @param isMove
     *         是否移动
     * @return {@code true}: 复制或移动成功<br>{@code false}: 复制或移动失败
     */
    public static boolean copyOrMoveDir(final File srcDir, final File destDir, final boolean isMove){
        if(srcDir == null || destDir == null) {
            return false;
        }
        // 如果目标目录在源目录中则返回false，看不懂的话好好想想递归怎么结束
        // srcPath : F:\\MyGithub\\AndroidUtilCode\\utilcode\\src\\test\\res
        // destPath: F:\\MyGithub\\AndroidUtilCode\\utilcode\\src\\test\\res1
        // 为防止以上这种情况出现出现误判，须分别在后面加个路径分隔符
        String srcPath = srcDir.getPath()+File.separator;
        String destPath = destDir.getPath()+File.separator;
        if(destPath.contains(srcPath)) {
            return false;
        }
        // 源文件不存在或者不是目录则返回false
        if(!srcDir.exists() || !srcDir.isDirectory()) {
            return false;
        }
        // 目标目录不存在返回false
        if(!destDir.mkdir()) {
            return false;
        }
        File[] files = srcDir.listFiles();
        for(File file : files) {
            File oneDestFile = new File(destPath+file.getName());
            if(file.isFile()) {
                // 如果操作失败返回false
                if(!copyOrMoveFile2(file, oneDestFile, isMove)) {
                    return false;
                }
            }else if(file.isDirectory()) {
                // 如果操作失败返回false
                if(!copyOrMoveDir(file, oneDestFile, isMove)) {
                    return false;
                }
            }
        }
        doClearFile(srcDir);
        return !isMove;
    }

    /**
     * 复制或移动文件
     *
     * @param srcFilePath
     *         源文件路径
     * @param destFilePath
     *         目标文件路径
     * @param isMove
     *         是否移动
     * @return {@code true}: 复制或移动成功<br>{@code false}: 复制或移动失败
     */
    public static Observable<Boolean> copyOrMoveFile(final String srcFilePath, final String destFilePath, final boolean
            isMove){
        return copyOrMoveFile(new File(srcFilePath), new File(destFilePath), isMove);
    }

    /**
     * 复制或移动文件
     *
     * @param srcFile
     *         源文件
     * @param destFile
     *         目标文件
     * @param isMove
     *         是否移动
     * @return {@code true}: 复制或移动成功<br>{@code false}: 复制或移动失败
     */
    private static Observable<Boolean> copyOrMoveFile(final File srcFile, final File destFile, final boolean isMove){
        return Observable.<Boolean>create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter){
                if(!srcFile.exists()) {
                    emitter.onNext(false);
                    emitter.onComplete();
                }
                if(destFile.exists()) {
                    emitter.onNext(true);
                    emitter.onComplete();
                }
                FileChannel sourceChannel = null;
                FileChannel destChannel = null;
                try {
                    sourceChannel = new FileInputStream(srcFile).getChannel();
                    destChannel = new FileOutputStream(destFile).getChannel();
                    destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
                    if(isMove) {
                        srcFile.delete();
                    }
                    emitter.onNext(true);
                    emitter.onComplete();
                }catch(IOException e) {
                    e.printStackTrace();
                    emitter.onNext(false);
                    emitter.onComplete();
                }finally {
                    if(sourceChannel != null) {
                        try {
                            sourceChannel.close();
                        }catch(IOException e) {
                            e.printStackTrace();
                            emitter.onNext(false);
                            emitter.onComplete();
                        }
                    }
                    if(destChannel != null) {
                        try {
                            destChannel.close();
                        }catch(IOException e) {
                            e.printStackTrace();
                            emitter.onNext(false);
                            emitter.onComplete();
                        }
                    }
                }
            }
        }).compose(RxUtill.<Boolean>defaultSchedulers_obser());
    }

    /**
     * 复制或移动文件
     *
     * @param srcFile
     *         源文件
     * @param destFile
     *         目标文件
     * @param isMove
     *         是否移动
     * @return {@code true}: 复制或移动成功<br>{@code false}: 复制或移动失败
     */
    private static boolean copyOrMoveFile2(final File srcFile, final File destFile, final boolean isMove){
        if(!srcFile.exists()) {
            return false;
        }
        if(destFile.exists()) {
            return true;
        }
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(srcFile).getChannel();
            destChannel = new FileOutputStream(destFile).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            if(isMove) {
                srcFile.delete();
            }
        }catch(IOException e) {
            e.printStackTrace();
            return false;
        }finally {
            if(sourceChannel != null) {
                try {
                    sourceChannel.close();
                }catch(IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            if(destChannel != null) {
                try {
                    destChannel.close();
                }catch(IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 复制目录
     *
     * @param srcDirPath
     *         源目录路径
     * @param destDirPath
     *         目标目录路径
     * @return {@code true}: 复制成功<br>{@code false}: 复制失败
     */
    public static boolean copyDir(final String srcDirPath, final String destDirPath){
        return copyDir(new File(srcDirPath), new File(destDirPath));
    }

    /**
     * 复制目录
     *
     * @param srcDir
     *         源目录
     * @param destDir
     *         目标目录
     * @return {@code true}: 复制成功<br>{@code false}: 复制失败
     */
    public static boolean copyDir(final File srcDir, final File destDir){
        return copyOrMoveDir(srcDir, destDir, false);
    }

    /**
     * 复制文件
     *
     * @param srcFilePath
     *         源文件路径
     * @param destFilePath
     *         目标文件路径
     * @return {@code true}: 复制成功<br>{@code false}: 复制失败
     */
    public static Observable copyFile(final String srcFilePath, final String destFilePath){
        return copyFile(new File(srcFilePath), new File(destFilePath));
    }

    /**
     * 复制文件
     *
     * @param srcFile
     *         源文件
     * @param destFile
     *         目标文件
     * @return {@code true}: 复制成功<br>{@code false}: 复制失败
     */
    public static Observable<Boolean> copyFile(final File srcFile, final File destFile){
        return copyOrMoveFile(srcFile, destFile, false);
    }

    /**
     * 移动目录
     *
     * @param srcDirPath
     *         源目录路径
     * @param destDirPath
     *         目标目录路径
     * @return {@code true}: 移动成功<br>{@code false}: 移动失败
     */
    public static boolean moveDir(final String srcDirPath, final String destDirPath){
        return moveDir(new File(srcDirPath), new File(destDirPath));
    }

    /**
     * 移动目录
     *
     * @param srcDir
     *         源目录
     * @param destDir
     *         目标目录
     * @return {@code true}: 移动成功<br>{@code false}: 移动失败
     */
    public static boolean moveDir(final File srcDir, final File destDir){
        return copyOrMoveDir(srcDir, destDir, true);
    }

    /**
     * 移动文件
     *
     * @param srcFilePath
     *         源文件路径
     * @param destFilePath
     *         目标文件路径
     * @return {@code true}: 移动成功<br>{@code false}: 移动失败
     */
    public static Observable<Boolean> moveFile(final String srcFilePath, final String destFilePath){
        return moveFile(new File(srcFilePath), new File(destFilePath));
    }

    /**
     * 移动文件
     *
     * @param srcFile
     *         源文件
     * @param destFile
     *         目标文件
     * @return {@code true}: 移动成功<br>{@code false}: 移动失败
     */
    public static Observable<Boolean> moveFile(final File srcFile, final File destFile){
        return copyOrMoveFile(srcFile, destFile, true);
    }

    public static String getMimeType(String fileUrl) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(fileUrl);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
}
