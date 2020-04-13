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
package net.sf.l2j.gameserver.handler.admincommandhandlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.gameserver.datatables.CharTemplateTable;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.instancemanager.CastleManager;
import net.sf.l2j.gameserver.instancemanager.ClanHallManager;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.L2Summon;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PetInstance;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.network.L2GameClient;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.GMViewItemList;
import net.sf.l2j.gameserver.network.serverpackets.HennaInfo;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.util.Util;

public class AdminEditChar implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_edit_character",
		"admin_current_player",
		"admin_nokarma", // this is to remove karma from selected char...
		"admin_setkarma", // sets karma of target char to any amount. //setkarma <karma>
		"admin_character_list", // same as character_info, kept for compatibility purposes
		"admin_character_info", // given a player name, displays an information window
		"admin_show_characters", // list of characters
		"admin_find_character", // find a player by his name or a part of it (case-insensitive)
		"admin_find_ip", // find all the player connections from a given IPv4 number
		"admin_find_account", // list all the characters from an account (useful for GMs w/o DB access)
		"admin_find_dualbox", // list all IPs with more than 1 char logged in (dualbox)
		"admin_rec", // gives recommendation points
		"admin_settitle", // changes char's title
		"admin_setname", // changes char's name
		"admin_setsex", // changes char's sex
		"admin_setcolor", // change char name's color
		"admin_settcolor", // change char title's color
		"admin_setclass", // changes char's classId
		
		"admin_summon_info", // displays an information window about target summon
		"admin_unsummon", // unsummon target's pet/summon
		"admin_summon_setlvl", // set the pet's level
		"admin_show_pet_inv", // show pet's inventory
		"admin_fullfood", // fulfills a pet's food bar
		
		"admin_party_info", // find party infos of targeted character, if any
		"admin_clan_info", // find clan infos of the character, if any
		"admin_remove_clan_penalty" // removes clan penalties
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.equals("admin_current_player"))
			showCharacterInfo(activeChar, null);
		else if ((command.startsWith("admin_character_list")) || (command.startsWith("admin_character_info")))
		{
			try
			{
				String val = command.substring(21);
				L2PcInstance target = L2World.getInstance().getPlayer(val);
				if (target != null)
					showCharacterInfo(activeChar, target);
				else
					activeChar.sendPacket(SystemMessageId.CHARACTER_DOES_NOT_EXIST);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Usage: //character_info <player_name>");
			}
		}
		else if (command.startsWith("admin_show_characters"))
		{
			try
			{
				String val = command.substring(22);
				int page = Integer.parseInt(val);
				listCharacters(activeChar, page);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				// Case of empty page number
				activeChar.sendMessage("Usage: //show_characters <page_number>");
			}
		}
		else if (command.startsWith("admin_find_character"))
		{
			try
			{
				String val = command.substring(21);
				findCharacter(activeChar, val);
			}
			catch (StringIndexOutOfBoundsException e)
			{ // Case of empty character name
				activeChar.sendMessage("Usage: //find_character <character_name>");
				listCharacters(activeChar, 0);
			}
		}
		else if (command.startsWith("admin_find_ip"))
		{
			try
			{
				String val = command.substring(14);
				findCharactersPerIp(activeChar, val);
			}
			catch (Exception e)
			{
				// Case of empty or malformed IP number
				activeChar.sendMessage("Usage: //find_ip <www.xxx.yyy.zzz>");
				listCharacters(activeChar, 0);
			}
		}
		else if (command.startsWith("admin_find_account"))
		{
			try
			{
				String val = command.substring(19);
				findCharactersPerAccount(activeChar, val);
			}
			catch (Exception e)
			{
				// Case of empty or malformed player name
				activeChar.sendMessage("Usage: //find_account <player_name>");
				listCharacters(activeChar, 0);
			}
		}
		else if (command.startsWith("admin_find_dualbox"))
		{
			int multibox = 2;
			try
			{
				String val = command.substring(19);
				multibox = Integer.parseInt(val);
				if (multibox < 1)
				{
					activeChar.sendMessage("Usage: //find_dualbox [number > 0]");
					return false;
				}
			}
			catch (Exception e)
			{
			}
			findDualbox(activeChar, multibox);
		}
		else if (command.equals("admin_edit_character"))
			editCharacter(activeChar);
		// Karma control commands
		else if (command.equals("admin_nokarma"))
			setTargetKarma(activeChar, 0);
		else if (command.startsWith("admin_setkarma"))
		{
			try
			{
				String val = command.substring(15);
				int karma = Integer.parseInt(val);
				setTargetKarma(activeChar, karma);
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Usage: //setkarma <new_karma_value>");
			}
		}
		else if (command.startsWith("admin_rec"))
		{
			try
			{
				String val = command.substring(10);
				int recVal = Integer.parseInt(val);
				L2Object target = activeChar.getTarget();
				L2PcInstance player = null;
				
				if (target instanceof L2PcInstance)
					player = (L2PcInstance) target;
				else
					return false;
				
				player.setRecomHave(recVal);
				player.sendMessage("You have been recommended by a GM");
				player.broadcastUserInfo();
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Usage: //rec number");
			}
		}
		else if (command.startsWith("admin_setclass"))
		{
			try
			{
				String val = command.substring(15);
				int classidval = Integer.parseInt(val);
				L2Object target = activeChar.getTarget();
				L2PcInstance player = null;
				
				if (target instanceof L2PcInstance)
					player = (L2PcInstance) target;
				else
					return false;
				
				boolean valid = false;
				for (ClassId classid : ClassId.values())
					if (classidval == classid.getId())
						valid = true;
				
				if (valid && (player.getClassId().getId() != classidval))
				{
					player.setClassId(classidval);
					if (!player.isSubClassActive())
						player.setBaseClass(classidval);
					
					String newclass = player.getTemplate().getClassName();
					
					player.refreshOverloaded();
					player.store();
					player.sendPacket(new HennaInfo(player));
					player.broadcastUserInfo();
					
					// Messages
					if (player != activeChar)
						player.sendMessage("A GM changed your class to " + newclass + ".");
					activeChar.sendMessage(player.getName() + " is now a " + newclass + ".");
				}
				else
					activeChar.sendMessage("Usage: //setclass <valid classid>");
			}
			catch (StringIndexOutOfBoundsException e)
			{
				AdminHelpPage.showHelpPage(activeChar, "charclasses.htm");
			}
		}
		else if (command.startsWith("admin_settitle"))
		{
			try
			{
				String val = command.substring(15);
				
				L2Object target = activeChar.getTarget();
				L2PcInstance player = null;
				
				if (target instanceof L2PcInstance)
					player = (L2PcInstance) target;
				else
					return false;
				
				player.setTitle(val);
				player.sendMessage("Your title has been changed by a GM.");
				player.broadcastTitleInfo();
			}
			catch (StringIndexOutOfBoundsException e)
			{
				// Case of empty character title
				activeChar.sendMessage("Usage: //settitle title");
			}
		}
		else if (command.startsWith("admin_setname"))
		{
			try
			{
				String val = command.substring(14);
				if (!StringUtil.isValidPlayerName(val))
				{
					activeChar.sendMessage("The new name doesn't fit with the regex pattern.");
					return false;
				}
				
				L2Object target = activeChar.getTarget();
				L2PcInstance player = null;
				
				if (target instanceof L2PcInstance)
					player = (L2PcInstance) target;
				else
					return false;
				
				player.setName(val);
				player.sendMessage("Your name has been changed by a GM.");
				player.broadcastUserInfo();
				player.store();
			}
			catch (StringIndexOutOfBoundsException e)
			{
				// Case of empty character name
				activeChar.sendMessage("Usage: //setname name");
			}
		}
		else if (command.startsWith("admin_setsex"))
		{
			L2Object target = activeChar.getTarget();
			L2PcInstance player = null;
			
			if (target instanceof L2PcInstance)
				player = (L2PcInstance) target;
			else
				return false;
			
			player.getAppearance().setSex(player.getAppearance().getSex() ? false : true);
			player.sendMessage("Your gender has been changed by a GM");
			player.broadcastUserInfo();
			player.decayMe();
			player.spawnMe(player.getX(), player.getY(), player.getZ());
		}
		else if (command.startsWith("admin_setcolor"))
		{
			try
			{
				String val = command.substring(15);
				L2Object target = activeChar.getTarget();
				L2PcInstance player = null;
				
				if (target instanceof L2PcInstance)
					player = (L2PcInstance) target;
				else
					return false;
				
				player.getAppearance().setNameColor(Integer.decode("0x" + val));
				player.sendMessage("Your name color has been changed by a GM.");
				player.broadcastUserInfo();
			}
			catch (Exception e)
			{
				// Case of empty color or invalid hex string
				activeChar.sendMessage("You need to specify a valid new color.");
			}
		}
		else if (command.startsWith("admin_settcolor"))
		{
			try
			{
				String val = command.substring(16);
				L2Object target = activeChar.getTarget();
				L2PcInstance player = null;
				
				if (target instanceof L2PcInstance)
					player = (L2PcInstance) target;
				else
					return false;
				
				player.getAppearance().setTitleColor(Integer.decode("0x" + val));
				player.sendMessage("Your title color has been changed by a GM.");
				player.broadcastUserInfo();
			}
			catch (Exception e)
			{
				// Case of empty color or invalid hex string
				activeChar.sendMessage("You need to specify a valid new color.");
			}
		}
		else if (command.startsWith("admin_summon_info"))
		{
			L2Object target = activeChar.getTarget();
			if (target instanceof L2Summon)
				gatherSummonInfo((L2Summon) target, activeChar);
			// Allow to target a player to find his pet - target the pet then.
			else if (target instanceof L2PcInstance)
			{
				L2Summon pet = ((L2PcInstance) target).getPet();
				if (pet != null)
				{
					gatherSummonInfo(pet, activeChar);
					activeChar.setTarget(pet);
				}
				else
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			}
			else
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
		}
		else if (command.startsWith("admin_unsummon"))
		{
			L2Object target = activeChar.getTarget();
			if (target instanceof L2Summon)
				((L2Summon) target).unSummon(((L2Summon) target).getOwner());
			else
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
		}
		else if (command.startsWith("admin_summon_setlvl"))
		{
			L2Object target = activeChar.getTarget();
			if (target instanceof L2PetInstance)
			{
				L2PetInstance pet = (L2PetInstance) target;
				try
				{
					String val = command.substring(20);
					int level = Integer.parseInt(val);
					long newexp, oldexp = 0;
					oldexp = pet.getStat().getExp();
					newexp = pet.getStat().getExpForLevel(level);
					if (oldexp > newexp)
						pet.getStat().removeExp(oldexp - newexp);
					else if (oldexp < newexp)
						pet.getStat().addExp(newexp - oldexp);
				}
				catch (Exception e)
				{
				}
			}
			else
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
		}
		else if (command.startsWith("admin_show_pet_inv"))
		{
			L2Object target;
			try
			{
				target = L2World.getInstance().getPet(Integer.parseInt(command.substring(19)));
			}
			catch (Exception e)
			{
				target = activeChar.getTarget();
			}
			
			if (target instanceof L2PetInstance)
				activeChar.sendPacket(new GMViewItemList((L2PetInstance) target));
			else
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			
		}
		else if (command.startsWith("admin_fullfood"))
		{
			L2Object target = activeChar.getTarget();
			if (target instanceof L2PetInstance)
			{
				L2PetInstance targetPet = (L2PetInstance) target;
				targetPet.setCurrentFed(targetPet.getMaxFed());
			}
			else
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
		}
		else if (command.startsWith("admin_party_info"))
		{
			String val;
			L2Object target;
			try
			{
				val = command.substring(17);
				target = L2World.getInstance().getPlayer(val);
				if (target == null)
					target = activeChar.getTarget();
			}
			catch (Exception e)
			{
				target = activeChar.getTarget();
			}
			
			if (target instanceof L2PcInstance)
			{
				if (((L2PcInstance) target).isInParty())
					gatherPartyInfo((L2PcInstance) target, activeChar);
				else
					activeChar.sendMessage(((L2PcInstance) target).getName() + " isn't in a party.");
			}
			else
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
		}
		else if (command.startsWith("admin_clan_info"))
		{
			try
			{
				final L2PcInstance player = L2World.getInstance().getPlayer(command.substring(16));
				if (player == null)
				{
					activeChar.sendPacket(SystemMessageId.TARGET_CANT_FOUND);
					return false;
				}
				
				final L2Clan clan = player.getClan();
				if (clan == null)
				{
					activeChar.sendMessage("This player isn't in a clan.");
					return false;
				}
				
				final NpcHtmlMessage html = new NpcHtmlMessage(0);
				html.setFile("data/html/admin/claninfo.htm");
				html.replace("%clan_name%", clan.getName());
				html.replace("%clan_leader%", clan.getLeaderName());
				html.replace("%clan_level%", clan.getLevel());
				html.replace("%clan_has_castle%", (clan.hasCastle()) ? CastleManager.getInstance().getCastleById(clan.getCastleId()).getName() : "No");
				html.replace("%clan_has_clanhall%", (clan.hasHideout()) ? ClanHallManager.getInstance().getClanHallById(clan.getHideoutId()).getName() : "No");
				html.replace("%clan_points%", clan.getReputationScore());
				html.replace("%clan_players_count%", clan.getMembersCount());
				html.replace("%clan_ally%", (clan.getAllyId() > 0) ? clan.getAllyName() : "Not in ally");
				activeChar.sendPacket(html);
			}
			catch (Exception e)
			{
				activeChar.sendPacket(SystemMessageId.TARGET_CANT_FOUND);
			}
		}
		else if (command.startsWith("admin_remove_clan_penalty"))
		{
			try
			{
				StringTokenizer st = new StringTokenizer(command, " ");
				if (st.countTokens() != 3)
				{
					activeChar.sendMessage("Usage: //remove_clan_penalty join|create charname");
					return false;
				}
				
				st.nextToken();
				
				boolean changeCreateExpiryTime = st.nextToken().equalsIgnoreCase("create");
				String playerName = st.nextToken();
				
				L2PcInstance player = L2World.getInstance().getPlayer(playerName);
				if (player == null)
				{
					try (Connection con = L2DatabaseFactory.getInstance().getConnection())
					{
						PreparedStatement ps = con.prepareStatement("UPDATE characters SET " + (changeCreateExpiryTime ? "clan_create_expiry_time" : "clan_join_expiry_time") + " WHERE char_name=? LIMIT 1");
						
						ps.setString(1, playerName);
						ps.execute();
						ps.close();
					}
				}
				else
				{
					// removing penalty
					if (changeCreateExpiryTime)
						player.setClanCreateExpiryTime(0);
					else
						player.setClanJoinExpiryTime(0);
				}
				activeChar.sendMessage("Clan penalty is successfully removed for " + playerName + ".");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private static void listCharacters(L2PcInstance activeChar, int page)
	{
		final Collection<L2PcInstance> allPlayers = L2World.getInstance().getAllPlayers().values();
		final int playersCount = allPlayers.size();
		
		final L2PcInstance[] players = allPlayers.toArray(new L2PcInstance[playersCount]);
		
		final int maxCharactersPerPage = 20;
		
		int maxPages = playersCount / maxCharactersPerPage;
		if (playersCount > maxCharactersPerPage * maxPages)
			maxPages++;
		
		// Check if number of users changed
		if (page > maxPages)
			page = maxPages;
		
		int charactersStart = maxCharactersPerPage * page;
		int charactersEnd = playersCount;
		
		if (charactersEnd - charactersStart > maxCharactersPerPage)
			charactersEnd = charactersStart + maxCharactersPerPage;
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/admin/charlist.htm");
		
		final StringBuilder sb = new StringBuilder(500);
		
		// First use of sb.
		for (int x = 0; x < maxPages; x++)
		{
			int pagenr = x + 1;
			sb.append("<a action=\"bypass -h admin_show_characters " + x + "\">Page " + pagenr + "</a>");
		}
		html.replace("%pages%", sb.toString());
		
		// Cleanup current sb.
		sb.setLength(0);
		
		// Second use of sb, add player info into new table row.
		for (int i = charactersStart; i < charactersEnd; i++)
			sb.append("<tr><td width=80><a action=\"bypass -h admin_character_info " + players[i].getName() + "\">" + players[i].getName() + "</a></td><td width=110>" + players[i].getTemplate().getClassName() + "</td><td width=40>" + players[i].getLevel() + "</td></tr>");
		
		html.replace("%players%", sb.toString());
		activeChar.sendPacket(html);
	}
	
	public static void showCharacterInfo(L2PcInstance activeChar, L2PcInstance player)
	{
		if (player == null)
		{
			L2Object target = activeChar.getTarget();
			if (!(target instanceof L2PcInstance))
				return;
			
			player = (L2PcInstance) target;
		}
		else
			activeChar.setTarget(player);
		
		gatherCharacterInfo(activeChar, player, "charinfo.htm");
	}
	
	/**
	 * Gather character informations.
	 * @param activeChar The player who requested that action.
	 * @param player The target to gather informations from.
	 * @param filename The name of the HTM to send.
	 */
	private static void gatherCharacterInfo(L2PcInstance activeChar, L2PcInstance player, String filename)
	{
		final String clientInfo = player.getClient().toString();
		final String account = clientInfo.substring(clientInfo.indexOf("Account: ") + 9, clientInfo.indexOf(" - IP: "));
		final String ip = clientInfo.substring(clientInfo.indexOf(" - IP: ") + 7, clientInfo.lastIndexOf("]"));
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/admin/" + filename);
		html.replace("%name%", player.getName());
		html.replace("%level%", player.getLevel());
		html.replace("%clan%", player.getClan() != null ? "<a action=\"bypass -h admin_clan_info " + player.getName() + "\">" + player.getClan().getName() + "</a>" : "none");
		html.replace("%xp%", player.getExp());
		html.replace("%sp%", player.getSp());
		html.replace("%class%", player.getTemplate().getClassName());
		html.replace("%ordinal%", player.getClassId().ordinal());
		html.replace("%classid%", player.getClassId().toString());
		html.replace("%baseclass%", CharTemplateTable.getInstance().getClassNameById(player.getBaseClass()));
		html.replace("%x%", player.getX());
		html.replace("%y%", player.getY());
		html.replace("%z%", player.getZ());
		html.replace("%currenthp%", (int) player.getCurrentHp());
		html.replace("%maxhp%", player.getMaxHp());
		html.replace("%karma%", player.getKarma());
		html.replace("%currentmp%", (int) player.getCurrentMp());
		html.replace("%maxmp%", player.getMaxMp());
		html.replace("%pvpflag%", player.getPvpFlag());
		html.replace("%currentcp%", (int) player.getCurrentCp());
		html.replace("%maxcp%", player.getMaxCp());
		html.replace("%pvpkills%", player.getPvpKills());
		html.replace("%pkkills%", player.getPkKills());
		html.replace("%currentload%", player.getCurrentLoad());
		html.replace("%maxload%", player.getMaxLoad());
		html.replace("%percent%", Util.roundTo(((float) player.getCurrentLoad() / (float) player.getMaxLoad()) * 100, 2));
		html.replace("%patk%", player.getPAtk(null));
		html.replace("%matk%", player.getMAtk(null, null));
		html.replace("%pdef%", player.getPDef(null));
		html.replace("%mdef%", player.getMDef(null, null));
		html.replace("%accuracy%", player.getAccuracy());
		html.replace("%evasion%", player.getEvasionRate(null));
		html.replace("%critical%", player.getCriticalHit(null, null));
		html.replace("%runspeed%", player.getRunSpeed());
		html.replace("%patkspd%", player.getPAtkSpd());
		html.replace("%matkspd%", player.getMAtkSpd());
		html.replace("%account%", account);
		html.replace("%ip%", ip);
		html.replace("%ai%", player.getAI().getIntention().name());
		activeChar.sendPacket(html);
	}
	
	private static void setTargetKarma(L2PcInstance activeChar, int newKarma)
	{
		L2Object target = activeChar.getTarget();
		if (!(target instanceof L2PcInstance))
			return;
		
		L2PcInstance player = (L2PcInstance) target;
		
		if (newKarma >= 0)
		{
			int oldKarma = player.getKarma();
			
			player.setKarma(newKarma);
			activeChar.sendMessage("You changed " + player.getName() + "'s karma from " + oldKarma + " to " + newKarma + ".");
		}
		else
			activeChar.sendMessage("The karma value must be greater or equal to 0.");
	}
	
	private static void editCharacter(L2PcInstance activeChar)
	{
		L2Object target = activeChar.getTarget();
		if (!(target instanceof L2PcInstance))
			return;
		
		gatherCharacterInfo(activeChar, (L2PcInstance) target, "charedit.htm");
	}
	
	/**
	 * Find the character based on his name, and send back the result to activeChar.
	 * @param activeChar The player to send back results.
	 * @param characterToFind The name to search.
	 */
	private static void findCharacter(L2PcInstance activeChar, String characterToFind)
	{
		int charactersFound = 0;
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/admin/charfind.htm");
		
		final StringBuilder sb = new StringBuilder();
		
		// First use of sb, add player info into new Table row
		for (L2PcInstance player : L2World.getInstance().getAllPlayers().values())
		{
			String name = player.getName();
			if (name.toLowerCase().contains(characterToFind.toLowerCase()))
			{
				charactersFound++;
				sb.append("<tr><td width=80><a action=\"bypass -h admin_character_list " + name + "\">" + name + "</a></td><td width=110>" + player.getTemplate().getClassName() + "</td><td width=40>" + player.getLevel() + "</td></tr>");
			}
			
			if (charactersFound > 20)
				break;
		}
		html.replace("%results%", sb.toString());
		
		// Cleanup sb.
		sb.setLength(0);
		
		// Second use of sb.
		if (charactersFound == 0)
			sb.append("s. Please try again.");
		else if (charactersFound > 20)
		{
			html.replace("%number%", " more than 20.");
			sb.append("s.<br>Please refine your search to see all of the results.");
		}
		else if (charactersFound == 1)
			sb.append(".");
		else
			sb.append("s.");
		
		html.replace("%number%", charactersFound);
		html.replace("%end%", sb.toString());
		activeChar.sendPacket(html);
	}
	
	/**
	 * @param activeChar
	 * @param IpAdress
	 * @throws IllegalArgumentException
	 */
	private static void findCharactersPerIp(L2PcInstance activeChar, String IpAdress) throws IllegalArgumentException
	{
		boolean findDisconnected = false;
		
		if (IpAdress.equals("disconnected"))
			findDisconnected = true;
		else
		{
			if (!IpAdress.matches("^(?:(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2(?:[0-4][0-9]|5[0-5]))\\.){3}(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2(?:[0-4][0-9]|5[0-5]))$"))
				throw new IllegalArgumentException("Malformed IPv4 number");
		}
		
		int charactersFound = 0;
		String ip = "0.0.0.0";
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/admin/ipfind.htm");
		
		final StringBuilder sb = new StringBuilder(1000);
		for (L2PcInstance player : L2World.getInstance().getAllPlayers().values())
		{
			L2GameClient client = player.getClient();
			if (client.isDetached())
			{
				if (!findDisconnected)
					continue;
			}
			else
			{
				if (findDisconnected)
					continue;
				
				ip = client.getConnection().getInetAddress().getHostAddress();
				if (!ip.equals(IpAdress))
					continue;
			}
			
			String name = player.getName();
			charactersFound++;
			StringUtil.append(sb, "<tr><td width=80><a action=\"bypass -h admin_character_list ", name, "\">", name, "</a></td><td width=110>", player.getTemplate().getClassName(), "</td><td width=40>", player.getLevel(), "</td></tr>");
			
			if (charactersFound > 20)
				break;
		}
		html.replace("%results%", sb.toString());
		
		final String replyMSG2;
		
		if (charactersFound == 0)
			replyMSG2 = ".";
		else if (charactersFound > 20)
		{
			html.replace("%number%", " more than 20.");
			replyMSG2 = "s.";
		}
		else if (charactersFound == 1)
			replyMSG2 = ".";
		else
			replyMSG2 = "s.";
		
		html.replace("%ip%", IpAdress);
		html.replace("%number%", charactersFound);
		html.replace("%end%", replyMSG2);
		activeChar.sendPacket(html);
	}
	
	/**
	 * Returns accountinfo.htm with
	 * @param activeChar
	 * @param characterName
	 */
	private static void findCharactersPerAccount(L2PcInstance activeChar, String characterName)
	{
		if (!StringUtil.isValidPlayerName(characterName))
		{
			activeChar.sendMessage("Malformed character name.");
			return;
		}
		
		final L2PcInstance player = L2World.getInstance().getPlayer(characterName);
		if (player == null)
		{
			activeChar.sendPacket(SystemMessageId.TARGET_CANT_FOUND);
			return;
		}
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/admin/accountinfo.htm");
		html.replace("%characters%", String.join("<br1>", player.getAccountChars().values()));
		html.replace("%account%", player.getAccountName());
		html.replace("%player%", characterName);
		activeChar.sendPacket(html);
	}
	
	/**
	 * @param activeChar
	 * @param multibox
	 */
	private static void findDualbox(L2PcInstance activeChar, int multibox)
	{
		Map<String, List<L2PcInstance>> ipMap = new HashMap<>();
		
		String ip = "0.0.0.0";
		
		final Map<String, Integer> dualboxIPs = new HashMap<>();
		
		for (L2PcInstance player : L2World.getInstance().getAllPlayers().values())
		{
			L2GameClient client = player.getClient();
			if (client == null || client.isDetached())
				continue;
			
			ip = client.getConnection().getInetAddress().getHostAddress();
			if (ipMap.get(ip) == null)
				ipMap.put(ip, new ArrayList<L2PcInstance>());
			
			ipMap.get(ip).add(player);
			
			if (ipMap.get(ip).size() >= multibox)
			{
				Integer count = dualboxIPs.get(ip);
				if (count == null)
					dualboxIPs.put(ip, multibox);
				else
					dualboxIPs.put(ip, count++);
			}
		}
		
		List<String> keys = new ArrayList<>(dualboxIPs.keySet());
		Collections.sort(keys, new Comparator<String>()
		{
			@Override
			public int compare(String left, String right)
			{
				return dualboxIPs.get(left).compareTo(dualboxIPs.get(right));
			}
		});
		Collections.reverse(keys);
		
		final StringBuilder sb = new StringBuilder();
		for (String dualboxIP : keys)
			StringUtil.append(sb, "<a action=\"bypass -h admin_find_ip ", dualboxIP, "\">", dualboxIP, " (", dualboxIPs.get(dualboxIP), ")</a><br1>");
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/admin/dualbox.htm");
		html.replace("%multibox%", multibox);
		html.replace("%results%", sb.toString());
		html.replace("%strict%", "");
		activeChar.sendPacket(html);
	}
	
	private static void gatherSummonInfo(L2Summon target, L2PcInstance activeChar)
	{
		final String name = target.getName();
		final String owner = target.getActingPlayer().getName();
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/admin/petinfo.htm");
		html.replace("%name%", (name == null) ? "N/A" : name);
		html.replace("%level%", target.getLevel());
		html.replace("%exp%", target.getStat().getExp());
		html.replace("%owner%", " <a action=\"bypass -h admin_character_info " + owner + "\">" + owner + "</a>");
		html.replace("%class%", target.getClass().getSimpleName());
		html.replace("%ai%", (target.hasAI()) ? target.getAI().getIntention().name() : "NULL");
		html.replace("%hp%", (int) target.getStatus().getCurrentHp() + "/" + target.getStat().getMaxHp());
		html.replace("%mp%", (int) target.getStatus().getCurrentMp() + "/" + target.getStat().getMaxMp());
		html.replace("%karma%", target.getKarma());
		html.replace("%undead%", (target.isUndead()) ? "yes" : "no");
		
		if (target instanceof L2PetInstance)
		{
			final L2PetInstance pet = ((L2PetInstance) target);
			
			html.replace("%inv%", " <a action=\"bypass admin_show_pet_inv " + target.getActingPlayer().getObjectId() + "\">view</a>");
			html.replace("%food%", pet.getCurrentFed() + "/" + pet.getPetLevelData().getPetMaxFeed());
			html.replace("%load%", pet.getInventory().getTotalWeight() + "/" + pet.getMaxLoad());
		}
		else
		{
			html.replace("%inv%", "none");
			html.replace("%food%", "N/A");
			html.replace("%load%", "N/A");
		}
		activeChar.sendPacket(html);
	}
	
	private static void gatherPartyInfo(L2PcInstance target, L2PcInstance activeChar)
	{
		final StringBuilder sb = new StringBuilder(400);
		for (L2PcInstance member : target.getParty().getPartyMembers())
		{
			if (member.getParty().getPartyLeaderOID() != member.getObjectId())
				StringUtil.append(sb, "<tr><td><table width=270 border=0 cellpadding=2><tr><td width=30 align=right>", member.getLevel(), "</td><td width=130><a action=\"bypass -h admin_character_info ", member.getName(), "\">", member.getName(), "</a></td><td width=110 align=right>", member.getClassId().toString(), "</td></tr></table></td></tr>");
			else
				StringUtil.append(sb, "<tr><td><table width=270 border=0 cellpadding=2><tr><td width=30 align=right><font color=\"LEVEL\">", member.getLevel(), "</td><td width=130><a action=\"bypass -h admin_character_info ", member.getName(), "\">", member.getName(), " (Party leader)</a></td><td width=110 align=right>", member.getClassId().toString(), "</font></td></tr></table></td></tr>");
		}
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/admin/partyinfo.htm");
		html.replace("%party%", sb.toString());
		activeChar.sendPacket(html);
	}
}