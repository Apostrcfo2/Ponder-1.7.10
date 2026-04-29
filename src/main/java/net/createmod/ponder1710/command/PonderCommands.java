package net.createmod.ponder1710.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;

import net.minecraft.commands.CommandSourceStack;

public class PonderCommands {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		CommandNode<CommandSourceStack> ponderRoot = PonderCommand.register().build();

		dispatcher.getRoot().addChild(ponderRoot);
	}

}
