package com.genggb.tools.svnToGit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class FileUtil {

    public static String getFile(String filePath) throws Exception {
        log.debug("文件路径:{}", filePath);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        String str = "";
        try {
            File file = new File(filePath);
            FileInputStream fs = new FileInputStream(file);
            byte[] b = new byte[1024];
            int n;
            while ((n = (fs.read(b))) != -1) {
                bos.write(b, 0, n);
            }
            str = bos.toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            bos.close();
        }
        return str;
    }

    public static byte[] getFileByte(String filePath) throws Exception {
        log.debug("文件路径:{}", filePath);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] bytes;
        try {
            File file = new File(filePath);
            FileInputStream fs = new FileInputStream(file);
            byte[] b = new byte[1024];
            int n;
            while ((n = (fs.read(b))) != -1) {
                bos.write(b, 0, n);
            }
            bytes = bos.toByteArray();
        } catch (Exception e) {
            bytes = new byte[0];
            log.error(e.getMessage(), e);
        } finally {
            bos.close();
        }
        return bytes;
    }

    public static String listToStr(List<String> list) {
        return StringUtils.join(list.toArray(), '\n');
    }


}
