//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.lkdt.modules.online.cgreport.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lkdt.modules.online.cgreport.entity.OnlCgreportItem;

import java.util.List;
import java.util.Map;

public interface IOnlCgreportItemService extends IService<OnlCgreportItem> {
    List<Map<String, String>> getAutoListQueryInfo(String var1);
}
