//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.lkdt.modules.online.cgreport.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;

import org.lkdt.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

@TableName("onl_cgreport_head")
public class OnlCgreportHead implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(
            type = IdType.ID_WORKER_STR
    )
    private String id;
    private String code;
    private String name;
    private String cgrSql;
    private String returnValField;
    private String returnTxtField;
    private String returnType;
    @Dict(
            dicCode = "code",
            dicText = "name",
            dictTable = "sys_data_source"
    )
    private String dbSource;
    private String content;
    @JsonFormat(
            timezone = "GMT+8",
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    @DateTimeFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private Date updateTime;
    private String updateBy;
    @JsonFormat(
            timezone = "GMT+8",
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    @DateTimeFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private Date createTime;
    private String createBy;

    public OnlCgreportHead() {
    }

    public String getId() {
        return this.id;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public String getCgrSql() {
        return this.cgrSql;
    }

    public String getReturnValField() {
        return this.returnValField;
    }

    public String getReturnTxtField() {
        return this.returnTxtField;
    }

    public String getReturnType() {
        return this.returnType;
    }

    public String getDbSource() {
        return this.dbSource;
    }

    public String getContent() {
        return this.content;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public String getUpdateBy() {
        return this.updateBy;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public String getCreateBy() {
        return this.createBy;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCgrSql(String cgrSql) {
        this.cgrSql = cgrSql;
    }

    public void setReturnValField(String returnValField) {
        this.returnValField = returnValField;
    }

    public void setReturnTxtField(String returnTxtField) {
        this.returnTxtField = returnTxtField;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public void setDbSource(String dbSource) {
        this.dbSource = dbSource;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof OnlCgreportHead)) {
            return false;
        } else {
            OnlCgreportHead var2 = (OnlCgreportHead)o;
            if (!var2.canEqual(this)) {
                return false;
            } else {
                label167: {
                    String var3 = this.getId();
                    String var4 = var2.getId();
                    if (var3 == null) {
                        if (var4 == null) {
                            break label167;
                        }
                    } else if (var3.equals(var4)) {
                        break label167;
                    }

                    return false;
                }

                String var5 = this.getCode();
                String var6 = var2.getCode();
                if (var5 == null) {
                    if (var6 != null) {
                        return false;
                    }
                } else if (!var5.equals(var6)) {
                    return false;
                }

                label153: {
                    String var7 = this.getName();
                    String var8 = var2.getName();
                    if (var7 == null) {
                        if (var8 == null) {
                            break label153;
                        }
                    } else if (var7.equals(var8)) {
                        break label153;
                    }

                    return false;
                }

                String var9 = this.getCgrSql();
                String var10 = var2.getCgrSql();
                if (var9 == null) {
                    if (var10 != null) {
                        return false;
                    }
                } else if (!var9.equals(var10)) {
                    return false;
                }

                label139: {
                    String var11 = this.getReturnValField();
                    String var12 = var2.getReturnValField();
                    if (var11 == null) {
                        if (var12 == null) {
                            break label139;
                        }
                    } else if (var11.equals(var12)) {
                        break label139;
                    }

                    return false;
                }

                String var13 = this.getReturnTxtField();
                String var14 = var2.getReturnTxtField();
                if (var13 == null) {
                    if (var14 != null) {
                        return false;
                    }
                } else if (!var13.equals(var14)) {
                    return false;
                }

                label125: {
                    String var15 = this.getReturnType();
                    String var16 = var2.getReturnType();
                    if (var15 == null) {
                        if (var16 == null) {
                            break label125;
                        }
                    } else if (var15.equals(var16)) {
                        break label125;
                    }

                    return false;
                }

                label118: {
                    String var17 = this.getDbSource();
                    String var18 = var2.getDbSource();
                    if (var17 == null) {
                        if (var18 == null) {
                            break label118;
                        }
                    } else if (var17.equals(var18)) {
                        break label118;
                    }

                    return false;
                }

                String var19 = this.getContent();
                String var20 = var2.getContent();
                if (var19 == null) {
                    if (var20 != null) {
                        return false;
                    }
                } else if (!var19.equals(var20)) {
                    return false;
                }

                label104: {
                    Date var21 = this.getUpdateTime();
                    Date var22 = var2.getUpdateTime();
                    if (var21 == null) {
                        if (var22 == null) {
                            break label104;
                        }
                    } else if (var21.equals(var22)) {
                        break label104;
                    }

                    return false;
                }

                label97: {
                    String var23 = this.getUpdateBy();
                    String var24 = var2.getUpdateBy();
                    if (var23 == null) {
                        if (var24 == null) {
                            break label97;
                        }
                    } else if (var23.equals(var24)) {
                        break label97;
                    }

                    return false;
                }

                Date var25 = this.getCreateTime();
                Date var26 = var2.getCreateTime();
                if (var25 == null) {
                    if (var26 != null) {
                        return false;
                    }
                } else if (!var25.equals(var26)) {
                    return false;
                }

                String var27 = this.getCreateBy();
                String var28 = var2.getCreateBy();
                if (var27 == null) {
                    if (var28 != null) {
                        return false;
                    }
                } else if (!var27.equals(var28)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof OnlCgreportHead;
    }

    public int hashCode() {
        boolean var1 = true;
        byte var2 = 1;
        String var3 = this.getId();
        int var16 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
        String var4 = this.getCode();
        var16 = var16 * 59 + (var4 == null ? 43 : var4.hashCode());
        String var5 = this.getName();
        var16 = var16 * 59 + (var5 == null ? 43 : var5.hashCode());
        String var6 = this.getCgrSql();
        var16 = var16 * 59 + (var6 == null ? 43 : var6.hashCode());
        String var7 = this.getReturnValField();
        var16 = var16 * 59 + (var7 == null ? 43 : var7.hashCode());
        String var8 = this.getReturnTxtField();
        var16 = var16 * 59 + (var8 == null ? 43 : var8.hashCode());
        String var9 = this.getReturnType();
        var16 = var16 * 59 + (var9 == null ? 43 : var9.hashCode());
        String var10 = this.getDbSource();
        var16 = var16 * 59 + (var10 == null ? 43 : var10.hashCode());
        String var11 = this.getContent();
        var16 = var16 * 59 + (var11 == null ? 43 : var11.hashCode());
        Date var12 = this.getUpdateTime();
        var16 = var16 * 59 + (var12 == null ? 43 : var12.hashCode());
        String var13 = this.getUpdateBy();
        var16 = var16 * 59 + (var13 == null ? 43 : var13.hashCode());
        Date var14 = this.getCreateTime();
        var16 = var16 * 59 + (var14 == null ? 43 : var14.hashCode());
        String var15 = this.getCreateBy();
        var16 = var16 * 59 + (var15 == null ? 43 : var15.hashCode());
        return var16;
    }

    public String toString() {
        return "OnlCgreportHead(id=" + this.getId() + ", code=" + this.getCode() + ", name=" + this.getName() + ", cgrSql=" + this.getCgrSql() + ", returnValField=" + this.getReturnValField() + ", returnTxtField=" + this.getReturnTxtField() + ", returnType=" + this.getReturnType() + ", dbSource=" + this.getDbSource() + ", content=" + this.getContent() + ", updateTime=" + this.getUpdateTime() + ", updateBy=" + this.getUpdateBy() + ", createTime=" + this.getCreateTime() + ", createBy=" + this.getCreateBy() + ")";
    }
}
