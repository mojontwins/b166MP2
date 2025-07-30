package net.minecraft.server;

import com.mojontwins.minecraft.commands.ComplexCommand;

public class ServerCommand {
	public final ComplexCommand command;
	public final ICommandListener commandListener;

	public ServerCommand(ComplexCommand complexCommand, ICommandListener iCommandListener2) {
		this.command = complexCommand;
		this.commandListener = iCommandListener2;
	}
}
