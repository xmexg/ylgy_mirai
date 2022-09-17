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
    private int MAXCYCLE = 5;
    public static String HEADER_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36 Edg/105.0.1343.33";

    private Play() {
        super(Ylgy.INSTANCE, "羊了个羊");
        setDescription("秒过羊了个羊mirai插件版");
    }

    String help(){
        StringBuffer bfHelp = new StringBuffer();
        bfHelp.append("这是一个根据 a-sheep-assistant 改编的羊了个羊秒完成 mirai 插件\n\n");
        bfHelp.append("羊了个羊命令帮助:\n");
        bfHelp.append("说明:\nhelp 查看帮助\ntoken=eyXXXXXXXXX 设置token(仅私聊时有效)\ncosttime=60 设置耗时\ncycle=1 设置通关次数(最大"+MAXCYCLE+"次,超过"+MAXCYCLE+"次会被设置成1次)\nuid=12345678 通过uid来羊了个羊\nuidinfo=12345678 查询uid信息\n");
        bfHelp.append("示例:\n羊了个羊\n羊了个羊 token=eyXXXXXXXXX\n羊了个羊 token=eyXXXXXXXXX costtime=60 cycle=1\n羊了个羊 uid=12345678\n羊了个羊 uidinfo=12345678");
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

        //查看现在是不是在qq群里
//        boolean isGroup = sender.getSubject().getId() != sender.getUser().getId();
        boolean isGroup = true;//默认为在群聊中
        String textWithGroupId = sender.getPermitteeId().toString();//群信息,如果在群里获取到的是群号.发送者qq号
        if(textWithGroupId.indexOf(".") == -1){//这才是不在群里
                isGroup = false;
        }


        //初始化参数
        for(String info:arg){
            if (info.equals("help")){
                sender.getSubject().sendMessage(help());
                return;
            }
            String list[] = info.split("=");
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
            sendTokenByUid(sender, uid, user_token, cost_time, cycle_time);
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
            if(cycle_time==1)
                sender.getSubject().sendMessage("开始执行羊了个羊秒完成,请稍等...");
            else
                sender.getSubject().sendMessage("开始执行第 ( "+i+" / "+cycle_time+" ) 次羊了个羊秒完成,请稍等...");
            StartPlay(sender, user_token, cost_time);
        }
    }

    private String StartPlay(CommandSenderOnMessage sender, String user_token, int cost_time){
        String result = null;
        if(cost_time <= 0)
            cost_time = new Random().nextInt(600)+1;
        try {
            result = SendData.FinishGame_get(user_token, HEADER_USER_AGENT, cost_time);
            String errorCode = result.substring(result.indexOf("err_code")+10,result.indexOf("err_code")+11);
            if(errorCode.equals("0")){
                sender.getSubject().sendMessage("羊了个羊成功,闯关时长: "+cost_time+" 秒");
            }else{
                sender.getSubject().sendMessage("羊了个羊秒完成失败:"+result);
            }
        }catch (IOException e){
            sender.getSubject().sendMessage("发生了一点错误:\n"+e.toString());
            return e.toString();
        }
        return result;
    }

    private void sendUidInfo(CommandSenderOnMessage sender, String uid, String token) {
        String result = null;
        StringBuffer info = new StringBuffer();
        token = checkAndGetToken(sender, token);
        if(token == null)
            return;
        try {
            result = SendData.GetUidInfo_get(uid, token, HEADER_USER_AGENT);
            if(result == null){
                sender.getSubject().sendMessage("查询失败,请稍后重试");
                return;
            }
            if(!result.substring(result.indexOf("err_code")+10,result.indexOf(",")).equals("0")){
                sender.getSubject().sendMessage(result);
                return;
            }
            info.append(uid+" 的信息为:\n");
            info.append("昵称:"+result.substring(result.indexOf("nick_name")+12,result.indexOf("avatar")-3)+"\n");
            info.append("头像:"+result.substring(result.indexOf("avatar")+9,result.indexOf("region")-3)+"\n");
            info.append("省份:"+result.substring(result.indexOf("region")+9,result.indexOf("city")-3)+"\n");
            info.append("城市:"+result.substring(result.indexOf("city")+7,result.indexOf("uid")-3)+"\n");
            info.append("state:"+result.substring(result.indexOf("state")+7,result.indexOf("time")-2)+"\n");
            info.append("上次完成耗时:"+result.substring(result.indexOf("time")+6,result.indexOf("role")-2)+"秒\n");
            info.append("role:"+result.substring(result.indexOf("role")+6,result.indexOf("gender")-2)+"\n");
            info.append("gender:"+result.substring(result.indexOf("gender")+8,result.indexOf("first")-2)+"\n");
            info.append("first:"+result.substring(result.indexOf("first")+7,result.indexOf("ts")-2)+"\n");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Long time = Long.valueOf(result.substring(result.indexOf("ts")+4,result.indexOf("fail")-2));
            Date date = new Date(time*1000L);
            info.append("上次完成挑战时间:"+sdf.format(date)+"\n");
            info.append("fail:"+result.substring(result.indexOf("fail")+6,result.indexOf("skin")-2)+"\n");
            info.append("skin:"+result.substring(result.indexOf("skin")+6,result.indexOf("}")));
//            sender.getSubject().sendMessage(result);
            sender.getSubject().sendMessage(info.toString());
        }catch (IOException e){
            sender.getSubject().sendMessage("在获取 "+uid+" 信息时发生了一点错误:\n"+ e);
        }
    }

    private String checkAndGetToken(CommandSenderOnMessage sender, String token) {//用户没有自己的token就随机获取一个,有的话就用用户的
        if(token == null || token.length()==0) {
            //随机获取一个token
            token = tokenFile.getOne();
            if(token == null || token.length()==0) {
                sender.getSubject().sendMessage("你还没有设置token,且服务器中没有找到任何token,无法查询用户信息");
                return token;
            }
//            sender.getSubject().sendMessage("你还没有设置token,已随机获取一个token");
        } else {
            return token;
        }
        return token;
    }

    private String sendTokenByUid(CommandSenderOnMessage sender, String uid, String y_token, int cost_time, int cycle_time) {//传过来的token暂时假装不存在
        String result = null;
        String user_tokenResult, user_token = null;
        String OpenIdResult, OpenId;
        String token = checkAndGetToken(sender, null);//用户使用uid,一定不会带token
        if (token == null)
            return null;
        try {
            sender.getSubject().sendMessage("正在获取"+uid+"的openid");
            OpenIdResult = SendData.GetOpenIdByUid(uid, token, HEADER_USER_AGENT);
            if(OpenIdResult == null){
                sender.getSubject().sendMessage("查询失败,请稍后重试");
                return null;
            }
            if(OpenIdResult.substring(OpenIdResult.indexOf("err_code")+10,OpenIdResult.indexOf(",")).equals("0")){
                sender.getSubject().sendMessage("正在根据"+uid+"的openid获取token");
                OpenId = OpenIdResult.substring(OpenIdResult.indexOf("wx_open_id")+13,OpenIdResult.indexOf("wx_union_id")-3);
//                sender.getSubject().sendMessage("获取到的Openid:"+OpenId);
                user_tokenResult = SendData.GetTokenByOpenId(OpenId, token, HEADER_USER_AGENT);
//                sender.getSubject().sendMessage("获取到的user_token:"+user_tokenResult);
                if(user_tokenResult == null){
                    sender.getSubject().sendMessage("获取"+uid+"的token失败,请稍后重试");
                    return null;
                }
                if(user_tokenResult.substring(user_tokenResult.indexOf("err_code")+10,user_tokenResult.indexOf(",")).equals("0")) {
                    user_token = user_tokenResult.substring(user_tokenResult.indexOf("token") + 8, user_tokenResult.indexOf("\",\"uid"));
//                    sender.getSubject().sendMessage("获取到的user_token:"+user_token);
                    run(sender,user_token,cost_time,cycle_time);

                }else{
                    sender.getSubject().sendMessage("在获取 "+uid+" 的token时发生了一点错误:\n"+user_tokenResult);
                    return null;
                }
            }else {
                sender.getSubject().sendMessage("在获取 "+uid+" OpenId时发生错误:\n"+OpenIdResult);
            }
        }catch (IOException e){
            sender.getSubject().sendMessage("在获取 "+uid+" 的信息时发生了一点错误:\n"+ e);
        }

        return result;
    }
}