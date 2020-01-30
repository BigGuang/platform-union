package com.waps.service.jd.db.model;

import java.io.Serializable;
import java.util.Date;

/**
 * waps_jd_user
 * @author 
 */
public class WapsJdUser implements Serializable {
    private Integer id;

    /**
     * 用户名（代理账号）
     */
    private String uName;

    /**
     * 代理名称
     */
    private String uMark;

    /**
     * 密码
     */
    private String uPwd;

    private Float uBalance;

    /**
     * 分成比例
     */
    private Float uFee;

    /**
     * 电话
     */
    private String uPhone;

    /**
     * qq
     */
    private String uQq;

    /**
     * 支付宝账号
     */
    private String uAlpay;

    /**
     * 微信账号
     */
    private String uWx;

    /**
     * 开户行
     */
    private String uBank;

    /**
     * 银行卡号
     */
    private String uCard;

    /**
     * 账号名称
     */
    private String uBankAccount;

    /**
     * 推荐位ID
     */
    private String uPid;

    /**
     * 小程序openid
     */
    private String uXOpenid;

    /**
     * 微信openid
     */
    private String uOpenid;

    /**
     * 分群ID
     */
    private String uGid;

    /**
     * 信用卡真实姓名
     */
    private String uPayname;

    /**
     * 是否使用0使用1关闭
     */
    private Integer uYn;

    /**
     * 创建时间
     */
    private Date uCreatetime;

    /**
     * 0群主1代理2超级会员
     */
    private Integer uType;

    /**
     * 成为教练的时间2019-12-12格式
     */
    private String uTime;

    /**
     * 成为高级教练的时间
     */
    private Integer oldType;

    /**
     * 商城域名
     */
    private String uDomain;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getuMark() {
        return uMark;
    }

    public void setuMark(String uMark) {
        this.uMark = uMark;
    }

    public String getuPwd() {
        return uPwd;
    }

    public void setuPwd(String uPwd) {
        this.uPwd = uPwd;
    }

    public Float getuBalance() {
        return uBalance;
    }

    public void setuBalance(Float uBalance) {
        this.uBalance = uBalance;
    }

    public Float getuFee() {
        return uFee;
    }

    public void setuFee(Float uFee) {
        this.uFee = uFee;
    }

    public String getuPhone() {
        return uPhone;
    }

    public void setuPhone(String uPhone) {
        this.uPhone = uPhone;
    }

    public String getuQq() {
        return uQq;
    }

    public void setuQq(String uQq) {
        this.uQq = uQq;
    }

    public String getuAlpay() {
        return uAlpay;
    }

    public void setuAlpay(String uAlpay) {
        this.uAlpay = uAlpay;
    }

    public String getuWx() {
        return uWx;
    }

    public void setuWx(String uWx) {
        this.uWx = uWx;
    }

    public String getuBank() {
        return uBank;
    }

    public void setuBank(String uBank) {
        this.uBank = uBank;
    }

    public String getuCard() {
        return uCard;
    }

    public void setuCard(String uCard) {
        this.uCard = uCard;
    }

    public String getuBankAccount() {
        return uBankAccount;
    }

    public void setuBankAccount(String uBankAccount) {
        this.uBankAccount = uBankAccount;
    }

    public String getuPid() {
        return uPid;
    }

    public void setuPid(String uPid) {
        this.uPid = uPid;
    }

    public String getuXOpenid() {
        return uXOpenid;
    }

    public void setuXOpenid(String uXOpenid) {
        this.uXOpenid = uXOpenid;
    }

    public String getuOpenid() {
        return uOpenid;
    }

    public void setuOpenid(String uOpenid) {
        this.uOpenid = uOpenid;
    }

    public String getuGid() {
        return uGid;
    }

    public void setuGid(String uGid) {
        this.uGid = uGid;
    }

    public String getuPayname() {
        return uPayname;
    }

    public void setuPayname(String uPayname) {
        this.uPayname = uPayname;
    }

    public Integer getuYn() {
        return uYn;
    }

    public void setuYn(Integer uYn) {
        this.uYn = uYn;
    }

    public Date getuCreatetime() {
        return uCreatetime;
    }

    public void setuCreatetime(Date uCreatetime) {
        this.uCreatetime = uCreatetime;
    }

    public Integer getuType() {
        return uType;
    }

    public void setuType(Integer uType) {
        this.uType = uType;
    }

    public String getuTime() {
        return uTime;
    }

    public void setuTime(String uTime) {
        this.uTime = uTime;
    }

    public Integer getOldType() {
        return oldType;
    }

    public void setOldType(Integer oldType) {
        this.oldType = oldType;
    }

    public String getuDomain() {
        return uDomain;
    }

    public void setuDomain(String uDomain) {
        this.uDomain = uDomain;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        WapsJdUser other = (WapsJdUser) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getuName() == null ? other.getuName() == null : this.getuName().equals(other.getuName()))
            && (this.getuMark() == null ? other.getuMark() == null : this.getuMark().equals(other.getuMark()))
            && (this.getuPwd() == null ? other.getuPwd() == null : this.getuPwd().equals(other.getuPwd()))
            && (this.getuBalance() == null ? other.getuBalance() == null : this.getuBalance().equals(other.getuBalance()))
            && (this.getuFee() == null ? other.getuFee() == null : this.getuFee().equals(other.getuFee()))
            && (this.getuPhone() == null ? other.getuPhone() == null : this.getuPhone().equals(other.getuPhone()))
            && (this.getuQq() == null ? other.getuQq() == null : this.getuQq().equals(other.getuQq()))
            && (this.getuAlpay() == null ? other.getuAlpay() == null : this.getuAlpay().equals(other.getuAlpay()))
            && (this.getuWx() == null ? other.getuWx() == null : this.getuWx().equals(other.getuWx()))
            && (this.getuBank() == null ? other.getuBank() == null : this.getuBank().equals(other.getuBank()))
            && (this.getuCard() == null ? other.getuCard() == null : this.getuCard().equals(other.getuCard()))
            && (this.getuBankAccount() == null ? other.getuBankAccount() == null : this.getuBankAccount().equals(other.getuBankAccount()))
            && (this.getuPid() == null ? other.getuPid() == null : this.getuPid().equals(other.getuPid()))
            && (this.getuXOpenid() == null ? other.getuXOpenid() == null : this.getuXOpenid().equals(other.getuXOpenid()))
            && (this.getuOpenid() == null ? other.getuOpenid() == null : this.getuOpenid().equals(other.getuOpenid()))
            && (this.getuGid() == null ? other.getuGid() == null : this.getuGid().equals(other.getuGid()))
            && (this.getuPayname() == null ? other.getuPayname() == null : this.getuPayname().equals(other.getuPayname()))
            && (this.getuYn() == null ? other.getuYn() == null : this.getuYn().equals(other.getuYn()))
            && (this.getuCreatetime() == null ? other.getuCreatetime() == null : this.getuCreatetime().equals(other.getuCreatetime()))
            && (this.getuType() == null ? other.getuType() == null : this.getuType().equals(other.getuType()))
            && (this.getuTime() == null ? other.getuTime() == null : this.getuTime().equals(other.getuTime()))
            && (this.getOldType() == null ? other.getOldType() == null : this.getOldType().equals(other.getOldType()))
            && (this.getuDomain() == null ? other.getuDomain() == null : this.getuDomain().equals(other.getuDomain()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getuName() == null) ? 0 : getuName().hashCode());
        result = prime * result + ((getuMark() == null) ? 0 : getuMark().hashCode());
        result = prime * result + ((getuPwd() == null) ? 0 : getuPwd().hashCode());
        result = prime * result + ((getuBalance() == null) ? 0 : getuBalance().hashCode());
        result = prime * result + ((getuFee() == null) ? 0 : getuFee().hashCode());
        result = prime * result + ((getuPhone() == null) ? 0 : getuPhone().hashCode());
        result = prime * result + ((getuQq() == null) ? 0 : getuQq().hashCode());
        result = prime * result + ((getuAlpay() == null) ? 0 : getuAlpay().hashCode());
        result = prime * result + ((getuWx() == null) ? 0 : getuWx().hashCode());
        result = prime * result + ((getuBank() == null) ? 0 : getuBank().hashCode());
        result = prime * result + ((getuCard() == null) ? 0 : getuCard().hashCode());
        result = prime * result + ((getuBankAccount() == null) ? 0 : getuBankAccount().hashCode());
        result = prime * result + ((getuPid() == null) ? 0 : getuPid().hashCode());
        result = prime * result + ((getuXOpenid() == null) ? 0 : getuXOpenid().hashCode());
        result = prime * result + ((getuOpenid() == null) ? 0 : getuOpenid().hashCode());
        result = prime * result + ((getuGid() == null) ? 0 : getuGid().hashCode());
        result = prime * result + ((getuPayname() == null) ? 0 : getuPayname().hashCode());
        result = prime * result + ((getuYn() == null) ? 0 : getuYn().hashCode());
        result = prime * result + ((getuCreatetime() == null) ? 0 : getuCreatetime().hashCode());
        result = prime * result + ((getuType() == null) ? 0 : getuType().hashCode());
        result = prime * result + ((getuTime() == null) ? 0 : getuTime().hashCode());
        result = prime * result + ((getOldType() == null) ? 0 : getOldType().hashCode());
        result = prime * result + ((getuDomain() == null) ? 0 : getuDomain().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", uName=").append(uName);
        sb.append(", uMark=").append(uMark);
        sb.append(", uPwd=").append(uPwd);
        sb.append(", uBalance=").append(uBalance);
        sb.append(", uFee=").append(uFee);
        sb.append(", uPhone=").append(uPhone);
        sb.append(", uQq=").append(uQq);
        sb.append(", uAlpay=").append(uAlpay);
        sb.append(", uWx=").append(uWx);
        sb.append(", uBank=").append(uBank);
        sb.append(", uCard=").append(uCard);
        sb.append(", uBankAccount=").append(uBankAccount);
        sb.append(", uPid=").append(uPid);
        sb.append(", uXOpenid=").append(uXOpenid);
        sb.append(", uOpenid=").append(uOpenid);
        sb.append(", uGid=").append(uGid);
        sb.append(", uPayname=").append(uPayname);
        sb.append(", uYn=").append(uYn);
        sb.append(", uCreatetime=").append(uCreatetime);
        sb.append(", uType=").append(uType);
        sb.append(", uTime=").append(uTime);
        sb.append(", oldType=").append(oldType);
        sb.append(", uDomain=").append(uDomain);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}