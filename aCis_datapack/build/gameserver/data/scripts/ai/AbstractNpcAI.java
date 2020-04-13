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
package ai;

import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.actor.L2Attackable;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.L2Playable;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestEventType;
import net.sf.l2j.gameserver.util.Util;
import net.sf.l2j.util.Rnd;

/**
 * Abstract NPC AI class for datapack based AIs.
 * @author UnAfraid, Zoey76
 */
public abstract class AbstractNpcAI extends Quest
{
	public AbstractNpcAI(String name, String descr)
	{
		super(-1, name, descr);
	}
	
	/**
	 * Register a monster on all event types.
	 * @param mob A mob.
	 */
	public void registerMob(int mob)
	{
		addEventId(mob, QuestEventType.ON_ATTACK);
		addEventId(mob, QuestEventType.ON_KILL);
		addEventId(mob, QuestEventType.ON_SPAWN);
		addEventId(mob, QuestEventType.ON_SPELL_FINISHED);
		addEventId(mob, QuestEventType.ON_SKILL_SEE);
		addEventId(mob, QuestEventType.ON_FACTION_CALL);
		addEventId(mob, QuestEventType.ON_AGGRO_RANGE_ENTER);
	}
	
	/**
	 * Register a monster on particular event types.
	 * @param mob A mob.
	 * @param types Types of event to register mob on.
	 */
	public void registerMob(int mob, QuestEventType... types)
	{
		for (QuestEventType type : types)
			addEventId(mob, type);
	}
	
	/**
	 * Register monsters on all event types.
	 * @param mobs An array of mobs.
	 */
	public void registerMobs(int[] mobs)
	{
		for (int id : mobs)
		{
			addEventId(id, QuestEventType.ON_ATTACK);
			addEventId(id, QuestEventType.ON_KILL);
			addEventId(id, QuestEventType.ON_SPAWN);
			addEventId(id, QuestEventType.ON_SPELL_FINISHED);
			addEventId(id, QuestEventType.ON_SKILL_SEE);
			addEventId(id, QuestEventType.ON_FACTION_CALL);
			addEventId(id, QuestEventType.ON_AGGRO_RANGE_ENTER);
		}
	}
	
	/**
	 * Register monsters on particular event types.
	 * @param mobs An array of mobs.
	 * @param types Types of event to register mobs on.
	 */
	public void registerMobs(int[] mobs, QuestEventType... types)
	{
		for (int id : mobs)
		{
			for (QuestEventType type : types)
				addEventId(id, type);
		}
	}
	
	/**
	 * Register monsters on particular event types.
	 * @param mobs An array of mobs.
	 * @param types Types of event to register mobs on.
	 */
	public void registerMobs(Iterable<Integer> mobs, QuestEventType... types)
	{
		for (int id : mobs)
		{
			for (QuestEventType type : types)
				addEventId(id, type);
		}
	}
	
	/**
	 * Monster runs and attacks the playable.
	 * @param npc The npc to use.
	 * @param playable The victim.
	 * @param aggro The aggro to add, 999 if not given.
	 */
	public static void attack(L2Attackable npc, L2Playable playable, int aggro)
	{
		npc.setIsRunning(true);
		npc.addDamageHate(playable, 0, (aggro <= 0) ? 999 : aggro);
		npc.getAI().setIntention(CtrlIntention.ATTACK, playable);
	}
	
	/**
	 * This method selects a random player.<br>
	 * Player can't be dead and isn't an hidden GM aswell.
	 * @param npc to check.
	 * @return the random player.
	 */
	public static L2PcInstance getRandomPlayer(L2Npc npc)
	{
		List<L2PcInstance> result = new ArrayList<>();
		
		for (L2PcInstance player : npc.getKnownList().getKnownType(L2PcInstance.class))
		{
			if (player.isDead())
				continue;
			
			if (player.isGM() && player.getAppearance().getInvisible())
				continue;
			
			result.add(player);
		}
		
		return (result.isEmpty()) ? null : result.get(Rnd.get(result.size()));
	}
	
	/**
	 * Return the number of players in a defined radius.<br>
	 * Dead players aren't counted, invisible ones is the boolean parameter.
	 * @param range : the radius.
	 * @param npc : the object to make the test on.
	 * @param invisible : true counts invisible characters.
	 * @return the number of targets found.
	 */
	public static int getPlayersCountInRadius(int range, L2Character npc, boolean invisible)
	{
		int count = 0;
		for (L2PcInstance player : npc.getKnownList().getKnownType(L2PcInstance.class))
		{
			if (player.isDead())
				continue;
			
			if (!invisible && player.getAppearance().getInvisible())
				continue;
			
			if (Util.checkIfInRange(range, npc, player, true))
				count++;
		}
		return count;
	}
	
	/**
	 * Under that barbarian name, return the number of players in front, back and sides of the npc.<br>
	 * Dead players aren't counted, invisible ones is the boolean parameter.
	 * @param range : the radius.
	 * @param npc : the object to make the test on.
	 * @param invisible : true counts invisible characters.
	 * @return an array composed of front, back and side targets number.
	 */
	public static int[] getPlayersCountInPositions(int range, L2Character npc, boolean invisible)
	{
		int frontCount = 0;
		int backCount = 0;
		int sideCount = 0;
		
		for (L2PcInstance player : npc.getKnownList().getKnownType(L2PcInstance.class))
		{
			if (player.isDead())
				continue;
			
			if (!invisible && player.getAppearance().getInvisible())
				continue;
			
			if (!Util.checkIfInRange(range, npc, player, true))
				continue;
			
			if (player.isInFrontOf(npc))
				frontCount++;
			else if (player.isBehind(npc))
				backCount++;
			else
				sideCount++;
		}
		
		int[] array =
		{
			frontCount,
			backCount,
			sideCount
		};
		return array;
	}
	
	public static void attack(L2Attackable npc, L2Playable playable)
	{
		attack(npc, playable, 0);
	}
}