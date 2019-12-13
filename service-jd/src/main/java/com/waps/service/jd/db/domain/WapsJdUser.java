package com.waps.service.jd.db.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * waps_jd_user
 * @author 
 */
public class WapsJdUser implements Serializable {
    private Integer id;

    /**
     * 用户名
     */
    private String uName;

    /**
     * 密码
     */
    private String uPwd;

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
     * 是否使用0使用1关闭
     */
    private Integer uYn;

    /**
     * 创建时间
     */
    private Date uCreatetime;

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

    public String getuPwd() {
        return uPwd;
    }

    public void setuPwd(String uPwd) {
        this.uPwd = uPwd;
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
}