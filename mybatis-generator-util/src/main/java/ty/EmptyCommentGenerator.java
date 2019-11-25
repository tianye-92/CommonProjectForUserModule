package com.starlink.cloud.res.util;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

/**
 * 自定义mybatis-generator 生成注解
 */
public class EmptyCommentGenerator implements CommentGenerator {

    /**
     * The suppress date.
     * 禁止日期生成
     */
    private boolean suppressDate;

    private SimpleDateFormat dateFormat;

    private Properties properties;

    public EmptyCommentGenerator() {
        properties = new Properties();
    }

    @Override
    public void addConfigurationProperties(Properties properties) {
        this.properties.putAll(properties);
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        //字段备注信息
        String remarks = introspectedColumn.getRemarks();
        field.addJavaDocLine("@ApiModelProperty(value = \"" + remarks + "\")");

        //当字段数据类型为date时添加日期注释
        StringBuffer sb = new StringBuffer();
        if (introspectedColumn.getJdbcType() == 93) {
            sb.append("@DateTimeFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")");
        }

        if (sb.length() > 0) {
            field.addJavaDocLine(sb.toString());
        }
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {

    }

    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String author = properties.getProperty("author");
        String dateFormat = properties.getProperty("dateFormat", "yyyy-MM-dd");
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);

        //重新写方法从数据库中获取表备注的信息
        String remarks = introspectedTable.getRemarks();
        //获取实体类名称
        String entityName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        StringBuilder sb = new StringBuilder();
        //添加类注释
        topLevelClass.addJavaDocLine("/**");
        sb.append(" * " + remarks);
        sb.append("\n");
        sb.append(" * 实体类对应的数据表为：  ");
        sb.append(introspectedTable.getFullyQualifiedTable());
        topLevelClass.addJavaDocLine(sb.toString());
        topLevelClass.addJavaDocLine(" * @author " + author);

        //添加时间
        topLevelClass.addJavaDocLine(" * @date " + getDateString());
        topLevelClass.addJavaDocLine(" */");
        topLevelClass.addJavaDocLine("@ApiModel(value =\"" + entityName + "\")");
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {

    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean b) {

    }

    @Override
    public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {

    }

    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        StringBuilder sb = new StringBuilder();
        if (introspectedColumn.getJdbcType() == 93) {
            sb.append("@JsonFormat(locale = \"zh\", timezone = \"GMT+8\", pattern = \"yyyy-MM-dd HH:mm:ss\")");
        }

        if (sb.length() > 0) {
            method.addJavaDocLine(sb.toString());
        }
    }

    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {

    }

    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {

    }

    @Override
    public void addJavaFileComment(CompilationUnit compilationUnit) {

    }

    @Override
    public void addComment(XmlElement xmlElement) {

    }

    @Override
    public void addRootComment(XmlElement xmlElement) {

    }

    @Override
    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> set) {

    }

    @Override
    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> set) {

    }

    @Override
    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> set) {

    }

    @Override
    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> set) {

    }

    @Override
    public void addClassAnnotation(InnerClass innerClass, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> set) {

    }

    protected String getDateString() {
        if (suppressDate) {
            return null;
        } else if (dateFormat != null) {
            return dateFormat.format(new Date());
        } else {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        }
    }
}
