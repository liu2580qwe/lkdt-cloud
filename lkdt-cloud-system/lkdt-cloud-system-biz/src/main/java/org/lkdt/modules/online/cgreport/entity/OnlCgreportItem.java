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

@TableName("onl_cgreport_item")
public class OnlCgreportItem implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(
            type = IdType.ID_WORKER_STR
    )
    private String id;
    private String cgrheadId;
    private String fieldName;
    private String fieldTxt;
    private Integer fieldWidth;
    private String fieldType;
    private String searchMode;
    private Integer isOrder;
    private Integer isSearch;
    private String dictCode;
    private String fieldHref;
    private Integer isShow;
    private Integer orderNum;
    private String replaceVal;
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

    public OnlCgreportItem() {
    }

    public String getId() {
        return this.id;
    }

    public String getCgrheadId() {
        return this.cgrheadId;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public String getFieldTxt() {
        return this.fieldTxt;
    }

    public Integer getFieldWidth() {
        return this.fieldWidth;
    }

    public String getFieldType() {
        return this.fieldType;
    }

    public String getSearchMode() {
        return this.searchMode;
    }

    public Integer getIsOrder() {
        return this.isOrder;
    }

    public Integer getIsSearch() {
        return this.isSearch;
    }

    public String getDictCode() {
        return this.dictCode;
    }

    public String getFieldHref() {
        return this.fieldHref;
    }

    public Integer getIsShow() {
        return this.isShow;
    }

    public Integer getOrderNum() {
        return this.orderNum;
    }

    public String getReplaceVal() {
        return this.replaceVal;
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

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setFieldTxt(String fieldTxt) {
        this.fieldTxt = fieldTxt;
    }

    public void setFieldWidth(Integer fieldWidth) {
        this.fieldWidth = fieldWidth;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public void setSearchMode(String searchMode) {
        this.searchMode = searchMode;
    }

    public void setIsOrder(Integer isOrder) {
        this.isOrder = isOrder;
    }

    public void setIsSearch(Integer isSearch) {
        this.isSearch = isSearch;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public void setFieldHref(String fieldHref) {
        this.fieldHref = fieldHref;
    }

    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public void setReplaceVal(String replaceVal) {
        this.replaceVal = replaceVal;
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
        } else if (!(o instanceof OnlCgreportItem)) {
            return false;
        } else {
            OnlCgreportItem var2 = (OnlCgreportItem)o;
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

                String var7 = this.getFieldName();
                String var8 = var2.getFieldName();
                if (var7 == null) {
                    if (var8 != null) {
                        return false;
                    }
                } else if (!var7.equals(var8)) {
                    return false;
                }

                label206: {
                    String var9 = this.getFieldTxt();
                    String var10 = var2.getFieldTxt();
                    if (var9 == null) {
                        if (var10 == null) {
                            break label206;
                        }
                    } else if (var9.equals(var10)) {
                        break label206;
                    }

                    return false;
                }

                label199: {
                    Integer var11 = this.getFieldWidth();
                    Integer var12 = var2.getFieldWidth();
                    if (var11 == null) {
                        if (var12 == null) {
                            break label199;
                        }
                    } else if (var11.equals(var12)) {
                        break label199;
                    }

                    return false;
                }

                String var13 = this.getFieldType();
                String var14 = var2.getFieldType();
                if (var13 == null) {
                    if (var14 != null) {
                        return false;
                    }
                } else if (!var13.equals(var14)) {
                    return false;
                }

                label185: {
                    String var15 = this.getSearchMode();
                    String var16 = var2.getSearchMode();
                    if (var15 == null) {
                        if (var16 == null) {
                            break label185;
                        }
                    } else if (var15.equals(var16)) {
                        break label185;
                    }

                    return false;
                }

                label178: {
                    Integer var17 = this.getIsOrder();
                    Integer var18 = var2.getIsOrder();
                    if (var17 == null) {
                        if (var18 == null) {
                            break label178;
                        }
                    } else if (var17.equals(var18)) {
                        break label178;
                    }

                    return false;
                }

                Integer var19 = this.getIsSearch();
                Integer var20 = var2.getIsSearch();
                if (var19 == null) {
                    if (var20 != null) {
                        return false;
                    }
                } else if (!var19.equals(var20)) {
                    return false;
                }

                String var21 = this.getDictCode();
                String var22 = var2.getDictCode();
                if (var21 == null) {
                    if (var22 != null) {
                        return false;
                    }
                } else if (!var21.equals(var22)) {
                    return false;
                }

                label157: {
                    String var23 = this.getFieldHref();
                    String var24 = var2.getFieldHref();
                    if (var23 == null) {
                        if (var24 == null) {
                            break label157;
                        }
                    } else if (var23.equals(var24)) {
                        break label157;
                    }

                    return false;
                }

                label150: {
                    Integer var25 = this.getIsShow();
                    Integer var26 = var2.getIsShow();
                    if (var25 == null) {
                        if (var26 == null) {
                            break label150;
                        }
                    } else if (var25.equals(var26)) {
                        break label150;
                    }

                    return false;
                }

                Integer var27 = this.getOrderNum();
                Integer var28 = var2.getOrderNum();
                if (var27 == null) {
                    if (var28 != null) {
                        return false;
                    }
                } else if (!var27.equals(var28)) {
                    return false;
                }

                label136: {
                    String var29 = this.getReplaceVal();
                    String var30 = var2.getReplaceVal();
                    if (var29 == null) {
                        if (var30 == null) {
                            break label136;
                        }
                    } else if (var29.equals(var30)) {
                        break label136;
                    }

                    return false;
                }

                String var31 = this.getCreateBy();
                String var32 = var2.getCreateBy();
                if (var31 == null) {
                    if (var32 != null) {
                        return false;
                    }
                } else if (!var31.equals(var32)) {
                    return false;
                }

                label122: {
                    Date var33 = this.getCreateTime();
                    Date var34 = var2.getCreateTime();
                    if (var33 == null) {
                        if (var34 == null) {
                            break label122;
                        }
                    } else if (var33.equals(var34)) {
                        break label122;
                    }

                    return false;
                }

                String var35 = this.getUpdateBy();
                String var36 = var2.getUpdateBy();
                if (var35 == null) {
                    if (var36 != null) {
                        return false;
                    }
                } else if (!var35.equals(var36)) {
                    return false;
                }

                Date var37 = this.getUpdateTime();
                Date var38 = var2.getUpdateTime();
                if (var37 == null) {
                    if (var38 != null) {
                        return false;
                    }
                } else if (!var37.equals(var38)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof OnlCgreportItem;
    }

    public int hashCode() {
        boolean var1 = true;
        byte var2 = 1;
        String var3 = this.getId();
        int var21 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
        String var4 = this.getCgrheadId();
        var21 = var21 * 59 + (var4 == null ? 43 : var4.hashCode());
        String var5 = this.getFieldName();
        var21 = var21 * 59 + (var5 == null ? 43 : var5.hashCode());
        String var6 = this.getFieldTxt();
        var21 = var21 * 59 + (var6 == null ? 43 : var6.hashCode());
        Integer var7 = this.getFieldWidth();
        var21 = var21 * 59 + (var7 == null ? 43 : var7.hashCode());
        String var8 = this.getFieldType();
        var21 = var21 * 59 + (var8 == null ? 43 : var8.hashCode());
        String var9 = this.getSearchMode();
        var21 = var21 * 59 + (var9 == null ? 43 : var9.hashCode());
        Integer var10 = this.getIsOrder();
        var21 = var21 * 59 + (var10 == null ? 43 : var10.hashCode());
        Integer var11 = this.getIsSearch();
        var21 = var21 * 59 + (var11 == null ? 43 : var11.hashCode());
        String var12 = this.getDictCode();
        var21 = var21 * 59 + (var12 == null ? 43 : var12.hashCode());
        String var13 = this.getFieldHref();
        var21 = var21 * 59 + (var13 == null ? 43 : var13.hashCode());
        Integer var14 = this.getIsShow();
        var21 = var21 * 59 + (var14 == null ? 43 : var14.hashCode());
        Integer var15 = this.getOrderNum();
        var21 = var21 * 59 + (var15 == null ? 43 : var15.hashCode());
        String var16 = this.getReplaceVal();
        var21 = var21 * 59 + (var16 == null ? 43 : var16.hashCode());
        String var17 = this.getCreateBy();
        var21 = var21 * 59 + (var17 == null ? 43 : var17.hashCode());
        Date var18 = this.getCreateTime();
        var21 = var21 * 59 + (var18 == null ? 43 : var18.hashCode());
        String var19 = this.getUpdateBy();
        var21 = var21 * 59 + (var19 == null ? 43 : var19.hashCode());
        Date var20 = this.getUpdateTime();
        var21 = var21 * 59 + (var20 == null ? 43 : var20.hashCode());
        return var21;
    }

    public String toString() {
        return "OnlCgreportItem(id=" + this.getId() + ", cgrheadId=" + this.getCgrheadId() + ", fieldName=" + this.getFieldName() + ", fieldTxt=" + this.getFieldTxt() + ", fieldWidth=" + this.getFieldWidth() + ", fieldType=" + this.getFieldType() + ", searchMode=" + this.getSearchMode() + ", isOrder=" + this.getIsOrder() + ", isSearch=" + this.getIsSearch() + ", dictCode=" + this.getDictCode() + ", fieldHref=" + this.getFieldHref() + ", isShow=" + this.getIsShow() + ", orderNum=" + this.getOrderNum() + ", replaceVal=" + this.getReplaceVal() + ", createBy=" + this.getCreateBy() + ", createTime=" + this.getCreateTime() + ", updateBy=" + this.getUpdateBy() + ", updateTime=" + this.getUpdateTime() + ")";
    }
}
