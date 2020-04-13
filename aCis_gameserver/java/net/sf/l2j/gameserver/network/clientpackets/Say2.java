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
package net.sf.l2j.gameserver.network.clientpackets;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.ChatHandler;
import net.sf.l2j.gameserver.handler.IChatHandler;
import net.sf.l2j.gameserver.model.RandomFight;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.util.IllegalPlayerAction;
import net.sf.l2j.gameserver.util.Util;

public final class Say2 extends L2GameClientPacket
{
	private static Logger _logChat = Logger.getLogger("chat");
	
	public final static int ALL = 0;
	public final static int SHOUT = 1; // !
	public final static int TELL = 2;
	public final static int PARTY = 3; // #
	public final static int CLAN = 4; // @
	public final static int GM = 5;
	public final static int PETITION_PLAYER = 6;
	public final static int PETITION_GM = 7;
	public final static int TRADE = 8; // +
	public final static int ALLIANCE = 9; // $
	public final static int ANNOUNCEMENT = 10;
	public final static int BOAT = 11;
	public final static int L2FRIEND = 12;
	public final static int MSNCHAT = 13;
	public final static int PARTYMATCH_ROOM = 14;
	public final static int PARTYROOM_COMMANDER = 15; // (Yellow)
	public final static int PARTYROOM_ALL = 16; // (Red)
	public final static int HERO_VOICE = 17;
	public final static int CRITICAL_ANNOUNCE = 18;
	
	private final static String[] CHAT_NAMES =
	{
		"ALL",
		"SHOUT",
		"TELL",
		"PARTY",
		"CLAN",
		"GM",
		"PETITION_PLAYER",
		"PETITION_GM",
		"TRADE",
		"ALLIANCE",
		"ANNOUNCEMENT", // 10
		"BOAT",
		"WILLCRASHCLIENT:)",
		"FAKEALL?",
		"PARTYMATCH_ROOM",
		"PARTYROOM_COMMANDER",
		"PARTYROOM_ALL",
		"HERO_VOICE",
		"CRITICAL_ANNOUNCEMENT"
	};
	
	private static final String[] WALKER_COMMAND_LIST =
	{
		"USESKILL",
		"USEITEM",
		"BUYITEM",
		"SELLITEM",
		"SAVEITEM",
		"LOADITEM",
		"MSG",
		"DELAY",
		"LABEL",
		"JMP",
		"CALL",
		"RETURN",
		"MOVETO",
		"NPCSEL",
		"NPCDLG",
		"DLGSEL",
		"CHARSTATUS",
		"POSOUTRANGE",
		"POSINRANGE",
		"GOHOME",
		"SAY",
		"EXIT",
		"PAUSE",
		"STRINDLG",
		"STRNOTINDLG",
		"CHANGEWAITTYPE",
		"FORCEATTACK",
		"ISMEMBER",
		"REQUESTJOINPARTY",
		"REQUESTOUTPARTY",
		"QUITPARTY",
		"MEMBERSTATUS",
		"CHARBUFFS",
		"ITEMCOUNT",
		"FOLLOWTELEPORT"
	};
	
	private String _text;
	private int _type;
	private String _target;
	
	@Override
	protected void readImpl()
	{
		_text = readS();
		_type = readD();
		_target = (_type == TELL) ? readS() : null;
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		 /**
		  * @author: CAI
		  */
		if (activeChar == null)
			return;
		
		if (_type < 0 || _type >= CHAT_NAMES.length)
		{
			_log.warning("Say2: Invalid type: " + _type + " Player : " + activeChar.getName() + " text: " + _text);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			activeChar.logout();
			return;
		}
		
		if (_text.isEmpty())
		{
			_log.warning(activeChar.getName() + ": sending empty text. Possible packet hack.");
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			activeChar.logout();
			return;
		}
		
		if (_text.length() >= 100)
			return;
		
		if (Config.L2WALKER_PROTECTION && _type == TELL && checkBot(_text))
		{
			Util.handleIllegalPlayerAction(activeChar, "Client Emulator Detect: " + activeChar.getName() + " is using L2Walker.", Config.DEFAULT_PUNISH);
			return;
		}
		
		if (!activeChar.isGM() && _type == ANNOUNCEMENT)
		{
			Util.handleIllegalPlayerAction(activeChar, activeChar.getName() + " tried to announce without GM statut.", IllegalPlayerAction.PUNISH_BROADCAST);
			_log.warning(activeChar.getName() + " tried to use announcements without GM statut.");
			return;
		}
		
		if (activeChar.isChatBanned() || (activeChar.isInJail() && !activeChar.isGM()))
		{
			activeChar.sendPacket(SystemMessageId.CHATTING_PROHIBITED);
			return;
		}
		
		checkRandomFight(_text,activeChar);
		 if(_text.equalsIgnoreCase("?register") || _text.equalsIgnoreCase("?unregister"))
		  return;
		
		if (_type == PETITION_PLAYER && activeChar.isGM())
			_type = PETITION_GM;
		
		if (Config.LOG_CHAT)
		{
			LogRecord record = new LogRecord(Level.INFO, _text);
			record.setLoggerName("chat");
			
			if (_type == TELL)
				record.setParameters(new Object[]
				{
					CHAT_NAMES[_type],
					"[" + activeChar.getName() + " to " + _target + "]"
				});
			else
				record.setParameters(new Object[]
				{
					CHAT_NAMES[_type],
					"[" + activeChar.getName() + "]"
				});
			
			_logChat.log(record);
		}
		
		_text = _text.replaceAll("\\\\n", "");
		
		IChatHandler handler = ChatHandler.getInstance().getChatHandler(_type);
		if (handler != null)
			handler.handleChat(_type, activeChar, _target, _text);
		else
			_log.warning(activeChar.getName() + " tried to use unregistred chathandler type: " + _type + ".");
	}
	
	private static boolean checkBot(String text)
	{
		for (String botCommand : WALKER_COMMAND_LIST)
		{
			if (text.startsWith(botCommand))
				return true;
		}
		return false;
	}
	
	static void checkRandomFight(String text,L2PcInstance player)
	   {
	       if(text.equalsIgnoreCase("?register"))
	       {
	           if(RandomFight.players.contains(player))
	           {
	               player.sendMessage("You have already registed to the event.");
	               return;
	           }
	           if(RandomFight.state == RandomFight.State.INACTIVE)
	               return;
	           if(RandomFight.state != RandomFight.State.REGISTER)
	           {
	               player.sendMessage("Event has already started.");
	               return;
	           }
	           RandomFight.players.add(player);
	           player.sendMessage("You registed to the event!!");
	           return;
	       }
	       if(text.equalsIgnoreCase("?unregister"))
	       {
	           if(!RandomFight.players.contains(player))
	           {
	               player.sendMessage("You haven't registed to the event.");
	               return;
	           }
	           if(RandomFight.state == RandomFight.State.INACTIVE)
	               return;
	           if(RandomFight.state != RandomFight.State.REGISTER)
	           {
	               player.sendMessage("Event has already started.");
	               return;
	           }
	           RandomFight.players.remove(player);
	           player.sendMessage("You unregisted from the  event!!");
	       }
	      
	   }
	   
	@Override
	protected boolean triggersOnActionRequest()
	{
		return false;
	}
}