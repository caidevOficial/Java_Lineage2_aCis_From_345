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

import java.util.StringTokenizer;

import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

public class AdminSkill implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_show_skills",
		"admin_remove_skills",
		"admin_skill_list",
		"admin_skill_index",
		"admin_add_skill",
		"admin_remove_skill",
		"admin_get_skills",
		"admin_reset_skills",
		"admin_give_all_skills",
		"admin_remove_all_skills",
		"admin_add_clan_skill",
		"admin_st"
	};
	
	private static L2Skill[] adminSkills;
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.equals("admin_show_skills"))
			showMainPage(activeChar);
		else if (command.startsWith("admin_remove_skills"))
		{
			try
			{
				String val = command.substring(20);
				removeSkillsPage(activeChar, Integer.parseInt(val));
			}
			catch (StringIndexOutOfBoundsException e)
			{
			}
		}
		else if (command.startsWith("admin_skill_list"))
		{
			AdminHelpPage.showHelpPage(activeChar, "skills.htm");
		}
		else if (command.startsWith("admin_skill_index"))
		{
			try
			{
				String val = command.substring(18);
				AdminHelpPage.showHelpPage(activeChar, "skills/" + val + ".htm");
			}
			catch (StringIndexOutOfBoundsException e)
			{
			}
		}
		else if (command.startsWith("admin_add_skill"))
		{
			try
			{
				String val = command.substring(15);
				adminAddSkill(activeChar, val);
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Usage: //add_skill <skill_id> <level>");
			}
		}
		else if (command.startsWith("admin_remove_skill"))
		{
			try
			{
				String id = command.substring(19);
				int idval = Integer.parseInt(id);
				adminRemoveSkill(activeChar, idval);
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Usage: //remove_skill <skill_id>");
			}
		}
		else if (command.equals("admin_get_skills"))
		{
			adminGetSkills(activeChar);
		}
		else if (command.equals("admin_reset_skills"))
			adminResetSkills(activeChar);
		else if (command.equals("admin_give_all_skills"))
			adminGiveAllSkills(activeChar);
		else if (command.equals("admin_remove_all_skills"))
		{
			if (activeChar.getTarget() instanceof L2PcInstance)
			{
				L2PcInstance player = (L2PcInstance) activeChar.getTarget();
				
				for (L2Skill skill : player.getAllSkills())
					player.removeSkill(skill);
				
				activeChar.sendMessage("You removed all skills from " + player.getName() + ".");
				if (player != activeChar)
					player.sendMessage("Admin removed all skills from you.");
				
				player.sendSkillList();
			}
		}
		else if (command.startsWith("admin_add_clan_skill"))
		{
			try
			{
				String[] val = command.split(" ");
				adminAddClanSkill(activeChar, Integer.parseInt(val[1]), Integer.parseInt(val[2]));
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Usage: //add_clan_skill <skill_id> <level>");
			}
		}
		else if (command.startsWith("admin_st"))
		{
			try
			{
				StringTokenizer st = new StringTokenizer(command);
				st.nextToken();
				
				int id = Integer.parseInt(st.nextToken());
				adminTestSkill(activeChar, id);
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Used to test skills' visual effect, format : //st <ID>");
			}
		}
		
		return true;
	}
	
	private static void adminTestSkill(L2PcInstance activeChar, int id)
	{
		L2Character player;
		L2Object target = activeChar.getTarget();
		
		if (target == null || !(target instanceof L2Character))
			player = activeChar;
		else
			player = (L2Character) target;
		
		player.broadcastPacket(new MagicSkillUse(activeChar, player, id, 1, 1, 1));
	}
	
	/**
	 * This function will give all the skills that the target can learn at his/her level
	 * @param activeChar The GM char.
	 */
	private static void adminGiveAllSkills(L2PcInstance activeChar)
	{
		L2Object target = activeChar.getTarget();
		L2PcInstance player = null;
		
		if (target instanceof L2PcInstance)
			player = (L2PcInstance) target;
		else
		{
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return;
		}
		
		// Notify player and admin
		activeChar.sendMessage("You gave " + player.giveAvailableSkills() + " skills to " + player.getName() + ".");
		player.sendSkillList();
	}
	
	private static void removeSkillsPage(L2PcInstance activeChar, int page)
	{
		L2Object target = activeChar.getTarget();
		L2PcInstance player = null;
		if (target instanceof L2PcInstance)
			player = (L2PcInstance) target;
		else
		{
			activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
			return;
		}
		
		L2Skill[] skills = player.getAllSkills();
		
		int maxSkillsPerPage = 10;
		int maxPages = skills.length / maxSkillsPerPage;
		if (skills.length > maxSkillsPerPage * maxPages)
			maxPages++;
		
		if (page > maxPages)
			page = maxPages;
		
		int skillsStart = maxSkillsPerPage * page;
		int skillsEnd = skills.length;
		if (skillsEnd - skillsStart > maxSkillsPerPage)
			skillsEnd = skillsStart + maxSkillsPerPage;
		
		final StringBuilder sb = new StringBuilder(3000);
		StringUtil.append(sb, "<html><body><table width=270><tr><td width=45><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td width=180><center>Delete Skills Menu</center></td><td width=45><button value=\"Back\" action=\"bypass -h admin_show_skills\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr></table><br><br><center>Editing <font color=\"LEVEL\">", player.getName(), "</font>, ", player.getTemplate().getClassName(), " lvl ", player.getLevel(), ".<br><center><table width=270><tr>");
		
		for (int x = 0; x < maxPages; x++)
			StringUtil.append(sb, "<td><a action=\"bypass -h admin_remove_skills ", x, "\">Page ", x + 1, "</a></td>");
		
		sb.append("</tr></table></center><br><table width=270><tr><td width=80>Name:</td><td width=60>Level:</td><td width=40>Id:</td></tr>");
		
		for (int i = skillsStart; i < skillsEnd; i++)
			StringUtil.append(sb, "<tr><td width=80><a action=\"bypass -h admin_remove_skill ", skills[i].getId(), "\">", skills[i].getName(), "</a></td><td width=60>", skills[i].getLevel(), "</td><td width=40>", skills[i].getId(), "</td></tr>");
		
		sb.append("</table><br><center><table width=200><tr><td width=50 align=right>Id: </td><td><edit var=\"id_to_remove\" width=55></td><td width=100><button value=\"Remove skill\" action=\"bypass -h admin_remove_skill $id_to_remove\" width=95 height=21 back=\"bigbutton_over\" fore=\"bigbutton\"></td></tr><tr><td></td><td></td><td><button value=\"Back to stats\" action=\"bypass -h admin_current_player\" width=95 height=21 back=\"bigbutton_over\" fore=\"bigbutton\"></td></tr></table></center></body></html>");
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setHtml(sb.toString());
		activeChar.sendPacket(html);
	}
	
	private static void showMainPage(L2PcInstance activeChar)
	{
		L2Object target = activeChar.getTarget();
		L2PcInstance player = null;
		if (target instanceof L2PcInstance)
			player = (L2PcInstance) target;
		else
		{
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return;
		}
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/admin/charskills.htm");
		html.replace("%name%", player.getName());
		html.replace("%level%", player.getLevel());
		html.replace("%class%", player.getTemplate().getClassName());
		activeChar.sendPacket(html);
	}
	
	private static void adminGetSkills(L2PcInstance activeChar)
	{
		L2Object target = activeChar.getTarget();
		L2PcInstance player = null;
		
		if (target instanceof L2PcInstance)
			player = (L2PcInstance) target;
		else
		{
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return;
		}
		
		if (player == activeChar)
			player.sendPacket(SystemMessageId.CANNOT_USE_ON_YOURSELF);
		else
		{
			L2Skill[] skills = player.getAllSkills();
			adminSkills = activeChar.getAllSkills();
			
			for (L2Skill skill : adminSkills)
				activeChar.removeSkill(skill);
			
			for (L2Skill skill : skills)
				activeChar.addSkill(skill, true);
			
			activeChar.sendMessage("You ninjaed " + player.getName() + "'s skills list.");
			activeChar.sendSkillList();
			skills = null;
		}
	}
	
	private static void adminResetSkills(L2PcInstance activeChar)
	{
		if (adminSkills == null)
			activeChar.sendMessage("Ninja first skills of someone to use that command.");
		else
		{
			L2Skill[] skills = activeChar.getAllSkills();
			
			for (L2Skill skill : skills)
				activeChar.removeSkill(skill);
			
			for (L2Skill skill : adminSkills)
				activeChar.addSkill(skill, true);
			
			activeChar.sendMessage("All your skills have been returned back.");
			activeChar.sendSkillList();
			adminSkills = null;
			skills = null;
		}
	}
	
	private static void adminAddSkill(L2PcInstance activeChar, String val)
	{
		L2Object target = activeChar.getTarget();
		L2PcInstance player = null;
		
		if (target instanceof L2PcInstance)
			player = (L2PcInstance) target;
		else
		{
			showMainPage(activeChar);
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return;
		}
		
		StringTokenizer st = new StringTokenizer(val);
		if (st.countTokens() != 2)
			showMainPage(activeChar);
		else
		{
			L2Skill skill = null;
			try
			{
				String id = st.nextToken();
				String level = st.nextToken();
				int idval = Integer.parseInt(id);
				int levelval = Integer.parseInt(level);
				skill = SkillTable.getInstance().getInfo(idval, levelval);
			}
			catch (Exception e)
			{
			}
			
			if (skill != null)
			{
				String name = skill.getName();
				
				player.addSkill(skill, true);
				player.sendMessage("Admin gave you the skill " + name + ".");
				if (player != activeChar)
					activeChar.sendMessage("You gave the skill " + name + " to " + player.getName() + ".");
				
				player.sendSkillList();
			}
			else
				activeChar.sendMessage("Error: there is no such skill.");
			
			showMainPage(activeChar); // Back to start
		}
	}
	
	private static void adminRemoveSkill(L2PcInstance activeChar, int idval)
	{
		L2Object target = activeChar.getTarget();
		L2PcInstance player = null;
		
		if (target instanceof L2PcInstance)
			player = (L2PcInstance) target;
		else
		{
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return;
		}
		
		L2Skill skill = SkillTable.getInstance().getInfo(idval, player.getSkillLevel(idval));
		if (skill != null)
		{
			String skillname = skill.getName();
			
			player.removeSkill(skill);
			activeChar.sendMessage("You removed the skill " + skillname + " from " + player.getName() + ".");
			if (player != activeChar)
				player.sendMessage("Admin removed the skill " + skillname + " from your skills list.");
			
			player.sendSkillList();
		}
		else
			activeChar.sendMessage("Error: there is no such skill.");
		
		removeSkillsPage(activeChar, 0); // Back to previous page
	}
	
	private static void adminAddClanSkill(L2PcInstance activeChar, int id, int level)
	{
		L2Object target = activeChar.getTarget();
		L2PcInstance player = null;
		
		if (target instanceof L2PcInstance)
			player = (L2PcInstance) target;
		else
		{
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			showMainPage(activeChar);
			return;
		}
		
		if (!player.isClanLeader())
		{
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_IS_NOT_A_CLAN_LEADER).addPcName(player));
			showMainPage(activeChar);
			return;
		}
		
		if (id < 370 || id > 391 || level < 1 || level > 3)
		{
			activeChar.sendMessage("Usage: //add_clan_skill <skill_id> <level>");
			showMainPage(activeChar);
			return;
		}
		
		L2Skill skill = SkillTable.getInstance().getInfo(id, level);
		if (skill == null)
		{
			activeChar.sendMessage("Error: there is no such skill.");
			return;
		}
		
		player.getClan().addNewSkill(skill);
		
		activeChar.sendMessage("You gave " + skill.getName() + " Clan Skill to " + player.getClan().getName() + " clan.");
		
		showMainPage(activeChar);
		return;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}