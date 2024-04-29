//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.lkdt.modules.online.cgreport.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lkdt.common.api.vo.Result;
import org.lkdt.modules.online.cgreport.entity.OnlCgreportHead;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface IOnlCgreportHeadService extends IService<OnlCgreportHead> {
    Result<?> delete(String var1);

    Result<?> bathDelete(String[] var1);

    Map<String, Object> executeSelectSql(String var1, String var2, Map<String, Object> var3) throws SQLException;

    Map<String, Object> executeSelectSqlDynamic(String var1, String var2, Map<String, Object> var3, String var4);

    List<String> getSqlFields(String var1, String var2) throws SQLException;

    List<String> getSqlParams(String var1);

    Map<String, Object> queryCgReportConfig(String var1);

    List<Map<?, ?>> queryByCgReportSql(String var1, Map var2, Map var3, int var4, int var5);
}
