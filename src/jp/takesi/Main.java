package jp.takesi;

import java.io.File;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
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
			File f = new File(this.getDataFolder() + File.separator + "world_data" + File.separator + player.getName()+".json");
			Config config = new Config(f,Config.JSON);
			config.set("spawn_point_x", 0);
			config.set("spawn_point_y", 10);
			config.set("spawn_point_z", 0);
			config.set("time_set", 4000);
			config.set("time_stop", true);
			config.set("weather", 0);
			config.set("allow_attack", false);
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
                	 File f = new File(this.getDataFolder() + File.separator + "world_data" + File.separator + player.getLevel().getName()+".json");
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
                	 File f = new File(this.getDataFolder() + File.separator + "world_data" + File.separator +  player.getLevel().getName()+".json");
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
		File f = new File(this.getDataFolder() + File.separator + "world_data" + File.separator + event.getEntity().getLevel().getName()+".json");
		Config config = new Config(f,Config.JSON);
		Player player = (Player)event.getEntity();
		if(config.exists("baneed_"+event.getEntity().getName())){
			player.sendMessage("§l§cワールド管理システム>>ワールドBanされているため行くことができません。");
			event.setCancelled();
		}else{
		if(event.getEntity().getName().equals(event.getTarget().getName())){
			player.setGamemode(1);
		}
        }
		}
	}

	@EventHandler()
	public void onDamage(EntityDamageEvent event){
		if(event.getEntity() instanceof Player){
			File f = new File(this.getDataFolder() + File.separator + "world_data" + File.separator + event.getEntity().getLevel().getName()+".json");
    		 Config config = new Config(f,Config.JSON);
			if(config.getBoolean("allow_attack") != true){
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

	public int getGamemodeCode(String gamemode){
		switch(gamemode){
			case "0":
			case "s":
			case "S":
			case "survival":
			case "Survival":
			case "サバイバル":
				return 0;
			case "1":
			case "c":
			case "C":
			case "creative":
			case "Creative":
			case "クリエイティブ":
				return 1;
			case "2":
			case "a":
			case "A":
			case "adventure":
			case "Adventure":
			case "アドベンチャー":
				return 2;
			case "3":
			case "sp":
			case "spectator":
			case "Spectator":
			case "スペクテイター":
				return 3;
			default:
				return 4;
		}
	}

	public int getWeatherCode(String object) {
		switch(object) {
			case "0":
				return 0;
			case "1":
				return 1;
			case "2":
				return 2;
			case "clear":
				return 0;
			case "rain":
				return 1;
			case "thunder":
				return 2;
			case "晴れ":
			case "快晴":
			case "はれ":
				return 0;
			case "あめ":
			case "雨":
			case "小雨":
				return 1;
			case "かみなり":
			case "カミナリ":
			case "雷":
				return 2;
			default:
				return 3;
		}
	}

	public boolean hasPermisson(Player player,Level level) {
		File f = new File(this.getDataFolder() + File.separator + "world_data" + File.separator + level.getName()+".json");
		Config config = new Config(f,Config.JSON);
		if(config.exists("invited_"+player.getName())) {
			return true;
		}else {
			return false;
		}
	}

	public void goLevel(Player player,Level targetlevel){
		 File f = new File(this.getDataFolder() + File.separator + "world_data" + File.separator + targetlevel.getName()+".json");
		 Config config = new Config(f,Config.JSON);
		 player.teleport(new Position(config.getDouble("spawn_point_x"),config.getDouble("spawn_point_y"),config.getDouble("spawn_point_z"),targetlevel));
		switch(this.getWeatherCode(config.getString("weather"))){
			case 0:
				targetlevel.setRaining(false);
				targetlevel.setThundering(false);
				targetlevel.setRainTime(600 * 20 * 20);
				targetlevel.setThunderTime(600 * 20 * 20);
			case 1:
				targetlevel.setRaining(true);
				targetlevel.setRainTime(600 * 20 * 20);
			case 2:
				targetlevel.setThundering(true);
				targetlevel.setRainTime(600 * 20 * 20);
				targetlevel.setThunderTime(600 * 20 * 20);
		}
		 targetlevel.setTime(config.getInt("time_set"));
		 if((boolean)config.getBoolean("time_stop")){
			 targetlevel.stopTime();
		 }
	}

	public boolean onCommand(final CommandSender sender, Command command, String label, String[] args){
		switch(command.getName()){
			case "wo":
				if(args.length < 1){
					if(sender instanceof Player){
						sender.sendMessage("====Worldコマンドの使用方法======");
						sender.sendMessage("/wo me: 自分のワールドに移動します");//OK
						sender.sendMessage("/wo **: **のワールドに移動します");//OK
						sender.sendMessage("/wo s: ワールドの詳細設定をします");
						sender.sendMessage("/wo gm 0~3 : 自分のゲームモードの変更をします");//OK
						sender.sendMessage("/wo give ** : **に自分が今持っているアイテムを渡します");//OK
						sender.sendMessage("/wo invite **: **にワールドの編集権限を与えます");//OK
						sender.sendMessage("/wo uninvite **: **の編集権限を剥奪します");//OK
						sender.sendMessage("/wo kick **: **をワールドからkickします");//OK
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
					final Player player = (Player)sender;
					switch(args[0]) {
						case "me":
							if(sender instanceof Player){
								sender.sendMessage("§l§eワールド管理システム>>自分のワールドに戻っています...");
								this.goLevel((Player) sender,this.getServer().getLevelByName(sender.getName()));
								player.setGamemode(1);
							}
							return true;

						case "gm":
							if(args.length < 2){
								player.sendMessage("§l§e使用方法>> /wo gm 0~3");
							}else{
								switch(this.getGamemodeCode(args[1])){
									case 0:
										player.setGamemode(0);
										player.sendMessage("§l§eワールド管理システム>>サバイバルに変更しました");
										return true;
									case 1:
										player.setGamemode(1);
										player.sendMessage("§l§eワールド管理システム>>クリエイティブに変更しました");
										return true;
									case 2:
										player.setGamemode(2);
										player.sendMessage("§l§eワールド管理システム>>アドベンチャーに変更しました");
										return true;
									case 3:
										player.setGamemode(3);
										player.sendMessage("§l§eワールド管理システム>>スペクテイターに変更しました");
										return true;
									case 4:
										player.sendMessage("§l§eワールド管理システム>>不正なゲームモードです");
										return true;
								}
							}
						case "give":
							if(args.length < 2) {
								player.sendMessage("§l§cワールド管理システム>>プレイヤーを指定してください");
							}else {
								if(this.hasPermisson(player, player.getLevel())) {
									Player target = this.getServer().getPlayer(args[1]);
									if(target != null) {
										Item ItemInHand = player.getInventory().getItemInHand();
										target.getInventory().addItem(ItemInHand);
										player.getInventory().remove(ItemInHand);
										player.sendMessage("§l§eワールド管理システム>>"+target.getName()+"に"+ItemInHand.getName()+"を渡しました。");
									}else {
										player.sendMessage("§l§cワールド管理システム>>指定したプレイヤーは見つかりませんでした");
									}
								}else {
										player.sendMessage("§l§cワールド管理システム>>権限のないワールドでは実行できません");
								}
							}

						case "invite":
							if(this.hasPermisson(player, player.getLevel())) {
								if(args.length < 2) {
									player.sendMessage("§l§cワールド管理システム>>プレイヤーを指定してください");
							    }else {
								File f = new File(this.getDataFolder() + File.separator + "world_data" + File.separator + player.getName()+".json");
								Config config = new Config(f,Config.JSON);
								config.set("invited_"+args[1], true);
								config.save();
								player.sendMessage("§l§cワールド管理システム>>"+args[1]+"さんに編集権限を与えました");
							}
							}else {
								player.sendMessage("§l§cワールド管理システム>>権限のないワールドでは実行できません");
						}
						case "uninvite":
							if(this.hasPermisson(player, player.getLevel())) {
							if(args.length < 2) {
								player.sendMessage("§l§cワールド管理システム>>プレイヤーを指定してください");
							}else {
								File f = new File(this.getDataFolder() + File.separator + "world_data" + File.separator + player.getName()+".json");
								Config config = new Config(f,Config.JSON);
								if(config.exists("invited"+args[1])) {
									config.remove("invited"+args[1]);
									config.save();
									player.sendMessage("§l§eワールド管理システム>>"+args[1]+"さんの編集権限を剥奪しました");
								}else {
									player.sendMessage("§l§cワールド管理システム>>"+args[1]+"さんはもともと編集権限がありません");
								}
							}
							}else {
								player.sendMessage("§l§cワールド管理システム>>権限のないワールドでは実行できません");
						}
						case "kick":
							if(this.hasPermisson(player, player.getLevel())) {
								if(args.length < 2) {
									player.sendMessage("§l§cワールド管理システム>>プレイヤーを指定してください");
									}else {
										if(args[1] == player.getLevel().getName()) {
												player.sendMessage("§l§cワールド管理システム>>ワールドの管理者をKICKすることはできません");
											}else {
											player.getLevel().getPlayers().forEach((Long, Player) -> {
												if(Player.getName().contains(args[1])) {
												Player.kick("§l§cワールドの管理者によりKICKされました");
												player.sendMessage("§l§eワールド管理システム>>"+Player.getName()+"をKICKしました");
												}
												});
											player.sendMessage("終了");
										}
									}
							}else {
						player.sendMessage("§l§cワールド管理システム>>権限のないワールドでは実行できません");
				}
						case "ban":
							if(this.hasPermisson(player, player.getLevel())) {
								if(args[1] == player.getLevel().getName()) {
									player.sendMessage("§l§cワールド管理システム>>ワールドの管理者はBan出来ません");
								}else {
								if(args.length < 2) {
									player.sendMessage("§l§cワールド管理システム>>プレイヤーを指定してください");
									}else {
										File f = new File(this.getDataFolder() + File.separator + "world_data" + File.separator + player.getName()+".json");
										Config config = new Config(f,Config.JSON);
										config.set("baneed_"+args[1], true);
									}
								}
							}else {
								player.sendMessage("§l§cワールド管理システム>>権限のないワールドでは実行できません");
							}

						case "unban":
							if(this.hasPermisson(player, player.getLevel())) {
								if(args.length < 2) {
									player.sendMessage("§l§cワールド管理システム>>プレイヤーを指定してください");
									}else {
										File f = new File(this.getDataFolder() + File.separator + "world_data" + File.separator + player.getName()+".json");
										Config config = new Config(f,Config.JSON);
										if(config.exists("baneed_"+args[1])) {
											config.remove("baneed_"+args[1]);
											config.save();
											player.sendMessage("§l§eワールド管理システム>>"+args[1]+"のBANを解除しました");
										}else {
											player.sendMessage("§l§cワールド管理システム>>"+args[1]+"はBANされていません");
										}
									}
							}else {
								player.sendMessage("§l§cワールド管理システム>>権限のないワールドでは実行できません");
							}

						case "banlist":
							if(this.hasPermisson(player, player.getLevel())) {
								File f = new File(this.getDataFolder() + File.separator + "world_data" + File.separator + player.getName()+".json");
								Config config = new Config(f,Config.JSON);
								player.sendMessage("現在BAN中のプレイヤー");
								config.getAll().forEach((String, Object) -> {
									if(Object.toString().contains("baneed_")) {
										player.sendMessage(Object.toString().replace("baneed_",""));
									}
								});
							}else {
								player.sendMessage("§l§cワールド管理システム>>権限のないワールドでは実行できません");
							}
						default:
							if(exsistlevel(args[1])) {
								sender.sendMessage("§l§eワールド管理システム>>"+args[1]+"のワールドに移動しています...");
								this.getLogger().notice("sender:"+sender.getName()+" arg1:"+args[1]);
								if(sender.isPlayer()) {
									this.goLevel((Player)sender,this.getServer().getLevelByName(args[1]));
								}else {
									this.getLogger().notice("コンソールからの利用不可");
								}
								return true;
							}else {
								sender.sendMessage("§l§eワールド管理システム>>"+args[1]+"のワールドは存在しません");
								return true;
							}
					}
				}
		}
		return false;
	}

}
