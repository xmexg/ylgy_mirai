package org.sdn;

import net.mamoe.mirai.console.command.CommandSenderOnMessage;
import net.mamoe.mirai.console.command.java.JSimpleCommand;

import java.io.IOException;
import java.util.Random;

public class Play extends JSimpleCommand {
    public static final Play INSTANCE = new Play();

    public static String HEADER_TOKEN = "";// 用户token
    public static String HEADER_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36 Edg/105.0.1343.33";
    public static int COST_TIME = 0;// 耗时,<=0为随机时间
    public static int CYCLE_COUNT = 1;// 要通关次数

    private Play() {
        super(Ylgy.INSTANCE, "羊了个羊");
        setDescription("秒过羊了个羊mirai插件版");
    }

    String help(){
        StringBuffer bfHelp = new StringBuffer();
        bfHelp.append("这是一个根据 a-sheep-assistant 改编的 mirai 插件版羊了个羊秒完成插件\n\n");
        bfHelp.append("羊了个羊命令帮助:\n");
        bfHelp.append("说明:\nhelp 查看帮助\ntoken=eyXXXXXXXXX 设置token\ncosttime=60 设置耗时\ncycle=1 设置通关次数(最大40次)\n");
        bfHelp.append("示例:\n羊了个羊\n羊了个羊 token=eyXXXXXXXXX\n羊了个羊 token=eyXXXXXXXXX costtime=60 cycle=1\n");
        return bfHelp.toString();
    }

//    @Handler
//    public void handle(CommandSenderOnMessage sender) {
//        String qq = sender.getUser().getId() + "";
//        TokenFile tokenFile = new TokenFile();
//        if(tokenFile.get(qq) == null){
//            sender.getSubject().sendMessage("没有找到你的微信token,请通过 羊了个羊 token=(你的token) 来自动羊了个羊\n\n"+help());
//            return;
//        }else{//应该看一看返回的结果,看一看error为0即为成功
//            StartPlay(sender);
//        }
//    }

    @Handler
    public void handle(CommandSenderOnMessage sender, String ...arg){
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
                case "token":
                    HEADER_TOKEN = list[1];
                    break;
                case "costtime":
                    COST_TIME = Integer.parseInt(list[1]);
                    break;
                case "cycle":
                    CYCLE_COUNT = Integer.parseInt(list[1]);
                    break;
                default:
                    sender.getSubject().sendMessage("参数: "+info+" 无法解析");
                    break;
            }
        }

        //开始执行
        sender.getSubject().sendMessage("开始执行羊了个羊秒完成");
        for(int i = CYCLE_COUNT>40?1:CYCLE_COUNT; i <= CYCLE_COUNT; i++){
            StartPlay(sender);
        }
    }

    private String StartPlay(CommandSenderOnMessage sender){
        String result = null;
        if(COST_TIME <= 0)
            COST_TIME = new Random().nextInt(600)+1;
        try {
            result = SendData.get(HEADER_TOKEN, HEADER_USER_AGENT, COST_TIME);
            String errorCode = result.substring(result.indexOf("err_code")+10,result.indexOf("err_code")+11);
            if(errorCode.equals("0")){
                sender.getSubject().sendMessage("羊了个羊成功");
            }else{
                sender.getSubject().sendMessage("羊了个羊秒完成失败:"+result);
            }
        }catch (IOException e){
            sender.getSubject().sendMessage("发生了一点错误:\n"+e.toString());
            return e.toString();
        }
        return result;
    }
}

