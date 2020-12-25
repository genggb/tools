package com.genggb.tools.getGitPatch;


import java.io.*;

public class FileUtils {

    /**
     * 复制TXT文件
     *
     * @param oldFile 需要复制的文件
     * @param newFile 新文件路径
     * @return 是否成功
     */
    public static boolean copyFile(File oldFile, File newFile) {
        if (!oldFile.exists()) {
            return false;
        }
        File dirFile = newFile.getParentFile();
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        if (newFile.exists()) {
            newFile.delete();
        }
        BufferedWriter bufferedWriter = null;
        BufferedReader bufferedReader = null;
        try {
            if (!newFile.createNewFile()) {
                return false;
            }
            bufferedWriter = new BufferedWriter(new FileWriter(newFile));
            FileReader fileReader = new FileReader(oldFile);
            bufferedReader = new BufferedReader(fileReader);
            String lineStr = "";
            while ((lineStr = bufferedReader.readLine()) != null) {
                bufferedWriter.write(lineStr);
                bufferedWriter.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * 复制byte文件
     *
     * @param oldFile 需要复制的文件
     * @param newFile 新文件路径
     * @return 是否成功
     */
    public static boolean copyByteFile(File oldFile, File newFile) {
        if (!oldFile.exists()) {
            return false;
        }
        File dirFile = newFile.getParentFile();
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        if (newFile.exists()) {
            newFile.delete();
        }
        FileInputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            if (!newFile.createNewFile()) {
                return false;
            }
            inputStream = new FileInputStream(oldFile);
            fileOutputStream = new FileOutputStream(newFile);
            byte[] bytes = new byte[1024];
            int l;
            while ((l = inputStream.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, l);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                    fileOutputStream.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public static boolean copyFile(String oldPath, String newPath) {
        File oldFile = new File(oldPath);
        File newFile = new File(newPath);
        return copyFile(oldFile, newFile);
    }

    public static boolean copyByteFile(String oldPath, String newPath) {
        File oldFile = new File(oldPath);
        File newFile = new File(newPath);
        return copyByteFile(oldFile, newFile);
    }

    public static String readFile(File file) throws Exception {
        int len;
        byte[] bytes = new byte[1024];
        StringBuilder str = new StringBuilder();
        FileInputStream fileInputStream = new FileInputStream(file);
        while ((len = fileInputStream.read(bytes)) != -1) {
            str.append(new String(bytes, 0, len, "UTF-8"));
        }
        return str.toString();
    }

    public static void getSonClass(File javaFile, String newJavaPath) {
        String name = javaFile.getName().split("\\.")[0];
        File dirFile = javaFile.getParentFile();
        File[] list = dirFile.listFiles();
        for (File file : list) {
            String tempName = file.getName();
            if (tempName.indexOf(name + "$") >= 0) {
                System.out.println(tempName);
                String sonName = file.getName();
                File newFile = new File(newJavaPath + sonName);
                copyByteFile(file, newFile);
            }
        }
    }


}
