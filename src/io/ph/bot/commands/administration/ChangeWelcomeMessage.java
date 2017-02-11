package io.ph.bot.commands.administration;

import io.ph.bot.commands.Command;
import io.ph.bot.commands.CommandData;
import io.ph.bot.model.Guild;
import io.ph.bot.model.Permission;
import io.ph.bot.procedural.ProceduralAnnotation;
import io.ph.bot.procedural.ProceduralCommand;
import io.ph.bot.procedural.ProceduralListener;
import io.ph.bot.procedural.StepType;
import sx.blah.discord.handle.obj.IMessage;

/**
 * Change welcome message the server sends when a new person is joined
 * $user$ and $server$ are replaced with the new user and the server respectively
 * Note: this will not send if there is no welcome channel set
 * @author Paul
 *
 */
@CommandData (
		defaultSyntax = "changewelcome",
		aliases = {"welcomemessage", "changewelcomemessage"},
		permission = Permission.MANAGE_SERVER,
		description = "Change the server's welcome message. Use $user$ and $server$ to replace with the new user and the server name, respectively",
		example = "Welcome $user$ to $server$!"
		)
@ProceduralAnnotation (
		title = "Welcome message",
		steps = {"PM message to the user or broadcast it in this channel? (y/n)", 
				"What message do you want to send? Use $user$ and $server$ to replace "
				+ "with a mention and server name, respectively. Leave empty to clear welcome message"}, 
		types = {StepType.YES_NO, StepType.STRING},
		breakOut = "finish"
		)
public class ChangeWelcomeMessage extends ProceduralCommand implements Command {
	public ChangeWelcomeMessage(IMessage msg) {
		super(msg);
		super.setTitle(getTitle());
	}
	
	/**
	 * Necessary constructor to register to commandhandler
	 */
	public ChangeWelcomeMessage() {
		super(null);
	}
	
	@Override
	public void executeCommand(IMessage msg) {
		ChangeWelcomeMessage instance = new ChangeWelcomeMessage(msg);
		ProceduralListener.getInstance().addListener(msg, instance);
		instance.sendMessage(getSteps()[super.getCurrentStep()]);
	}

	@Override
	public void finish() {
		boolean pmWelcomeMessage = (boolean) super.getResponses().get(0);
		String welcomeMessage = (String) super.getResponses().get(1);
		Guild.guildMap.get(super.getStarter().getGuild().getID()).getGuildConfig().setPmWelcomeMessage(pmWelcomeMessage);
		Guild.guildMap.get(super.getStarter().getGuild().getID()).getGuildConfig().setWelcomeMessage(welcomeMessage);
		if(welcomeMessage.equals(""))
			super.sendMessage("Reset welcome message");
		else
			super.sendMessage("**Changed welcome message to**\n" + welcomeMessage);
		super.exit();
	}
}
