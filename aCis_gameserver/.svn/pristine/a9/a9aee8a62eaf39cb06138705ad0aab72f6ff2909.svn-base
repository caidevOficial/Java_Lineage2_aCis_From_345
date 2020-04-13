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
package net.sf.l2j.gameserver.taskmanager.tasks;

import java.io.File;
import java.util.logging.Logger;

import javax.script.ScriptException;

import net.sf.l2j.gameserver.scripting.L2ScriptEngineManager;
import net.sf.l2j.gameserver.taskmanager.TaskManager.ExecutedTask;

/**
 * @author janiii
 */
public final class TaskScript extends ATask
{
	private static final Logger _log = Logger.getLogger(TaskScript.class.getName());
	
	private static final String NAME = "script";
	
	@Override
	public String getName()
	{
		return NAME;
	}
	
	@Override
	public void onTimeElapsed(ExecutedTask task)
	{
		File file = new File(L2ScriptEngineManager.SCRIPT_FOLDER, "cron/" + task.getParams()[2]);
		if (file.isFile())
		{
			try
			{
				L2ScriptEngineManager.getInstance().executeScript(file);
			}
			catch (ScriptException e)
			{
				_log.warning("Failed loading: " + task.getParams()[2]);
				L2ScriptEngineManager.reportScriptFileError(file, e);
			}
			catch (Exception e)
			{
				_log.warning("Failed loading: " + task.getParams()[2]);
			}
		}
		else
			_log.warning("File Not Found: " + task.getParams()[2]);
	}
}