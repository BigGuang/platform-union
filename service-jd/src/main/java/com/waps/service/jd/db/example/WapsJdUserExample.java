package com.waps.service.jd.db.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WapsJdUserExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    private Integer limit;

    private Long offset;

    private Boolean forUpdate;

    public WapsJdUserExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Long getOffset() {
        return offset;
    }

    public void setForUpdate(Boolean forUpdate) {
        this.forUpdate = forUpdate;
    }

    public Boolean getForUpdate() {
        return forUpdate;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andUNameIsNull() {
            addCriterion("u_name is null");
            return (Criteria) this;
        }

        public Criteria andUNameIsNotNull() {
            addCriterion("u_name is not null");
            return (Criteria) this;
        }

        public Criteria andUNameEqualTo(String value) {
            addCriterion("u_name =", value, "uName");
            return (Criteria) this;
        }

        public Criteria andUNameNotEqualTo(String value) {
            addCriterion("u_name <>", value, "uName");
            return (Criteria) this;
        }

        public Criteria andUNameGreaterThan(String value) {
            addCriterion("u_name >", value, "uName");
            return (Criteria) this;
        }

        public Criteria andUNameGreaterThanOrEqualTo(String value) {
            addCriterion("u_name >=", value, "uName");
            return (Criteria) this;
        }

        public Criteria andUNameLessThan(String value) {
            addCriterion("u_name <", value, "uName");
            return (Criteria) this;
        }

        public Criteria andUNameLessThanOrEqualTo(String value) {
            addCriterion("u_name <=", value, "uName");
            return (Criteria) this;
        }

        public Criteria andUNameLike(String value) {
            addCriterion("u_name like", value, "uName");
            return (Criteria) this;
        }

        public Criteria andUNameNotLike(String value) {
            addCriterion("u_name not like", value, "uName");
            return (Criteria) this;
        }

        public Criteria andUNameIn(List<String> values) {
            addCriterion("u_name in", values, "uName");
            return (Criteria) this;
        }

        public Criteria andUNameNotIn(List<String> values) {
            addCriterion("u_name not in", values, "uName");
            return (Criteria) this;
        }

        public Criteria andUNameBetween(String value1, String value2) {
            addCriterion("u_name between", value1, value2, "uName");
            return (Criteria) this;
        }

        public Criteria andUNameNotBetween(String value1, String value2) {
            addCriterion("u_name not between", value1, value2, "uName");
            return (Criteria) this;
        }

        public Criteria andUPwdIsNull() {
            addCriterion("u_pwd is null");
            return (Criteria) this;
        }

        public Criteria andUPwdIsNotNull() {
            addCriterion("u_pwd is not null");
            return (Criteria) this;
        }

        public Criteria andUPwdEqualTo(String value) {
            addCriterion("u_pwd =", value, "uPwd");
            return (Criteria) this;
        }

        public Criteria andUPwdNotEqualTo(String value) {
            addCriterion("u_pwd <>", value, "uPwd");
            return (Criteria) this;
        }

        public Criteria andUPwdGreaterThan(String value) {
            addCriterion("u_pwd >", value, "uPwd");
            return (Criteria) this;
        }

        public Criteria andUPwdGreaterThanOrEqualTo(String value) {
            addCriterion("u_pwd >=", value, "uPwd");
            return (Criteria) this;
        }

        public Criteria andUPwdLessThan(String value) {
            addCriterion("u_pwd <", value, "uPwd");
            return (Criteria) this;
        }

        public Criteria andUPwdLessThanOrEqualTo(String value) {
            addCriterion("u_pwd <=", value, "uPwd");
            return (Criteria) this;
        }

        public Criteria andUPwdLike(String value) {
            addCriterion("u_pwd like", value, "uPwd");
            return (Criteria) this;
        }

        public Criteria andUPwdNotLike(String value) {
            addCriterion("u_pwd not like", value, "uPwd");
            return (Criteria) this;
        }

        public Criteria andUPwdIn(List<String> values) {
            addCriterion("u_pwd in", values, "uPwd");
            return (Criteria) this;
        }

        public Criteria andUPwdNotIn(List<String> values) {
            addCriterion("u_pwd not in", values, "uPwd");
            return (Criteria) this;
        }

        public Criteria andUPwdBetween(String value1, String value2) {
            addCriterion("u_pwd between", value1, value2, "uPwd");
            return (Criteria) this;
        }

        public Criteria andUPwdNotBetween(String value1, String value2) {
            addCriterion("u_pwd not between", value1, value2, "uPwd");
            return (Criteria) this;
        }

        public Criteria andUPhoneIsNull() {
            addCriterion("u_phone is null");
            return (Criteria) this;
        }

        public Criteria andUPhoneIsNotNull() {
            addCriterion("u_phone is not null");
            return (Criteria) this;
        }

        public Criteria andUPhoneEqualTo(String value) {
            addCriterion("u_phone =", value, "uPhone");
            return (Criteria) this;
        }

        public Criteria andUPhoneNotEqualTo(String value) {
            addCriterion("u_phone <>", value, "uPhone");
            return (Criteria) this;
        }

        public Criteria andUPhoneGreaterThan(String value) {
            addCriterion("u_phone >", value, "uPhone");
            return (Criteria) this;
        }

        public Criteria andUPhoneGreaterThanOrEqualTo(String value) {
            addCriterion("u_phone >=", value, "uPhone");
            return (Criteria) this;
        }

        public Criteria andUPhoneLessThan(String value) {
            addCriterion("u_phone <", value, "uPhone");
            return (Criteria) this;
        }

        public Criteria andUPhoneLessThanOrEqualTo(String value) {
            addCriterion("u_phone <=", value, "uPhone");
            return (Criteria) this;
        }

        public Criteria andUPhoneLike(String value) {
            addCriterion("u_phone like", value, "uPhone");
            return (Criteria) this;
        }

        public Criteria andUPhoneNotLike(String value) {
            addCriterion("u_phone not like", value, "uPhone");
            return (Criteria) this;
        }

        public Criteria andUPhoneIn(List<String> values) {
            addCriterion("u_phone in", values, "uPhone");
            return (Criteria) this;
        }

        public Criteria andUPhoneNotIn(List<String> values) {
            addCriterion("u_phone not in", values, "uPhone");
            return (Criteria) this;
        }

        public Criteria andUPhoneBetween(String value1, String value2) {
            addCriterion("u_phone between", value1, value2, "uPhone");
            return (Criteria) this;
        }

        public Criteria andUPhoneNotBetween(String value1, String value2) {
            addCriterion("u_phone not between", value1, value2, "uPhone");
            return (Criteria) this;
        }

        public Criteria andUQqIsNull() {
            addCriterion("u_qq is null");
            return (Criteria) this;
        }

        public Criteria andUQqIsNotNull() {
            addCriterion("u_qq is not null");
            return (Criteria) this;
        }

        public Criteria andUQqEqualTo(String value) {
            addCriterion("u_qq =", value, "uQq");
            return (Criteria) this;
        }

        public Criteria andUQqNotEqualTo(String value) {
            addCriterion("u_qq <>", value, "uQq");
            return (Criteria) this;
        }

        public Criteria andUQqGreaterThan(String value) {
            addCriterion("u_qq >", value, "uQq");
            return (Criteria) this;
        }

        public Criteria andUQqGreaterThanOrEqualTo(String value) {
            addCriterion("u_qq >=", value, "uQq");
            return (Criteria) this;
        }

        public Criteria andUQqLessThan(String value) {
            addCriterion("u_qq <", value, "uQq");
            return (Criteria) this;
        }

        public Criteria andUQqLessThanOrEqualTo(String value) {
            addCriterion("u_qq <=", value, "uQq");
            return (Criteria) this;
        }

        public Criteria andUQqLike(String value) {
            addCriterion("u_qq like", value, "uQq");
            return (Criteria) this;
        }

        public Criteria andUQqNotLike(String value) {
            addCriterion("u_qq not like", value, "uQq");
            return (Criteria) this;
        }

        public Criteria andUQqIn(List<String> values) {
            addCriterion("u_qq in", values, "uQq");
            return (Criteria) this;
        }

        public Criteria andUQqNotIn(List<String> values) {
            addCriterion("u_qq not in", values, "uQq");
            return (Criteria) this;
        }

        public Criteria andUQqBetween(String value1, String value2) {
            addCriterion("u_qq between", value1, value2, "uQq");
            return (Criteria) this;
        }

        public Criteria andUQqNotBetween(String value1, String value2) {
            addCriterion("u_qq not between", value1, value2, "uQq");
            return (Criteria) this;
        }

        public Criteria andUAlpayIsNull() {
            addCriterion("u_alpay is null");
            return (Criteria) this;
        }

        public Criteria andUAlpayIsNotNull() {
            addCriterion("u_alpay is not null");
            return (Criteria) this;
        }

        public Criteria andUAlpayEqualTo(String value) {
            addCriterion("u_alpay =", value, "uAlpay");
            return (Criteria) this;
        }

        public Criteria andUAlpayNotEqualTo(String value) {
            addCriterion("u_alpay <>", value, "uAlpay");
            return (Criteria) this;
        }

        public Criteria andUAlpayGreaterThan(String value) {
            addCriterion("u_alpay >", value, "uAlpay");
            return (Criteria) this;
        }

        public Criteria andUAlpayGreaterThanOrEqualTo(String value) {
            addCriterion("u_alpay >=", value, "uAlpay");
            return (Criteria) this;
        }

        public Criteria andUAlpayLessThan(String value) {
            addCriterion("u_alpay <", value, "uAlpay");
            return (Criteria) this;
        }

        public Criteria andUAlpayLessThanOrEqualTo(String value) {
            addCriterion("u_alpay <=", value, "uAlpay");
            return (Criteria) this;
        }

        public Criteria andUAlpayLike(String value) {
            addCriterion("u_alpay like", value, "uAlpay");
            return (Criteria) this;
        }

        public Criteria andUAlpayNotLike(String value) {
            addCriterion("u_alpay not like", value, "uAlpay");
            return (Criteria) this;
        }

        public Criteria andUAlpayIn(List<String> values) {
            addCriterion("u_alpay in", values, "uAlpay");
            return (Criteria) this;
        }

        public Criteria andUAlpayNotIn(List<String> values) {
            addCriterion("u_alpay not in", values, "uAlpay");
            return (Criteria) this;
        }

        public Criteria andUAlpayBetween(String value1, String value2) {
            addCriterion("u_alpay between", value1, value2, "uAlpay");
            return (Criteria) this;
        }

        public Criteria andUAlpayNotBetween(String value1, String value2) {
            addCriterion("u_alpay not between", value1, value2, "uAlpay");
            return (Criteria) this;
        }

        public Criteria andUWxIsNull() {
            addCriterion("u_wx is null");
            return (Criteria) this;
        }

        public Criteria andUWxIsNotNull() {
            addCriterion("u_wx is not null");
            return (Criteria) this;
        }

        public Criteria andUWxEqualTo(String value) {
            addCriterion("u_wx =", value, "uWx");
            return (Criteria) this;
        }

        public Criteria andUWxNotEqualTo(String value) {
            addCriterion("u_wx <>", value, "uWx");
            return (Criteria) this;
        }

        public Criteria andUWxGreaterThan(String value) {
            addCriterion("u_wx >", value, "uWx");
            return (Criteria) this;
        }

        public Criteria andUWxGreaterThanOrEqualTo(String value) {
            addCriterion("u_wx >=", value, "uWx");
            return (Criteria) this;
        }

        public Criteria andUWxLessThan(String value) {
            addCriterion("u_wx <", value, "uWx");
            return (Criteria) this;
        }

        public Criteria andUWxLessThanOrEqualTo(String value) {
            addCriterion("u_wx <=", value, "uWx");
            return (Criteria) this;
        }

        public Criteria andUWxLike(String value) {
            addCriterion("u_wx like", value, "uWx");
            return (Criteria) this;
        }

        public Criteria andUWxNotLike(String value) {
            addCriterion("u_wx not like", value, "uWx");
            return (Criteria) this;
        }

        public Criteria andUWxIn(List<String> values) {
            addCriterion("u_wx in", values, "uWx");
            return (Criteria) this;
        }

        public Criteria andUWxNotIn(List<String> values) {
            addCriterion("u_wx not in", values, "uWx");
            return (Criteria) this;
        }

        public Criteria andUWxBetween(String value1, String value2) {
            addCriterion("u_wx between", value1, value2, "uWx");
            return (Criteria) this;
        }

        public Criteria andUWxNotBetween(String value1, String value2) {
            addCriterion("u_wx not between", value1, value2, "uWx");
            return (Criteria) this;
        }

        public Criteria andUBankIsNull() {
            addCriterion("u_bank is null");
            return (Criteria) this;
        }

        public Criteria andUBankIsNotNull() {
            addCriterion("u_bank is not null");
            return (Criteria) this;
        }

        public Criteria andUBankEqualTo(String value) {
            addCriterion("u_bank =", value, "uBank");
            return (Criteria) this;
        }

        public Criteria andUBankNotEqualTo(String value) {
            addCriterion("u_bank <>", value, "uBank");
            return (Criteria) this;
        }

        public Criteria andUBankGreaterThan(String value) {
            addCriterion("u_bank >", value, "uBank");
            return (Criteria) this;
        }

        public Criteria andUBankGreaterThanOrEqualTo(String value) {
            addCriterion("u_bank >=", value, "uBank");
            return (Criteria) this;
        }

        public Criteria andUBankLessThan(String value) {
            addCriterion("u_bank <", value, "uBank");
            return (Criteria) this;
        }

        public Criteria andUBankLessThanOrEqualTo(String value) {
            addCriterion("u_bank <=", value, "uBank");
            return (Criteria) this;
        }

        public Criteria andUBankLike(String value) {
            addCriterion("u_bank like", value, "uBank");
            return (Criteria) this;
        }

        public Criteria andUBankNotLike(String value) {
            addCriterion("u_bank not like", value, "uBank");
            return (Criteria) this;
        }

        public Criteria andUBankIn(List<String> values) {
            addCriterion("u_bank in", values, "uBank");
            return (Criteria) this;
        }

        public Criteria andUBankNotIn(List<String> values) {
            addCriterion("u_bank not in", values, "uBank");
            return (Criteria) this;
        }

        public Criteria andUBankBetween(String value1, String value2) {
            addCriterion("u_bank between", value1, value2, "uBank");
            return (Criteria) this;
        }

        public Criteria andUBankNotBetween(String value1, String value2) {
            addCriterion("u_bank not between", value1, value2, "uBank");
            return (Criteria) this;
        }

        public Criteria andUCardIsNull() {
            addCriterion("u_card is null");
            return (Criteria) this;
        }

        public Criteria andUCardIsNotNull() {
            addCriterion("u_card is not null");
            return (Criteria) this;
        }

        public Criteria andUCardEqualTo(String value) {
            addCriterion("u_card =", value, "uCard");
            return (Criteria) this;
        }

        public Criteria andUCardNotEqualTo(String value) {
            addCriterion("u_card <>", value, "uCard");
            return (Criteria) this;
        }

        public Criteria andUCardGreaterThan(String value) {
            addCriterion("u_card >", value, "uCard");
            return (Criteria) this;
        }

        public Criteria andUCardGreaterThanOrEqualTo(String value) {
            addCriterion("u_card >=", value, "uCard");
            return (Criteria) this;
        }

        public Criteria andUCardLessThan(String value) {
            addCriterion("u_card <", value, "uCard");
            return (Criteria) this;
        }

        public Criteria andUCardLessThanOrEqualTo(String value) {
            addCriterion("u_card <=", value, "uCard");
            return (Criteria) this;
        }

        public Criteria andUCardLike(String value) {
            addCriterion("u_card like", value, "uCard");
            return (Criteria) this;
        }

        public Criteria andUCardNotLike(String value) {
            addCriterion("u_card not like", value, "uCard");
            return (Criteria) this;
        }

        public Criteria andUCardIn(List<String> values) {
            addCriterion("u_card in", values, "uCard");
            return (Criteria) this;
        }

        public Criteria andUCardNotIn(List<String> values) {
            addCriterion("u_card not in", values, "uCard");
            return (Criteria) this;
        }

        public Criteria andUCardBetween(String value1, String value2) {
            addCriterion("u_card between", value1, value2, "uCard");
            return (Criteria) this;
        }

        public Criteria andUCardNotBetween(String value1, String value2) {
            addCriterion("u_card not between", value1, value2, "uCard");
            return (Criteria) this;
        }

        public Criteria andUBankAccountIsNull() {
            addCriterion("u_bank_account is null");
            return (Criteria) this;
        }

        public Criteria andUBankAccountIsNotNull() {
            addCriterion("u_bank_account is not null");
            return (Criteria) this;
        }

        public Criteria andUBankAccountEqualTo(String value) {
            addCriterion("u_bank_account =", value, "uBankAccount");
            return (Criteria) this;
        }

        public Criteria andUBankAccountNotEqualTo(String value) {
            addCriterion("u_bank_account <>", value, "uBankAccount");
            return (Criteria) this;
        }

        public Criteria andUBankAccountGreaterThan(String value) {
            addCriterion("u_bank_account >", value, "uBankAccount");
            return (Criteria) this;
        }

        public Criteria andUBankAccountGreaterThanOrEqualTo(String value) {
            addCriterion("u_bank_account >=", value, "uBankAccount");
            return (Criteria) this;
        }

        public Criteria andUBankAccountLessThan(String value) {
            addCriterion("u_bank_account <", value, "uBankAccount");
            return (Criteria) this;
        }

        public Criteria andUBankAccountLessThanOrEqualTo(String value) {
            addCriterion("u_bank_account <=", value, "uBankAccount");
            return (Criteria) this;
        }

        public Criteria andUBankAccountLike(String value) {
            addCriterion("u_bank_account like", value, "uBankAccount");
            return (Criteria) this;
        }

        public Criteria andUBankAccountNotLike(String value) {
            addCriterion("u_bank_account not like", value, "uBankAccount");
            return (Criteria) this;
        }

        public Criteria andUBankAccountIn(List<String> values) {
            addCriterion("u_bank_account in", values, "uBankAccount");
            return (Criteria) this;
        }

        public Criteria andUBankAccountNotIn(List<String> values) {
            addCriterion("u_bank_account not in", values, "uBankAccount");
            return (Criteria) this;
        }

        public Criteria andUBankAccountBetween(String value1, String value2) {
            addCriterion("u_bank_account between", value1, value2, "uBankAccount");
            return (Criteria) this;
        }

        public Criteria andUBankAccountNotBetween(String value1, String value2) {
            addCriterion("u_bank_account not between", value1, value2, "uBankAccount");
            return (Criteria) this;
        }

        public Criteria andUPidIsNull() {
            addCriterion("u_pid is null");
            return (Criteria) this;
        }

        public Criteria andUPidIsNotNull() {
            addCriterion("u_pid is not null");
            return (Criteria) this;
        }

        public Criteria andUPidEqualTo(String value) {
            addCriterion("u_pid =", value, "uPid");
            return (Criteria) this;
        }

        public Criteria andUPidNotEqualTo(String value) {
            addCriterion("u_pid <>", value, "uPid");
            return (Criteria) this;
        }

        public Criteria andUPidGreaterThan(String value) {
            addCriterion("u_pid >", value, "uPid");
            return (Criteria) this;
        }

        public Criteria andUPidGreaterThanOrEqualTo(String value) {
            addCriterion("u_pid >=", value, "uPid");
            return (Criteria) this;
        }

        public Criteria andUPidLessThan(String value) {
            addCriterion("u_pid <", value, "uPid");
            return (Criteria) this;
        }

        public Criteria andUPidLessThanOrEqualTo(String value) {
            addCriterion("u_pid <=", value, "uPid");
            return (Criteria) this;
        }

        public Criteria andUPidLike(String value) {
            addCriterion("u_pid like", value, "uPid");
            return (Criteria) this;
        }

        public Criteria andUPidNotLike(String value) {
            addCriterion("u_pid not like", value, "uPid");
            return (Criteria) this;
        }

        public Criteria andUPidIn(List<String> values) {
            addCriterion("u_pid in", values, "uPid");
            return (Criteria) this;
        }

        public Criteria andUPidNotIn(List<String> values) {
            addCriterion("u_pid not in", values, "uPid");
            return (Criteria) this;
        }

        public Criteria andUPidBetween(String value1, String value2) {
            addCriterion("u_pid between", value1, value2, "uPid");
            return (Criteria) this;
        }

        public Criteria andUPidNotBetween(String value1, String value2) {
            addCriterion("u_pid not between", value1, value2, "uPid");
            return (Criteria) this;
        }

        public Criteria andUYnIsNull() {
            addCriterion("u_yn is null");
            return (Criteria) this;
        }

        public Criteria andUYnIsNotNull() {
            addCriterion("u_yn is not null");
            return (Criteria) this;
        }

        public Criteria andUYnEqualTo(Integer value) {
            addCriterion("u_yn =", value, "uYn");
            return (Criteria) this;
        }

        public Criteria andUYnNotEqualTo(Integer value) {
            addCriterion("u_yn <>", value, "uYn");
            return (Criteria) this;
        }

        public Criteria andUYnGreaterThan(Integer value) {
            addCriterion("u_yn >", value, "uYn");
            return (Criteria) this;
        }

        public Criteria andUYnGreaterThanOrEqualTo(Integer value) {
            addCriterion("u_yn >=", value, "uYn");
            return (Criteria) this;
        }

        public Criteria andUYnLessThan(Integer value) {
            addCriterion("u_yn <", value, "uYn");
            return (Criteria) this;
        }

        public Criteria andUYnLessThanOrEqualTo(Integer value) {
            addCriterion("u_yn <=", value, "uYn");
            return (Criteria) this;
        }

        public Criteria andUYnIn(List<Integer> values) {
            addCriterion("u_yn in", values, "uYn");
            return (Criteria) this;
        }

        public Criteria andUYnNotIn(List<Integer> values) {
            addCriterion("u_yn not in", values, "uYn");
            return (Criteria) this;
        }

        public Criteria andUYnBetween(Integer value1, Integer value2) {
            addCriterion("u_yn between", value1, value2, "uYn");
            return (Criteria) this;
        }

        public Criteria andUYnNotBetween(Integer value1, Integer value2) {
            addCriterion("u_yn not between", value1, value2, "uYn");
            return (Criteria) this;
        }

        public Criteria andUCreatetimeIsNull() {
            addCriterion("u_createtime is null");
            return (Criteria) this;
        }

        public Criteria andUCreatetimeIsNotNull() {
            addCriterion("u_createtime is not null");
            return (Criteria) this;
        }

        public Criteria andUCreatetimeEqualTo(Date value) {
            addCriterion("u_createtime =", value, "uCreatetime");
            return (Criteria) this;
        }

        public Criteria andUCreatetimeNotEqualTo(Date value) {
            addCriterion("u_createtime <>", value, "uCreatetime");
            return (Criteria) this;
        }

        public Criteria andUCreatetimeGreaterThan(Date value) {
            addCriterion("u_createtime >", value, "uCreatetime");
            return (Criteria) this;
        }

        public Criteria andUCreatetimeGreaterThanOrEqualTo(Date value) {
            addCriterion("u_createtime >=", value, "uCreatetime");
            return (Criteria) this;
        }

        public Criteria andUCreatetimeLessThan(Date value) {
            addCriterion("u_createtime <", value, "uCreatetime");
            return (Criteria) this;
        }

        public Criteria andUCreatetimeLessThanOrEqualTo(Date value) {
            addCriterion("u_createtime <=", value, "uCreatetime");
            return (Criteria) this;
        }

        public Criteria andUCreatetimeIn(List<Date> values) {
            addCriterion("u_createtime in", values, "uCreatetime");
            return (Criteria) this;
        }

        public Criteria andUCreatetimeNotIn(List<Date> values) {
            addCriterion("u_createtime not in", values, "uCreatetime");
            return (Criteria) this;
        }

        public Criteria andUCreatetimeBetween(Date value1, Date value2) {
            addCriterion("u_createtime between", value1, value2, "uCreatetime");
            return (Criteria) this;
        }

        public Criteria andUCreatetimeNotBetween(Date value1, Date value2) {
            addCriterion("u_createtime not between", value1, value2, "uCreatetime");
            return (Criteria) this;
        }
    }

    /**
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}