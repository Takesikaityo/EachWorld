package jp.takesi;

import java.io.File;
import java.time.LocalDateTime;

import cn.nukkit.scheduler.PluginTask;
import cn.nukkit.utils.Config;
import jp.takesi.Main;

public class task extends PluginTask<Main> {

  public task(Main owner) {
  super(owner);
  }

  @Override
  public void onRun(int currentTick) {
	  LocalDateTime d = LocalDateTime.now();
	  this.getOwner().getServer().getOnlinePlayers().forEach((uuid, player) -> {
		  File f = new File(this.getOwner().getDataFolder() + File.separator + player.getLevel().getName()+".json");
		  Config config = new Config(f,Config.JSON);
		  if(config.exists("invited_"+player.getName())) {
			  if(player.getGamemode() == 0){
					player.setGamemode(1);
				}
				}else{
				if(player.getGamemode() == 0){
				}else{
					player.setGamemode(0);
				}
		  }
		  if(player.hasEffect(14)) {
			  player.removeEffect(14);//透明Effectは禁止
		  }
		   player.sendPopup("INFO\nDATE : " + d.getHour()+":"+d.getMinute()+":"+d.getSecond()+
	       "\nYOUR POSITION : "+"X>"+player.getFloorX()+" Y>"+player.getFloorY()+" Z>"+player.getFloorZ()+
	       "\nWORLD : "+player.getLevel().getName()
	      );
	  });
  }
}
