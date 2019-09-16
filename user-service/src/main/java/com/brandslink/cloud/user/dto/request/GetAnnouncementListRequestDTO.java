package com.brandslink.cloud.user.dto.request;

import com.brandslink.cloud.common.utils.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 模糊查询公告列表请求model
 *
 * @ClassName GetAnnouncementListRequestDTO
 * @Author tianye
 * @Date 2019/7/26 15:25
 * @Version 1.0
 */
@ApiModel(value = "模糊查询公告列表请求model")
public class GetAnnouncementListRequestDTO implements Serializable {

    @ApiModelProperty(value = "页码", required = true)
    private String page;

    @ApiModelProperty(value = "每页显示行数", required = true)
    private String row;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "创建开始时间 yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @ApiModelProperty(value = "创建结束时间 yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_announcement_info
     *
     * @mbg.generated 2019-07-26 14:52:10
     */
    private static final long serialVersionUID = 1L;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = DateUtils.strToDate(startTime, DateUtils.FORMAT_2);
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = DateUtils.strToDate(endTime, DateUtils.FORMAT_2);
    }
}
