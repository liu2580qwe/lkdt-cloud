//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.lkdt.modules.online.cgreport.service.a;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.lkdt.modules.online.cgreport.service.CgReportExcelServiceI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("cgReportExcelService")
public class a implements CgReportExcelServiceI {
    private static final Logger a = LoggerFactory.getLogger(a.class);

    public a() {
    }

    public HSSFWorkbook exportExcel(String title, Collection<?> titleSet, Collection<?> dataSet) {
        HSSFWorkbook var4 = null;

        try {
            if (titleSet == null || titleSet.size() == 0) {
                throw new Exception("读取表头失败！");
            }

            if (title == null) {
                title = "";
            }

            var4 = new HSSFWorkbook();
            HSSFSheet var5 = var4.createSheet(title);
            int var6 = 0;
            int var7 = 0;
            Row var8 = var5.createRow(var6);
            var8.setHeight((short)450);
            HSSFCellStyle var9 = this.a(var4);
            List var10 = (List)titleSet;
            Iterator var11 = dataSet.iterator();

            Map var13;
            for(Iterator var12 = var10.iterator(); var12.hasNext(); ++var7) {
                var13 = (Map)var12.next();
                String var14 = (String)var13.get("field_txt");
                Cell var15 = var8.createCell(var7);
                HSSFRichTextString var16 = new HSSFRichTextString(var14);
                var15.setCellValue(var16);
                var15.setCellStyle(var9);
            }

            HSSFCellStyle var21 = this.c(var4);

            while(var11.hasNext()) {
                var7 = 0;
                ++var6;
                var8 = var5.createRow(var6);
                var13 = (Map)var11.next();

                for(Iterator var23 = var10.iterator(); var23.hasNext(); ++var7) {
                    Map var24 = (Map)var23.next();
                    String var25 = (String)var24.get("field_name");
                    String var17 = var13.get(var25) == null ? "" : var13.get(var25).toString();
                    Cell var18 = var8.createCell(var7);
                    HSSFRichTextString var19 = new HSSFRichTextString(var17);
                    var18.setCellStyle(var21);
                    var18.setCellValue(var19);
                }
            }

            for(int var22 = 0; var22 < var10.size(); ++var22) {
                var5.autoSizeColumn(var22);
            }
        } catch (Exception var20) {
            a.error(var20.getMessage(), var20);
        }

        return var4;
    }

    private HSSFCellStyle a(HSSFWorkbook var1) {
        HSSFCellStyle var2 = var1.createCellStyle();
        var2.setBorderLeft((short)1);
        var2.setBorderRight((short)1);
        var2.setBorderBottom((short)1);
        var2.setBorderTop((short)1);
        var2.setAlignment((short)2);
        var2.setFillForegroundColor((short)22);
        var2.setFillPattern((short)1);
        return var2;
    }

    private void a(int var1, int var2, HSSFWorkbook var3) {
        HSSFSheet var4 = var3.getSheetAt(0);
        HSSFCellStyle var5 = this.c(var3);

        for(int var6 = 1; var6 <= var1; ++var6) {
            Row var7 = var4.createRow(var6);

            for(int var8 = 0; var8 < var2; ++var8) {
                var7.createCell(var8).setCellStyle(var5);
            }
        }

    }

    private HSSFCellStyle b(HSSFWorkbook var1) {
        HSSFCellStyle var2 = var1.createCellStyle();
        var2.setBorderLeft((short)1);
        var2.setBorderRight((short)1);
        var2.setBorderBottom((short)1);
        var2.setBorderTop((short)1);
        var2.setFillForegroundColor((short)41);
        var2.setFillPattern((short)1);
        return var2;
    }

    private HSSFCellStyle c(HSSFWorkbook var1) {
        HSSFCellStyle var2 = var1.createCellStyle();
        var2.setBorderLeft((short)1);
        var2.setBorderRight((short)1);
        var2.setBorderBottom((short)1);
        var2.setBorderTop((short)1);
        return var2;
    }
}
