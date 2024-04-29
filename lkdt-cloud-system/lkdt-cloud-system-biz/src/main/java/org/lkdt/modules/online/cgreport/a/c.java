//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.lkdt.modules.online.cgreport.a;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.lkdt.common.api.vo.Result;
import org.lkdt.common.system.query.QueryGenerator;
import org.lkdt.modules.online.cgreport.entity.OnlCgreportItem;
import org.lkdt.modules.online.cgreport.service.IOnlCgreportItemService;
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

@RestController("onlCgreportItemController")
@RequestMapping({"/online/cgreport/item"})
public class c {
    private static final Logger a = LoggerFactory.getLogger(c.class);
    @Autowired
    private IOnlCgreportItemService onlCgreportItemService;

    public c() {
    }

    @GetMapping({"/listByHeadId"})
    public Result<?> a(@RequestParam("headId") String var1) {
        QueryWrapper var2 = new QueryWrapper();
        var2.eq("cgrhead_id", var1);
        var2.orderByAsc("order_num");
        List var3 = this.onlCgreportItemService.list(var2);
        Result var4 = new Result();
        var4.setSuccess(true);
        var4.setResult(var3);
        return var4;
    }

    @GetMapping({"/list"})
    public Result<IPage<OnlCgreportItem>> a(OnlCgreportItem var1, @RequestParam(name = "pageNo",defaultValue = "1") Integer var2, @RequestParam(name = "pageSize",defaultValue = "10") Integer var3, HttpServletRequest var4) {
        Result var5 = new Result();
        QueryWrapper var6 = QueryGenerator.initQueryWrapper(var1, var4.getParameterMap());
        Page var7 = new Page((long)var2, (long)var3);
        IPage var8 = this.onlCgreportItemService.page(var7, var6);
        var5.setSuccess(true);
        var5.setResult(var8);
        return var5;
    }

    @PostMapping({"/add"})
    public Result<?> a(@RequestBody OnlCgreportItem var1) {
        this.onlCgreportItemService.save(var1);
        return Result.ok("添加成功!");
    }

    @PutMapping({"/edit"})
    public Result<?> b(@RequestBody OnlCgreportItem var1) {
        this.onlCgreportItemService.updateById(var1);
        return Result.ok("编辑成功!");
    }

    @DeleteMapping({"/delete"})
    public Result<?> b(@RequestParam(name = "id",required = true) String var1) {
        this.onlCgreportItemService.removeById(var1);
        return Result.ok("删除成功!");
    }

    @DeleteMapping({"/deleteBatch"})
    public Result<?> c(@RequestParam(name = "ids",required = true) String var1) {
        this.onlCgreportItemService.removeByIds(Arrays.asList(var1.split(",")));
        return Result.ok("批量删除成功!");
    }

    @GetMapping({"/queryById"})
    public Result<OnlCgreportItem> d(@RequestParam(name = "id",required = true) String var1) {
        Result var2 = new Result();
        OnlCgreportItem var3 = (OnlCgreportItem)this.onlCgreportItemService.getById(var1);
        var2.setResult(var3);
        var2.setSuccess(true);
        return var2;
    }
}
