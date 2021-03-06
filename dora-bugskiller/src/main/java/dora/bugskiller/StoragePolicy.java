package dora.bugskiller;

import android.os.Environment;
import android.os.Process;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 把崩溃日志信息保存到SD卡，请自行申请存储权限。
 */
public class StoragePolicy extends CrashReportPolicyWrapper {

    private String mFolderName = "android-dora"; //手机系统根目录保存日志文件夹的名称

    public StoragePolicy(CrashReportPolicy policy) {
        super(policy);
    }

    public StoragePolicy() {
        super(null);
    }

    public StoragePolicy(CrashReportPolicy policy, String folderName) {
        super(policy);
        this.mFolderName = folderName;
    }

    public StoragePolicy(String folerName) {
        super(null);
        this.mFolderName = folerName;
    }

    @Override
    public void report(CrashInfo info) {
        super.report(info);
        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            String time = simpleDateFormat.format(new Date());
            File folder = new File(path, mFolderName);
            folder.mkdirs();
            File file = new File(folder.getAbsolutePath(), "log" + time + ".txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            byte[] buffer = info.toString().trim().getBytes();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(buffer, 0, buffer.length);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            Log.e("dora", "崩溃日志信息存储失败");
            Process.killProcess(Process.myPid());
            System.exit(0);
        }
    }
}