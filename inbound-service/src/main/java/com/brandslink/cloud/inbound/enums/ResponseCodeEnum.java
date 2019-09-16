package com.brandslink.cloud.inbound.enums;

import com.brandslink.cloud.common.enums.ResponseCodeEnumSupper;

public enum ResponseCodeEnum implements ResponseCodeEnumSupper {

    /**
     * 定义返回码
     */

    RETURN_CODE_100001("100001", "用户名或密码错误"),
    RETURN_CODE_100002("100002", "原始密码错误"),
    RETURN_CODE_100003("100003", "账号已经存在，请重新输入账号"),
    RETURN_CODE_100004("100004", "角色名称已经存在，请重新输入角色"),
    RETURN_CODE_100005("100005", "请先删除子组织后再删除父组织"),
    RETURN_CODE_100006("100006", "该组织已经绑定账号，需先解绑后再删除"),
    RETURN_CODE_100007("100007", "该角色有绑定账户权限，请先更换账户角色后再删除"),
    RETURN_CODE_100008("100008", "该名称已存在，请重新输入"),
    RETURN_CODE_100010("100010", "职位名称不能重复，请重新输入"),
    RETURN_CODE_100011("100011", "姓名已经存在，请重新输入姓名"),
    RETURN_CODE_100012("100012", "角色名称不能包含'-'符号，请重新输入角色名称"),
    RETURN_CODE_100013("100013", "admin账号仅限登录后修改密码，不支持重置密码!"),
    RETURN_CODE_100400("100400", "请求参数错误"),
    RETURN_CODE_100401("100401", "权限不足"),
    RETURN_CODE_100402("100402", "数据库操作异常"),
    RETURN_CODE_100403("100403", "参数不允许为空"),
    RETURN_CODE_100404("100404", "请求资源不存在"),
    RETURN_CODE_100406("100406", "未登录"),
    RETURN_CODE_100407("100407", "根据requestUrl查询角色列表出错"),
    RETURN_CODE_100408("100408", "监听队列，接收仓库名称修改信息异常"),
    RETURN_CODE_100200("100200", "请求成功"),
    RETURN_CODE_100500("100500", "系统异常"),
    RETURN_CODE_100501("100501", "未查询出此运单号的商品信息"),
	
	RETURN_CODE_RK_200400("200400", "请求参数错误"),
	RETURN_CODE_RK_200401("200401", "权限不足"),
	RETURN_CODE_RK_200402("200402", "数据库操作异常"),
	RETURN_CODE_RK_200403("200403", "参数不允许为空"),
	RETURN_CODE_RK_200404("200404", "请求资源不存在"),
	RETURN_CODE_RK_200406("200406", "未登录"),
	RETURN_CODE_RK_200407("200407", "单号"),
	RETURN_CODE_RK_200408("200408", "输入参数错误"),
	RETURN_CODE_RK_200409("200409", "查询结果为空"),
	RETURN_CODE_RK_200500("200500", "系统异常"),
	RETURN_CODE_RK_200501("200501", "未查询出此运单号的商品信息"),
    RETURN_CODE_RK_200601("200601","excel文件格式不正确,请重新下载模板");

    private String code;
    private String msg;

    private ResponseCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
