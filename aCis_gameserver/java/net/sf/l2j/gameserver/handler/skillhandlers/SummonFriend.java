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
package net.sf.l2j.gameserver.handler.skillhandlers;

import net.sf.l2j.gameserver.handler.ISkillHandler;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ConfirmDlg;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.templates.skills.L2SkillType;
import net.sf.l2j.gameserver.util.Util;

/**
 * @authors BiTi, Sami
 */
public class SummonFriend implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.SUMMON_FRIEND
	};
	
	@Override
	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		if (!(activeChar instanceof L2PcInstance))
			return;
		
		L2PcInstance activePlayer = (L2PcInstance) activeChar;
		
		if (!L2PcInstance.checkSummonerStatus(activePlayer))
			return;
		
		for (L2Object obj : targets)
		{
			if (!(obj instanceof L2Character))
				continue;
			
			final L2Character target = ((L2Character) obj);
			if (activeChar == target)
				continue;
			
			if (target instanceof L2PcInstance)
			{
				L2PcInstance targetPlayer = (L2PcInstance) target;
				
				if (!L2PcInstance.checkSummonTargetStatus(targetPlayer, activePlayer))
					continue;
				
				if (!Util.checkIfInRange(50, activeChar, target, false))
				{
					if (!targetPlayer.teleportRequest(activePlayer, skill))
					{
						activePlayer.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_ALREADY_SUMMONED).addPcName(targetPlayer));
						continue;
					}
					
					if (skill.getId() == 1403) // summon friend
					{
						// Send message
						ConfirmDlg confirm = new ConfirmDlg(SystemMessageId.S1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT.getId());
						confirm.addPcName(activePlayer);
						confirm.addZoneName(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						confirm.addTime(30000);
						confirm.addRequesterId(activePlayer.getObjectId());
						target.sendPacket(confirm);
						confirm = null;
					}
					else
					{
						L2PcInstance.teleToTarget(targetPlayer, activePlayer, skill);
						targetPlayer.teleportRequest(null, null);
					}
				}
			}
		}
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}