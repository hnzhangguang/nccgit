package com.yonyou.common.vo;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;


/*
 * @功能: 用户实体
 * @Date  2020/8/11;
 * @Author zhangg
 **/
public class NCCUserVo extends LitePalSupport implements Serializable {


    private String userName;
    private String userCode;
    private String userType;
    private String password;
    private String tenantId;
    private String accessToken;
    private String userid;
    private String yhtUserId;
    private String phone;  // 手机号

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getYhtUserId() {
        return yhtUserId;
    }

    public void setYhtUserId(String yhtUserId) {
        this.yhtUserId = yhtUserId;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UserVo{");
        sb.append("userName='").append(userName).append('\'');
        sb.append(", userCode='").append(userCode).append('\'');
        sb.append(", userType='").append(userType).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", tenantId='").append(tenantId).append('\'');
        sb.append(", accessToken='").append(accessToken).append('\'');
        sb.append(", userid='").append(userid).append('\'');
        sb.append(", yhtUserId='").append(yhtUserId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
