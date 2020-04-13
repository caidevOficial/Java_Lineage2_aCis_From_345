/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver.model;

import java.util.Vector;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.olympiad.OlympiadManager;
import net.sf.l2j.gameserver.util.Broadcast;
import net.sf.l2j.util.Rnd;

/**
 * @author CaiDev
 *
 */
public class RandomFight
{
    public static enum State{INACTIVE,REGISTER,LOADING,FIGHT}
    public static State state = State.INACTIVE;
    
   public static Vector<L2PcInstance> players = new Vector<>();
   
   protected void openRegistrations()
   {
       state = State.REGISTER;
       Broadcast.announceToOnlinePlayers("Random Fight Event will start in 1 minute.");
       Broadcast.announceToOnlinePlayers("To register write ?register");
       ThreadPoolManager.getInstance().scheduleGeneral(new checkRegist(), 60000 );
   }
    
   protected void checkRegistrations()
    {
       state=State.LOADING;
       
       if(players.isEmpty() || players.size() < 2)
       {
           Broadcast.announceToOnlinePlayers("Random Fight Event will not start cause of no many partitipations, we are sorry.");
           clean();
           return;
       }
       Broadcast.announceToOnlinePlayers("Amount of players Registed: "+players.size());
       Broadcast.announceToOnlinePlayers("2 Random players will be choosen in 30 seconds!");
       ThreadPoolManager.getInstance().scheduleGeneral(new pickPlayers(), 30000 );
    }
    

   protected void pickPlayers()
    {
       if(players.isEmpty() || players.size() < 2)
        {
           Broadcast.announceToOnlinePlayers("Random Fight Event aborted because no many partitipations, we are sorry.");
           clean();
           return;
       }
       
       for(L2PcInstance p : players)
           if(p.isInOlympiadMode() || OlympiadManager.getInstance().isRegistered(p))
           {
               players.remove(p);
               p.sendMessage("You automatically left from event because of your olympiad obligations.");
           }

       
       int rnd1=Rnd.get(players.size());
       int rnd2=Rnd.get(players.size());
       
       while(rnd2==rnd1)
           rnd2=Rnd.get(players.size());
       
       for(L2PcInstance player : players)
       {
           if(player != players.get(rnd1) && player != players.get(rnd2))
               players.remove(player);
       }
       
       Broadcast.announceToOnlinePlayers("Players selected: "+players.firstElement().getName()+" || "+players.lastElement().getName());
       Broadcast.announceToOnlinePlayers("Players will be teleported in 15 seconds");
       ThreadPoolManager.getInstance().scheduleGeneral(new teleportPlayers(), 15000);
    }
    

   protected void teleport()
    {
       if(players.isEmpty() || players.size() < 2)
        {
           Broadcast.announceToOnlinePlayers("Random Fight Event aborted because no many partitipations, we are sorry.");
           clean();
           return;
       }
       Broadcast.announceToOnlinePlayers("Players teleported!");
       
       players.firstElement().teleToLocation(148268,46694,-3409,0);//coliseum west side
       players.lastElement().teleToLocation(150700,46724,-3408,0);//coliseum east side
       players.firstElement().setTeam(1);
       players.lastElement().setTeam(2);
       
       //para,etc
       
       players.firstElement().sendMessage("Fight will begin in 15 seconds!");
       players.lastElement().sendMessage("Fight will begin in 15 seconds!");
       
       ThreadPoolManager.getInstance().scheduleGeneral(new fight(), 15000);
    }
   
   protected void startFight()
    {
       
       if(players.isEmpty() || players.size() < 2)
        {
           Broadcast.announceToOnlinePlayers("One of the players isn't online, event aborted we are sorry!");
           clean();
           return;
       }
       
       state = State.FIGHT;
       Broadcast.announceToOnlinePlayers("FIGHT STARTED!");
       players.firstElement().sendMessage("Start Fight!!");
       players.lastElement().sendMessage("Start Fight!");
       ThreadPoolManager.getInstance().scheduleGeneral(new checkLast(), 120000 );
    }
    
    protected void lastCheck()
    {
       if(state == State.FIGHT)
       {
           if(players.isEmpty() || players.size() < 2)
           {
               revert();
               clean();
               return;
           }
           
           int alive=0;
           for(L2PcInstance player : players)
           {
               if(!player.isDead())
                   alive++;
           }
           
           if(alive==2)
           {
               Broadcast.announceToOnlinePlayers("Random Fight ended tie!");
               clean();
               revert();
           }
       }
    }
   
   public static void revert()
   {
       if(!players.isEmpty())
       for(L2PcInstance p : players)
       {
           if(p == null)
               continue;
           
           if(p.isDead())
           
               p.doRevive();
           {
           p.setCurrentHp(p.getMaxHp());
           p.setCurrentCp(p.getMaxCp());
           p.setCurrentMp(p.getMaxMp());
           }
           p.broadcastUserInfo();
           p.teleToLocation(82698,148638,-3473,0); //giran
           
       }
   }
   
   public static void clean()
   {
       
       if(state == State.FIGHT)
           for(L2PcInstance p : players)
               p.setTeam(0);

       
       players.clear();
       state = State.INACTIVE;
       
   }
   
   protected RandomFight()
   {
       ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new Event(), 60000 * Config.EVERY_MINUTES , 60000 * Config.EVERY_MINUTES);
   }
   
   public static RandomFight getInstance()
   {
       return SingletonHolder._instance;
   }
   
   private static class SingletonHolder
   {
       protected static final RandomFight _instance = new RandomFight();
   }
   
   protected class Event implements Runnable
   {
       @Override
       public void run()
       {
           if(state == State.INACTIVE)
               openRegistrations();
       }
       
   }
   
   protected class checkRegist implements Runnable
   {

       @Override
       public void run()
       {
               checkRegistrations();
       }
       
   }
   
   protected class pickPlayers implements Runnable
   {
       @Override
       public void run()
       {
           pickPlayers();
       }
       
   }
   
   protected class teleportPlayers implements Runnable
   {
       @Override
       public void run()
       {
           teleport();
       }
       
   }
   
   protected class fight implements Runnable
   {

       @Override
       public void run()
       {
           startFight();
       }
       
   }
   
   protected class checkLast implements Runnable
   {
       @Override
       public void run()
       {
           lastCheck();
       }
       
   }
}