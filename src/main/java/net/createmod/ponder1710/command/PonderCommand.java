package net.createmod.ponder1710.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.createmod.ponder1710.foundation.PonderIndex;
import net.createmod.ponder1710.foundation.ui.PonderUI;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

// 1.7.10 command using CommandBase instead of Brigadier
public class PonderCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "ponder";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/ponder <item_id> - Open Ponder UI for an item";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0; // Available to all players
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayerMP player))
            throw new CommandException("commands.generic.noPermission");

        if (args.length < 1)
            throw new CommandException("Usage: " + getCommandUsage(sender));

        String idStr = args[0];
        ResourceLocation id = new ResourceLocation(idStr);

        if (!PonderIndex.getSceneAccess().doScenesExistForId(id))
            throw new CommandException("No Ponder scenes found for: " + idStr);

        // Open on client side via packet
        net.createmod.ponder1710.foundation.SimplePonderActions.openPonderForId(player, id);
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> options = new ArrayList<>();
            PonderIndex.getSceneAccess().getRegisteredEntries().forEach(entry ->
                options.add(entry.getKey().toString()));
            return getListOfStringsMatchingLastWord(args, options.toArray(new String[0]));
        }
        return new ArrayList<>();
    }
}
