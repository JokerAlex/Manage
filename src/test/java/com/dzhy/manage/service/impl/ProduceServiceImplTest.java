package com.dzhy.manage.service.impl;

import com.dzhy.manage.dto.ResponseDTO;
import com.dzhy.manage.entity.Produce;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @ClassName ProduceServiceImplTest
 * @Description 进度管理测试
 * @Author alex
 * @Date 2018/10/31
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProduceServiceImplTest {

    @Autowired
    private ProduceServiceImpl produceService;

    @Test
    public void addProduce() {
        Produce produce = new Produce();
        produce.setProduceProductId(11);
        produce.setProduceProductName("chuang4");
        produce.setProduceXiadan(10);
        produce.setProduceXiadanComment("test");
        ResponseDTO r = produceService.addProduce(produce);
        System.out.println(r.toString());
    }

    @Test
    public void updateProduce() {
        Produce produce = new Produce();
        produce.setProduceId(14);
        produce.setProduceProductId(10);
        produce.setProduceProductName("chuang3");
        //produce.setProduceMugong(10);
        //produce.setProduceMugongComment("mu gong test -10");

        //produce.setProduceYoufang(5);
        //produce.setProduceYoufangComment("you fang test +5ssdsd");

        //produce.setProduceBaozhuang(5);
        //produce.setProduceBaozhuangComment("bao zhuang +5");

        //produce.setProduceTeding(5);
        //produce.setProduceTedingComment("te ding + 5");

        //produce.setProduceBeijing(5);
        //produce.setProduceBeijingComment("bei jing + 5");

        //produce.setProduceBeijingteding(5);
        //produce.setProduceBeijingtedingComment("bei jing te ding + 5");

        //produce.setProduceBendihetong(10);
        //produce.setProduceBendihetongComment("ben di he tong + 10");

        //produce.setProduceWaidihetong(10);
        //produce.setProduceWaidihetongComment("wai di he tong + 10");

        produce.setProduceDeng(10);
        produce.setProduceDengComment("deng + 10");
        ResponseDTO r = produceService.updateProduce(produce);
        System.out.println(r.toString());
    }

    @Test
    public void changeProduce() {
        Produce produce = new Produce();
        produce.setProduceId(14);
        produce.setProduceProductId(10);
        produce.setProduceProductName("chuang3");

        //produce.setProduceXiadan(100);
        //produce.setProduceXiadanComment("xia dan test");

        //produce.setProduceMugong(100);
        //produce.setProduceMugongComment("mu gong test");

        //produce.setProduceYoufang(100);
        //produce.setProduceYoufangComment("you fang test +5ssdsd");

        //produce.setProduceBaozhuang(100);
        //produce.setProduceBaozhuangComment("bao zhuang +5");

        //produce.setProduceTeding(100);
        //produce.setProduceTedingComment("te ding + 5");

        //produce.setProduceBeijing(100);
        //produce.setProduceBeijingComment("bei jing + 5");

        //produce.setProduceBeijingteding(100);
        //produce.setProduceBeijingtedingComment("bei jing te ding + 5");

        //produce.setProduceBendihetong(100);
        //produce.setProduceBendihetongComment("ben di he tong + 10");

        //produce.setProduceWaidihetong(100);
        //produce.setProduceWaidihetongComment("wai di he tong + 10");

        produce.setProduceDeng(100);
        //produce.setProduceDengComment("deng + 10");
        ResponseDTO r = produceService.changeProduce(produce);
        System.out.println(r.toString());
    }

    @Test
    public void deleteProduceBatch() {
    }

    @Test
    public void listProduce() {
        ResponseDTO r = produceService.listProduce(1, 10,2018, 10, 31, null);
        System.out.println(r.toString());
    }

    @Test
    public void getDetails() {
        ResponseDTO r = produceService.getDetails(14);
        System.out.println(r.toString());
    }

    @Test
    public void importFromDB() {
        ResponseDTO r = produceService.importFromDB(2018, 10, 31);
        System.out.println(r.toString());
    }

    @Test
    public void exportExcelTest() {
        String filePathAndName = "/Users/alex/Desktop/test1.xlsx";
        try {
            File file = new File(filePathAndName);
            if (!file.exists()) {
                boolean isCreate = file.createNewFile();
                System.out.println("[createNewFile] isCreate = "+ isCreate);
            }

            FileOutputStream fileOutputStream = new FileOutputStream(file);

            ResponseDTO exportResult = produceService.exportExcel(2018, 11,5, fileOutputStream);
            System.out.println("[exportExcel] exportResult = "+exportResult.toString());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}