package com.stpi.campus.util;

import android.R;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author JackyHo
 * @version 2014-3-3
 */
public class ApkSetup {

    // 捆绑安装
    public static boolean retrieveApkFromAssets(Context context, String fileName, String path) {
        boolean bRet = false;

        try {
            File file = new File(path);
            if (file.exists()) {
                return true;
            } else {
                file.createNewFile();
                InputStream is = context.getAssets().open(fileName);
                FileOutputStream fos = new FileOutputStream(file);

                byte[] temp = new byte[1024];
                int i = 0;
                while ((i = is.read(temp)) != -1) {
                    fos.write(temp, 0, i);
                }
                fos.flush();
                fos.close();
                is.close();

                bRet = true;
            }

        } catch (IOException e) {
            Toast.makeText(context, e.getMessage(), 2000).show();
            Builder builder = new Builder(context);
            builder.setMessage(e.getMessage());
            builder.show();
            e.printStackTrace();
        }

        return bRet;
    }

    /**
     * 提示用户安装程序
     */
    public static void showInstallConfirmDialog(final Context context, final String filePath) {
        AlertDialog.Builder tDialog = new AlertDialog.Builder(context);
        tDialog.setIcon(R.drawable.ic_dialog_info);
        tDialog.setTitle("未安装该程序");
        tDialog.setMessage("请安装该程序");

        tDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                // 修改apk权限
                try {
                    String command = "chmod " + "777" + " " + filePath;
                    Runtime runtime = Runtime.getRuntime();
                    runtime.exec(command);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // install the apk.
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
                context.startActivity(intent);

            }
        });

        tDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
            }
        });

        tDialog.show();
    }

    /**
     * 检测是否已经安装
     *
     * @param packageName
     * @return true已安装 false未安装
     */
    public static boolean detectApk(PackageManager manager, String packageName) {
        return getPackagNameList(manager).contains(packageName.toLowerCase());

    }

    public static ArrayList<String> getPackagNameList(PackageManager manager) {
        // 初始化小模块列表
        ArrayList<String> packagNameList = new ArrayList<String>();

        if (manager != null) {
            List<PackageInfo> pkgList = manager.getInstalledPackages(0);
            for (int i = 0; i < pkgList.size(); i++) {
                PackageInfo pI = pkgList.get(i);
                packagNameList.add(pI.packageName.toLowerCase());
            }
        }

        return packagNameList;
    }
}
