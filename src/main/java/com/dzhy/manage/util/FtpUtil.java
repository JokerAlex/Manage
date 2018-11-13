package com.dzhy.manage.util;

import com.dzhy.manage.exception.GeneralException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @ClassName FtpUtil
 * @Description ftp 工具类
 * @Author alex
 * @Date 2018/11/12
 **/
@Slf4j
public class FtpUtil {

    public static boolean uploadFile(Map<String, InputStream> map, String ip, String username, String pass, String path) throws GeneralException {

        boolean success = false;
        FTPClient ftp = new FTPClient();
        ftp.setControlEncoding("UTF-8");
        try {
            int reply;
            // 连接FTP服务器
            ftp.connect(ip, 21);
            // 登录
            ftp.login(username, pass);
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                log.error("connect to ftp server failed");
                ftp.disconnect();
                return success;
            }
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftp.changeWorkingDirectory(path);

            for (Map.Entry<String, InputStream> entry : map.entrySet()) {
                log.info("[uploadFile] fileName : {}", entry.getKey());
                ftp.storeFile(entry.getKey(), entry.getValue());
            }
            ftp.logout();
            success = true;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new GeneralException("上传失败");
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    log.error(ioe.getMessage());
                }
            }
        }
        return success;
    }

    public static boolean delFile(List<String> fileNames, String ip, String username, String pass, String path) throws GeneralException {

        boolean success = false;
        FTPClient ftp = new FTPClient();
        ftp.setControlEncoding("UTF-8");
        try {
            int reply;
            // 连接FTP服务器
            ftp.connect(ip, 21);
            // 登录
            ftp.login(username, pass);
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                log.error("connect to ftp server failed");
                ftp.disconnect();
                return success;
            }
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftp.changeWorkingDirectory(path);

            for(String fileName : fileNames) {
                success = ftp.deleteFile(fileName);
                log.info("FTP : delFile --> result : {}, fileName : {}", success, fileName);
            }
            ftp.logout();
            success = true;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new GeneralException("删除失败");
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    log.error(ioe.getMessage());
                }
            }
        }
        return success;
    }
}
