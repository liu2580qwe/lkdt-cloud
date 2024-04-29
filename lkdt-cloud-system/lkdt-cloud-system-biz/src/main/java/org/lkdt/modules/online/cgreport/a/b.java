//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.lkdt.modules.online.cgreport.a;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.system.api.ISysBaseAPI;
import org.lkdt.common.system.query.QueryGenerator;
import org.lkdt.common.system.vo.DynamicDataSourceModel;
import org.lkdt.common.util.SqlInjectionUtil;
import org.lkdt.modules.online.cgreport.entity.OnlCgreportHead;
import org.lkdt.modules.online.cgreport.entity.OnlCgreportItem;
import org.lkdt.modules.online.cgreport.entity.OnlCgreportParam;
import org.lkdt.modules.online.cgreport.model.OnlCgreportModel;
import org.lkdt.modules.online.cgreport.service.IOnlCgreportHeadService;
import org.lkdt.modules.online.cgreport.service.IOnlCgreportItemService;
import org.lkdt.modules.online.cgreport.service.IOnlCgreportParamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("onlCgreportHeadController")
@RequestMapping({"/online/cgreport/head"})
public class b {
    private static final Logger a = LoggerFactory.getLogger(b.class);
    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private IOnlCgreportHeadService onlCgreportHeadService;
    @Autowired
    private IOnlCgreportParamService onlCgreportParamService;
    @Autowired
    private IOnlCgreportItemService onlCgreportItemService;

    public b() {
    }

    @GetMapping({"/parseSql"})
    public Result<?> a(@RequestParam(name = "sql") String var1, @RequestParam(name = "dbKey",required = false) String var2) {
        if (StringUtils.isNotBlank(var2)) {
            DynamicDataSourceModel var3 = this.sysBaseAPI.getDynamicDbSourceByCode(var2);
            if (var3 == null) {
                return Result.error("数据源不存在");
            }
        }

        HashMap var13 = new HashMap();
        ArrayList var4 = new ArrayList();
        ArrayList var5 = new ArrayList();
        List var6 = null;
        List var7 = null;

        try {
            a.info("Online报表，sql解析：" + var1);
            this.sysBaseAPI.addLog("Online报表，sql解析：" + var1, 2, 2);
            SqlInjectionUtil.specialFilterContentForOnlineReport(var1);
            var6 = this.onlCgreportHeadService.getSqlFields(var1, var2);
            var7 = this.onlCgreportHeadService.getSqlParams(var1);
            int var8 = 1;

            String var14;
            Iterator var15;
            for(var15 = var6.iterator(); var15.hasNext(); ++var8) {
                var14 = (String)var15.next();
                OnlCgreportItem var11 = new OnlCgreportItem();
                var11.setFieldName(var14.toLowerCase());
                var11.setFieldTxt(var14);
                var11.setIsShow(1);
                var11.setOrderNum(var8);
                var11.setId(org.lkdt.modules.online.cgform.util.b.a());
                var11.setFieldType("String");
                var4.add(var11);
            }

            var15 = var7.iterator();

            while(var15.hasNext()) {
                var14 = (String)var15.next();
                OnlCgreportParam var16 = new OnlCgreportParam();
                var16.setParamName(var14);
                var16.setParamTxt(var14);
                var5.add(var16);
            }

            var13.put("fields", var4);
            var13.put("params", var5);
            return Result.ok(var13);
        } catch (Exception var12) {
            a.error(var12.getMessage(), var12);
            String var9 = "解析失败，";
            int var10 = var12.getMessage().indexOf("Connection refused: connect");
            if (var10 != -1) {
                var9 = var9 + "数据源连接失败.";
            } else if (var12.getMessage().indexOf("值可能存在SQL注入风险") != -1) {
                var9 = var9 + "SQL可能存在SQL注入风险.";
            } else if (var12.getMessage().indexOf("该报表sql没有数据") != -1) {
                var9 = var9 + "报表sql查询数据为空，无法解析字段.";
            } else if (var12.getMessage().indexOf("SqlServer不支持SQL内排序") != -1) {
                var9 = var9 + "SqlServer不支持SQL内排序.";
            } else {
                var9 = var9 + "SQL语法错误.";
            }

            return Result.error(var9);
        }
    }


    @GetMapping({"/list"})
    public Result<IPage<OnlCgreportHead>> a(OnlCgreportHead var1, @RequestParam(name = "pageNo",defaultValue = "1") Integer var2, @RequestParam(name = "pageSize",defaultValue = "10") Integer var3, HttpServletRequest var4) {
        Result var5 = new Result();
        QueryWrapper var6 = QueryGenerator.initQueryWrapper(var1, var4.getParameterMap());
        Page var7 = new Page((long)var2, (long)var3);
        IPage var8 = this.onlCgreportHeadService.page(var7, var6);
        var5.setSuccess(true);
        var5.setResult(var8);
        return var5;
    }

    @PostMapping({"/add"})
    public Result<?> a(@RequestBody org.lkdt.modules.online.cgreport.model.OnlCgreportModel var1) {
        Result var2 = new Result();

        try {
            String var3 = org.lkdt.modules.online.cgform.util.b.a();
            OnlCgreportHead var4 = var1.getHead();
            List var5 = var1.getParams();
            List var6 = var1.getItems();
            var4.setId(var3);
            Iterator var7 = var5.iterator();

            while(var7.hasNext()) {
                OnlCgreportParam var8 = (OnlCgreportParam)var7.next();
                var8.setId((String)null);
                var8.setCgrheadId(var3);
            }

            var7 = var6.iterator();

            while(var7.hasNext()) {
                OnlCgreportItem var10 = (OnlCgreportItem)var7.next();
                var10.setId((String)null);
                var10.setFieldName(var10.getFieldName().trim().toLowerCase());
                var10.setCgrheadId(var3);
            }

            this.onlCgreportHeadService.save(var4);
            this.onlCgreportParamService.saveBatch(var5);
            this.onlCgreportItemService.saveBatch(var6);
            var2.success("添加成功！");
        } catch (Exception var9) {
            a.error(var9.getMessage(), var9);
            var2.error500("操作失败");
        }

        return var2;
    }

//    @PutMapping({"/editAll"})
//    public Result<?> b(@RequestBody OnlCgreportModel var1) {
//        try {
//            return this.onlCgreportHeadService.editAll(var1);
//        } catch (Exception var3) {
//            a.error(var3.getMessage(), var3);
//            return Result.error("操作失败");
//        }
//    }

    @DeleteMapping({"/delete"})
    public Result<?> a(@RequestParam(name = "id",required = true) String var1) {
        return this.onlCgreportHeadService.delete(var1);
    }

    @DeleteMapping({"/deleteBatch"})
    public Result<?> b(@RequestParam(name = "ids",required = true) String var1) {
        return this.onlCgreportHeadService.bathDelete(var1.split(","));
    }

    @GetMapping({"/queryById"})
    public Result<OnlCgreportHead> c(@RequestParam(name = "id",required = true) String var1) {
        Result var2 = new Result();
        OnlCgreportHead var3 = (OnlCgreportHead)this.onlCgreportHeadService.getById(var1);
        var2.setResult(var3);
        return var2;
    }
}
