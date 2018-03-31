package red.man10.voting_rps;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Command implements CommandExecutor {

    private Voting_RPS plugin;

    public Command(Voting_RPS plugin){
        this.plugin = plugin;
    }

    @Override
    public  boolean  onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args){

        if (!(sender instanceof Player)) return false;
        Player p = (Player) sender;
        UUID puuid = p.getUniqueId();

        switch (args.length){

            case 0:

                p.sendMessage("§f§l--------§a§lVoting §e§lRPS§f§l--------");
                p.sendMessage("§6§l/mv new [金額]§f§r:ゲームを開く");
                p.sendMessage("§6§l/mv join§f§r:ゲームに参加する");
                p.sendMessage("§6§l/mv vote§f§r:グーチョキパーのどれかに投票する");
                p.sendMessage("§6§l/mv game§f§r:インベントリ間違えて閉じた場合開く");
                p.sendMessage("§l--------------------------------------------");

                if (!plugin.voting){
                    p.sendMessage(plugin.prefex + "§4§lまだ投票の受付を開始していません");
                    return true;
                }

                if (plugin.battleplayer.contains(p)){
                    p.sendMessage(plugin.prefex + "§4§lあなたは参加者なので投票できません");
                    return true;
                }

                if (plugin.voteplayer.contains(p)){
                    p.sendMessage(plugin.prefex + "§4§lあなたはすでに投票しています");
                    return true;
                }

                plugin.voteinv = Bukkit.createInventory(null, 45, "§a§lVoting Menu");

                for (int i = 0; i < 45; i++){

                    plugin.voteinv.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)15));

                }

                plugin.voteinv.setItem(20, plugin.rock);
                plugin.voteinv.setItem(22, plugin.scissor);
                plugin.voteinv.setItem(24, plugin.paper);

                p.openInventory(plugin.voteinv);

                return true;

            case 1:

                if (args[0].equalsIgnoreCase("join")) {

                    if (!plugin.setup) {
                        p.sendMessage(plugin.prefex + "§4§lまだ始まってません");
                        return true;
                    }

                    if (plugin.battleplayer.size() > 1) {
                        p.sendMessage(plugin.prefex + "§4§l定員オーバーです");
                        return true;
                    }

                    if (plugin.battleplayer.contains(p)){
                        p.sendMessage(plugin.prefex + "§4§lあなたはすでに参加しています");
                        return true;
                    }

                    if (plugin.val.getBalance(puuid) < plugin.bal){
                        p.sendMessage(plugin.prefex + "§4§lお金が足りません");
                        return true;
                    }

                    plugin.val.withdraw(puuid, plugin.bal);

                    plugin.battleplayer.add(p);

                    plugin.uuid2 = p.getUniqueId();

                    Bukkit.broadcastMessage(plugin.prefex + "§e§l" + p.getDisplayName() + "§f§lさん§a§lが参加しました");

                    plugin.game.votingTime();

                    return true;
                }

                if (args[0].equalsIgnoreCase("cancel")) {

                    if (plugin.setup == false){
                        p.sendMessage(plugin.prefex + "§4§lまだ始まっていません");
                        return true;
                    }

                    if (!p.hasPermission("votingrps.op")) {
                        p.sendMessage(plugin.prefex + "§4§lあなたは権限がありません");
                        return true;
                    }

                    p.sendMessage(plugin.prefex + "§4§lキャンセルしました");
                    plugin.cancelGame();

                    return true;

                }

                if (args[0].equalsIgnoreCase("view")){

                    if (!p.hasPermission("votingrps.op")){
                        p.sendMessage(plugin.prefex + "§4§lあなたには権限がありません");
                        return true;
                    }

                    if (plugin.setup == false){
                        p.sendMessage(plugin.prefex + "§4§lまだ始まっていません");
                        return true;
                    }

                    for (int i = 0; i < plugin.voteitem.size(); i++) {
                        p.sendMessage(plugin.prefex + "§c§l" + plugin.voteplayer.get(i).getDisplayName() + "§l:" + plugin.voteitem.get(i).getItemMeta().getDisplayName());
                    }

                    return true;
                }

                if (args[0].equalsIgnoreCase("vote")){

                    if (!plugin.voting){
                        p.sendMessage(plugin.prefex + "§4§lまだ投票の受付を開始していません");
                        return true;
                    }

                    if (plugin.battleplayer.contains(p)){
                        p.sendMessage(plugin.prefex + "§4§lあなたは参加者なので投票できません");
                        return true;
                    }

                    if (plugin.voteplayer.contains(p)){
                        p.sendMessage(plugin.prefex + "§4§lあなたはすでに投票しています");
                        return true;
                    }

                    plugin.voteinv = Bukkit.createInventory(null, 45, "§a§lVoting Menu");

                    for (int i = 0; i < 45; i++){

                        plugin.voteinv.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)15));

                    }

                    plugin.voteinv.setItem(20, plugin.rock);
                    plugin.voteinv.setItem(22, plugin.scissor);
                    plugin.voteinv.setItem(24, plugin.paper);

                    p.openInventory(plugin.voteinv);

                    return true;

                }

                if (args[0].equalsIgnoreCase("game")){

                    if (plugin.setup == false && plugin.voting == true ) {
                        return true;
                    }

                    if (plugin.battleitem1 == null || plugin.battleitem2 == null){
                        return true;
                    }

                    if (p == plugin.battleplayer.get(0)){

                        p.openInventory(plugin.battleinv1);

                        return true;

                    }

                    if (p == plugin.battleplayer.get(1)){

                        p.openInventory(plugin.battleinv2);

                        return true;

                    }

                }

                if (args[0].equalsIgnoreCase("on")){
                    if (!p.hasPermission("votingrps.op")){
                        p.sendMessage(plugin.prefex + "§4§l権限がありません");
                        return true;
                    }
                    plugin.enable = true;
                    p.sendMessage(plugin.prefex + "§a§l起動しました");
                    return true;
                }

                if (args[0].equalsIgnoreCase("off")){
                    if (!p.hasPermission("votingrps.op")){
                        p.sendMessage(plugin.prefex + "§4§l権限がありません");
                        return true;
                    }
                    plugin.enable = false;
                    if (plugin.setup == true) {
                        plugin.cancelGame();
                    }
                    p.sendMessage(plugin.prefex + "§a§lオフしました");
                    return true;
                }

                if (args[0].equalsIgnoreCase("reload")){
                    if (!p.hasPermission("votingrps.op")){
                        p.sendMessage(plugin.prefex + "§4§l権限がありません");
                        return true;
                    }
                    plugin.config.reloadConfig();
                    p.sendMessage(plugin.prefex + "§aReload Complite");
                    return true;
                }

                break;

            case 2:
                if (args[0].equalsIgnoreCase("new")){

                    if (plugin.enable == false){

                        p.sendMessage(plugin.prefex + "§4§l今起動できません");

                        return true;

                    }

                    if (plugin.setup){
                        p.sendMessage(plugin.prefex + "§4§lすでに始まっています!");

                        return true;
                    }

                    try {
                        plugin.bal = Double.parseDouble(args[1]);
                        if (plugin.config.getConfig().getInt("minbal") > plugin.bal) {
                            p.sendMessage(plugin.prefex + "§4§l最低金額は" + plugin.config.getConfig().getInt("minbal") + "円です");
                            return false;
                        }

                    } catch (NumberFormatException vrps) {
                        sender.sendMessage(plugin.prefex + "§4§l金額を指定してください.");
                        return false;

                    }

                    if (plugin.val.getBalance(puuid) < plugin.bal){
                        p.sendMessage(plugin.prefex + "§4§lお金が足りません");
                        return true;
                    }

                    plugin.val.withdraw(puuid, plugin.bal);

                    plugin.setup = true;

                    plugin.battleplayer.add(p);

                    plugin.uuid1 = p.getUniqueId();

                    Bukkit.broadcastMessage(plugin.prefex + "§e§l" + p.getName() + "§f§rさんが§6§l" + plugin.jpnBalForm(plugin.bal) + "§f§l円の§a§l投票じゃんけん§f§rを開始しました:§l/mv");

                    return true;

                }



                break;

        }



        return false;
    }
}
