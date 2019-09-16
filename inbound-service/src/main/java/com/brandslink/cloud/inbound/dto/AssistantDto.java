package com.brandslink.cloud.inbound.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 协助人信息
 * @date 2019/6/25 20:48
 */
@ApiModel(value ="AssistantDto")
public class AssistantDto {

    @ApiModelProperty(value = "协助人信息")
    private String assistants;

    public String getAssistants() {
        return assistants;
    }

    public void setAssistants(String assistants) {
        this.assistants = assistants;
    }
}
