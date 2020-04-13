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
package net.sf.l2j.gameserver.model.item;

import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.itemcontainer.Inventory;

/**
 * @author Luno
 */
public final class ArmorSet
{
	private final int _chest;
	private final int _legs;
	private final int _head;
	private final int _gloves;
	private final int _feet;
	private final int _skillId;
	
	private final int _shield;
	private final int _shieldSkillId;
	
	private final int _enchant6Skill;
	
	public ArmorSet(int chest, int legs, int head, int gloves, int feet, int skillId, int shield, int shieldSkillId, int enchant6skill)
	{
		_chest = chest;
		_legs = legs;
		_head = head;
		_gloves = gloves;
		_feet = feet;
		_skillId = skillId;
		
		_shield = shield;
		_shieldSkillId = shieldSkillId;
		
		_enchant6Skill = enchant6skill;
	}
	
	/**
	 * Checks if player have equipped all items from set (not checking shield)
	 * @param player whose inventory is being checked
	 * @return True if player equips whole set
	 */
	public boolean containAll(L2PcInstance player)
	{
		final Inventory inv = player.getInventory();
		
		int legs = 0;
		int head = 0;
		int gloves = 0;
		int feet = 0;
		
		final ItemInstance legsItem = inv.getPaperdollItem(Inventory.PAPERDOLL_LEGS);
		if (legsItem != null)
			legs = legsItem.getItemId();
		
		if (_legs != 0 && _legs != legs)
			return false;
		
		final ItemInstance headItem = inv.getPaperdollItem(Inventory.PAPERDOLL_HEAD);
		if (headItem != null)
			head = headItem.getItemId();
		
		if (_head != 0 && _head != head)
			return false;
		
		final ItemInstance glovesItem = inv.getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
		if (glovesItem != null)
			gloves = glovesItem.getItemId();
		
		if (_gloves != 0 && _gloves != gloves)
			return false;
		
		final ItemInstance feetItem = inv.getPaperdollItem(Inventory.PAPERDOLL_FEET);
		if (feetItem != null)
			feet = feetItem.getItemId();
		
		if (_feet != 0 && _feet != feet)
			return false;
		
		return true;
	}
	
	public boolean containItem(int slot, int itemId)
	{
		switch (slot)
		{
			case Inventory.PAPERDOLL_CHEST:
				return _chest == itemId;
				
			case Inventory.PAPERDOLL_LEGS:
				return _legs == itemId;
				
			case Inventory.PAPERDOLL_HEAD:
				return _head == itemId;
				
			case Inventory.PAPERDOLL_GLOVES:
				return _gloves == itemId;
				
			case Inventory.PAPERDOLL_FEET:
				return _feet == itemId;
				
			default:
				return false;
		}
	}
	
	public int getSkillId()
	{
		return _skillId;
	}
	
	public boolean containShield(L2PcInstance player)
	{
		final ItemInstance shieldItem = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
		if (shieldItem != null && shieldItem.getItemId() == _shield)
			return true;
		
		return false;
	}
	
	public boolean containShield(int shieldId)
	{
		if (_shield == 0)
			return false;
		
		return _shield == shieldId;
	}
	
	public int getShieldSkillId()
	{
		return _shieldSkillId;
	}
	
	public int getEnchant6skillId()
	{
		return _enchant6Skill;
	}
	
	/**
	 * Checks if all parts of set are enchanted to +6 or more
	 * @param player
	 * @return
	 */
	public boolean isEnchanted6(L2PcInstance player)
	{
		final Inventory inv = player.getInventory();
		
		final ItemInstance chestItem = inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST);
		if (chestItem.getEnchantLevel() < 6)
			return false;
		
		int legs = 0;
		int head = 0;
		int gloves = 0;
		int feet = 0;
		
		final ItemInstance legsItem = inv.getPaperdollItem(Inventory.PAPERDOLL_LEGS);
		if (legsItem != null && legsItem.getEnchantLevel() > 5)
			legs = legsItem.getItemId();
		
		if (_legs != 0 && _legs != legs)
			return false;
		
		final ItemInstance headItem = inv.getPaperdollItem(Inventory.PAPERDOLL_HEAD);
		if (headItem != null && headItem.getEnchantLevel() > 5)
			head = headItem.getItemId();
		
		if (_head != 0 && _head != head)
			return false;
		
		final ItemInstance glovesItem = inv.getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
		if (glovesItem != null && glovesItem.getEnchantLevel() > 5)
			gloves = glovesItem.getItemId();
		
		if (_gloves != 0 && _gloves != gloves)
			return false;
		
		final ItemInstance feetItem = inv.getPaperdollItem(Inventory.PAPERDOLL_FEET);
		if (feetItem != null && feetItem.getEnchantLevel() > 5)
			feet = feetItem.getItemId();
		
		if (_feet != 0 && _feet != feet)
			return false;
		
		return true;
	}
	
	/**
	 * @return chest, legs, gloves, feet, head
	 */
	public int[] getSetItemsId()
	{
		return new int[]
		{
			_chest,
			_legs,
			_gloves,
			_feet,
			_head
		};
	}
	
	/**
	 * @return shield id
	 */
	public int getShield()
	{
		return _shield;
	}
}