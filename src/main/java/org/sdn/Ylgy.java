package org.sdn;

import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;

public final class Ylgy extends JavaPlugin {
    public static final Ylgy INSTANCE = new Ylgy();

    private Ylgy() {
        super(new JvmPluginDescriptionBuilder("org.mex.ylgy", "0.3.3")
                .name("羊了个羊")
                .info("秒过羊了个羊mirai插件")
                .author("mex")
                .build());
    }

    @Override
    public void onEnable() {
        getLogger().info("加载 秒过羊了个羊mirai插件");
        CommandManager.INSTANCE.registerCommand(Play.INSTANCE,false);
    }
}