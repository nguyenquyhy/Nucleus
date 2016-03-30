/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.commands;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.internal.annotations.*;
import io.github.nucleuspowered.nucleus.internal.command.OldCommandBase;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import io.github.nucleuspowered.nucleus.modules.kit.handlers.KitHandler;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.ArrayList;

/**
 * Lists all the kits.
 *
 * Command Usage: /kit list Permission: nucleus.kit.list.base
 */
@Permissions(root = "kit", suggestedLevel = SuggestedLevel.ADMIN)
@RegisterCommand(value = {"list", "ls"}, subcommandOf = KitCommand.class)
@RunAsync
@NoWarmup
@NoCooldown
@NoCost
public class KitListCommand extends OldCommandBase<CommandSource> {

    @Inject private KitHandler kitConfig;

    @Override
    public CommandSpec createSpec() {
        return getSpecBuilderBase().description(Text.of("Lists kits.")).build();
    }

    @Override
    public CommandResult executeCommand(final CommandSource src, CommandContext args) throws Exception {
        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
        ArrayList<Text> kitText = Lists.newArrayList();

        for (String kit : kitConfig.getKitNames()) {
            Text item = Text.builder(kit).onClick(TextActions.runCommand("/kit " + kit))
                    .onHover(TextActions.showText(Util.getTextMessageWithFormat("command.kit.list.text", kit))).color(TextColors.AQUA)
                    .style(TextStyles.UNDERLINE).build();
            kitText.add(item);
        }

        PaginationList.Builder paginationBuilder =
                paginationService.builder().contents(kitText).title(Util.getTextMessageWithFormat("command.kit.list.kits")).padding(Text.of(TextColors.GREEN, "-"));
        paginationBuilder.sendTo(src);

        return CommandResult.success();
    }
}
