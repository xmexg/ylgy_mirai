package org.sdn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class TokenFile {
    private static HashMap<String, String> tokenMap;
    private static File file_token;

    {
        file_token = new File("ylgy_token.txt"); // 保存token的文件
        if (!file_token.exists()) {
            try {
                file_token.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        tokenMap = new HashMap<>(); // 保存token的map

        try {// 读取token文件
            BufferedReader br = new BufferedReader(new FileReader(file_token));
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split("=");
                tokenMap.put(split[0], split[1]);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String get(String key) {
        //如果不存在就返回空
        return tokenMap.get(key);
    }

    public void add(String key, String value) {
        //如果不存在则添加
        if (!tokenMap.containsKey(key)) {
            tokenMap.put(key, value);
            //保存到文件
            try {
                java.io.FileWriter fw = new java.io.FileWriter(file_token, true);
                fw.write(key + "=" + value);
                fw.write(System.getProperty("line.separator"));//换行
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
