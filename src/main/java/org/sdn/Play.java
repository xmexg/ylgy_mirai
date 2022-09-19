package org.sdn;

import net.mamoe.mirai.console.command.CommandSenderOnMessage;
import net.mamoe.mirai.console.command.java.JSimpleCommand;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Play extends JSimpleCommand {
    public static final Play INSTANCE = new Play();
    private TokenFile tokenFile = new TokenFile();
    private final int MAXCYCLE = 50;
    public static String HEADER_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36 Edg/105.0.1343.33";

    private Play() {
        super(Ylgy.INSTANCE, "羊了个羊");
        setDescription("秒过羊了个羊mirai插件版");
    }

    String help(){
        StringBuffer bfHelp = new StringBuffer();
        bfHelp.append("这是一个根据 a-sheep-assistant 改编的羊了个羊秒完成 mirai 插件\n\n");
        bfHelp.append("羊了个羊命令帮助:\n");
        bfHelp.append("说明:\nhelp 查看帮助\ntoken=eyXXXXXXXXX 设置token(仅私聊时有效)\ncosttime=60 设置耗时\ncycle=1 设置通关次数(最大"+MAXCYCLE+"次,超过"+MAXCYCLE+"次会被设置成1次)\nuid=12345678 通过uid来羊了个羊\nuidinfo=12345678 查询uid信息\nhead=http://xxx.jpg 配合uid设置头像(外部链接好像会清空头像)\n");
        bfHelp.append("示例:\n羊了个羊\n羊了个羊 token=eyXXXXXXXXX\n羊了个羊 token=eyXXXXXXXXX costtime=60 cycle=1\n羊了个羊 uid=12345678\n羊了个羊 uidinfo=12345678\n羊了个羊 uid=12345678 head=http://xxx\n");
        return bfHelp.toString();
    }

    @Handler
    public void handle(CommandSenderOnMessage sender, String ...arg){
        String qq = sender.getUser().getId() + "";
        String user_token = tokenFile.get(qq);//没有时为null
        int cost_time = 0;//耗时
        int cycle_time = 1;//通关次数
        String uidinfo = null;//查询指定用户时才需要的uid
        String uid = null;//羊了个羊时才需要的uid
        String head = null;//头像连接

        //查看现在是不是在qq群里
//        boolean isGroup = sender.getSubject().getId() != sender.getUser().getId();
        boolean isGroup = true;//默认为在群聊中
        String textWithGroupId = sender.getPermitteeId().toString();//群信息,如果在群里获取到的是群号.发送者qq号
        if(!textWithGroupId.contains(".")){//这才是不在群里
                isGroup = false;
        }


        //初始化参数
        for(String info:arg){
            if (info.equals("help")){
                sender.getSubject().sendMessage(help());
                return;
            }
            String[] list = info.split("=",2);
            if(list.length != 2 || list[0] == null || list[1] == null || list[0].length()==0 || list[1].length()==0) {
                sender.getSubject().sendMessage("参数不正确,请使用help查看帮助");
                return;
            }
            switch (list[0]){
                case "uidinfo"://获取指定用户的信息
                    uidinfo = list[1];
                    break;
                case "uid"://通过uid来羊了个羊
                    uid = list[1];
                    break;
                case "token":
                    if(isGroup){
                        sender.getSubject().sendMessage("请撤回token并私聊机器人设置token");
                        return;
                    }
                    user_token = list[1];
                    if(tokenFile.get(qq) == null)
                        tokenFile.set(qq,user_token);
                    break;
                case "costtime":
                    cost_time = Integer.parseInt(list[1]);
                    break;
                case "cycle":
                    cycle_time = Integer.parseInt(list[1]);
                    break;
                case "head":
                    head = list[1];
                    sender.getSubject().sendMessage("头像:"+head);
                    break;
                default:
                    sender.getSubject().sendMessage("参数: "+info+" 无法解析");
                    break;
            }
        }

        if (uidinfo != null){
            sender.getSubject().sendMessage("开始查询 "+uidinfo+" 的信息");
            sendUidInfo(sender, uidinfo, user_token);
            return;
        }

        if(uid != null){
            sender.getSubject().sendMessage("开始羊了个羊uid:"+uid);
            sendTokenByUid(sender, uid, user_token, cost_time, cycle_time, head);
            return;
        }

        if (user_token==null || user_token.length()==0) {
            sender.getSubject().sendMessage("没有找到你的token,无法羊了个羊打卡,请私聊机器人带上token参数");
            return;
        }

        run(sender, user_token, cost_time, cycle_time);
    }

    private void run(CommandSenderOnMessage sender, String user_token, int cost_time, int cycle_time) {
        //开始执行
        cycle_time = cycle_time > MAXCYCLE || cycle_time < 1? 1 : cycle_time;
        for(int i = 1; i <= cycle_time; i++){
            int set_cost_time = cost_time <= 0 ? new Random().nextInt(600)+1 : cost_time;
            if(cycle_time==1) {
                sender.getSubject().sendMessage("开始羊了个羊秒完成,设置耗时:"+set_cost_time+"秒");
            }
            else {
                sender.getSubject().sendMessage("开始第 ( " + i + " / " + cycle_time + " ) 次羊了个羊秒完成,设置耗时:"+set_cost_time+"秒");
            }
            String result = StartPlay(sender, user_token, set_cost_time);
            if(!result.startsWith("success")){
                sender.getSubject().sendMessage("羊了个羊秒完成失败,错误信息:\n"+result);
                return;
            }
        }
    }

    private String StartPlay(CommandSenderOnMessage sender, String user_token, int cost_time){
        String result;
        if(cost_time <= 0)
            cost_time = new Random().nextInt(600)+1;
        try {
            result = SendData.FinishGame_get(user_token, HEADER_USER_AGENT, cost_time);
            String errorCode = result.substring(result.indexOf("err_code")+10,result.indexOf("err_code")+11);
            if(errorCode.equals("0")){
                return "success";
            }else{
                return "fail:"+result;
            }
        }catch (IOException e){
            return "fail:"+e.toString();
        }
    }

    public static String[] getUidInfo1(String uid, String token) {//这里绝对不能引入其他方法,也不能使用post
        String result;
        String[] uidinfo = new String[26];
//        StringBuffer info = new StringBuffer();
        if(token == null)
            return new String[]{"0","机器人中没有token,请私聊机器人设置token"};
        try {
            result = SendData.GetUidInfo_get(uid, token, HEADER_USER_AGENT);
        }catch (IOException e){
            return new String[]{"0","发生了一点错误:\n"+ e};
        }

        if (result.length()==0) {
            return new String[]{"0","羊了个羊没有返回数据"};
        }
        if (!result.substring(result.indexOf("err_code") + 10, result.indexOf(",")).equals("0")) {
            return new String[]{"0","羊了个羊返回错误信息:"+result};
        }
        uidinfo[0] = "昵称";
        uidinfo[1] = result.substring(result.indexOf("nick_name") + 12, result.indexOf("avatar") - 3);
        uidinfo[2] = "头像";
        if(result.indexOf("language") != -1)
            uidinfo[3] = result.substring(result.indexOf("avatar") + 9, result.indexOf("language") - 5);
        else
            uidinfo[3] = result.substring(result.indexOf("avatar") + 9, result.indexOf("region") - 3);
        uidinfo[4] = "省份";
        uidinfo[5] = result.substring(result.indexOf("region") + 9, result.indexOf("city") - 3);
        uidinfo[6] = "城市";
        uidinfo[7] = result.substring(result.indexOf("city") + 7, result.indexOf("uid") - 3);
        uidinfo[8] = "state";
        uidinfo[9] = result.substring(result.indexOf("state") + 7, result.indexOf("time") - 2);
        uidinfo[10] = "上次完成耗时";
        uidinfo[11] = result.substring(result.indexOf("time") + 6, result.indexOf("role") - 2);
        uidinfo[12] = "role";
        uidinfo[13] = result.substring(result.indexOf("role") + 6, result.indexOf("gender") - 2);
        uidinfo[14] = "gender";
        uidinfo[15] = result.substring(result.indexOf("gender") + 8, result.indexOf("first") - 2);
        uidinfo[16] = "first";
        uidinfo[17] = result.substring(result.indexOf("first") + 7, result.indexOf("ts") - 2);
        uidinfo[18] = "上次完成挑战时间";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long time = Long.valueOf(result.substring(result.indexOf("ts") + 4, result.indexOf("fail") - 2));
        Date date = new Date(time * 1000L);
        uidinfo[19] = sdf.format(date);
        uidinfo[20] = "fail";
        uidinfo[21] = result.substring(result.indexOf("fail") + 6, result.indexOf("skin") - 2);
        uidinfo[22] = "skin";
        uidinfo[23] = result.substring(result.indexOf("skin") + 6, result.indexOf("}"));
        uidinfo[24] = "uid";
        uidinfo[25] = uid;
        return uidinfo;
    }

    public static String[] getUidInfo2(String uid, String token){
        String result;
        String uidinfo[] = new String[20];
        if (token == null){
            return new String[]{"0","机器人中没有token,请私聊机器人设置token"};
        }
        try {
            result = SendData.GetUidInfo2_get(uid, token, HEADER_USER_AGENT);
        }catch (IOException e){
            return new String[]{"0","发生了一点错误:\n"+e.toString()};
        }
        if (result.length()==0) {
            return new String[]{"0","羊了个羊没有返回数据"};
        }
        if (!result.substring(result.indexOf("err_code") + 10, result.indexOf(",")).equals("0")) {
            return new String[]{"0","羊了个羊返回错误信息:"+result};
        }
        uidinfo[0] = "created_at";
        uidinfo[1] = result.substring(result.indexOf("created_at") + 13, result.indexOf("updated_at") - 3);
        uidinfo[2] = "updated_at";
        uidinfo[3] = result.substring(result.indexOf("updated_at") + 13, result.indexOf("role") - 3);
        uidinfo[4] = "role";
        uidinfo[5] = result.substring(result.indexOf("role") + 6, result.indexOf("uid")-2);
        uidinfo[6] = "头像";
        if(result.indexOf("language") != -1)
            uidinfo[7] = result.substring(result.indexOf("avatar") + 9, result.indexOf("language") - 3);
        else
            uidinfo[7] = result.substring(result.indexOf("avatar") + 9, result.indexOf("wx_open_id") - 3);
        uidinfo[8] = "last_login_time";
        uidinfo[9] = result.substring(result.indexOf("last_login_time") + 17, result.indexOf("last_logout_time") - 2);
        uidinfo[10] = "last_logout_time";
        uidinfo[11] = result.substring(result.indexOf("last_logout_time") + 18, result.indexOf("charge_first_time") - 2);
        uidinfo[12] = "charge_first_time";
        uidinfo[13] = result.substring(result.indexOf("charge_first_time") + 19, result.indexOf("charge_last_time") - 2);
        uidinfo[14] = "charge_last_time";
        uidinfo[15] = result.substring(result.indexOf("charge_last_time") + 18, result.indexOf("charge_total") - 2);
        uidinfo[16] = "charge_total";
        uidinfo[17] = result.substring(result.indexOf("charge_total") + 14, result.indexOf("charge_times") - 2);
        uidinfo[18] = "charge_times";
        uidinfo[19] = result.substring(result.indexOf("charge_times") + 14, result.indexOf("}"));
        return uidinfo;
    }

    private void sendUidInfo(CommandSenderOnMessage sender, String uid, String token) {
        StringBuffer info = new StringBuffer(uid+" 的信息为:\n");
        token = checkAndGetToken(token);
        if(token == null){
            sender.getSubject().sendMessage("机器人中没有可用的token,请私聊机器人设置token");
            return;
        }
        String[] uidinfo1 = getUidInfo1(uid, token);
        String[] uidinfo2 = getUidInfo2(uid, token);
        info.append("接口一:\n");
        for (int i = 0; i < uidinfo1.length; i++) {
            info.append(uidinfo1[i] + ":" + uidinfo1[i + 1] + "\n");
            i++;
        }
        info.append("\n接口二:\n");
        for (int i = 0; i < uidinfo2.length; i++) {
            info.append(uidinfo2[i] + ":"  + uidinfo2[i + 1] + "\n");
            i++;
        }
        sender.sendMessage(info.toString());
    }

    private String checkAndGetToken( String token) {//用户没有自己的token就随机获取一个,有的话就用用户的
        if(token == null || token.length()==0) {
            //随机获取一个token
            token = tokenFile.getOne();
            if(token == null || token.length()==0) {//用户没写token,并且服务器中也没有token
                return null;
            }
        }
        return token;
    }

    private void sendTokenByUid(CommandSenderOnMessage sender, String uid, String y_token, int cost_time, int cycle_time, String head) {
        String user_tokenResult, user_token;
        String OpenIdResult, OpenId;
        String token = checkAndGetToken(y_token);//用户没有自己的token就随机获取一个,有的话就用用户的
        if (token == null)
            return;
        try {
            sender.getSubject().sendMessage("正在获取"+uid+"的openid");
            OpenIdResult = SendData.GetOpenIdByUid(uid, token, HEADER_USER_AGENT);
            if(OpenIdResult.length()==0){
                sender.getSubject().sendMessage("查询失败,请稍后重试");
                return;
            }
            if(OpenIdResult.substring(OpenIdResult.indexOf("err_code")+10,OpenIdResult.indexOf(",")).equals("0")){
                sender.getSubject().sendMessage("正在根据"+uid+"的openid获取token");
                OpenId = OpenIdResult.substring(OpenIdResult.indexOf("wx_open_id")+13,OpenIdResult.indexOf("wx_union_id")-3);
//                sender.getSubject().sendMessage("获取到的Openid:"+OpenId);
                if(head == null || head.length() == 0) {
                    String result_touxiang = SendData.GetUidInfo2_get(uid, token, HEADER_USER_AGENT);
                    head = result_touxiang.substring(SendData.GetUidInfo2_get(uid, token, HEADER_USER_AGENT).indexOf("avatar")+9,SendData.GetUidInfo2_get(uid, token, HEADER_USER_AGENT).indexOf("wx_open_id")-3);
                }
                user_tokenResult = SendData.GetTokenByOpenId(uid, OpenId, token, HEADER_USER_AGENT, head);
//                sender.getSubject().sendMessage("获取到的user_token:"+user_tokenResult);
                if(user_tokenResult.length()==0){
                    sender.getSubject().sendMessage("获取"+uid+"的token失败,请稍后重试");
                    return;
                }
                if(user_tokenResult.substring(user_tokenResult.indexOf("err_code")+10,user_tokenResult.indexOf(",")).equals("0")) {
                    user_token = user_tokenResult.substring(user_tokenResult.indexOf("token") + 8, user_tokenResult.indexOf("\",\"uid"));
//                    sender.getSubject().sendMessage("获取到的user_token:"+user_token);
                    run(sender,user_token,cost_time,cycle_time);

                }else{
                    sender.getSubject().sendMessage("在获取 "+uid+" 的token时发生了一点错误:\n"+user_tokenResult);
                }
            }else {
                sender.getSubject().sendMessage("在获取 "+uid+" OpenId时发生错误:\n"+OpenIdResult);
            }
        }catch (IOException e){
            sender.getSubject().sendMessage("在获取 "+uid+" 的信息时发生了一点错误:\n"+ e);
        }
    }
}