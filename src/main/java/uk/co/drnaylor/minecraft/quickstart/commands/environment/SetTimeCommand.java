package uk.co.drnaylor.minecraft.quickstart.commands.environment;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;
import uk.co.drnaylor.minecraft.quickstart.Util;
import uk.co.drnaylor.minecraft.quickstart.argumentparsers.WorldTimeParser;
import uk.co.drnaylor.minecraft.quickstart.internal.CommandBase;
import uk.co.drnaylor.minecraft.quickstart.internal.annotations.Permissions;

import java.text.MessageFormat;

@Permissions(root = "time")
public class SetTimeCommand extends CommandBase {
    private final String time = "time";
    private final String world = "world";

    @Override
    public CommandSpec createSpec() {
        return CommandSpec.builder().executor(this).description(Text.of("Sets the time")).arguments(
                GenericArguments.onlyOne(GenericArguments.optional(GenericArguments.world(Text.of(world)))),
                GenericArguments.onlyOne(new WorldTimeParser(Text.of(time)))
        ).build();
    }

    @Override
    public String[] getAliases() {
        return new String[] { "set" };
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        World w = args.<World>getOne(world).orElse(null);
        int tick = args.<Integer>getOne(time).get();
        if (w == null) {
            // Actually, we just care about where we are.
            if (src instanceof Player) {
                w = ((Player) src).getWorld();
            } else if (src instanceof CommandBlockSource) {
                w = ((CommandBlockSource) src).getWorld();
            }
        }

        WorldProperties pr;
        if (w == null) {
            src.sendMessage(Text.of(TextColors.YELLOW, Util.messageBundle.getString("command.settime.default")));
            pr = Sponge.getServer().getDefaultWorld().get();
        } else {
            pr = w.getProperties();
        }

        pr.setWorldTime(tick);
        src.sendMessage(Text.of(TextColors.YELLOW, MessageFormat.format(Util.messageBundle.getString("command.settime.done"), Util.getTimeFromTicks(tick))));
        return CommandResult.success();
    }
}