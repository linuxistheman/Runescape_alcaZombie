import java.awt.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.Skills;
import org.rsbot.script.wrappers.RSArea;
import org.rsbot.script.wrappers.RSComponent;
import org.rsbot.script.wrappers.RSGroundItem;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.util.GlobalConfiguration;
import java.awt.event.*;
import javax.swing.*;

/* "Property" of alca. Please do not distribute as this is a paid script. I made it paid not only so I could benefit from it but because it limits the amount of leechers
 * (ie someone who just downloads scripts and runs them without giving any feedback or any contribution to the scripter or community)
 * 
 * If you distribute this script freely, armoured zombies will become a very crowded place and will cease to be the best melee training spot in the game. 
 * 
 * Thank you for your support and consideration. Any feedback or issues (a screenshot or thorough description would be nice) are greatly recommended, you can post them on
 * http://www.powerbot.org/vb/showthread.php?t=682982&highlight=
 * or send me a private message on the forums.
*/

@SuppressWarnings("deprecation")
@ScriptManifest(
	authors = {"Alca"},
	version = 3.0,
	keywords = {},
	description = "Alca's Flawless Zombie Killer! - RELEASE- ",
	name = "alcaZombie"
)

public class alcaZombie extends Script implements PaintListener, ServerMessageListener, MouseListener{
	final RSTile[] tilesToHartwin = {new RSTile(3189,3435),new RSTile(3193,3430), new RSTile(3203,3429), new RSTile(3207, 3433), new RSTile(3213,3440),new RSTile(3212,3449), new RSTile(3212,3457), new RSTile(3212,3468), new RSTile(3207,3476),new RSTile(3206,3485), new RSTile(3203,3492)};
	final RSTile[] tilesToBank = {new RSTile(3207,3427),new RSTile(3199,3429),new RSTile(3192,3431),new RSTile(3188,3436)};
	final RSTile[] GEtoBank = {new RSTile(3168,3458), new RSTile(3177,3450),new RSTile(3186,3445), new RSTile(3188,3437)};   
	final RSArea bankArea = new RSArea(new RSTile(3187, 3435), new RSTile(3189, 3438));
	final RSArea doorArea = new RSArea(new RSTile(3201,3490), new RSTile(3206,3497));
	final RSArea doorLocation = new RSArea(new RSTile (3201,3492), new RSTile(3205,3496));
	final int doorID = 15536;
	final int[] summonPot = {12146, 12144, 12142, 12140};
	final int[] strengthPot = {157, 159, 161, 2440};
	final int[] attackPot = {145, 147, 149, 2436};
	final int[] EattackPot = {15311,15310,15309,15308};
	final int[] EstrengthPot = {15315,15314,15313,15312};
	final int pouchID = 12029; 
    int specID = 0;
    int weaponID = 4151;
    int foodID = 7946;
    final int tabID = 8007;
    final int[] monsterID = {8150, 5575, 8151, 8149,  8153, 8152};
    int Status = 1;
    int noAgression = 0;
    int abnumber =0;
    RSNPC hart = null;
    RSItem tablet = null;
    public long startTime = 0;
    public long millis = 0;
    public long hours = 0;
    public long minutes = 0;
    public long seconds = 0;
    public long last = 0;
    public long startExp = 0;
    public long expGained = 0;
    public int expHour = 0;
    public long profit = 0;
    public int plankcost = 0;
    public int teakcost= 0;
    public int oakcost= 0;
    public int esscost= 0;
    public long profitHour =0;
    Rectangle closeButton = new Rectangle(439,308,76,27);//<<<Change x and y to the coordinate
    Point p;
    boolean showPaint = true;
    boolean donebanking = false;
    boolean guiDone = false;
    boolean prayerMethod = true;
    boolean bunyip = false;
    int telePath = 1; //1 is varrock , 2 is GE method
    int potions = 0; //1 is super, 2 is extreme, 3 is none
    RSItem strpotion;
	RSItem atkpotion;
	
	private boolean loadSettings() {
		AZombieSettings set = new AZombieSettings();
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream(settingsFile);
			in = new ObjectInputStream(fis);
			set = (AZombieSettings) in.readObject();
			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		potions = set.potions;
		telePath = set.telePath;
		bunyip = set.bunyip;
		prayerMethod = set.prayerMethod;
		weaponID = set.weaponID;
		foodID = set.foodID;
		specID = set.specID;
		return true;
}
	
	private void saveSettings() {
		AZombieSettings set = new AZombieSettings();
		set.potions = potions ;
		set.telePath = telePath;
		set.bunyip = bunyip;
		set.prayerMethod = prayerMethod;
		set.weaponID = weaponID;
		set.foodID = foodID;
		set.specID = specID;
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(settingsFile);
			out = new ObjectOutputStream(fos);
			out.writeObject(set);
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

    //walks to the door before hartwin
	public void toHart(){
		try{
			walking.newTilePath(tilesToHartwin).traverse();
			sleep(random(200,300));
			
			if (doorArea.contains(players.getMyPlayer().getLocation())){
				Status=3;
				sleep(random(1500,1800));
			}
		}catch (Exception e) {}
	}

	//opens the door before the stairs that lead up to hartwin
	public void openDoor(){
		try{
			sleep(random(500,1000));

			if ((doorArea.contains(players.getMyPlayer().getLocation()))){
				
				if (objects.getNearest(doorID) != null  && doorArea.contains(objects.getNearest(doorID).getLocation()) && objects.getNearest(doorID).isOnScreen()){
					objects.getNearest(doorID).doAction("Open");
					Status = 4;
					sleep(random(1000,1500));
				}
				else if ((objects.getNearest(15535) != null) && doorArea.contains(objects.getNearest(15535).getLocation()) ){
					Status = 4;
					sleep(random(1000,1500));
				}
			}	
		}catch (Exception e) {}
	}
	
	//goes up the stairs
	public void stairs(){
		//log(Status);

		try{
			walking.walkTileOnScreen(new RSTile (3203,3496));
			sleep(random(2000,2500));
			
			if (objects.getNearest(24350) != null && objects.getNearest(24350).isOnScreen()){
				objects.getNearest(24350).doAction("Climb");
				sleep(random(3500,4500));
			}
		
			
			if (objects.getNearest(24352) != null && objects.getNearest(24352).isOnScreen()){
				objects.getNearest(24352).doAction("Climb-up");
				sleep(random(3000,4500));
			}
		
			Status = 5;
		}catch (Exception e) {}
	}
	
	//talks to hartwin to have him tele you to zombies
	public void talkHart(){
		try{
			if (npcs.getNearest(13485) == null){
				Status = 3; //or maybe set it to 1 and just redo the entire thing? in case it's not the door (but the stairs fault)
				return;
			}
			
			hart = npcs.getNearest(13485);
			hart.doAction("Talk-to");
			sleep(random(1800,2000));
			 
			interfaces.clickContinue();
			sleep(random(1800,2000));
		  
			interfaces.clickContinue();
			sleep(random(1800,2000));
	
			interfaces.clickContinue();
			sleep(random(1800,2000));

			RSComponent temp = interfaces.getComponent(236, 1);
			temp.doClick(true);
			sleep(random(1800,2000));
	
			sleep(random(3000, 4500));
			Status = 6;
		}catch (Exception e) {}
	}
	
	//fights
	public void combat(){
		try {
			if (prayerMethod) prayer();
			if (specID != 1377 && potions != 0)drinkPotions();
			looter();
			if (bunyip)Summoning();
			
			if (inventory.containsOneOf(weaponID)){
	    		inventory.getItem(weaponID).doAction("Wield");
	    		sleep(random(1200,1400));
	    	}
			
			if (  specID == 1377 && combat.getSpecialBarEnergy() == 1000 && 8 > skills.getCurrentLevel(Skills.STRENGTH) - skills.getRealLevel(Skills.STRENGTH)  )  weaponSpec(); //dragon battle axe
			if (specID != 1377 && specID != 0 && combat.getSpecialBarEnergy() == 1000 && combat.getLifePoints() <= random(650,700)) weaponSpec(); //excalibur
				
			if (combat.getLifePoints() <= random(600,650)){
				if (inventory.contains(foodID)){
					inventory.getItem(foodID).doAction("Eat");
					sleep(random(800,1000));
				}
				else if( combat.getLifePoints() <= random(500,600) && !inventory.contains(foodID) ){
					donebanking = false;
					Status = 1;
				}
			} 
			
			if(!getMyPlayer().isInCombat()){
				noAgression = noAgression + 1;
			}
			else{
				noAgression = 0;
			}
			
			if (noAgression > 100){
				Status = 1;
				noAgression = 0;
			}
			else if(noAgression > 20){
				noAgression = 0;
				walking.walkTileMM(new RSTile (3242+random(-2,2),9995+random(-2,2)));
				sleep(random(1250,1500));
			}

		}catch (Exception e) {}
	}
	
	//equips excalibur, specs for 200 healing + defense and then switchs back to an ABYSSAL WHIP
	private void weaponSpec(){
		try{
			RSItem exc = inventory.getItem(specID);
			
			if (game.getCurrentTab() != Game.TAB_INVENTORY) game.openTab(Game.TAB_INVENTORY);
			sleep(random(750, 1000));
			exc.doAction("Wield");
			sleep(random(1500,2000));
			
			if (inventory.containsOneOf(specID)){
	    		log("failsafe - re-equipping whip");
	    		inventory.getItem(specID).doAction("Wield");
	    		sleep(random(1500,2000));
	    	}
			
			if (game.getCurrentTab() != Game.TAB_ATTACK){
				game.openTab(Game.TAB_ATTACK);
				sleep(random(500, 1000));
			}
			interfaces.getComponent(884, 4).doClick();
			sleep(random(400, 700));
			
			RSItem whip = inventory.getItem(weaponID);
			if (game.getCurrentTab() != Game.TAB_INVENTORY) game.openTab(Game.TAB_INVENTORY);
			sleep(random(750, 1000));
			whip.doAction("Wield");
			sleep(random(1500,2000));
			
	    	if (specID == 1377){
	    		if (inventory.getItem(2436) == null){
					Status = 1;
					donebanking = false;
				}
	    		
	    		if (game.getCurrentTab() != Game.TAB_INVENTORY) game.openTab(Game.TAB_INVENTORY);
	    		
	    		inventory.getItem(2436).doAction("Drink");
	    		sleep(random(250, 350));
	    	}
			
	    	if (!combat.isAutoRetaliateEnabled()) {
                combat.setAutoRetaliate(true);
                sleep(random(750,1000));
            }
	    	
		}catch (Exception e) {}
	}
	
	//drink the super str and super atk if the bonus has worn down to 6 or 7
	public void drinkPotions() {
		try{
			int atk = skills.getCurrentLevel(Skills.ATTACK) - skills.getRealLevel(Skills.ATTACK);
			if (atk <= random(8,9)){
				
				if (potions == 1){
					strpotion = inventory.getItem(strengthPot);
					atkpotion = inventory.getItem(attackPot);
				}
				else{
					strpotion = inventory.getItem(EstrengthPot);
					atkpotion = inventory.getItem(EattackPot);
				}
				
				if (strpotion == null || atkpotion == null){
					Status = 1;
					donebanking = false;
				}
				else{
					game.openTab(Game.TAB_STATS);
		            sleep(random(5000, 7000));
		            game.openTab(Game.TAB_INVENTORY);
		            sleep(random(2500,3000));
	
					strpotion.doAction("Drink");
					sleep(random(1000, 1300));
					
					atkpotion.doAction("Drink");
					sleep(random(1000, 1300));
				}
			}
		}catch (Exception e) {}
	}
	
	//Uses quick prays and also recharges prayer at chaos alter
	public void prayer(){
		try{
			//Determines when to pray
			if (combat.getPrayerPoints() <= random(7,9) || combat.getPrayerPoints() == 0){

				walking.walkTileMM(new RSTile (3241,9991));
				sleep(random(2500,2800));

				if (objects.getNearest(39191) != null){
					objects.getNearest(39191).doAction("Climb-up");
					sleep(random(6000,6500));
				}

				if (objects.getNearest(37990) != null){
					sleep(random(1000,1500));
					objects.getNearest(37990).doAction("Pray-at");
					sleep(random(1000,1500));
					trapdoor();
				}
			}
			else if(!combat.isQuickPrayerOn()) interfaces.getComponent(749, 1).doClick(true); 
		}catch (Exception e) {}
	}
	
	//picks up loot(gets you near 200kGP/hr  -> this is the loot table
	public void looter(){
		try{
			if (inventory.contains(229)){
				inventory.getItem(229).doAction("Drop");
				sleep(random(800,900));
			}
			
			if (inventory.contains(526)){
				inventory.getItem(526).doAction("Drop");
				sleep(random(800,900));
			}
		
			if (inventory.contains(995)){
				inventory.getItem(995).doAction("Drop");
				sleep(random(800,900));
			}
			
			if (inventory.contains(888)){
				inventory.getItem(888).doAction("Drop");
				sleep(random(800,900));
			}
			
			if (inventory.contains(14664)){
				inventory.getItem(14664).doAction("Drop");
				sleep(random(800,900));
			}

			//Global rare drops
			itempickup(18778,"Starved ancient effigy");
			itempickup(1249,"Dragon spear");
			itempickup(1163,"Rune full helm");
			itempickup(1247,"Rune spear");
			itempickup(1617,"Uncut diamond");
			itempickup(14469,"Strange key loop");
			itempickup(14470,"Strange key teeth");
			itempickup(2366,"Shield left half ");
			//Ring of Wealth drop table
			itempickup(20667,"Vecna skull");
			itempickup(1516,"Yew logs");
			itempickup(452, "Runite ore");
			itempickup(570, "Fire orb");
			itempickup(6686,"Saradomin brew (4)");
			itempickup(454, "Coal");
			itempickup(270, "Clean torstol");
			itempickup(574, "Air orb");
			itempickup(450, "Adamantite ore");
			itempickup(1392,"Battlestaff");
			itempickup(2999,"Clean toadflax");
			itempickup(3001,"Clean snapdragon");
			itempickup(1216,"Dragon dagger");
			itempickup(5316,"Magic seed");
			itempickup(9342,"Onyx bolts");
			itempickup(1631,"Uncut dragonstone");
			itempickup(384,"Raw shark");
			itempickup(2364,"Rune bar");
			itempickup(892,"Rune arrow");
			itempickup(372,"Raw swordfish");
			itempickup(5289,"Palm tree seed");
			itempickup(533,"Big bones");
			itempickup(1443,"Fire talisman");
			itempickup(9143,"Adamant bolts");
			itempickup(5315,"Yew seed");
			//Stackable drops
			itempickup(12158, "Gold charm");
			itempickup(12163,"Blue charm");
			itempickup(12160,"Crimson charm");
			itempickup(12159,"Green charm");
			itempickup(8781,"Teak plank");
			itempickup(8779,"Oak plank");
			itempickup(961,"Plank");
			itempickup(7937,"Pure essence");
			//Champion scroll
			itempickup(6807, "Champion's scroll");
			
			//herb drops - picks it up if i at least 2 free slots (reserve 1 slot for rare drops)
			if (inventory.getCount() < 27 && inventory.contains(12158) && inventory.contains(12160) && inventory.contains(12159) && inventory.contains(8781) && inventory.contains(8779) && inventory.contains(961) && inventory.contains(7937) && inventory.contains(12163)){
				itempickup(2485,"Grimy lantadyme");
				if (inventory.getCount() < 27) itempickup(217,"Grimy dwarf weed");
				if (inventory.getCount() < 27) itempickup(207,"Grimy ranarr");
			}
			
		}catch (Exception e) {}
	}
	
	//subfunction for looting
    public void itempickup(int itemID, String itemString) {
    	try{
    		if (groundItems.getNearest(itemID) != null){
	    		RSGroundItem item = groundItems.getNearest(itemID);
	    		if ((groundItems.getNearest(itemID) != null)&& groundItems.getNearest(itemID).isOnScreen()) {
	    			if (inventory.isFull() && !(itemID == 12158 || itemID == 12163 || itemID == 12160 || itemID == 12159 || itemID == 8781 || itemID == 8779 || itemID == 961 || itemID == 7937) ){
	    				inventory.getItem(foodID).doAction("Drop");
	    				sleep(random(1200,1400));
	    			}
	    			
	    			if (!(itemID == 207 || itemID == 217 || itemID == 2485 || itemID == 12158 || itemID == 12163 || itemID == 12160 || itemID == 12159 || itemID == 8781 || itemID == 8779 || itemID == 961 || itemID == 7937)){
	    				log("Got a rare/RoW Drop: " + itemString);
	    			}
	    			
	    			item.doAction("Take " + itemString);
	    			sleep(random(6000,7500));
	    			
	    			switch (itemID){
	    			case 8781: profit += 2928;
	    				break;
	    			case 8779: profit += 2050;
    					break;
	    			case 961: profit += 2592;
    					break;
	    			case 7937: profit += 1000;
    					break;
	    			case 217:  profit += 3960;
					break;
	    			case 207: profit += 3600;
					break;
	    			case 2485: profit += 5000;
					break;
    				default:
					break;

				sleep(random(6000,7500));

	    			}
	    			
	    		}
    		}
   
    	}catch (Exception e) {}
    }	
    
    //goes down the trapdoor beside the chaos alter(in the chaos temple)
	public void trapdoor(){
		
		sleep(random(1000,2000));
		if (objects.getNearest(39190) != null && objects.getNearest(39190).isOnScreen()){
			camera.turnToObject(objects.getNearest(39190));
			objects.getNearest(39190).doAction("Enter");
			sleep(random(2000,3500));
		}
		
		if (players.getMyPlayer().getLocation().getY() > 9000){
			Status = 7;
		}
	}

	//deposits loot and withdraws potions/pots/excalibur and teletab.
	public void bank(){
		try{
			
			if (inventory.getCount() != 0){
				bank.depositAll();
				sleep(random(1000,1300));
			}
		
			if (potions == 1){
				if (specID != 1377){
					bank.withdraw(2440, 6);   //super str (4)
					sleep(random(600,800));
					bank.withdraw(2436, 6);   //super atk (4)
					sleep(random(600,800));
				}
				else{
					bank.withdraw(2436, 12);   //super atk (4)
					sleep(random(600,800));
				}
				
				if ( !inventory.contains(2436) && (!inventory.contains(2440) || specID == 1377)) {
					bank();
					//log("error withdrawing items - rebank");
				}
			}
			
			else if (potions == 2){
				bank.withdraw(15312, 6);   //extreme str (4)
				sleep(random(600,800));
				bank.withdraw(15308, 6);   //extreme atk (4)
				sleep(random(600,800));
				if ( !inventory.contains(15312) || !inventory.contains(15308)){
					bank();
				}
			}
			
			if (!bunyip){
				if (potions == 0){
					bank.withdraw(foodID, 12);  //food/monks
				}
				else{
					bank.withdraw(foodID, 6);  //food/monks
					sleep(random(600,800));
				}
				if (!inventory.contains(foodID)){
					bank();
				}
			}
			else{
				bank.withdraw(foodID, 1);  //food/monks
				sleep(random(600,800));
				bank.withdraw(pouchID, 4); //bunyip pouch
				sleep(random(600,800));
				bank.withdraw(12140, 1);  //summoning potion (4)
				sleep(random(600,800));
				if (!(inventory.contains(foodID) && inventory.contains(pouchID) && inventory.contains(12140))){
					bank();
				}
			}
			
			bank.withdraw(8007, 1);  //teletab
			sleep(random(1000,1200));
			
			if (!inventory.contains(8007)){
				bank();
			}
			
			if (specID != 0){
				bank.withdraw(specID, 1);  //your spec weapon.
				sleep(random(600,800));
				if (!inventory.contains(specID)){
					bank();
				}
			}
			donebanking = true;
			Status = 2;
			sleep(random(1000,1300));	
			
		}catch (Exception e) {}
	}
	
	
	//antiban subfunction - checks progress on exp
	public void randomXPcheck(){
		switch(random(0, 4)) {
        	case 0:
        		game.openTab(Game.TAB_STATS);
        		sleep(random(1500, 2000));
	            skills.doHover(Skills.INTERFACE_ATTACK);
	            sleep(random(5000, 7000));
	            game.openTab(Game.TAB_INVENTORY);
	            break;
	        case 1:
	        	game.openTab(Game.TAB_STATS);
        		sleep(random(1500, 2000));
	            skills.doHover(Skills.INTERFACE_STRENGTH);
	            sleep(random(5000, 7000));
	            game.openTab(Game.TAB_INVENTORY);
	            break;
	        case 2:
	        	game.openTab(Game.TAB_STATS);
        		sleep(random(1500, 2000));
	            skills.doHover(Skills.INTERFACE_CONSTITUTION);
	            sleep(random(5000, 7000));
	            game.openTab(Game.TAB_INVENTORY);
	            break;
	        case 3:
	        	game.openTab(Game.TAB_STATS);
        		sleep(random(1500, 2000));
	            skills.doHover(Skills.INTERFACE_PRAYER);
	            sleep(random(5000, 7000));
	            game.openTab(Game.TAB_INVENTORY);
	            break;
	        case 4:
	        	game.openTab(Game.TAB_STATS);
        		sleep(random(1500, 2000));
	        	skills.doHover(Skills.INTERFACE_DEFENSE);
	            sleep(random(5000, 7000));
	            game.openTab(Game.TAB_INVENTORY);
	            break;
	        default: log("switch error in check tab");
	        	break;
    	}
	}

	public void toBank(){
		if (inventory.contains(tabID) && !(bankArea.contains(players.getMyPlayer().getLocation()))  ){
			if (combat.isQuickPrayerOn()) interfaces.getComponent(749, 1).doClick(true); 
				tablet = inventory.getItem(tabID);
				tablet.doAction("Break");
				sleep(random(5000,6000));
		}
		if (!bankArea.contains(players.getMyPlayer().getLocation()) && telePath == 1){
			walking.newTilePath(tilesToBank).traverse();
			sleep(random(200,300));
			return;
		}
		
		else if(!bankArea.contains(players.getMyPlayer().getLocation()) && telePath == 2){
			walking.newTilePath(GEtoBank).traverse();
			sleep(random(200,300));
			return;
		}
		
	}

	//randomly moves the camera around
	public void wigglecamera(){
		int time = random(2000,4000);
		camera.moveRandomly(time);
	}
	
	//antiban - saves your account from getting banned ^^
	public void antiban(){
		
		if (random(0,2500) == 0){
			//log("start antiban");
			abnumber = random(0,60);
			
			if (abnumber >= 0 && abnumber <= 30){
				//log("wiggle camera");
					wigglecamera();
			}
			else if (abnumber >= 30 && abnumber <= 50){
				//log("check exp");
				randomXPcheck();
			}
			else if (abnumber >= 50 && abnumber <= 60){
				//log("Taking offscreen break 30-60 secs");
	            mouse.moveOffScreen();
	            sleep(random(30000, 60000));
			}
			
		}
	}
	
	//Summons a bunyip if you dont have one - drinks a summoning potion if you need one.
	public void Summoning(){
		try{
			if(summoning.isFamiliarSummoned()==false){
				RSItem bunyippouch=inventory.getItem(pouchID);
				RSItem summoningpot=inventory.getItem(summonPot);
				if((bunyippouch!=null)&&(summoningpot!=null)){
					if(summoning.getSummoningPoints()<=15){
						summoningpot.doAction("Drink");
						sleep(random(500,750));
					}
					bunyippouch.doAction("Summon");
					sleep(random(750,1000));}
				}
			}catch (Exception e) {}
	}
	
	//executes after the script ends
	public void onFinish(){
		millis = System.currentTimeMillis() - startTime;
    	hours = millis / (1000 * 60 * 60);
    	millis -= hours * (1000 * 60 * 60);
    	minutes = millis / (1000 * 60);
    	millis -= minutes * (1000 * 60);
    	seconds = millis / 1000;
		profit = oakcost * inventory.getCount(true,8779)+ plankcost* inventory.getCount(true,961)+ teakcost*inventory.getCount(true,8781) + esscost*inventory.getCount(true,7937);
    	expGained = 3*(skills.getCurrentExp(Skills.CONSTITUTION) - startExp);
		log("Thanks for using alcaZombie!");
		log("The script ran for: " + hours + ":" + minutes + ":" + seconds);
		log("Profit was: " + profit);
		log("Exp gain was: " + expGained);
		
		stopScript();
	}
	
	//executes when the script begins
	public boolean onStart(){
		log("script start - displaying gui");
		alcaZombieGUI GUI = new alcaZombieGUI();
        GUI.setVisible(true);
        while(!guiDone){
            sleep(50);
        }
        if(useSavedSettings) loadSettings();
        sleep(500);
		log("randoms enabled");
		env.enableRandoms();
		env.disableRandom("Improved Rewards Box");
		walking.setRun(true);
		startTime = System.currentTimeMillis();
		startExp = skills.getCurrentExp(Skills.CONSTITUTION);
		plankcost = 220;//grandExchange.loadItemInfo(961).getMarketPrice();
		//grandExchange.getMarketPrice(961);
		oakcost = 400;//grandExchange.loadItemInfo(8779).getMarketPrice();
		teakcost = 700;//grandExchange.loadItemInfo(8781).getMarketPrice();
		esscost = 100;//grandExchange.loadItemInfo(7937).getMarketPrice();
		log("weapon id is :" + weaponID);
		log("food id is: " + foodID);
		log("prayer is:" + prayerMethod);
		log("bunyip is:" + bunyip);
		log("spec id is:" + specID);
		log("potion is:" + potions);
		log("telepath is:" + telePath);
		return true;
	}

	
	//failsafe based on what servermessages are returned by game
    public void serverMessageRecieved(ServerMessageEvent e) {
        String msg = e.getMessage();
        
        if (msg.contains("You have run out of Prayer points; you can recharge at an alter.")){
        	//log("server message receieved - stat 7 go pray");
        	Status = 7;
        	prayer();
        }
        
        //More to be announced?
    }
    
	@Override
	public int loop() {
		try{
			if (Status == 1 && !donebanking){
				toBank();
			}
			
			if (!donebanking && !bank.isOpen() && bankArea.contains(players.getMyPlayer().getLocation())){
				sleep(random(1000,1200));
				bank.open();
				sleep(random(1000,1200));
			}
			
			if (bank.isOpen() && !donebanking){
				bank();
				sleep(random(2000,3000));
			}

			if (Status == 2)toHart();
			if (Status == 3)openDoor();
			if (Status == 4)stairs();
			if (Status == 5 || npcs.getNearest(13485) != null)talkHart(); //
			if ((Status == 6 || (objects.getNearest(37990) != null && combat.getPrayerPoints() > 9))) trapdoor(); // 
			if (Status == 7 || players.getMyPlayer().getLocation().getY() > 9000)combat();
			antiban();
		}catch (Exception e) {}
		return 1;
	}
 

    //START: Code generated using Enfilade's Easel
    private Image getImage(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch(IOException e) {
            return null;
        }
    }

    private final Color color1 = new Color(0, 0, 0); //black i think
    private final Color color2 = new Color(255, 0, 51);
    private final Color color3 = new Color(240, 240, 240);
    private final Color color4 = new Color(255, 255, 255);
    private final Font font1 = new Font("Papyrus", 3, 30);
    private final Font font2 = new Font("Arial", 0, 14);
    private final Font font3 = new Font("Arial", 0, 10);

    private final Image img1 = getImage("http://images2.wikia.nocookie.net/__cb20100824140459/runescape/images/thumb/7/77/Armoured_Zombie.png/180px-Armoured_Zombie.png");

    public void onRepaint(Graphics g1) {
        Graphics2D g = (Graphics2D)g1;

        if(showPaint){
        	millis = System.currentTimeMillis() - startTime;
        	hours = millis / (1000 * 60 * 60);
        	millis -= hours * (1000 * 60 * 60);
        	minutes = millis / (1000 * 60);
        	millis -= minutes * (1000 * 60);
        	seconds = millis / 1000;
        	
        	expGained = 3*(skills.getCurrentExp(Skills.CONSTITUTION) - startExp);
        	expHour = (int) ((expGained) * 3600000D / (System.currentTimeMillis() - startTime));
        	profitHour = (int) ((profit) * 3600000D / (System.currentTimeMillis() - startTime));

        	 g.setColor(color1);
             g.fillRect(2, 339, 511, 120);
             g.drawImage(img1, 298, 186, null);
             g.setFont(font1);
             g.setColor(color2);
             g.drawString("alcaZombie", 301, 454);
             g.setFont(font2);
             g.setColor(color3);
             g.drawString("EXP Gained: " + expGained, 14, 404);
             g.drawString("Run Time: " + hours +":"+ minutes + ":" + seconds, 14, 363);
             g.drawString("EXP/Hr: " + expHour, 14, 384);
             g.drawString("Profit: " + profit,14,424);
             g.drawString("Profit/Hr: " + profitHour,14,444);
             
             g.setFont(font3);
        	 g.setColor(color4);
             g.fillRect(closeButton.x, closeButton.y, closeButton.width, closeButton.height);//<<Fills a rectangle
             g.setColor(color1);
             g.drawString("Toggle paint", 443, 326);
        }
        else {
        	g.setColor(color4);
            g.fillRect(closeButton.x, closeButton.y, closeButton.width, closeButton.height);//<<Fills a rectangle
            g.setColor(color1);
            g.setFont(font3);
            g.drawString("Toggle paint", 443, 326);
        }
    }
    
	  //END: Code generated using Enfilade's Easel
	public void mouseClicked(MouseEvent e){
		p = e.getPoint();
		if(closeButton.contains(p)){
		showPaint = !showPaint;
		}
	}
	
	public void mouseEntered(MouseEvent e){
	}
	public void mouseExited(MouseEvent e){
	}
	public void mousePressed(MouseEvent e){
	}
	public void mouseReleased(MouseEvent e){
	}
	
	
	public class alcaZombieGUI extends JPanel {
		private static final long serialVersionUID = 1L;
		public alcaZombieGUI() {
			initComponents();
		}
		
		private void button1ActionPerformed(ActionEvent e) {
			
			if(radioButton1.isSelected()){
				bunyip = true;
			}
			if (radioButton2.isSelected()){
				prayerMethod = true;
			}

			weaponID = Integer.parseInt(textField1.getText().toString().trim());
			foodID = Integer.parseInt(textField2.getText().toString().trim());
			
			if (comboBox2.getSelectedItem().toString().equalsIgnoreCase("Excalibur")) specID = 35;
			else if (comboBox2.getSelectedItem().toString().equalsIgnoreCase("Enhanced Excalibur")) specID = 14632;
			else if (comboBox2.getSelectedItem().toString().equalsIgnoreCase("Dragon Battle Axe"))specID = 1377;
			else if (comboBox2.getSelectedItem().toString().equalsIgnoreCase("None")) specID = 0; //none
			
			if (comboBox3.getSelectedItem().toString().equalsIgnoreCase("Super")) potions = 1; //1 = super
			else if (comboBox3.getSelectedItem().toString().equalsIgnoreCase("Extreme"))potions = 2;  //2 = extreme
			else if (comboBox3.getSelectedItem().toString().equalsIgnoreCase("None"))potions = 0; //none
			
			if (comboBox1.getSelectedItem().toString().equalsIgnoreCase("Varrock Centre"))telePath = 1;
			else if (comboBox1.getSelectedItem().toString().equalsIgnoreCase("Grand Exchange")) telePath = 2;
			
			saveSettings();
			guiDone = true;
			frame1.setVisible(false);
			panel1.setVisible(false);
			frame1.dispose();
		}
		
		private void button2ActionPerformed(ActionEvent e) {
			useSavedSettings = true;
			guiDone = true;
			frame1.setVisible(false);
			panel1.setVisible(false);
			frame1.dispose();
		}
		
		private void initComponents() {
			// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
			// Generated using JFormDesigner Evaluation license
			frame1 = new JFrame();
			panel1 = new JPanel();
			comboBox1 = new JComboBox();
			comboBox2 = new JComboBox();
			comboBox3 = new JComboBox();
			label1 = new JLabel();
			label2 = new JLabel();
			label3 = new JLabel();
			label4 = new JLabel();
			label5 = new JLabel();
			textField1 = new JTextField();
			textField2 = new JTextField();
			radioButton1 = new JRadioButton();
			radioButton2 = new JRadioButton();
			button1 = new JButton();
			label6 = new JLabel();
			label7 = new JLabel();
			button2 = new JButton();
			
			//======== frame1 ========
			{
				frame1.setTitle("alcaZombe Paid Release!");
				Container frame1ContentPane = frame1.getContentPane();
				frame1ContentPane.setLayout(null);
				frame1.setVisible(true);
				frame1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame1.setResizable(false);  // this is optional, but STRONGLY recommended.
				//======== panel1 ========
				{
					panel1.setLayout(null);

					//---- comboBox1 ----
					comboBox1.setModel(new DefaultComboBoxModel(new String[] {
						"Varrock Centre",
						"Grand Exchange"
					}));
					panel1.add(comboBox1);
					comboBox1.setBounds(85, 175, 130, 35);

					//---- comboBox2 ----
					comboBox2.setModel(new DefaultComboBoxModel(new String[] {
						"Excalibur",
						"Enhanced Excalibur",
						"Dragon Battle Axe",
						"None"
					}));
					panel1.add(comboBox2);
					comboBox2.setBounds(85, 95, 125, 30);

					//---- comboBox3 ----
					comboBox3.setModel(new DefaultComboBoxModel(new String[] {
						"Super",
						"Extreme",
						"None"
					}));
					panel1.add(comboBox3);
					comboBox3.setBounds(85, 135, 75, 30);

					//---- label1 ----
					label1.setText("Weapon ID");
					panel1.add(label1);
					label1.setBounds(15, 15, 60, 30);

					//---- label2 ----
					label2.setText("Food ID");
					panel1.add(label2);
					label2.setBounds(15, 50, 55, 35);

					//---- label3 ----
					label3.setText("Special weapon");
					panel1.add(label3);
					label3.setBounds(5, 100, 85, 20);

					//---- label4 ----
					label4.setText("Potions");
					panel1.add(label4);
					label4.setBounds(10, 135, 60, 25);

					//---- label5 ----
					label5.setText("Teleport location");
					panel1.add(label5);
					label5.setBounds(5, 180, 85, 20);
					panel1.add(textField1);
					textField1.setBounds(85, 20, 65, 25);
					panel1.add(textField2);
					textField2.setBounds(85, 55, 65, 25);
					panel1.add(radioButton1);
					radioButton1.setBounds(85, 215, radioButton1.getPreferredSize().width, 20);
					panel1.add(radioButton2);
					radioButton2.setBounds(new Rectangle(new Point(85, 255), radioButton2.getPreferredSize()));

					//---- button1 ----
					button1.setText("START");
					panel1.add(button1);
					button1.setBounds(125, 225, 85, 55);
					button1.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							button1ActionPerformed(e);
						}
					});
					
					
					
					//---- label6 ----
					label6.setText("Use Prayer");
					panel1.add(label6);
					label6.setBounds(10, 250, 65, 25);

					//---- label7 ----
					label7.setText("Use Bunyips");
					panel1.add(label7);
					label7.setBounds(10, 215, 75, 25);
					
					//---- button2 ----
					button2.setText("Use Previous Setting");
					panel1.add(button2);
					button2.setBounds(40, 295, 165, 25);
					
					button2.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							button2ActionPerformed(e);
						}
					});
					{ // compute preferred size
						Dimension preferredSize = new Dimension();
						for(int i = 0; i < panel1.getComponentCount(); i++) {
							Rectangle bounds = panel1.getComponent(i).getBounds();
							preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
							preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
						}
						Insets insets = panel1.getInsets();
						preferredSize.width += insets.right;
						preferredSize.height += insets.bottom;
						panel1.setMinimumSize(preferredSize);
						panel1.setPreferredSize(preferredSize);
					}
				}
				frame1ContentPane.add(panel1);
				panel1.setBounds(0, 0, 255, 335);

				{ // compute preferred size
					Dimension preferredSize = new Dimension();
					for(int i = 0; i < frame1ContentPane.getComponentCount(); i++) {
						Rectangle bounds = frame1ContentPane.getComponent(i).getBounds();
						preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
						preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
					}
					Insets insets = frame1ContentPane.getInsets();
					preferredSize.width += insets.right;
					preferredSize.height += insets.bottom;
					frame1ContentPane.setMinimumSize(preferredSize);
					frame1ContentPane.setPreferredSize(preferredSize);
				}
				frame1.pack();
				frame1.setLocationRelativeTo(frame1.getOwner());
			}
			// JFormDesigner - End of component initialization  //GEN-END:initComponents
		}

		// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
		// Generated using JFormDesigner Evaluation license - Kevan Pawel
		private JFrame frame1;
		private JPanel panel1;
		private JComboBox comboBox1;
		private JComboBox comboBox2;
		private JComboBox comboBox3;
		private JLabel label1;
		private JLabel label2;
		private JLabel label3;
		private JLabel label4;
		private JLabel label5;
		private JTextField textField1;
		private JTextField textField2;
		private JRadioButton radioButton1;
		private JRadioButton radioButton2;
		private JButton button1;
		private JLabel label6;
		private JLabel label7;
		private JButton button2;
		// JFormDesigner - End of variables declaration  //GEN-END:variables
	}
	private boolean useSavedSettings = false;
    private File settingsFile = new File(new File(
			GlobalConfiguration.Paths.getSettingsDirectory()),
			"AZombieSettings.ini");
}

	class AZombieSettings implements Serializable {
		private static final long serialVersionUID = 1L;
		public int potions;
		public int telePath;
		public boolean bunyip;
		public boolean prayerMethod;
		public int weaponID;
		public int foodID;
		public int specID;
	}
	

