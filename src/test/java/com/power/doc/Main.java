package com.power.doc;

import java.util.HashSet;
import java.util.Set;

public class Main {
    private final static Set<String> PREFIX_LIST = new HashSet<>();


    static {
        PREFIX_LIST.add("maven");
        PREFIX_LIST.add("asm");
        PREFIX_LIST.add("tomcat");
        PREFIX_LIST.add("jboss");
        PREFIX_LIST.add("undertow");
        PREFIX_LIST.add("jackson");
        PREFIX_LIST.add("micrometer");
        PREFIX_LIST.add("spring-boot-actuator");
        PREFIX_LIST.add("sharding");
        PREFIX_LIST.add("mybatis-spring-boot-starter");
        PREFIX_LIST.add("flexmark");
    }

    public static void main(String[] args) {
        String artifactId = "ksharding-jdbc";
        System.out.println(ignoreArtifactById(artifactId));
    }


    public static boolean ignoreArtifactById(String artifactId) {
        if (PREFIX_LIST.stream().anyMatch(artifactId::startsWith)) {
            return true;
        }
        return false;
    }
}
