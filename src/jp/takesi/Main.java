package jp.takesi;

import java.io.File;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
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

	public void onJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		getLogger().info(player.getName());
		if(this.exsistlevel(player.getName())){
			player.teleport(new Position(-1,8,2,this.getServer().getDefaultLevel()));
			player.setGamemode(0);
			player.sendMessage("[§eSYSTEM§r] "+player.getName()+"さん、おかえり！");
		}else{
			File f = new File(this.getDataFolder() + File.separator + player.getName()+".json");
			Config config = new Config(f,Config.JSON);
			config.set("spawn_point_x", 0);
			config.set("spawn_point_x", 10);
			config.set("spawn_point_x", 0);
			config.set("time_set", 4000);
			config.set("time_stop", true);
			config.set("weather", 0);
			config.set("allow_attack", "false");
			config.save();
			this.getServer().generateLevel(player.getName());
			this.getServer().loadLevel(player.getName());
			player.teleport(new Position(-1,8,2,this.getServer().getDefaultLevel()));
			player.setGamemode(0);
			player.sendMessage("[§eSYSTEM§r] 生徒サーバーへようこそ");
			player.sendMessage("[§eSYSTEM§r] このサーバーは§b建築サーバー§rです！");
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
	public boolean onCommand(final CommandSender sender, Command command, String label, String[] args){
		 switch(command.getName()){
			case "wo":
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
		}
		return false;
	}

}
