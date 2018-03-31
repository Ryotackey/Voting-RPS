package red.man10.voting_rps;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Voting_RPS extends JavaPlugin {

    public boolean enable = true;

    VaultManager val = null;
    public CustomConfig config;
    public MySOLManager mysql;

    public Inventory battleinv1;
    public Inventory battleinv2;

    public Inventory voteinv;

    public List<Player> battleplayer = new ArrayList();
    public ArrayList<ItemStack> voteitem = new ArrayList<>();
    public ArrayList<ItemStack> recorditem = new ArrayList<>();
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
        mysql = new MySOLManager(this, "VRPS");

        getCommand("mv").setExecutor(new Command(this));
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
        recorditem.clear();

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
        recorditem.clear();

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
            val.deposit(uuid1, bal);
        }

        OfflinePlayer p2 = Bukkit.getOfflinePlayer(uuid2);
        if (p2 == null){
            Bukkit.getLogger().info(uuid1.toString()+"は見つからない");
        }else {
            val.deposit(uuid2, bal);
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

class MySQLexcute implements Runnable{

    private Voting_RPS plugin;

    private String winner_name;
    private String winner_uuid;
    private int winner_move;
    private String loser_name;
    private String loser_uuid;
    private int loser_move;
    private int bet;
    private int vote_count;

    public MySQLexcute(String winner_name, String winner_uuid, int winner_move, String loser_name, String loser_uuid, int loser_move, int bet, int vote_count, Voting_RPS plugin){

        this.plugin = plugin;

        this.winner_name = winner_name;
        this.winner_uuid = winner_uuid;
        this.winner_move = winner_move;
        this.loser_name = loser_name;
        this.loser_uuid = loser_uuid;
        this.loser_move = loser_move;
        this.bet = bet;
        this.vote_count = vote_count;

    }

    @Override
    public void run(){

        plugin.mysql.execute("insert into minigame.vrps_record(winner, winner_uuid, winner_move, loser, loser_uuid, loser_move, player_bet, vote_count) values('" + winner_name + "', '" + winner_uuid + "', " + winner_move + ", '" + loser_name + "', '" + loser_uuid + "', " + loser_move + ", " + bet + ", " + vote_count + ");");

        return;
    }

}

class MySQLRecord implements Runnable{

    private Voting_RPS plugin;

    public MySQLRecord(Voting_RPS plugin){

        this.plugin = plugin;

    }

    @Override
    public void run(){

        ResultSet rs = plugin.mysql.query("SELECT * FROM minigame.vrps_record ORDER BY id desc limit 1;");

        try {
            while(rs.next())
            {
                for (int i = 0; i < plugin.recorditem.size(); i++) {

                    int move = -1;

                    switch (plugin.recorditem.get(i).getItemMeta().getDisplayName()){

                        case "§e§lグー":

                            move = 0;

                            break;

                        case "§e§lチョキ":

                            move = 1;

                            break;

                        case "§e§lパー":

                            move = 2;

                            break;

                    }
                    plugin.mysql.execute("insert into minigame.vrps_vote(gameid, name, uuid, vote) values(" + rs.getInt("id") + ", '" + plugin.voteplayer.get(i).getName() + "', '" + plugin.voteplayer.get(i).getUniqueId().toString() + "', " + move + ");");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}


