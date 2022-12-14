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

    public static String FinishGame_get(String token, String user_agent, int cost_time, String type) throws IOException {//完成游戏,type = game,加入羊圈挑战;type = topic,加入话题挑战
        String url;
        switch (type) {
            case "game":
                url = "https://cat-match.easygame2021.com/sheep/v1/game/game_over?rank_score=1&rank_state="+"1"+"&rank_time="+cost_time+"&rank_role=1&skin=1";
                break;
            case "topic":
                url = "https://cat-match.easygame2021.com/sheep/v1/game/topic_game_over?rank_score=1&rank_state=1&rank_time="+cost_time+"&rank_role=2&skin="+"1"+"&t="+token;
                break;
            default:
                return null;
        }
        return get(token, user_agent, url);
    }

    public static String GetUidInfo_get(String uid, String token, String user_agent) throws IOException {//获取指定用户的信息,这一个信息每天重置一次
        String url = "https://cat-match.easygame2021.com/sheep/v1/game/user_rank_info?uid="+uid;
        return get(token, user_agent, url);
    }

    public static String GetUidInfo2_get(String uid, String token, String user_agent) throws IOException {//获取指定用户的信息,这一个头像不会每天重置
        String url = "https://cat-match.easygame2021.com/sheep/v1/game/user_info?uid="+uid+"&t="+token;
        return get(token, user_agent, url);
    }

    public static String GetOpenIdByUid(String uid, String token, String user_agent) throws IOException {//获取指定地图的信息
        String url = "https://cat-match.easygame2021.com/sheep/v1/game/user_info?uid="+uid;
        return get(token, user_agent, url);
    }

    public static String GetTokenByOpenId(String uid, String open_id, String token, String user_agent, String head) throws IOException {//获取指定地图的信息
//        String url = "https://cat-match.easygame2021.com/sheep/v1/user/login_tourist";
        String url = "https://cat-match.easygame2021.com/sheep/v1/user/login_oppo";
        return post(uid, open_id, token, user_agent, url, head);
    }

    private static String get(String token, String user_agent, String url) throws IOException {//这个游戏用的get方法,令牌,浏览器,完成耗时
        String Host = "cat-match.easygame2021.com";

        URL obj = new URL(url);

        //发送get请求
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //默认值我GET
        con.setRequestMethod("GET");

        //添加请求头
        con.setRequestProperty("Host", Host);
        con.setRequestProperty("User-Agent", user_agent);
        con.setRequestProperty("t", token);

        int responseCode = con.getResponseCode();
//        Ylgy.INSTANCE.getLogger().info("\nSending 'GET' request to URL : " + finishAPI);
//        Ylgy.INSTANCE.getLogger().info("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //打印结果
//        Ylgy.INSTANCE.getLogger().info(response.toString());
        return response.toString();
    }

    private static String post(String uid, String openid, String token, String user_agent, String url, String head) throws IOException {//这个游戏用的get方法,令牌,浏览器,完成耗时
//        String Host = "cat-match.easygame2021.com";
//        String body = "{\"uuid\":\""+openid+"\"}";
        String touxiang = head;
        String body = "avatar="+touxiang+"&nick_name="+(touxiang.length()-8)+"&sex=1&uid="+openid;
        URL obj = new URL(url);

        //发送get请求
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //设置为POST
        con.setRequestMethod("POST");

        //发送POST请求必须设置如下两行
        con.setDoOutput(true);
        con.setDoInput(true);

        //添加请求头
        con.setRequestProperty("Host", "cat-match.easygame2021.com");
        con.setRequestProperty("Accept", "*/*");
        con.setRequestProperty("Accept-Encoding", "gzip, deflate");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("User-Agent", user_agent);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("Referer", "https://wq.wxredcover.cn/");
        con.setRequestProperty("Content-Length", body.length()+"");
        con.setRequestProperty("t", token);
        //添加请求体
        con.setDoOutput(true);
        con.getOutputStream().write(body.getBytes());

        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //打印结果
//        Ylgy.INSTANCE.getLogger().info("post结果:"+response.toString());
        return response.toString();
    }
}
