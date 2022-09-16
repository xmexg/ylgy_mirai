# ylgy_mirai
羊了个羊mirai插件

# 如何使用
插件下载后放到mirai的plugins文件夹下重启mirai机器人就好了

# 效果展示
![qq机器人](/ylgyImg/show1.jpg)
![游戏截屏](/ylgyImg/show2.jpg)

# 如何获取pc微信token
1. 安装fiddler  
2. 启动fiddler并做一些修改  
![fiddler设置](/ylgyImg/1.png)
3. 删除两个微信小程序文件  
![微信小程序文件1](/ylgyImg/2.jpg)
4. 启动微信并设置代理  
![微信设置代理](/ylgyImg/3.jpg)
5. 微信打开羊了个羊小程序  
![羊了个羊](/ylgyImg/4.jpg)
6. 退出微信，查看fiddler抓到的url以 `/sheep/v1/game/user_info` 开头的数据包  
![查看数据包](/ylgyImg/5.jpg)
7. 点看这个数据包，按十六进制视图，以`ey`开头的这段数据就是token  
![得到token](/ylgyImg/6.jpg)
