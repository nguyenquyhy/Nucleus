/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.teleport.commands;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import io.github.nucleuspowered.nucleus.internal.command.OldCommandBase;
import io.github.nucleuspowered.nucleus.modules.teleport.handlers.TeleportHandler;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;

import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Permissions(root = "teleport")
@NoWarmup
@NoCost
@NoCooldown
@RegisterCommand({"tpaall", "tpaskall"})
@RunAsync
public class TeleportAskAllHereCommand extends OldCommandBase<Player> {

    @Inject private TeleportHandler tpHandler;

    @Override
    public CommandSpec createSpec() {
        return getSpecBuilderBase().arguments(GenericArguments.flags().flag("f").buildWith(GenericArguments.none())).build();
    }

    @Override
    public CommandResult executeCommand(Player src, CommandContext args) throws Exception {
        Sponge.getServer().getOnlinePlayers().forEach(x -> {
            if (x.equals(src)) {
                return;
            }

            TeleportHandler.TeleportBuilder tb = tpHandler.getBuilder().setFrom(x).setTo(src).setSafe(!args.<Boolean>getOne("f").orElse(false))
                    .setBypassToggle(true).setSilentSource(true);
            tpHandler.addAskQuestion(x.getUniqueId(), new TeleportHandler.TeleportPrep(Instant.now().plus(30, ChronoUnit.SECONDS), null, 0, tb));

            x.sendMessage(Util.getTextMessageWithFormat("command.tpahere.question", src.getName()));

            x.sendMessage(tpHandler.getAcceptDenyMessage());
        });

        src.sendMessage(Util.getTextMessageWithFormat("command.tpaall.success"));
        return CommandResult.success();
    }
}
