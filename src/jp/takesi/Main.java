package jp.takesi;

import java.io.File;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityLevelChangeEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import jp.takesi.task;

public class Main extends PluginBase implements Listener{

	public void onEnable(){
		this.getLogger().notice("これはtakesiによる自作プラグインです。");
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getScheduler().scheduleRepeatingTask(new task(this), 20);
		File data = new File(this.getDataFolder()+"");
		if(!data.exists()) {
			data.mkdir();
		}
		File f = new File(this.getDataFolder() + File.separator + "config.json");
		Config config = new Config(f,Config.JSON);
		config.set("key", "value");
		config.save();
	}

	@EventHandler()
	public void onJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		getLogger().info(player.getName());
		if(this.exsistlevel(player.getName())){
			player.teleport(new Position(-1,8,2,this.getServer().getDefaultLevel()));//テレポート
			player.setGamemode(0);//ゲームモードをサバイバルに
			player.sendMessage("[§eSYSTEM§r] "+player.getName()+"さん、おかえり！");
		}else{
			File f = new File(this.getDataFolder() + File.separator + player.getName()+".json");
			Config config = new Config(f,Config.JSON);
			config.set("spawn_point_x", 0);
			config.set("spawn_point_y", 10);
			config.set("spawn_point_z", 0);
			config.set("time_set", 4000);
			config.set("time_stop", true);
			config.set("weather", 0);
			config.set("allow_attack", "false");
			config.save();
			this.getServer().generateLevel(player.getName());//Levelの成形
			this.getServer().loadLevel(player.getName());//LevelのLoad
			player.teleport(new Position(-1,8,2,this.getServer().getDefaultLevel()));//テレポート
			player.setGamemode(0);//ゲームモードをサバイバルに
			player.sendMessage("[§eSYSTEM§r] 生徒サーバーへようこそ");
			player.sendMessage("[§eSYSTEM§r] このサーバーは§b建築サーバー§rです！");
		}
	}

	@EventHandler()
	public void onBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        Level level = player.getLevel();
        if(player.getName() != level.getName()){
                 if(!player.isOp()){
                	 File f = new File(this.getDataFolder() + File.separator + player.getLevel().getName()+".json");
             		 Config config = new Config(f,Config.JSON);
					  if(!config.exists("invited_"+player.getName())){
                          player.sendMessage("§l§cワールド管理システム>>破壊権限がありません。");
                          event.setCancelled();
				  }
                 }
			}
    }

	@EventHandler()
	public void onPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        Level level = player.getLevel();
        Block block = event.getBlock();
        if(player.getName() != level.getName()){
                 if(!player.isOp()){
                	 File f = new File(this.getDataFolder() + File.separator + player.getLevel().getName()+".json");
             		 Config config = new Config(f,Config.JSON);
					  if(config.exists("invited_"+player.getName())){
						  this.getLogger().debug("ID : "+block.getId());
						switch(block.getId()){
					  case 8:
					  case 9:
					  case 10:
					  case 11:
					  case 46:
					  case 79:
                 player.sendMessage("§l§cワールド管理システム>>設置権限がありません。");
                 event.setCancelled();
					  break;
                }
				 this.getLogger().debug("ItemInHand : "+player.getInventory().getItemInHand().getId());
				 switch(player.getInventory().getItemInHand().getId()){
					 case 259:
					 case 326:
					 case 327:
					 player.sendMessage("§l§cワールド管理システム>>設置権限がありません。");
                    event.setCancelled();
					  break;
				 }
					  }else{
                          player.sendMessage("§l§cワールド管理システム>>設置権限がありません。");
                          event.setCancelled();
                 }
			}
    }
	}

	@EventHandler()
	public void onTap(PlayerInteractEvent event){
		Item item = event.getItem();
		Player player = event.getPlayer();
		if(player.getName() == player.getLevel().getName()){
		}else{
		switch(item.getId()){
			case 259:
			case 325:
			player.sendMessage("§l§cワールド管理システム>>設置権限がありません。");
            event.setCancelled();
			break;
		}
		}
	}

	@EventHandler()
	public void onLevelChange(EntityLevelChangeEvent event){
		if(event.getEntity() instanceof Player){
		File f = new File(this.getDataFolder() + File.separator + event.getEntity().getLevel().getName()+".json");
		Config config = new Config(f,Config.JSON);
		if(config.exists("baneed_"+event.getEntity().getName())){
		event.getEntity().sendMessage("§l§cワールド管理システム>>ワールドBanされているため行くことができません。");
        event.setCancelled();
		}else{
		if(event.getEntity().getName() == event.getTarget().getName()){
		event.getEntity().setGamemode(1);
		}
        }
		}
	}

	@EventHandler()
	public void onDamage(EntityDamageEvent event){
		if(event.getEntity() instanceof Player){
			File f = new File(this.getDataFolder() + File.separator + event.getEntity().getLevel().getName()+".json");
    		 Config config = new Config(f,Config.JSON);
			if(!(boolean)config.get("allow_attack")){
				event.setCancelled();
			}
		}
	}

	public boolean exsistlevel(String level_name){
		if(new File(this.getServer().getFilePath()+File.separator+"worlds"+File.separator+level_name).exists()){
			this.getServer().loadLevel(level_name);
			return true;
		}else{
			return false;
		}
	}

	public void goLevel(Player player,Level targetlevel){
		 File f = new File(this.getDataFolder() + File.separator + targetlevel.getName()+".json");
		 Config config = new Config(f,Config.JSON);
		 player.teleport(new Position((double)config.get("spawn_point_x"),(double)config.get("spawn_point_y"),(double)config.get("spawn_point_z"),targetlevel));
		 //targetlevel.getWeather().setWeather(config.get("weather"));
		 targetlevel.setTime((int)config.get("time_set"));
		 if((boolean)config.get("time_stop")){
			 targetlevel.stopTime();
		 }
	}

	public boolean onCommand(final CommandSender sender, Command command, String label, String[] args){
		 switch(command.getName()){
			case "wo":
				if(args.length < 1){
				if(sender instanceof Player){
					sender.sendMessage("====Worldコマンドの使用方法======");
					sender.sendMessage("/wo me: 自分のワールドに移動します");
					sender.sendMessage("/wo **: **のワールドに移動します");
					sender.sendMessage("/wo s: ワールドの詳細設定をします");
					sender.sendMessage("/wo gm 0~3 : 自分のゲームモードの変更をします");
					sender.sendMessage("/wo give ** : **に自分が今持っているアイテムを渡します");
					sender.sendMessage("/wo invite **: **にワールドの編集権限を与えます");
					sender.sendMessage("/wo uninvite **: **の編集権限を剥奪します");
					sender.sendMessage("/wo kick **: **をワールドからkickします");
					sender.sendMessage("/wo ban **: **をワールドからBanします");
					sender.sendMessage("/wo unban **: **のワールドBanを解除します");
					sender.sendMessage("/wo banlist : ワールドBanしたプレイヤーの一覧");
					//sender.sendMessage("/wo backup: 自分のワールドをバックアップします。もし今までのバックアップが5個以上の場合、一番古いものが自動で削除されます。");
					//sender.sendMessage("/wo backuplist: 自分のワールドのバックアップの一覧を表示します");
					//sender.sendMessage("/wo restore Backup名: Backupを適用します");
					}else{
						this.getLogger().info("コンソールからは使用できません");
					}
				return true;
				}else {
					switch(args[0]) {
					case "me":
						if(sender instanceof Player){
						sender.sendMessage("§l§eワールド管理システム>>自分のワールドに戻っています...");
						this.goLevel((Player) sender,this.getServer().getLevelByName(sender.getName()));
						}
						return true;
					}
				}
		}
		return false;
	}

}
