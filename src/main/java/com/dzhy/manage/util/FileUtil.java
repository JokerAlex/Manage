package com.dzhy.manage.util;

import com.dzhy.manage.exception.GeneralException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @ClassName FileUtil
 * @Description 文件上传、删除
 * @Author alex
 * @Date 2019/2/17
 **/
@Slf4j
public class FileUtil {

    public static void upload(Map<String, MultipartFile> map, String path) throws GeneralException {
        for (Map.Entry<String, MultipartFile> entry : map.entrySet()) {
            try(
                    FileOutputStream out = new FileOutputStream(path + "/" + entry.getKey())
            ) {
                File targetFile = new File(path);
                if (!targetFile.exists()) {
                    targetFile.mkdirs();
                }
                out.write(entry.getValue().getBytes());
                out.flush();
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new GeneralException("文件上传失败");
            } finally {
            }
        }
    }

    public static void del(List<String> fileNames, String path) throws GeneralException {
        for (String filename : fileNames) {
            File file = new File(path, filename);
            if (file.isFile() && file.exists()) {
                System.gc();
                boolean flag = file.delete();
                log.info("[delPicture] fileName : {}, isDelete : {}", filename, flag);
            } else {
                log.info("file is not exists");
                throw new GeneralException("文件删除失败");
            }
        }
    }
}
