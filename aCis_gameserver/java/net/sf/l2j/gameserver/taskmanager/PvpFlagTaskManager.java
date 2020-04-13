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
package net.sf.l2j.gameserver.taskmanager;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * Updates and clears PvP flag of {@link L2PcInstance} after specified time.
 * @author Tryskell, Hasha
 */
public final class PvpFlagTaskManager implements Runnable
{
	private final Map<L2PcInstance, Long> _players = new ConcurrentHashMap<>();
	
	public static final PvpFlagTaskManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected PvpFlagTaskManager()
	{
		// Run task each second.
		ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(this, 1000, 1000);
	}
	
	/**
	 * Adds {@link L2PcInstance} to the PvpFlagTask.
	 * @param player : L2PcInstance to be added and checked.
	 * @param time : Time in ms, after which the PvP flag is removed.
	 */
	public final void add(L2PcInstance player, long time)
	{
		_players.put(player, System.currentTimeMillis() + time);
	}
	
	/**
	 * Removes {@link L2PcInstance} from the PvpFlagTask.
	 * @param player : {@link L2PcInstance} to be removed.
	 */
	public final void remove(L2PcInstance player)
	{
		_players.remove(player);
	}
	
	@Override
	public final void run()
	{
		// List is empty, skip.
		if (_players.isEmpty())
			return;
		
		// Get current time.
		final long currentTime = System.currentTimeMillis();
		
		// Loop all players.
		for (Iterator<Map.Entry<L2PcInstance, Long>> iterator = _players.entrySet().iterator(); iterator.hasNext();)
		{
			// Get entry of current iteration.
			Map.Entry<L2PcInstance, Long> entry = iterator.next();
			
			// Get time left and check.
			final long timeLeft = entry.getValue();
			
			// Time is running out, clear PvP flag and remove from list.
			if (currentTime > timeLeft)
			{
				entry.getKey().updatePvPFlag(0);
				iterator.remove();
			}
			// Time almost runned out, update to blinking PvP flag.
			else if (currentTime > (timeLeft - 5000))
				entry.getKey().updatePvPFlag(2);
			// Time didn't run out, keep PvP flag.
			else
				entry.getKey().updatePvPFlag(1);
		}
	}
	
	private static class SingletonHolder
	{
		protected static final PvpFlagTaskManager _instance = new PvpFlagTaskManager();
	}
}