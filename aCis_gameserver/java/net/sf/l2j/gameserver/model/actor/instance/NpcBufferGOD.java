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
package net.sf.l2j.gameserver.model.actor.instance;

/**
 * @author CaiDev
 *
 */
/**
 * @autor: fissban
 */
 
import java.util.Collection;
import net.sf.l2j.gameserver.ai.AbstractAI;
import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.L2Summon;
import net.sf.l2j.gameserver.model.holder.SkillHolder;
 
//import ai.npc.AbstractNpcAI;
 
//import com.l2jserver.gameserver.ThreadPoolManager;
//import com.l2jserver.gameserver.datatables.SkillTable;
//import com.l2jserver.gameserver.model.Location;
//import com.l2jserver.gameserver.model.actor.L2Npc;
//import com.l2jserver.gameserver.model.actor.L2Summon;
//import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
//import com.l2jserver.gameserver.model.base.ClassId;
//import com.l2jserver.gameserver.model.holders.SkillHolder;
//import com.l2jserver.gameserver.model.skills.L2Skill;
 
/*public class NpcBufferGOD extends AbstractNpcAI
{
    // NPC
    private static final int Newbie_Helper = 32327;
    // Spawn state
    private static boolean SPAWNED = false;
   
    static final Location[] SPAWNS =
    {
        // Sacado de la DB
        new Location(17136, 144896, -3008),//, 26624),
        new Location(43556, -47608, -792),//, 36864),
        new Location(82385, 53283, -1488),// 16384),
        new Location(82879, 149380, -3469),// 34120),
        new Location(87152, -141328, -1336),// 49296),
        new Location(111168, 221008, -3544),// 0),
        new Location(116935, 77258, -2688),// 40960),
        new Location(147099, 25939, -2008),// 49151),
        new Location(148077, -55367, -2728),// 32768),
        new Location(-84081, 243227, -3728),// 9000),
        new Location(115632, -177996, -896),// 32768),
        new Location(-45032, -113598, -192),// 32768),
        new Location(12111, 16686, -4584),// 63240),
        new Location(45475, 48359, -3056),// 49152),
        new Location(-119692, 44504, 360), //33324),
        new Location(-13920, 121977, -2984),// 33000),
        new Location(-83123, 150868, -3120),// 0),
    };
   
    protected NpcBufferGOD(String name, String descr)
    {
        super(name, descr);
       
        addStartNpc(Newbie_Helper);
        addSpawnId(Newbie_Helper);
        addTalkId(Newbie_Helper);
       
        if (!SPAWNED)
        {
            for (Location spawn : SPAWNS)
            {
                addSpawn(Newbie_Helper, spawn, false, 0);
            }
            SPAWNED = true;
        }
    }
   
    /**
	 * @param newbieHelper
	 * @param spawn
	 * @param b
	 * @param i
	 */
/*	private void addSpawn(int newbieHelper, Location spawn, boolean b, int i)
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param newbieHelper
	 */
/*	private void addTalkId(int newbieHelper)
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param newbieHelper
	 */
/*	private void addSpawnId(int newbieHelper)
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param newbieHelper
	 */
/*	private void addStartNpc(int newbieHelper)
	{
		// TODO Auto-generated method stub
		
	}

	public String onSpawn(L2Npc npc)
    {
        ThreadPoolManager.getInstance().scheduleGeneral(new BufferAI(npc), 5000);
        return super.onSpawn(npc);
    }
   
    public String onTalk(L2Npc npc, L2PcInstance player)
    {
        return "32327.htm";
    }
   
    protected class BufferAI implements Runnable
    {
        private final L2Npc _npc;
       
        // Skills
        private final SkillHolder HASTE_1 = new SkillHolder(4327, 1);
        private final SkillHolder HASTE_2 = new SkillHolder(5632, 1);
        private final SkillHolder CUBIC = new SkillHolder(4338, 1);
        private final SkillHolder[] FIGHTER_BUFFS =
        {
            new SkillHolder(4322, 1), // Wind Walk
            new SkillHolder(4323, 1), // Shield
            new SkillHolder(5637, 1), // Magic Barrier
            new SkillHolder(4324, 1), // Bless the Body
            new SkillHolder(4325, 1), // Vampiric Rage
            new SkillHolder(4326, 1), // Regeneration
        };
        private final SkillHolder[] MAGE_BUFFS =
        {
            new SkillHolder(4322, 1), // Wind Walk
            new SkillHolder(4323, 1), // Shield
            new SkillHolder(5637, 1), // Magic Barrier
            new SkillHolder(4324, 1), // Bless the Body
            new SkillHolder(4328, 1), // Bless the Soul
            new SkillHolder(4329, 1), // Acumen
            new SkillHolder(4330, 1), // Concentration
            new SkillHolder(4331, 1), // Empower
        };
        private final SkillHolder[] SUMMON_BUFFS =
        {
            new SkillHolder(4322, 1), // Wind Walk
            new SkillHolder(4323, 1), // Shield
            new SkillHolder(5637, 1), // Magic Barrier
            new SkillHolder(4324, 1), // Bless the Body
            new SkillHolder(4325, 1), // Vampiric Rage
            new SkillHolder(4326, 1), // Regeneration
            new SkillHolder(4328, 1), // Bless the Soul
            new SkillHolder(4329, 1), // Acumen
            new SkillHolder(4330, 1), // Concentration
            new SkillHolder(4331, 1), // Empower
        };
       
        protected BufferAI(L2Npc caster)
        {
            _npc = caster;
        }
       
        @Override
        public void run()
        {
            if ((_npc == null) || !_npc.isVisible())
            {
                return;
            }
           
            Collection<L2PcInstance> plrs = _npc.getKnownList().getKnownPlayers().values();
            for (L2PcInstance player : plrs)
            {
                if ((player == null) || player.isInvul() || player.isDead() || (player.getLevel() > 75) || (player.getLevel() < 6) || player.isCursedWeaponEquipped() || (player.getKarma() != 0) || !_npc.isInsideRadius(player, 500, false, false))
                {
                    continue;
                }
                // summons
                //if ((player.getSummon() != null) && player.getSummon().isServitor())
                if ((player.hasServitor()))
                    {
                    for (SkillHolder skills : SUMMON_BUFFS)
                    {
                        CastSummon(player.hasServitor(), skills.getSkill());
                    }
                    if (player.getLevel() > 40)
                    {
                        CastSummon(player.getSummon(), HASTE_2.getSkill());
                    }
                    else
                    {
                        CastSummon(player.getSummon(), HASTE_1.getSkill());
                    }
                }
                // magos
                if (player.isMageClass() && (player.getClassId() != ClassId.overlord) && (player.getClassId() != ClassId.warcryer))
                {
                    for (SkillHolder skills : MAGE_BUFFS)
                    {
                        CastPlayer(player, skills.getSkill());
                    }
                }
                // warrios
                else
                {
                    for (SkillHolder skills : FIGHTER_BUFFS)
                    {
                        CastPlayer(player, skills.getSkill());
                    }
                    if (player.getLevel() > 40)
                    {
                        CastPlayer(player, HASTE_2.getSkill());
                    }
                    else
                    {
                        CastPlayer(player, HASTE_1.getSkill());
                    }
                }
                if ((player.getLevel() >= 16) && (player.getLevel() <= 34))
                {
                    player.doSimultaneousCast(CUBIC.getSkill());
                }
            }
            ThreadPoolManager.getInstance().scheduleGeneral(this, 3000);
        }
       
        // metodo para los players
        private boolean CastPlayer(L2PcInstance player, L2Skill skill)
        {
            if (player.getFirstEffect(skill) == null)
            {
                skill.getEffects(_npc, player);
                SkillTable.getInstance().getInfo(skill.getId(), skill.getLevel()).getEffects(player, player);
                return true;
            }
            return false;
        }
       
        // metodo para los summons
        private boolean CastSummon(L2Summon summon, L2Skill skill)
        {
            if (summon.getFirstEffect(skill) == null)
            {
                skill.getEffects(_npc, summon);
                SkillTable.getInstance().getInfo(skill.getId(), skill.getLevel()).getEffects(summon, summon);
                return true;
            }
            return false;
        }
    }
   
    public static void main(String[] args)
    {
        new NpcBufferGOD(NpcBufferGOD.class.getSimpleName(), "ai/npc");
    }
   
}
*/