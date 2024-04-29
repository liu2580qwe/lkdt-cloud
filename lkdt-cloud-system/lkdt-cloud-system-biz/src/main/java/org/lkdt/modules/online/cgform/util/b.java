//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.lkdt.modules.online.cgform.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecgframework.core.util.ApplicationContextUtil;
import org.jeecgframework.poi.excel.entity.params.ExcelExportEntity;
import org.lkdt.common.system.api.ISysBaseAPI;
import org.lkdt.common.system.query.MatchTypeEnum;
import org.lkdt.common.system.query.QueryGenerator;
import org.lkdt.common.system.vo.LoginUser;
import org.lkdt.common.system.vo.SysPermissionDataRuleModel;
import org.lkdt.common.util.*;
import org.lkdt.common.util.jsonschema.BaseColumn;
import org.lkdt.common.util.jsonschema.CommonProperty;
import org.lkdt.common.util.jsonschema.JsonSchemaDescrip;
import org.lkdt.common.util.jsonschema.JsonschemaUtil;
import org.lkdt.common.util.jsonschema.validate.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class b {
    private static final Logger as = LoggerFactory.getLogger(b.class);
    public static final String a = "SELECT ";
    public static final String b = " FROM ";
    public static final String c = " AND ";
    public static final String d = " like ";
    public static final String e = " COUNT(*) ";
    public static final String f = " where 1=1  ";
    public static final String g = " ORDER BY ";
    public static final String h = "asc";
    public static final String i = "desc";
    public static final String j = "=";
    public static final String k = "!=";
    public static final String l = ">=";
    public static final String m = ">";
    public static final String n = "<=";
    public static final String o = "<";
    public static final String p = "Y";
    public static final String q = "$";
    public static final String r = "CREATE_TIME";
    public static final String s = "CREATE_BY";
    public static final String t = "UPDATE_TIME";
    public static final String u = "UPDATE_BY";
    public static final String v = "SYS_ORG_CODE";
    public static final int w = 2;
    public static final String x = "'";
    public static final String y = "N";
    public static final String z = ",";
    public static final String A = "single";
    public static final String B = "id";
    public static final String C = "bpm_status";
    public static final String D = "1";
    public static final String E = "force";
    public static final String F = "normal";
    public static final String G = "switch";
    public static final String H = "popup";
    public static final String I = "image";
    public static final String J = "sel_tree";
    public static final String K = "cat_tree";
    public static final String L = "link_down";
    public static final String M = "SYS_USER";
    public static final String N = "REALNAME";
    public static final String O = "USERNAME";
    public static final String P = "SYS_DEPART";
    public static final String Q = "DEPART_NAME";
    public static final String R = "ID";
    public static final String S = "SYS_CATEGORY";
    public static final String T = "NAME";
    public static final String U = "CODE";
    public static final String V = "ID";
    public static final String W = "PID";
    public static final String X = "HAS_CHILD";
    public static final String Y = "sel_search";
    public static final String Z = "sub-table-design_";
    public static final String aa = "import";
    public static final String ab = "export";
    public static final String ac = "query";
    public static final String ad = "form";
    public static final String ae = "list";
    public static final String af = "1";
    public static final String ag = "start";
    public static final String ah = "erp";
    public static final String ai = "exportSingleOnly";
    public static final String aj = "isSingleTableImport";
    public static final String ak = "foreignKeys";
    public static final int al = 1;
    public static final int am = 2;
    public static final int an = 0;
    public static final int ao = 1;
    public static final String ap = "1";
    public static final String aq = "id";
    public static final String ar = "center";
    private static final String at = "beforeAdd,beforeEdit,afterAdd,afterEdit,beforeDelete,afterDelete,mounted,created";
    private static String au;

    public b() {
    }


    public static String a(String var0) {
        return " to_date('" + var0 + "','yyyy-MM-dd HH24:mi:ss')";
    }

    public static String b(String var0) {
        return " to_date('" + var0 + "','yyyy-MM-dd')";
    }
    public static Map<String, Object> b(Map<String, Object> var0) {
        HashMap var1 = new HashMap();
        if (var0 != null && !var0.isEmpty()) {
            Set var2 = var0.keySet();
            Iterator var3 = var2.iterator();

            while(var3.hasNext()) {
                String var4 = (String)var3.next();
                Object var5 = var0.get(var4);
                if (var5 instanceof Clob) {
                    var5 = a((Clob)var5);
                } else if (var5 instanceof byte[]) {
                    var5 = new String((byte[])((byte[])var5));
                } else if (var5 instanceof Blob) {
                    try {
                        if (var5 != null) {
                            Blob var6 = (Blob)var5;
                            var5 = new String(var6.getBytes(1L, (int)var6.length()), "UTF-8");
                        }
                    } catch (Exception var7) {
                        var7.printStackTrace();
                    }
                }

                String var8 = var4.toLowerCase();
                var1.put(var8, var5);
            }

            return var1;
        } else {
            return var1;
        }
    }


    public static boolean c(String var0) {
        if ("list".equals(var0)) {
            return true;
        } else if ("radio".equals(var0)) {
            return true;
        } else if ("checkbox".equals(var0)) {
            return true;
        } else {
            return "list_multi".equals(var0);
        }
    }

    public static List<Map<String, Object>> d(List<Map<String, Object>> var0) {
        ArrayList var1 = new ArrayList();
        Iterator var2 = var0.iterator();

        while(var2.hasNext()) {
            Map var3 = (Map)var2.next();
            HashMap var4 = new HashMap();
            Set var5 = var3.keySet();
            Iterator var6 = var5.iterator();

            while(var6.hasNext()) {
                String var7 = (String)var6.next();
                Object var8 = var3.get(var7);
                if (var8 instanceof Clob) {
                    var8 = a((Clob)var8);
                } else if (var8 instanceof byte[]) {
                    var8 = new String((byte[])((byte[])var8));
                } else if (var8 instanceof Blob) {
                    try {
                        if (var8 != null) {
                            Blob var9 = (Blob)var8;
                            var8 = new String(var9.getBytes(1L, (int)var9.length()), "UTF-8");
                        }
                    } catch (Exception var10) {
                        var10.printStackTrace();
                    }
                }

                String var11 = var7.toLowerCase();
                var4.put(var11, var8);
            }

            var1.add(var4);
        }

        return var1;
    }

    public static String a(Clob var0) {
        String var1 = "";

        try {
            Reader var2 = var0.getCharacterStream();
            char[] var3 = new char[(int)var0.length()];
            var2.read(var3);
            var1 = new String(var3);
            var2.close();
        } catch (IOException var4) {
            var4.printStackTrace();
        } catch (SQLException var5) {
            var5.printStackTrace();
        }

        return var1;
    }


    public static String a() {
        long var0 = IdWorker.getId();
        return String.valueOf(var0);
    }

    public static String a(Exception var0) {
        String var1 = var0.getCause() != null ? var0.getCause().getMessage() : var0.getMessage();
        if (var1.indexOf("ORA-01452") != -1) {
            var1 = "ORA-01452: 无法 CREATE UNIQUE INDEX; 找到重复的关键字";
        } else if (var1.indexOf("duplicate key") != -1) {
            var1 = "无法 CREATE UNIQUE INDEX; 找到重复的关键字";
        }

        return var1;
    }

    private static String getDatabseType() {
        if (oConvertUtils.isNotEmpty(au)) {
            return au;
        } else {
            try {
                ISysBaseAPI var0 = (ISysBaseAPI)ApplicationContextUtil.getContext().getBean(ISysBaseAPI.class);
                au = var0.getDatabaseType();
                return au;
            } catch (Exception var1) {
                var1.printStackTrace();
                return au;
            }
        }
    }
}
