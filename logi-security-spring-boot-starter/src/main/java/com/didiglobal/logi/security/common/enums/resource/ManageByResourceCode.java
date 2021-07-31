package com.didiglobal.logi.security.common.enums.resource;

import lombok.Getter;

/**
 * @author cjm
 *
 * 资源权限管理 > 按资源管理等级
 * 这个主要是为了服务：管理权限用户数、查看权限用户数
 */
@Getter
public enum ManageByResourceCode {

    /**
     * 项目级别：具体某个项目的管理权限用户数、查看权限用户数
     */
    PROJECT(1, "项目级别"),

    /**
     * 资源类别级别：具体某个项目下的某个资源类别的管理权限用户数、查看权限用户数
     */
    RESOURCE_TYPE(2, "资源类别级别"),

    /**
     * 资源级别：具体某个项目下的某个资源类别的某个资源的管理权限用户数、查看权限用户数
     */
    RESOURCE(3, "资源级别");

    private final Integer type;

    private final String info;

    ManageByResourceCode(Integer type, String info) {
        this.type = type;
        this.info = info;
    }

    public static ManageByResourceCode getByType(Integer type) {
        ManageByResourceCode[] manageByResourceCodes = ManageByResourceCode.values();
        for(ManageByResourceCode manageByResourceCode : manageByResourceCodes) {
            if(manageByResourceCode.type.equals(type)) {
                return manageByResourceCode;
            }
        }
        return null;
    }
}
