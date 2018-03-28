package red.man10.voting_rps;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Voting_RPS extends JavaPlugin {

    VaultManager val = null;
    public CustomConfig config;

    public Inventory battleinv1;
    public Inventory battleinv2;

    public Inventory voteinv;

    public List<Player> battleplayer = new ArrayList();
    public List<ItemStack> voteitem = new ArrayList<>();
    public List<Player> voteplayer = new ArrayList<>();

    public List<ItemStack> item1 = new ArrayList<>();
    public List<ItemStack> item2 = new ArrayList<>();

    public final String prefex = "§l[§a§lVoting§e§lRPS§f§l]";

    public Double bal;

    public boolean setup = false;
    public boolean voting = false;

    public Game game;

    public Event event;

    public ItemStack battleitem1;
    public ItemStack battleitem2;

    public UUID uuid1;
    public UUID uuid2;

    public final ItemStack rock = new ItemStack(Material.STONE);
    public final ItemStack scissor = new ItemStack(Material.SHEARS);
    public final ItemStack paper = new ItemStack(Material.PAPER);

    public ItemMeta rockm = rock.getItemMeta();
    public ItemMeta paperm = paper.getItemMeta();
    public ItemMeta scissorm = scissor.getItemMeta();



    @Override
    public void onEnable() {
        // Plugin startup logic

        val = new VaultManager(this);

        getCommand("vrps").setExecutor(new Command(this));
        getServer().getPluginManager().registerEvents(new Event(this), this);

        config = new CustomConfig(this);
        config.saveDefaultConfig();
        game = new Game(this);
        event = new Event(this);

        rockm.setDisplayName("§e§lグー");
        paperm.setDisplayName("§e§lパー");
        scissorm.setDisplayName("§e§lチョキ");

        rock.setItemMeta(rockm);
        paper.setItemMeta(paperm);
        scissor.setItemMeta(scissorm);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (setup == true) {
            cancelGame();
        }

    }

    public void finishGame(){

        item1.clear();
        item2.clear();

        battleplayer.clear();

        voteitem.clear();

        setup = false;

        voting = false;

        bal = Double.valueOf(0);

        game.time = 0;

        voteplayer.clear();

        battleitem1 = null;
        battleitem2 = null;

        uuid1 = null;
        uuid2 = null;

    }

    public void cancelGame(){

        item1.clear();
        item2.clear();

        battleplayer.clear();

        voteitem.clear();

        setup = false;

        voting = false;



        game.time = 0;

        voteplayer.clear();

        battleitem1 = null;
        battleitem2 = null;

        OfflinePlayer p1 = Bukkit.getOfflinePlayer(uuid1);


        if (p1 == null){
            Bukkit.getLogger().info(uuid1.toString()+"は見つからない");
        }else {
        }

        OfflinePlayer p2 = Bukkit.getOfflinePlayer(uuid2);
        if (p2 == null){
            Bukkit.getLogger().info(uuid1.toString()+"は見つからない");
        }else {
        }

        bal = Double.valueOf(0);

        uuid1 = null;
        uuid2 = null;

    }

    String jpnBalForm(double val){
        long val2 = (long) val;

        String addition = "";
        String form = "万";
        long man = val2/10000;
        if(val >= 100000000){
            man = val2/100000000;
            form = "億";
            long mann = (val2 - man * 100000000) / 10000;
            addition = mann + "万";
        }
        return man + form + addition;
    }

}
