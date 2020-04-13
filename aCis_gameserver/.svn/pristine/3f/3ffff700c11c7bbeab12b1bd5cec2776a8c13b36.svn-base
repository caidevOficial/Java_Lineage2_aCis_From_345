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
package net.sf.l2j.gameserver.instancemanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.scripting.L2ScriptEngineManager;
import net.sf.l2j.gameserver.scripting.ScriptManager;

public class QuestManager extends ScriptManager<Quest>
{
	protected static final Logger _log = Logger.getLogger(QuestManager.class.getName());
	
	private final List<Quest> _quests = new ArrayList<>();
	
	public static final QuestManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected QuestManager()
	{
	}
	
	public final void report()
	{
		_log.info("QuestManager: Loaded " + _quests.size() + " scripts.");
	}
	
	/**
	 * Add new quest to quest list. Reloads the quest, if exists.
	 * @param quest : Quest to be add.
	 */
	public final void addQuest(Quest quest)
	{
		// Quest does not exist, return.
		if (quest == null)
			return;
		
		// Quest already loaded, unload id.
		Quest old = getQuest(quest.getQuestId());
		if (old != null && old.isRealQuest())
		{
			old.unload();
			_log.info("QuestManager: Replaced: (" + old.getName() + ") with a new version (" + quest.getName() + ").");
			
		}
		
		// Add new quest.
		_quests.add(quest);
	}
	
	/**
	 * Removes the quest from the list.
	 * @param quest : Quest to be removed.
	 * @return boolean : True if removed sucessfully, false otherwise.
	 */
	public final boolean removeQuest(Quest quest)
	{
		return _quests.remove(quest);
	}
	
	/**
	 * Returns the quest by given quest name.
	 * @param questName : The name of the quest.
	 * @return Quest : Quest to be returned, null if quest does not exist.
	 */
	public final Quest getQuest(String questName)
	{
		// Check all quests.
		for (Quest q : _quests)
		{
			// If quest found, return him.
			if (q.getName().equalsIgnoreCase(questName))
				return q;
		}
		// Otherwise return null.
		return null;
	}
	
	/**
	 * Returns the quest by given quest id.
	 * @param questId : The id of the quest.
	 * @return Quest : Quest to be returned, null if quest does not exist.
	 */
	public final Quest getQuest(int questId)
	{
		// Check all quests.
		for (Quest q : _quests)
		{
			// If quest found, return him.
			if (q.getQuestId() == questId)
				return q;
		}
		// Otherwise return null.
		return null;
	}
	
	/**
	 * Reloads the quest given by quest id.
	 * @param questId : The id of the quest to be reloaded.
	 * @return boolean : True if reload was successful, false otherwise.
	 */
	public final boolean reload(int questId)
	{
		// Get quest by questId.
		Quest q = getQuest(questId);
		
		// Quest does not exist, return.
		if (q == null)
			return false;
		
		// Reload the quest.
		return q.reload();
	}
	
	/**
	 * Reloads all quests. Simply reloads all quests according to the scripts.cfg.
	 */
	public final void reloadAllQuests()
	{
		_log.info("QuestManager: Reloading scripts.");
		try
		{
			// Unload all quests first.
			for (Quest quest : _quests)
			{
				if (quest != null)
					quest.unload(false);
			}
			
			// Clear the quest list.
			_quests.clear();
			
			// Load all quests again.
			File scripts = new File("./data/scripts.cfg");
			L2ScriptEngineManager.getInstance().executeScriptList(scripts);
			QuestManager.getInstance().report();
		}
		catch (IOException ioe)
		{
			_log.severe("QuestManager: Failed loading scripts.cfg, scripts won't be loaded.");
		}
	}
	
	/**
	 * @see net.sf.l2j.gameserver.scripting.ScriptManager#getAllManagedScripts()
	 */
	@Override
	public List<Quest> getAllManagedScripts()
	{
		return _quests;
	}
	
	/**
	 * @see net.sf.l2j.gameserver.scripting.ScriptManager#unload(net.sf.l2j.gameserver.scripting.ManagedScript)
	 */
	@Override
	public boolean unload(Quest quest)
	{
		return removeQuest(quest);
	}
	
	/**
	 * @see net.sf.l2j.gameserver.scripting.ScriptManager#getScriptManagerName()
	 */
	@Override
	public String getScriptManagerName()
	{
		return "QuestManager";
	}
	
	private static class SingletonHolder
	{
		protected static final QuestManager _instance = new QuestManager();
	}
}