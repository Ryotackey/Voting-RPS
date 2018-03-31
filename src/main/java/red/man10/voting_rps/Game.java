package red.man10.voting_rps;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;

public class Game {

    private Voting_RPS plugin;

    public Game(Voting_RPS plugin){
        this.plugin = plugin;
    }

    int time;

    int timer;

    public void votingTime(){

        if (!plugin.setup){
            return;
        }

        time = plugin.config.getConfig().getInt("time");

        plugin.voting = true;

        Bukkit.broadcastMessage(plugin.prefex + "§a§l投票フェーズが始まりました");
        Bukkit.broadcastMessage(plugin.prefex + "§f§r投票に参加する人は§b§l/mv§fからどうぞ");

        new BukkitRunnable(){
            @Override
            public void run() {

                if (time == 0){

                    if (plugin.voteitem.size() < plugin.config.getConfig().getInt("minvote")){

                        Bukkit.broadcastMessage(plugin.prefex + "§4§l投票数が足りないのでキャンセルします");

                        cancel();

                        plugin.cancelGame();

                        return;

                    }

                    Bukkit.broadcastMessage(plugin.prefex + "§c受付終了しました");
                    time = plugin.config.getConfig().getInt("time");
                    plugin.voting = false;
                    cancel();
                    game();
                    return;
                }

                if (time % 10 == 0 || time <= 5){
                    Bukkit.broadcastMessage(plugin.prefex + "§a投票受付終了まであと§f§l" + time + "秒");
                }

                time--;

            }
        }.runTaskTimer(plugin,0,20);
    }

    public void game(){

        plugin.battleinv1 = Bukkit.createInventory(null, 45, "§a§lVoting §e§lRPS");
        plugin.battleinv2 = Bukkit.createInventory(null, 45, "§a§lVoting §e§lRPS");

        int[] slot = {20, 22, 24};

        for (int i = 0; i < 45; i++){

            plugin.battleinv1.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)15));
            plugin.battleinv2.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)15));

        }

        for (int i = 0; i < 3; i++){

            Random r = new Random();

            int random = r.nextInt(plugin.voteitem.size());

            plugin.battleinv1.setItem(slot[i], plugin.voteitem.get(random));

            plugin.item1.add(plugin.voteitem.get(random));

            plugin.voteitem.remove(random);

        }

        for (int i = 0; i < 3; i++){

            Random r = new Random();

            int random = r.nextInt(plugin.voteitem.size());

            plugin.battleinv2.setItem(slot[i], plugin.voteitem.get(random));

            plugin.item2.add(plugin.voteitem.get(random));

            plugin.voteitem.remove(random);

        }

        plugin.battleplayer.get(0).openInventory(plugin.battleinv1);
        plugin.battleplayer.get(1).openInventory(plugin.battleinv2);

        return;

    }

    public void result(){

        if (!plugin.setup){
            return;
        }

        timer = 3;

        Bukkit.broadcastMessage(plugin.prefex + "§e§lジャンケン！...§f§k§laaa");

        new BukkitRunnable(){
            @Override
            public void run() {

                if (timer == 0){

                    Bukkit.broadcastMessage(plugin.prefex + "§e§lポン！");

                    int judge = judge();

                    switch (judge){

                        case 0:

                            Bukkit.broadcastMessage(plugin.prefex + "§a§lあいこでした");

                            cancel();
                            reGame();

                        case 1:

                            Bukkit.broadcastMessage(plugin.prefex + "§c§l" + plugin.battleplayer.get(0).getDisplayName() + "§f§lさんの§6§l勝利！");

                            Bukkit.broadcastMessage(plugin.prefex + "§c§l" + plugin.battleplayer.get(0).getDisplayName() + "§f§lさんの§6§l勝利！");

                            OfflinePlayer p1 = Bukkit.getOfflinePlayer(plugin.uuid1);
                            if (p1 == null){
                                Bukkit.getLogger().info(plugin.uuid1.toString()+"は見つからない");
                            }else {
                                plugin.val.deposit(plugin.uuid1, plugin.bal*2);
                            }

                            break;

                        case 2:

                            Bukkit.broadcastMessage(plugin.prefex + "§b§l" + plugin.battleplayer.get(1).getDisplayName() + "§f§lさんの§6§l勝利！");

                            OfflinePlayer p2 = Bukkit.getOfflinePlayer(plugin.uuid2);
                            if (p2 == null){
                                Bukkit.getLogger().info(plugin.uuid2.toString()+"は見つからない");
                            }else {
                                plugin.val.deposit(plugin.uuid2, plugin.bal * 2);
                            }

                            break;

                    }

                    for (int i = 0; i < plugin.voteplayer.size(); i++){

                        plugin.val.deposit(plugin.voteplayer.get(i).getUniqueId(), plugin.config.getConfig().getDouble("rewardmoney"));
                        plugin.voteplayer.get(i).sendMessage(plugin.prefex + "§a§l投票報酬を送りました");

                    }

                    new MySQLRecord(plugin).run();

                    cancel();

                    plugin.finishGame();

                    return;

                }

                timer--;

            }
        }.runTaskTimer(plugin,0,20);
    }

    public int judge(){

        if (plugin.battleitem1 == plugin.battleitem2){
            return 0;
        }

        if (plugin.battleitem1 == plugin.rock && plugin.battleitem2 == plugin.paper){

            Bukkit.broadcastMessage(plugin.prefex + "§c§l" + plugin.battleplayer.get(0).getDisplayName() + " §f§l: §6§lグー §f§lVS§b§l" + plugin.battleplayer.get(1).getDisplayName() + " §f§l: §6§lパー");

            new MySQLexcute(plugin.battleplayer.get(1).getName(), plugin.battleplayer.get(1).getUniqueId().toString(), 2, plugin.battleplayer.get(0).getName(), plugin.battleplayer.get(0).getUniqueId().toString(), 0, plugin.bal.intValue(), plugin.voteplayer.size(), plugin).run();

            return 2;
        }

        if (plugin.battleitem1 == plugin.rock && plugin.battleitem2 == plugin.scissor){

            Bukkit.broadcastMessage(plugin.prefex + "§c§l" + plugin.battleplayer.get(0).getDisplayName() + " §f§l: §6§lグー §f§lVS§b§l" + plugin.battleplayer.get(1).getDisplayName() + " §f§l: §6§lチョキ");

            new MySQLexcute(plugin.battleplayer.get(0).getName(), plugin.battleplayer.get(0).getUniqueId().toString(), 0, plugin.battleplayer.get(1).getName(), plugin.battleplayer.get(1).getUniqueId().toString(), 1, plugin.bal.intValue(), plugin.voteplayer.size(), plugin).run();

            return 1;
        }

        if (plugin.battleitem1 == plugin.paper && plugin.battleitem2 == plugin.scissor){

            Bukkit.broadcastMessage(plugin.prefex + "§c§l" + plugin.battleplayer.get(0).getDisplayName() + " §f§l: §6§lパー §f§lVS§b§l" + plugin.battleplayer.get(1).getDisplayName() + " §f§l: §6§lチョキ");

            new MySQLexcute(plugin.battleplayer.get(1).getName(), plugin.battleplayer.get(1).getUniqueId().toString(), 1, plugin.battleplayer.get(0).getName(), plugin.battleplayer.get(0).getUniqueId().toString(), 2, plugin.bal.intValue(), plugin.voteplayer.size(), plugin).run();

            return 2;
        }

        if (plugin.battleitem1 == plugin.paper && plugin.battleitem2 == plugin.rock){

            Bukkit.broadcastMessage(plugin.prefex + "§c§l" + plugin.battleplayer.get(0).getDisplayName() + " §f§l: §6§lパー §f§lVS§b§l" + plugin.battleplayer.get(1).getDisplayName() + " §f§l: §6§lグー");

            new MySQLexcute(plugin.battleplayer.get(0).getName(), plugin.battleplayer.get(0).getUniqueId().toString(), 2, plugin.battleplayer.get(1).getName(), plugin.battleplayer.get(1).getUniqueId().toString(), 0, plugin.bal.intValue(), plugin.voteplayer.size(), plugin).run();

            return 1;
        }

        if (plugin.battleitem1 == plugin.scissor && plugin.battleitem2 == plugin.paper){

            Bukkit.broadcastMessage(plugin.prefex + "§c§l" + plugin.battleplayer.get(0).getDisplayName() + " §f§l: §6§lチョキ §f§lVS§b§l" + plugin.battleplayer.get(1).getDisplayName() + " §f§l: §6§lパー");

            new MySQLexcute(plugin.battleplayer.get(0).getName(), plugin.battleplayer.get(0).getUniqueId().toString(), 1, plugin.battleplayer.get(1).getName(), plugin.battleplayer.get(1).getUniqueId().toString(), 2, plugin.bal.intValue(), plugin.voteplayer.size(), plugin).run();

            return 1;
        }

        if (plugin.battleitem1 == plugin.scissor && plugin.battleitem2 == plugin.rock){

            Bukkit.broadcastMessage(plugin.prefex + "§c§l" + plugin.battleplayer.get(0).getDisplayName() + " §f§l: §6§lチョキ §f§lVS§b§l" + plugin.battleplayer.get(1).getDisplayName() + " §f§l: §6§lグー");

            new MySQLexcute(plugin.battleplayer.get(1).getName(), plugin.battleplayer.get(1).getUniqueId().toString(), 0, plugin.battleplayer.get(0).getName(), plugin.battleplayer.get(0).getUniqueId().toString(), 1, plugin.bal.intValue(), plugin.voteplayer.size(), plugin).run();

            return 2;
        }

        return -1;
    }

    public void reGame(){

        if (!plugin.battleinv1.contains(plugin.rock) && !plugin.battleinv1.contains(plugin.paper) && !plugin.battleinv1.contains(plugin.scissor) && !plugin.battleinv2.contains(plugin.rock) && !plugin.battleinv2.contains(plugin.paper) && !plugin.battleinv2.contains(plugin.scissor)){

            Bukkit.broadcastMessage(plugin.prefex + "§4§lすべてあいこのためキャンセルされました");
            for (int i = 0; i < plugin.voteplayer.size(); i++){

                plugin.val.deposit(plugin.voteplayer.get(i).getUniqueId(), plugin.config.getConfig().getDouble("rewardmoney"));
                plugin.voteplayer.get(i).sendMessage(plugin.prefex + "§a§l投票報酬を送りました");

            }
            plugin.cancelGame();
            return;

        }

        plugin.battleitem1 = null;
        plugin.battleitem2 = null;

        plugin.battleplayer.get(0).openInventory(plugin.battleinv1);
        plugin.battleplayer.get(1).openInventory(plugin.battleinv2);

        return;

    }


}
