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
package market;

/**
 * @author CaiDev
 *
 */
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.EtcItem;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import java.util.ArrayList;
import java.util.HashMap;
 
import java.util.Collection;
import java.util.Map;
 
 
/**
 * 
 * @author Elfocrash
 *
 */
public class Market
{
public static final int MAX_LOTS = 50; 
public static final int MAX_CHAR_LOTS = 12; 
public static final int LOTS_PER_PAGE = 10;
 
public static final int[] DISALLOWED_ITEMS_FOR_BID = { 5588, 7694  }; 
public static final double MARKET_TAX = 0.1; 
 
public static final boolean SEND_MESSAGE_AFTER_TRADE = true; 
public static final boolean ALLOW_AUGMENTATED_ITEMS = true; 
public static final boolean ALLOW_ETC_ITEMS_FOR_SELL = false; 
public static final boolean ALLOW_ENCHATED_ITEMS = true; 
public static final String TRADE_MESSAGE_FORSELLER = "Your item %item% was successfully sold."; 
public static final String TRADE_MESSAGE_FORBUYER = "You have successfully bought %item%."; 
 
private int lotsCount = 0;
 
private static Map<Integer, ArrayList<Bid>> lots;
private static Map<String, Integer> prices;
 
 
private Market()
{
lots = new HashMap<>();
prices = new HashMap<>();
prices.put("Adena", 57);
prices.put("CoL", 4037);
}
 
public void addLot(int playerid, int itemObjId, int costItemId, int costItemCount, String tax)
{
NpcHtmlMessage html = new NpcHtmlMessage(1);
html.setFile("data/html/market/MarketReturnResult.htm");
L2PcInstance player = L2World.getInstance().getPlayer(playerid);
ItemInstance item = player.getInventory().getItemByObjectId(itemObjId);
MarketTaxType taxType = null;
if (tax.equalsIgnoreCase("Seller"))
taxType = MarketTaxType.SELLER;
else if (tax.equalsIgnoreCase("Buyer"))
taxType = MarketTaxType.BUYER;
if (!checkItemForMarket(item))
{
html.replace("%text%", "Sorry, that an item cannot be sold here.");
player.sendPacket(html);
return;
}
if (!prices.containsValue(costItemId))
{
html.replace("%text%", "Sorry, this currency is not supported by the market.");
player.sendPacket(html);
return;
}
if ((getBidsCount() +1) > MAX_LOTS)
{
html.replace("%text%", "Sorry, the auction is full.");
player.sendPacket(html);
return;
}
if (lots.get(player.getObjectId()) != null && (lots.get(player.getObjectId()).size() +1 > MAX_CHAR_LOTS))
{
html.replace("%text%", "Sorry, you exceeded the max quantity of goods.");
player.sendPacket(html);
return;
}
if (taxType == MarketTaxType.SELLER && (player.getInventory().getItemByItemId(costItemId) != null && player.getInventory().getItemByItemId(costItemId).getCount() < (costItemCount * MARKET_TAX)))
{
html.replace("%text%", "Sorry, you do not have enough money to pay the tax market.");
player.sendPacket(html);
return;
}
Bid biditem = new Bid(player, lotsCount++, item, costItemId, costItemCount, taxType);
if (biditem.getTaxType() == MarketTaxType.SELLER) 
player.destroyItemByItemId("Market tax", costItemId, (int)(costItemCount * MARKET_TAX), null, false);
if (lots.get(player.getObjectId()) != null)
lots.get(player.getObjectId()).add(biditem);
else
{
ArrayList<Bid> charBidItems = new ArrayList<>();
charBidItems.add(biditem);
lots.put(player.getObjectId(), charBidItems);
}
html.replace("%text%", "This product was successfully added to the market.");
player.sendPacket(html);
}
 
public void deleteLot(int charObjId, int bidId)
{
L2PcInstance player = L2World.getInstance().getPlayer(charObjId);
Bid bid = getBidById(bidId);
if (bid.getBidder().getObjectId() != player.getObjectId())
return;
if (!(lots.get(player.getObjectId()).contains(bid)))
return;
lots.get(player.getObjectId()).remove(bid);
sendResultHtml(player, "Your item was successfully removed from the market.");
}
 
public void buyLot(int buyerId, int bidId)
{
Bid bid = getBidById(bidId);
L2PcInstance seller = L2World.getInstance().getPlayer(bid.getBidder().getObjectId());
L2PcInstance buyer = L2World.getInstance().getPlayer(buyerId);
if (seller == null || buyer == null || buyer.getObjectId() == bid.getBidder().getObjectId())
{
lots.get(bid.getBidder().getObjectId()).clear();
return;
}
if (seller.getInventory().getItemByItemId(bid.getBidItem().getItemId()) == null)
{
if(lots.get(seller.getObjectId()) != null)
lots.get(seller.getObjectId()).remove(bid);
return;
}
if (buyer.getInventory().getItemByItemId(bid.getCostItemId()) == null || (bid.getTaxType() == MarketTaxType.BUYER && (buyer.getInventory().getItemByItemId(bid.getCostItemId()).getCount() < (bid.getCostItemCount() + (bid.getCostItemCount() * MARKET_TAX)))) || 
(bid.getTaxType() == MarketTaxType.SELLER && (buyer.getInventory().getItemByItemId(bid.getCostItemId()).getCount() < bid.getCostItemCount())))
{
sendResultHtml(buyer, "Sorry, you do not have enough money to pay for goods.");
return;
}
ItemInstance item = seller.getInventory().getItemByObjectId(bid.getBidItem().getObjectId());
if (item == null)
return;
 
double itemcount = (bid.getTaxType() == MarketTaxType.BUYER ? (bid.getCostItemCount() + (bid.getCostItemCount() * MARKET_TAX)) : bid.getCostItemCount());
buyer.destroyItemByItemId("Market", bid.getCostItemId(), (int)itemcount, buyer, false);
seller.addItem("Market", bid.getCostItemId(), bid.getCostItemCount(), seller, false);
seller.transferItem("Market", item.getObjectId(), 1, buyer.getInventory(), seller);
if(SEND_MESSAGE_AFTER_TRADE)
{
seller.sendMessage((TRADE_MESSAGE_FORSELLER.replace("%item%", bid.getBidItem().getItemName() + " +" + bid.getBidItem().getEnchantLevel())));
buyer.sendMessage((TRADE_MESSAGE_FORBUYER.replace("%item%", bid.getBidItem().getItemName() + " +" + bid.getBidItem().getEnchantLevel())));
}
lots.get(bid.getBidder().getObjectId()).remove(bid);
}
 
public Bid getBidById(int bidId)
{
Collection<ArrayList<Bid>> collect = lots.values();
for (ArrayList<Bid> list: collect)
{
for (Bid bid: list)
{
if (bid.getBidId() == bidId)
return bid;
}
}
return null;
}
 
public ArrayList<Bid> getAllBids()
{
ArrayList<Bid> result = new ArrayList<>();
Collection<ArrayList<Bid>> collect = lots.values();
for (ArrayList<Bid> list: collect)
{
for (Bid bid: list)
{
result.add(bid);
}
}
return result;
}
 
public int getBidsCount()
{
int count = 0;
 
Collection<ArrayList<Bid>> collect = lots.values();
for (ArrayList<Bid> list: collect)
{
count += list.size();
}
return count;
}
 
public String getShortItemName(int id)
{
for (Map.Entry<String, Integer> entry: prices.entrySet())
{
if (entry.getValue() == id)
return entry.getKey();
}
return "";
}
 
public int getShortItemId(String name)
{
for (Map.Entry<String, Integer> entry: prices.entrySet())
{
if (entry.getKey().equalsIgnoreCase(name))
return entry.getValue();
}
return 0;
}
 
public String getPriceList()
{
String res = "";
Object[] str = Market.prices.keySet().toArray();
for (int i = 0;i < str.length;i++)
{
res += (String)str[i];
if (!(i == str.length-1))
{
res += ";";
}
}
return res;
}
 
public boolean isInArray(int[] arr, int item)
{
for (int i: arr)
{
if (i == item)
return true;
}
return false;
}
 
@SuppressWarnings("static-method")
private void sendResultHtml(L2PcInstance player, String text)
{
NpcHtmlMessage html = new NpcHtmlMessage(1);
html.setFile("data/html/market/MarketReturnResult.htm");
html.replace("%text%", text);
player.sendPacket(html);
}
 
public boolean checkItemForMarket(ItemInstance item)
{
if (isInArray(DISALLOWED_ITEMS_FOR_BID, item.getItemId()) || (item.isAugmented() && !ALLOW_AUGMENTATED_ITEMS) || item.isStackable()
|| ((item.getItem() instanceof EtcItem) && !ALLOW_ETC_ITEMS_FOR_SELL) || (item.getEnchantLevel() > 0 && !ALLOW_ENCHATED_ITEMS))
return false;
return true;
}
 
public Map<Integer, ArrayList<Bid>> getLots()
{
return lots;
}
 
private static Market _instance;
 
public static Market getInstance()
{
if (_instance == null)
_instance = new Market();
return _instance;
}
}
