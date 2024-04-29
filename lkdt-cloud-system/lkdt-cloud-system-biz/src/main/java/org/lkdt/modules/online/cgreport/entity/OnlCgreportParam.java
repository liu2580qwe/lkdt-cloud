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
import org.springframework.format.annotation.DateTimeFormat;

@TableName("onl_cgreport_param")
public class OnlCgreportParam implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(
            type = IdType.ID_WORKER_STR
    )
    private String id;
    private String cgrheadId;
    private String paramName;
    private String paramTxt;
    private String paramValue;
    private Integer orderNum;
    private String createBy;
    @JsonFormat(
            timezone = "GMT+8",
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    @DateTimeFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private Date createTime;
    private String updateBy;
    @JsonFormat(
            timezone = "GMT+8",
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    @DateTimeFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private Date updateTime;

    public OnlCgreportParam() {
    }

    public String getId() {
        return this.id;
    }

    public String getCgrheadId() {
        return this.cgrheadId;
    }

    public String getParamName() {
        return this.paramName;
    }

    public String getParamTxt() {
        return this.paramTxt;
    }

    public String getParamValue() {
        return this.paramValue;
    }

    public Integer getOrderNum() {
        return this.orderNum;
    }

    public String getCreateBy() {
        return this.createBy;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public String getUpdateBy() {
        return this.updateBy;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCgrheadId(String cgrheadId) {
        this.cgrheadId = cgrheadId;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public void setParamTxt(String paramTxt) {
        this.paramTxt = paramTxt;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof OnlCgreportParam)) {
            return false;
        } else {
            OnlCgreportParam var2 = (OnlCgreportParam)o;
            if (!var2.canEqual(this)) {
                return false;
            } else {
                String var3 = this.getId();
                String var4 = var2.getId();
                if (var3 == null) {
                    if (var4 != null) {
                        return false;
                    }
                } else if (!var3.equals(var4)) {
                    return false;
                }

                String var5 = this.getCgrheadId();
                String var6 = var2.getCgrheadId();
                if (var5 == null) {
                    if (var6 != null) {
                        return false;
                    }
                } else if (!var5.equals(var6)) {
                    return false;
                }

                String var7 = this.getParamName();
                String var8 = var2.getParamName();
                if (var7 == null) {
                    if (var8 != null) {
                        return false;
                    }
                } else if (!var7.equals(var8)) {
                    return false;
                }

                label110: {
                    String var9 = this.getParamTxt();
                    String var10 = var2.getParamTxt();
                    if (var9 == null) {
                        if (var10 == null) {
                            break label110;
                        }
                    } else if (var9.equals(var10)) {
                        break label110;
                    }

                    return false;
                }

                label103: {
                    String var11 = this.getParamValue();
                    String var12 = var2.getParamValue();
                    if (var11 == null) {
                        if (var12 == null) {
                            break label103;
                        }
                    } else if (var11.equals(var12)) {
                        break label103;
                    }

                    return false;
                }

                Integer var13 = this.getOrderNum();
                Integer var14 = var2.getOrderNum();
                if (var13 == null) {
                    if (var14 != null) {
                        return false;
                    }
                } else if (!var13.equals(var14)) {
                    return false;
                }

                label89: {
                    String var15 = this.getCreateBy();
                    String var16 = var2.getCreateBy();
                    if (var15 == null) {
                        if (var16 == null) {
                            break label89;
                        }
                    } else if (var15.equals(var16)) {
                        break label89;
                    }

                    return false;
                }

                label82: {
                    Date var17 = this.getCreateTime();
                    Date var18 = var2.getCreateTime();
                    if (var17 == null) {
                        if (var18 == null) {
                            break label82;
                        }
                    } else if (var17.equals(var18)) {
                        break label82;
                    }

                    return false;
                }

                String var19 = this.getUpdateBy();
                String var20 = var2.getUpdateBy();
                if (var19 == null) {
                    if (var20 != null) {
                        return false;
                    }
                } else if (!var19.equals(var20)) {
                    return false;
                }

                Date var21 = this.getUpdateTime();
                Date var22 = var2.getUpdateTime();
                if (var21 == null) {
                    if (var22 != null) {
                        return false;
                    }
                } else if (!var21.equals(var22)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof OnlCgreportParam;
    }

    public int hashCode() {
        boolean var1 = true;
        byte var2 = 1;
        String var3 = this.getId();
        int var13 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
        String var4 = this.getCgrheadId();
        var13 = var13 * 59 + (var4 == null ? 43 : var4.hashCode());
        String var5 = this.getParamName();
        var13 = var13 * 59 + (var5 == null ? 43 : var5.hashCode());
        String var6 = this.getParamTxt();
        var13 = var13 * 59 + (var6 == null ? 43 : var6.hashCode());
        String var7 = this.getParamValue();
        var13 = var13 * 59 + (var7 == null ? 43 : var7.hashCode());
        Integer var8 = this.getOrderNum();
        var13 = var13 * 59 + (var8 == null ? 43 : var8.hashCode());
        String var9 = this.getCreateBy();
        var13 = var13 * 59 + (var9 == null ? 43 : var9.hashCode());
        Date var10 = this.getCreateTime();
        var13 = var13 * 59 + (var10 == null ? 43 : var10.hashCode());
        String var11 = this.getUpdateBy();
        var13 = var13 * 59 + (var11 == null ? 43 : var11.hashCode());
        Date var12 = this.getUpdateTime();
        var13 = var13 * 59 + (var12 == null ? 43 : var12.hashCode());
        return var13;
    }

    public String toString() {
        return "OnlCgreportParam(id=" + this.getId() + ", cgrheadId=" + this.getCgrheadId() + ", paramName=" + this.getParamName() + ", paramTxt=" + this.getParamTxt() + ", paramValue=" + this.getParamValue() + ", orderNum=" + this.getOrderNum() + ", createBy=" + this.getCreateBy() + ", createTime=" + this.getCreateTime() + ", updateBy=" + this.getUpdateBy() + ", updateTime=" + this.getUpdateTime() + ")";
    }
}
