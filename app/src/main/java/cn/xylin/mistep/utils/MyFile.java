package cn.xylin.mistep.utils;

import android.content.Context;
import java.io.File;

/**
 * @author XyLin
 * @date 2020/8/19 09:02:40
 * MyFile.java
 **/
public class MyFile {
    private static MyFile file;

    public static MyFile getFile(Context baseContext) {
        if (file == null && baseContext != null) {
            file = new MyFile(baseContext);
        }
        return file;
    }

    private File externalFile;

    private MyFile(Context baseContext) {
        this.externalFile = baseContext.getExternalFilesDir("").getParentFile();
    }

    public File getDir(String dirName) {
        File dir = new File(externalFile, dirName);
        if (!dir.exists()) {
            dir.mkdir();
        } else if (dir.isFile()) {
            dir.delete();
            dir.mkdir();
        }
        return dir;
    }
}
