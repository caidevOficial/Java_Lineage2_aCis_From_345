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
package net.sf.l2j.gameserver.handler.chathandlers;

import java.util.StringTokenizer;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IChatHandler;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.handler.VoicedCommandHandler;
import net.sf.l2j.gameserver.model.BlockList;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.util.FloodProtectors;
import net.sf.l2j.gameserver.util.FloodProtectors.Action;

public class ChatAll implements IChatHandler
{
	private static final int[] COMMAND_IDS =
	{
		0
	};
	
	@Override
	public void handleChat(int type, L2PcInstance activeChar, String params, String text)
	{
		if (!FloodProtectors.performAction(activeChar.getClient(), Action.GLOBAL_CHAT))
			return;
		
		//final CreatureSay cs = new CreatureSay(activeChar.getObjectId(), type, activeChar.getName(), text);
		//for (L2PcInstance player : activeChar.getKnownList().getKnownTypeInRadius(L2PcInstance.class, 1250))
		boolean vcd_used = false;
		if (text.startsWith("."))
		{
			//if (!BlockList.isBlocked(player, activeChar))
				//player.sendPacket(cs);
			StringTokenizer st = new StringTokenizer(text);
			IVoicedCommandHandler vch;
			String command = "";

			if (st.countTokens() > 1)
			{
				command = st.nextToken().substring(1);
				params = text.substring(command.length() + 2);
				vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler(command);
			}
			else
			{
				command = text.substring(1);
				if (Config.DEBUG)
					System.out.println("Command: " + command);
				vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler(command);
			}

			if (vch != null)
			{
				vch.useVoicedCommand(command, activeChar, command);
				vcd_used = true;
			}
			else
			{
				if (Config.DEBUG)
					System.out.println("No handler registered for bypass '" + command + "'");
				vcd_used = false;
			}
		}
		//activeChar.sendPacket(cs);
		if (!vcd_used)
		{
			final CreatureSay cs = new CreatureSay(activeChar.getObjectId(), type, activeChar.getName(), text);
			
			for (L2PcInstance player : activeChar.getKnownList().getKnownType(L2PcInstance.class))
			{
				if (activeChar.isInsideRadius(player, 1250, false, true) && !BlockList.isBlocked(player, activeChar))
					player.sendPacket(cs);
			}
			
			activeChar.sendPacket(cs);
			//if (Config.ALLOW_SOCIAL_ACTIONS == true)
			//{
			    if ((text.equalsIgnoreCase("lol") 
					|| text.equalsIgnoreCase("haha") 
					|| text.equalsIgnoreCase("kkkk") 
					|| text.equalsIgnoreCase("xaxa") 
					|| text.equalsIgnoreCase("jajaja")
					|| text.equalsIgnoreCase("xD")
					|| text.equalsIgnoreCase("xd")
					|| text.equalsIgnoreCase("jaja")) && !activeChar.isAttackingNow() && !activeChar.isRunning() && !activeChar.isCastingNow())
			    {
			            activeChar.broadcastPacket(new SocialAction(activeChar, 10));
			    }
			   
			    if ((text.equalsIgnoreCase("hello") 
					|| text.equalsIgnoreCase("hey") 
					|| text.equalsIgnoreCase("aloha") 
					|| text.equalsIgnoreCase("alo") 
					|| text.equalsIgnoreCase("ciao")
					|| text.equalsIgnoreCase("hola")
					|| text.equalsIgnoreCase("hi"))	&& !activeChar.isAttackingNow() && !activeChar.isRunning() && !activeChar.isCastingNow())
			    {
			            activeChar.broadcastPacket(new SocialAction(activeChar, 2));
			    }
			   
			    if ((text.equalsIgnoreCase("gracias") 
					|| text.equalsIgnoreCase("grax") 
				|| text.equalsIgnoreCase("thanks")
				|| text.equalsIgnoreCase("thank you")
				|| text.equalsIgnoreCase("thankyou")
				|| text.equalsIgnoreCase("ty")) && !activeChar.isAttackingNow() && !activeChar.isRunning() && !activeChar.isCastingNow())
			    {
			            activeChar.broadcastPacket(new SocialAction(activeChar, 7));
			    }
			   
			    if ((text.equalsIgnoreCase("yes") 
					|| text.equalsIgnoreCase("si")
					|| text.equalsIgnoreCase("yep")) && !activeChar.isAttackingNow() && !activeChar.isRunning() && !activeChar.isCastingNow())
			    {
			            activeChar.broadcastPacket(new SocialAction(activeChar, 6));
			    }
			   
			    if ((text.equalsIgnoreCase("no") 
					|| text.equalsIgnoreCase("nop") 
					|| text.equalsIgnoreCase("nope")) && !activeChar.isAttackingNow() && !activeChar.isRunning() && !activeChar.isCastingNow())
			    {
			            activeChar.broadcastPacket(new SocialAction(activeChar, 5));
			    }
			    
			    if ((text.equalsIgnoreCase("bien") 
					|| text.equalsIgnoreCase("good") 
				|| text.equalsIgnoreCase("perfecto")
				|| text.equalsIgnoreCase("perfect")
				|| text.equalsIgnoreCase("piola")) && !activeChar.isAttackingNow() && !activeChar.isRunning() && !activeChar.isCastingNow())
			        {
			                activeChar.broadcastPacket(new SocialAction(activeChar, 3));
			        }
			//}
			
		}

	}
	
	@Override
	public int[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}