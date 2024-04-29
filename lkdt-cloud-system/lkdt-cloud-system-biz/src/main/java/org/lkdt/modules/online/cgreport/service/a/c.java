//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.lkdt.modules.online.cgreport.service.a;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.lkdt.common.util.oConvertUtils;
import org.lkdt.modules.online.cgreport.entity.OnlCgreportItem;
import org.lkdt.modules.online.cgreport.mapper.OnlCgreportItemMapper;
import org.lkdt.modules.online.cgreport.service.IOnlCgreportItemService;
import org.springframework.stereotype.Service;

@Service("onlCgreportItemServiceImpl")
public class c extends ServiceImpl<OnlCgreportItemMapper, OnlCgreportItem> implements IOnlCgreportItemService {
    public c() {
    }

    public List<Map<String, String>> getAutoListQueryInfo(String cgrheadId) {
        LambdaQueryWrapper<OnlCgreportItem> var2 = new LambdaQueryWrapper();
        var2.eq(OnlCgreportItem::getCgrheadId, cgrheadId);
        var2.eq(OnlCgreportItem::getIsSearch, 1);
        List var3 = this.list(var2);
        ArrayList var4 = new ArrayList();
        int var5 = 0;

        HashMap var8;
        for(Iterator var6 = var3.iterator(); var6.hasNext(); var4.add(var8)) {
            OnlCgreportItem var7 = (OnlCgreportItem)var6.next();
            var8 = new HashMap();
            var8.put("label", var7.getFieldTxt());
            if (oConvertUtils.isNotEmpty(var7.getDictCode())) {
                var8.put("view", "list");
            } else {
                var8.put("view", var7.getFieldType().toLowerCase());
            }

            var8.put("mode", oConvertUtils.isEmpty(var7.getSearchMode()) ? "single" : var7.getSearchMode());
            var8.put("field", var7.getFieldName());
            ++var5;
            if (var5 > 2) {
                var8.put("hidden", "1");
            }
        }

        return var4;
    }
}
