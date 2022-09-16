package org.sdn;

import java.io.*;
import java.util.HashMap;
import java.util.Random;

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

    public String getOne() {
        //随机获取一个value
        int size = tokenMap.size();//获取map的大小
        return tokenMap.values().toArray(new String[size])[new Random().nextInt(size)];
    }

    public void set(String key, String value) {//不存在就添加,存在就修改并存文件
        //如果不存在则添加
        if (!tokenMap.containsKey(key)) {
            tokenMap.put(key, value);
            SaveToFile(key, value, true);
        }else{
            tokenMap.replace(key, value);
            ClearFile();
            SaveToFile(tokenMap, true);
        }
    }

    private void SaveToFile(String key, String value, boolean append) {//存文件
        try {
            FileWriter fw = new FileWriter(file_token, append);
            fw.write(key + "=" + value);
            fw.write(System.getProperty("line.separator"));//换行
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void SaveToFile(HashMap hashmap, boolean append) {//存文件
        try {
            FileWriter fw = new FileWriter(file_token, append);
            for (Object key : hashmap.keySet()) {
                fw.write(key + "=" + hashmap.get(key));
                fw.write(System.getProperty("line.separator"));//换行
            }
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ClearFile() {//清空文件
        try {
            FileWriter fw = new FileWriter(file_token, false);
            fw.write("");
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
