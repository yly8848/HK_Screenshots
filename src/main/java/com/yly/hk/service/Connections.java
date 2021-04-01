package com.yly.hk.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author yly
 * @date 2021/4/1
 */
@Component
@ConfigurationProperties(prefix = "cons")
public class Connections {

    private ConnectionInfo[] infos;

    public ConnectionInfo[] getInfos() {
        return infos;
    }

    public void setInfos(ConnectionInfo[] infos) {
        this.infos = infos;
    }

    public ConnectionInfo getItem(int index){
        if (infos!= null && infos.length <= index){
            return infos[0];
        }else if (infos != null){
            return infos[index];
        }else {
            return null;
        }
    }
}
