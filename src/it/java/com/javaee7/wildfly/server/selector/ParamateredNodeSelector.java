package com.javaee7.wildfly.server.selector;

import org.jboss.ejb.client.DeploymentNodeSelector;

import java.util.Arrays;

public class ParamateredNodeSelector implements DeploymentNodeSelector {

    @Override
    public String selectNode(String[] eligibleNodes, String appName, String moduleName, String distinctName) {
        String node = System.getProperty("ejb.fixedNode");
        String selected = Arrays.stream(eligibleNodes).filter(nod -> nod.equalsIgnoreCase(node)).findFirst().orElse(null);
        System.out.println(selected + " was selected");
        return selected;
    }
}