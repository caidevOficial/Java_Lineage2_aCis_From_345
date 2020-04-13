/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quests.Q112_WalkOfFate;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;

public class Q112_WalkOfFate extends Quest
{
	private static final String qn = "Q112_WalkOfFate";
	
	// NPCs
	private static final int LIVINA = 30572;
	private static final int KARUDA = 32017;
	
	// Rewards
	private static final int ENCHANT_D = 956;
	
	public Q112_WalkOfFate()
	{
		super(112, qn, "Walk of Fate");
		
		addStartNpc(LIVINA);
		addTalkId(LIVINA, KARUDA);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30572-02.htm"))
		{
			st.setState(STATE_STARTED);
			st.set("cond", "1");
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("32017-02.htm"))
		{
			st.giveItems(ENCHANT_D, 1);
			st.rewardItems(57, 4665);
			st.playSound(QuestState.SOUND_FINISH);
			st.exitQuest(false);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(qn);
		String htmltext = getNoQuestMsg();
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case STATE_CREATED:
				htmltext = (player.getLevel() < 20) ? "30572-00.htm" : "30572-01.htm";
				break;
			
			case STATE_STARTED:
				switch (npc.getNpcId())
				{
					case LIVINA:
						htmltext = "30572-03.htm";
						break;
					
					case KARUDA:
						htmltext = "32017-01.htm";
						break;
				}
				break;
			
			case STATE_COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new Q112_WalkOfFate();
	}
}