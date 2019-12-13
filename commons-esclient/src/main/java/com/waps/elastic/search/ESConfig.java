package com.waps.elastic.search;

public class ESConfig {
    public void init(){

    }

    public String getElasticSearchIP() {
        return elasticSearchIP;
    }

    public void setElasticSearchIP(String elasticSearchIP) {
        this.elasticSearchIP = elasticSearchIP;
    }

    public int getElasticSearchPort() {
        return elasticSearchPort;
    }

    public void setElasticSearchPort(int elasticSearchPort) {
        this.elasticSearchPort = elasticSearchPort;
    }

    private static String elasticSearchIP="10.1.0.2";
    private static int elasticSearchPort=9300;

    public String getElasticSearchClusterNodes() {
        return elasticSearchClusterNodes;
    }

    public void setElasticSearchClusterNodes(String elasticSearchClusterNodes) {
        this.elasticSearchClusterNodes = elasticSearchClusterNodes;
    }

    private static String elasticSearchClusterNodes;
}
