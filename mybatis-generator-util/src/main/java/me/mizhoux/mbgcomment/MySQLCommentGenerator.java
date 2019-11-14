package me.mizhoux.mbgcomment;


import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class MySQLCommentGenerator extends EmptyCommentGenerator {

    /** The suppress date.
     * 禁止日期生成
     */
    private boolean suppressDate;

    private SimpleDateFormat dateFormat;

    private Properties properties;

    public MySQLCommentGenerator() {
        properties = new Properties();
    }

    @Override
    public void addConfigurationProperties(Properties properties) {
        // 获取自定义的 properties
        this.properties.putAll(properties);
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
        sb.append(" * "+ remarks);
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
    public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        StringBuilder sb = new StringBuilder();
        if (introspectedColumn.getJdbcType() == 93) {
            sb.append("@JsonFormat(locale = \"zh\", timezone = \"GMT+8\", pattern = \"yyyy-MM-dd HH:mm:ss\")");
        }

        if (sb.length() > 0) {
            method.addJavaDocLine(sb.toString());
        }
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
