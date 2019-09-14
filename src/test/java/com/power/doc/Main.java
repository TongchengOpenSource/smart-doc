package com.power.doc;

import com.power.common.model.CommonResult;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    public static void exeCmd(String commandStr) {
        BufferedReader br = null;
        try {
            Process p = Runtime.getRuntime().exec(commandStr);
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            System.out.println(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Class cls = CommonResult.class;
        System.out.println("path:" + cls.getResource(""));
        String path = cls.getResource("").getPath();
        String commandStr = String.format("javap  -classpath %s -private CommonResult", path);
        String cmd = "java -jar d:/procyon-decompiler-0.5.30.jar D:/ProgramFiles/mvnrepository/repository/com/boco/sp/Common-util/1.0-SNAPSHOT/Common-util-1.0-20180105.062727-5.jar -o out";
        //String commandStr = "ipconfig";
        Main.exeCmd(cmd);
    }
}
