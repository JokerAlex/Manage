package com.dzhy.manage.scheduler;

import com.dzhy.manage.constants.Constants;
import com.dzhy.manage.dto.ResponseDTO;
import com.dzhy.manage.repository.OutputRepository;
import com.dzhy.manage.repository.ProduceRepository;
import com.dzhy.manage.service.MailService;
import com.dzhy.manage.service.OutputService;
import com.dzhy.manage.service.ProduceService;
import com.dzhy.manage.util.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @ClassName Task
 * @Description 定时任务
 * @Author alex
 * @Date 2018/11/4
 **/
@Component
@Slf4j
public class Task {

    private final ProduceService iProduceService;
    private final OutputService iOutputService;
    private final MailService iMailService;
    private final ProduceRepository produceRepository;
    private final OutputRepository outputRepository;

    @Value("${manage.files.path}")
    private String path;

    @Value("${manage.mail.to}")
    private String mailTo;

    @Autowired
    public Task(ProduceService iProduceService, OutputService iOutputService, MailService iMailService, ProduceRepository produceRepository, OutputRepository outputRepository) {
        this.iProduceService = iProduceService;
        this.iOutputService = iOutputService;
        this.iMailService = iMailService;
        this.produceRepository = produceRepository;
        this.outputRepository = outputRepository;
    }

    /**
     * 每天上午 01:00，导出前一天的生产进度表，并用邮箱发送
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void produceSendMailTask() {
        log.info("produceSendMail start time = {}", LocalDateTime.now());

        LocalDate date = LocalDate.now();
        date = date.minusDays(1);
        if (!produceRepository.existsByProduceYearAndProduceMonthAndProduceDay(date.getYear(), date.getMonthValue(), date.getDayOfMonth())) {
            log.info("produceSendMailTask 日期 {} 没有数据", date);
            return;
        }

        String fileName = date.toString().replace("-", "") + Constants.PRODUCE_TITLE + ExcelUtils.EXCEL_2007U;
        String filePathAndName = path + "/Excels/produce/" + fileName;

        try {
            //导出 Excel 文件
            File file = new File(filePathAndName);
            if (!file.exists()) {
                boolean isCreate = file.createNewFile();
                log.info("createNewFile isCreate = {}", isCreate);
            }

            FileOutputStream fileOutputStream = new FileOutputStream(file);

            ResponseDTO exportResult = iProduceService.exportExcel(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), fileOutputStream);
            log.info("exportExcel exportResult = {}", exportResult.toString());
            fileOutputStream.flush();
            fileOutputStream.close();

            //发送邮件- excel 文件名称作为邮件标题
            String content = "附件为" + date + "最终" + Constants.PRODUCE_TITLE;
            ResponseDTO sendMailResult = iMailService.sendAttachmentsMail(mailTo, fileName, content, filePathAndName);
            log.info("sendMail sendMailResult = {}", sendMailResult.toString());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 每天上午 02:00 ，将前一天的生产进度导入到今天，作为今天的开始数据
     */
    @Scheduled(cron = "0 10 1 * * ?")
    public void produceImportTask() {
        log.info("produceImportTask running time = {}", LocalDateTime.now());

        LocalDate date = LocalDate.now();
        date = date.minusDays(1);
        ResponseDTO responseDTO = iProduceService.importFromDB(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        log.info("[produceImportTask] running result, responseDTO = {}", responseDTO.toString());
    }

    /**
     * 每月 1 号 03:00 ，将本月的产值导出，并用邮箱发送
     */
    @Scheduled(cron = "0 0 1 1 * ?")
    public void outputSendMailTask() {
        log.info("[produceSendMailTask] start time = {}", LocalDateTime.now());

        LocalDate date = LocalDate.now();
        date = date.minusDays(1);
        if (!outputRepository.existsByOutputYearAndOutputMonth(date.getYear(), date.getMonthValue())) {
            log.info("[outputSendMailTask] 日期 = {} 没有数据", date);
            return;
        }

        String fileName = date.toString().replace("-", "").substring(0, 6) + Constants.OUTPUT_TITLE + ExcelUtils.EXCEL_2007U;
        String filePathAndName = path + "/Excels/output/" + fileName;

        try {
            //导出 Excel 文件
            File file = new File(filePathAndName);
            if (!file.exists()) {
                boolean isCreate = file.createNewFile();
                log.info("[createNewFile] isCreate = {}", isCreate);
            }

            FileOutputStream fileOutputStream = new FileOutputStream(file);

            ResponseDTO exportResult = iOutputService.exportExcel(date.getYear(), date.getMonthValue(), fileOutputStream);
            log.info("[exportExcel] exportResult = {}", exportResult.toString());
            fileOutputStream.flush();
            fileOutputStream.close();

            //发送邮件- excel 文件名称作为邮件标题
            String content = "附件为" + date.toString().replace("-", "").substring(0, 6) + "最终" + Constants.OUTPUT_TITLE;
            ResponseDTO sendMailResult = iMailService.sendAttachmentsMail(mailTo, fileName, content, filePathAndName);
            log.info("[sendMail] sendMailResult = {}", sendMailResult.toString());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
