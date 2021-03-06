package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FLog;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Enchant items.", usage = "/<command> <list | addall | max | reset | add <name> | remove <name> | god <level>>")
public class Command_enchant extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        ItemStack itemInHand = sender_p.getItemInHand();

        if (itemInHand == null)
        {
            sender.sendMessage("You are holding an invalid item.");
            return true;
        }

        if (args[0].equalsIgnoreCase("list"))
        {
            boolean has_enchantments = false;

            StringBuilder possible_ench = new StringBuilder("Possible enchantments for held item: ");
            for (Enchantment ench : Enchantment.values())
            {
                if (ench.canEnchantItem(itemInHand))
                {
                    has_enchantments = true;
                    possible_ench.append(ench.getName()).append(", ");
                }
            }

            if (has_enchantments)
            {
                msg(possible_ench.toString());
            }
            else
            {
                msg("The held item has no enchantments.");
            }
        }
        else if (args[0].equalsIgnoreCase("addall"))
        {
            for (Enchantment ench : Enchantment.values())
            {
                try
                {
                    if (ench.canEnchantItem(itemInHand))
                    {
                        itemInHand.addEnchantment(ench, ench.getMaxLevel());
                    }
                }
                catch (Exception ex)
                {
                    FLog.info("Error using " + ench.getName() + " on " + itemInHand.getType().name() + " held by " + sender_p.getName() + ".");
                }
            }

            msg("Added all possible enchantments for this item.");
        }
        else if (args[0].equalsIgnoreCase("max"))
        {
            for (Enchantment ench : Enchantment.values())
            {
                if (ench.equals(Enchantment.LOOT_BONUS_MOBS) || ench.equals(Enchantment.LOOT_BONUS_BLOCKS))
                {
                    continue;
                }
                itemInHand.addUnsafeEnchantment(ench, 32767);
            }

            sender.sendMessage(ChatColor.GREEN + "Maxed out enchants for this item.");
        }
        else if (args[0].equalsIgnoreCase("reset"))
        {
            for (Enchantment ench : itemInHand.getEnchantments().keySet())
            {
                itemInHand.removeEnchantment(ench);
            }

            msg("Removed all enchantments.");
        }
        else
        {
            if (args.length < 2)
            {
                return false;
            }
            if (args[0].equalsIgnoreCase("god"))
            {
                int level;
                try
                {
                    level = Integer.parseInt(args[1]);
                }
                catch (Exception ex)
                {
                    return false;
                }
                for (Enchantment ench : Enchantment.values())
                {
                    if (ench.equals(Enchantment.LOOT_BONUS_MOBS) || ench.equals(Enchantment.LOOT_BONUS_BLOCKS))
                    {
                        continue;
                    }
                    itemInHand.addUnsafeEnchantment(ench, level);
                    sender.sendMessage(ChatColor.GRAY + "Added god enchants for this item.");
                    return true;
                }
            }

            Enchantment ench = null;

            try
            {
                ench = Enchantment.getByName(args[1]);
            }
            catch (Exception ex)
            {
            }

            if (ench == null)
            {
                msg(args[1] + " is an invalid enchantment for the held item. Type \"/enchant list\" for valid enchantments for this item.");
                return true;
            }

            if (args[0].equalsIgnoreCase("add"))
            {
                if (ench.canEnchantItem(itemInHand))
                {
                    itemInHand.addEnchantment(ench, 10);

                    msg("Added enchantment: " + ench.getName());
                }
                else
                {
                    msg("Can't use this enchantment on held item.");
                }
            }
            else if (args[0].equals("remove"))
            {
                itemInHand.removeEnchantment(ench);

                msg("Removed enchantment: " + ench.getName());
            }
        }

        return true;
    }
}
