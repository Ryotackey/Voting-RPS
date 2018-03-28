package red.man10.voting_rps;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class Event implements Listener{

    private Voting_RPS plugin;

    public Event(Voting_RPS plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent e) {

        if (plugin.setup == false){
            return;
        }

        Player p = (Player) e.getWhoClicked();

        if (p == plugin.battleplayer.get(0)) {

            if (e.getInventory().getName().equalsIgnoreCase("§a§lVoting §e§lRPS")) {

                e.setCancelled(true);

                if (e.getCurrentItem().getType() == Material.STONE) {

                    plugin.battleitem1 = plugin.rock;

                    p.sendMessage(plugin.prefex + "§a§lグーを選びました");


                }

                if (e.getCurrentItem().getType() == Material.PAPER) {

                    plugin.battleitem1 = plugin.paper;

                    p.sendMessage(plugin.prefex + "§a§lパーを選びました");


                }

                if (e.getCurrentItem().getType() == Material.SHEARS) {

                    plugin.battleitem1 = plugin.scissor;

                    p.sendMessage(plugin.prefex + "§a§lチョキを選びました");

                }

                plugin.battleinv1.clear(e.getSlot());

                plugin.battleplayer.get(0).closeInventory();

                if (!(plugin.battleitem1 == null) && !(plugin.battleitem2 == null)){

                    plugin.game.result();

                }

                return;

            }

        }

        if (p == plugin.battleplayer.get(1)) {

            if (e.getInventory().getName().equalsIgnoreCase("§a§lVoting §e§lRPS")) {

                e.setCancelled(true);

                if (e.getCurrentItem().getType() == Material.STONE) {

                    plugin.battleitem2 = plugin.rock;

                    p.sendMessage(plugin.prefex + "§a§lグーを選びました");

                }

                if (e.getCurrentItem().getType() == Material.PAPER) {

                    plugin.battleitem2 = plugin.paper;

                    p.sendMessage(plugin.prefex + "§a§lパーを選びました");

                }

                if (e.getCurrentItem().getType() == Material.SHEARS) {

                    plugin.battleitem2 = plugin.scissor;

                    p.sendMessage(plugin.prefex + "§a§lチョキを選びました");

                }

                plugin.battleinv2.clear(e.getSlot());

                plugin.battleplayer.get(1).closeInventory();

                if (!(plugin.battleitem1 == null) && !(plugin.battleitem2 == null)){

                    plugin.game.result();

                }

                return;

            }
        }

        if (e.getInventory().getName().equalsIgnoreCase("§a§lVoting Menu")){

            e.setCancelled(true);

            if (e.getCurrentItem().getType() == Material.STONE) {

                plugin.voteitem.add(plugin.rock);

                p.sendMessage(plugin.prefex + "§a§lグーに投票しました");

                Bukkit.broadcastMessage(plugin.prefex + "§e§l" + p.getDisplayName() + "§a§lが投票しました §f§l<§6§l現在" + plugin.voteitem.size() + "枚です§f§l>");

                plugin.voteplayer.add(p);

                p.closeInventory();

                return;

            }

            if (e.getCurrentItem().getType() == Material.PAPER) {

                plugin.voteitem.add(plugin.paper);

                p.sendMessage(plugin.prefex + "§a§lパーに投票しました");

                Bukkit.broadcastMessage(plugin.prefex + "§e§l" + p.getDisplayName() + "§a§lが投票しました §f§l<§6§l現在" + plugin.voteitem.size() + "枚です§f§l>");

                plugin.voteplayer.add(p);

                p.closeInventory();

                return;

            }

            if (e.getCurrentItem().getType() == Material.SHEARS) {

                plugin.voteitem.add(plugin.scissor);

                p.sendMessage(plugin.prefex + "§a§lチョキに投票しました");

                Bukkit.broadcastMessage(plugin.prefex + "§e§l" + p.getDisplayName() + "§a§lが投票しました §f§l<§6§l現在" + plugin.voteitem.size() + "枚です§f§l>");

                plugin.voteplayer.add(p);

                p.closeInventory();

                return;

            }


        }



    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent e){

        if (plugin.setup == false){
            return;
        }

        if (!e.getInventory().getName().equalsIgnoreCase("§a§lVoting §e§lRPS")){
            return;
        }

        Player p = (Player) e.getPlayer();

        if (p == plugin.battleplayer.get(0)) {

            p.openInventory(plugin.battleinv1);

        }else {
            p.openInventory(plugin.battleinv2);
        }

        return;

    }

}
