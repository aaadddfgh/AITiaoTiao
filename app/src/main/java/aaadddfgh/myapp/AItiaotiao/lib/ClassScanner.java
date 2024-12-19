package aaadddfgh.myapp.AItiaotiao.lib;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

public class ClassScanner {

    public static List<String> getClassesInPackage(Context context, String packageName) {


        List<String> classNames = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();

        try {
            // 获取应用信息
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);

            // 获取应用 APK 文件的路径
            String apkPath = applicationInfo.sourceDir;

            // 通过 DexClassLoader 加载 APK 文件
            DexClassLoader dexClassLoader = new DexClassLoader(apkPath, context.getCacheDir().getAbsolutePath(), null, context.getClassLoader());

            // 获取 Dex 文件的路径
            String dexPath = context.getCacheDir() + "/output.dex";

            // 将 APK 文件解压到 Dex 文件
            DexFile dexFile = DexFile.loadDex(apkPath, dexPath, 0);

            // 获取 Dex 文件中的所有类名
            Enumeration<String> entries = dexFile.entries();
            while (entries.hasMoreElements()) {
                String className = entries.nextElement();
                classNames.add(className);
            }
            // 清除 DexClassLoader 缓存
            //上述代码在获取完类名后，将 DexClassLoader 设置为 null 并触发垃圾回收，以清除类加载器的缓存。这可能有助于解决新增类无法扫描到的问题
            dexClassLoader = null;
            System.gc();

        } catch (PackageManager.NameNotFoundException | IOException e) {
            e.printStackTrace();
        }

        List<String> needClassNames=new ArrayList<>();
        for(String name:classNames){
            if(name.startsWith(packageName)){
                needClassNames.add(name);
            }
        }


        return needClassNames;
    }


    public static List<Class<?>> scanClasses(String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            assert classLoader != null;
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if (resource.getProtocol().equals("file")) {
                    // 如果是文件系统
                    String fullPath = resource.getPath();
                    File root = new File(fullPath);
                    classes.addAll(findClasses(root, packageName));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return classes;
    }

    private static List<Class<?>> findClasses(File source, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!source.exists()) {
            return classes;
        }
        File[] files = source.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                classes.add(Class.forName(className));
            }
        }
        return classes;
    }

    private static void scanDirectory(File directory, String packageName) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                // 递归扫描子包
                scanDirectory(file, packageName + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' +
                        file.getName().substring(0, file.getName().length() - 6);

                try {
                    Class<?> clazz = Class.forName(className);
                    System.out.println("Found class: " + clazz.getName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}