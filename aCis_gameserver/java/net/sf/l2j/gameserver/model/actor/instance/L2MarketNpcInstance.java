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
import market.Bid;
import market.Market;
import market.MarketTaxType;
 


import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.MyTargetSelected;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.PledgeCrest;
import net.sf.l2j.gameserver.network.serverpackets.ValidateLocation;
import net.sf.l2j.gameserver.model.item.kind.EtcItem;
import net.sf.l2j.gameserver.model.item.kind.Armor;
import net.sf.l2j.gameserver.model.item.kind.Weapon;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.model.item.type.CrystalType;

 


import java.util.StringTokenizer;
import java.io.File;
import java.util.ArrayList;
 
 
@SuppressWarnings("unused")
public class L2MarketNpcInstance extends L2NpcInstance 
{
 
/**
* @author Elfocrash
*/
 
public static final String HTML_FOLDER = "data/html/market/";
public static final String INDEX_PAGE = HTML_FOLDER + "Market.htm";
public static final String MAIN_PAGE = HTML_FOLDER + "MarketMain.htm";
public static final String BID_INFO_PAGE = HTML_FOLDER + "MarketBidInfo.htm";
public static final String ADD_PAGE = HTML_FOLDER + "MarketAdd.htm";
public static final String CAB_PAGE = HTML_FOLDER + "MarketCabinet.htm";
 
public L2MarketNpcInstance(int objectId, NpcTemplate template) {
super(objectId, template);
}
 
@Override
public void onBypassFeedback(L2PcInstance player, String command)
{
StringTokenizer st = new StringTokenizer(command, " ");
String curCommand = st.nextToken();
NpcHtmlMessage html;
if(curCommand.startsWith("marketpage"))
{
String type = st.nextToken();
if(type.startsWith("index"))
{
html = new NpcHtmlMessage(1);
html.setFile(INDEX_PAGE);
html.replace("%totalbids%", String.valueOf(Market.getInstance().getBidsCount()));
sendHtmlMessage(player, html);
}
else if(type.startsWith("main"))
{
int page = Integer.parseInt(st.nextToken());
html = new NpcHtmlMessage(1);
html.setFile(MAIN_PAGE);
html.replace("%CONTENT%", getMarketPage(page));
sendHtmlMessage(player, html);
}
else if(type.startsWith("add"))
{
html = genAddPage(player);
sendHtmlMessage(player,html);
}
else if(type.startsWith("info"))
{
html = genBidInfoPage(player, Integer.parseInt(st.nextToken()));
sendHtmlMessage(player, html);
}
else if(type.startsWith("cab"))
{
html = genCabPage(player);
sendHtmlMessage(player, html);
}
}
else if(curCommand.startsWith("marketadd"))
{
int costItemCount = Integer.parseInt(st.nextToken());
int costItemId = Market.getInstance().getShortItemId(st.nextToken());
String tax = st.nextToken();
int itemObjId = Integer.parseInt(st.nextToken());
Market.getInstance().addLot(player.getObjectId(), itemObjId, costItemId, costItemCount, tax);
}
else if(curCommand.startsWith("marketdel"))
{
Market.getInstance().deleteLot(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
}
else if(curCommand.startsWith("marketbuy"))
{
Market.getInstance().buyLot(player.getObjectId(), Integer.parseInt(st.nextToken()));
}
}
 
@Override
public void onAction(L2PcInstance player)
{
 
if(this != player.getTarget()) 
{
player.setTarget(this);
MyTargetSelected my = new MyTargetSelected(getObjectId(), 0);
player.sendPacket(my);
player.sendPacket(new ValidateLocation(this));
}
else 
{
if(!canInteract(player))
{
player.getAI().setIntention(CtrlIntention.INTERACT, this);
}
else
{
       
NpcHtmlMessage html = new NpcHtmlMessage(1);
html.setFile(INDEX_PAGE);
html.replace("%totalbids%", String.valueOf(Market.getInstance().getBidsCount()));
sendHtmlMessage(player, html);
}
}
player.sendPacket(ActionFailed.STATIC_PACKET);
}
 
private String getMarketPage(int page)
{
 
ArrayList<Bid> items = Market.getInstance().getAllBids();
StringBuilder sb = new StringBuilder();
int numlines = page * Market.LOTS_PER_PAGE + Market.LOTS_PER_PAGE; 
if(items.size() != 0)
{
sb.append("<table width=\"300\">"); 
for(int i = page * Market.LOTS_PER_PAGE;i < numlines;i++)
{
if(items.size() - i <= 0)
break;
sb.append(BidItem(items.get(i)));
sb.append("<tr><td><font color=\"808080\">Type: " + getItemType(items.get(i).getBidItem().getItem()) + ". Seller: " + items.get(i).getBidder().getName() + "</font></td></tr>");
}
sb.append("</table>");
sb.append("<br>Page:&nbsp;");
int pg = getMarketPagesCount();
for(int i = 0;i < pg;i++)
{
sb.append("<a action=\"bypass -h npc_%objectId%_marketpage main "+i+"\">"+i+ "</a>&nbsp;");
}
}
else 
{
sb.append("No item is on the market!");
}
 
return sb.toString();
}
 
private static String getAddItemsList(L2PcInstance player)
{
StringBuilder tx = new StringBuilder();
ArrayList<ItemInstance> charItems = new ArrayList<ItemInstance>();
for(ItemInstance item : player.getInventory().getItems())
{
if(Market.getInstance().checkItemForMarket(item))
charItems.add(item);
}
for(int i = 0;i < charItems.size();i++)
{
if(isAlreadyAdded(player, charItems.get(i)))
continue;
tx.append("<a action=\"bypass -h npc_%objectId%_marketadd $count $value $tax "+charItems.get(i).getObjectId()+"\">"+charItems.get(i).getItemName() + " +" + charItems.get(i).getEnchantLevel() + "</a><br>");
}
return tx.toString();
}
 
private static NpcHtmlMessage genBidInfoPage(L2PcInstance player, int bidId)
{
        
Bid bid = Market.getInstance().getBidById(bidId);
String priceName = Market.getInstance().getShortItemName(bid.getCostItemId());
NpcHtmlMessage html = new NpcHtmlMessage(1);
html.setFile(BID_INFO_PAGE);
html.replace("%itemtitle%", bid.getBidItem().getItemName());
html.replace("%itemname%", bid.getBidItem().getItemName() + " (" + getItemGrade(bid.getBidItem().getItem()) + " Grade)");
html.replace("%itemtype%", getItemType(bid.getBidItem().getItem()));
html.replace("%itemaugmentation%", bid.getBidItem().isAugmented() ? "Yes" : "No");
html.replace("%enchantlevel%", String.valueOf(bid.getBidItem().getEnchantLevel()));
html.replace("%itemprice%", bid.getCostItemCount() + " <font color=\"LEVEL\">" + priceName + "</font>");
if(bid.getTaxType() == MarketTaxType.BUYER)
{
double mtax = bid.getCostItemCount() * Market.MARKET_TAX;
html.replace("%markettax%", (int)mtax + " <font color=\"LEVEL\">" + priceName + "</font> (Full price: " + (bid.getCostItemCount() + (int)mtax) + " <font color\"LEVEL\">" + priceName + "</font>)");
}
else
{
html.replace("%markettax%", "Paid by the seller");
}
html.replace("%seller%", bid.getBidder().getName());
if(player.getObjectId() == bid.getBidder().getObjectId())
{
html.replace("%LINKS%", "<a action=\"bypass -h npc_%objectId%_marketdel " + player.getObjectId() + " " + bid.getBidId() + "\">Remove from the market</a>");
}
else
{
html.replace("%LINKS%", "<a action=\"bypass -h npc_%objectId%_marketbuy " + bid.getBidId() + "\">Purchase</a>");
}
return html;
}
 
private static NpcHtmlMessage genAddPage(L2PcInstance player)
{
        
NpcHtmlMessage html = new NpcHtmlMessage(1);
html.setFile(ADD_PAGE);
html.replace("%values%", Market.getInstance().getPriceList());
html.replace("%charitems%", getAddItemsList(player));
html.replace("%taxprocent%", ""+((int)(Market.MARKET_TAX * 100)));
 
return html;
}
 
private static NpcHtmlMessage genCabPage(L2PcInstance player)
{
        
NpcHtmlMessage html = new NpcHtmlMessage(1);
html.setFile(CAB_PAGE);
html.replace("%charname%", player.getName());
 
StringBuilder tx = new StringBuilder();
ArrayList<Bid> bids = Market.getInstance().getLots().get(player.getObjectId());
if(bids != null)
{
for(Bid bid: bids)
{
tx.append(bid.getBidItem().getItemName() + " +" + bid.getBidItem().getEnchantLevel() + "&nbsp;<a action=\"bypass -h npc_%objectId%_marketpage info " + bid.getBidId() + "\">Info</a>&nbsp;<a action=\"bypass -h npc_%objectId%_marketdel " + player.getObjectId() + " " + bid.getBidId() + "\">Remove from the market</a><br>");
}
}
else
{
tx.append("You have no items on the market.");
}
html.replace("%charitems%", tx.toString());
return html;
}
 
private static String BidItem(Bid bid)
{
        
String info = "";
info += "<tr><td><a action=\"bypass -h npc_%objectId%_marketpage info " + bid.getBidId() + "\">" + bid.getBidItem().getName() + "</a>&nbsp;" + "(" + getItemGrade(bid.getBidItem().getItem()) + ")&nbsp;+" +  bid.getBidItem().getEnchantLevel() +  "</td></tr>";
info += "<tr><td><font color=\"808080\">Price: " + bid.getCostItemCount()  + " " + Market.getInstance().getShortItemName(bid.getCostItemId()) + "</font></td></tr>";
return info;
}
 
public int getMarketPagesCount()
{
int pages = 0;
for(int allbids = Market.getInstance().getBidsCount();allbids > 0;allbids -= Market.LOTS_PER_PAGE)
{
pages++;
}
return pages;
}
 
private void sendHtmlMessage(L2PcInstance player, NpcHtmlMessage html)
{
        
html.replace("%backlink%", "<a action=\"bypass -h npc_%objectId%_marketpage index\">Back</a>");
html.replace("%objectId%", String.valueOf(getObjectId()));
html.replace("%npcId%", String.valueOf(getNpcId()));
player.sendPacket(html);
}
 
private static boolean isAlreadyAdded(L2PcInstance player, ItemInstance item)
{
ArrayList<Bid> bids = Market.getInstance().getAllBids();
for(Bid bid: bids)
{
if(bid.getBidItem().getObjectId() == item.getObjectId())
return true;
}
return false;
}
private static String getItemType(Item item)
{
if(item instanceof Weapon)
return "Weapon";
else if(item instanceof Armor)
return "Armor";
else if(item instanceof EtcItem)
return "Other";
return "";
}
private static String getItemGrade(Item item)
{
String grade = "";
switch(item.getCrystalType())
	{
		case NONE:
		grade = "None";
		break;
		case D:
		grade = "D";
		break;
		case C:
		grade = "C";
		break;
		case B:
		grade = "B";
		break;
		case A:
		grade = "A";
		break;
		case S:
		grade = "S";
		break;
	}
return "<font color=\"LEVEL\">" + grade + "</font>";
}
 
}
