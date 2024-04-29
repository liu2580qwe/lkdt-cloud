//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.lkdt.modules.online.cgreport.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.lkdt.modules.online.cgreport.entity.OnlCgreportHead;
import org.lkdt.modules.online.cgreport.entity.OnlCgreportParam;
@Mapper
public interface OnlCgreportHeadMapper extends BaseMapper<OnlCgreportHead> {
    List<Map<?, ?>> executeSelete(@Param("sql") String var1);

    IPage<Map<String, Object>> selectPageBySql(Page<Map<String, Object>> var1, @Param("sqlStr") String var2);

    Long queryCountBySql(@Param("sql") String var1);

    Map<String, Object> queryCgReportMainConfig(@Param("reportId") String var1);

    List<Map<String, Object>> queryCgReportItems(@Param("cgrheadId") String var1);

    List<OnlCgreportParam> queryCgReportParams(@Param("cgrheadId") String var1);
}
