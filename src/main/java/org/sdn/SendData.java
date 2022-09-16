package org.sdn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendData {

//    private static String MAP_API = "https://cat-match.easygame2021.com/sheep/v1/game/map_info?map_id=%s";//获取地图信息,暂时用不到

    // 完成游戏接口 需要参数状态以及耗时（单位秒）
//    private static String FINIST_STATE = "1";//1表示完成游戏
//    private static int FINISH_COST_TIME = 60;//完成游戏耗时,单位秒
//    private static String FINISH_API = "https://cat-match.easygame2021.com/sheep/v1/game/game_over?rank_score=1&rank_state=%s&rank_time=%s&rank_role=1&skin=1";

    public static String get(String token, String user_agent, int cost_time) throws IOException {//这个游戏用的get方法,令牌,浏览器,完成耗时
        String finishAPI = "https://cat-match.easygame2021.com/sheep/v1/game/game_over?rank_score=1&rank_state="+"1"+"&rank_time="+cost_time+"&rank_role=1&skin=1";
        String Host = "cat-match.easygame2021.com";
        //发送get请求
        URL obj = new URL(finishAPI);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //默认值我GET
        con.setRequestMethod("GET");

        //添加请求头
        con.setRequestProperty("Host", Host);
        con.setRequestProperty("User-Agent", user_agent);
        con.setRequestProperty("t", token);

        int responseCode = con.getResponseCode();
        Ylgy.INSTANCE.getLogger().info("\nSending 'GET' request to URL : " + finishAPI);
        Ylgy.INSTANCE.getLogger().info("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //打印结果
        Ylgy.INSTANCE.getLogger().info(response.toString());
        return response.toString();
    }
}
