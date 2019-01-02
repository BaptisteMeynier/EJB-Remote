package com.javaee7.wildfly.server.selector;

import org.jboss.ejb.client.DeploymentNodeSelector;

import java.util.Random;

public class RandomClusterNodeSelector implements DeploymentNodeSelector {

    @Override
    public String selectNode(String[] eligibleNodes, String appName, String moduleName, String distinctName) {
        final Random random = new Random();
        // check if there are any connected nodes. If there are then just reuse them
        if (eligibleNodes.length > 0) {
            if (eligibleNodes.length == 1) {
                return eligibleNodes[0];
            }
            final int randomConnectedNode = random.nextInt(eligibleNodes.length);
            String selected = eligibleNodes[randomConnectedNode];
            System.out.println(selected + " was selected");
            return selected;
        }
        throw new IllegalStateException(
                "No node available for" + " appName '" + appName + "'" + " and moduleName '"
                + moduleName + ", impossible to find node available");
    }
}