/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.blacklist.commands;

import io.github.nucleuspowered.nucleus.dataservices.GeneralService;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.List;
import java.util.stream.Collectors;

@Permissions(root = "blacklist", suggestedLevel = SuggestedLevel.ADMIN)
@RegisterCommand(value = {"list", "ls"}, subcommandOf = BlacklistCommand.class)
public class BlacklistListCommand extends io.github.nucleuspowered.nucleus.internal.command.AbstractCommand<CommandSource> {

    @Override
    public CommandResult executeCommand(final CommandSource src, CommandContext args) throws Exception {
        GeneralService dataStore = this.plugin.getGeneralService();

        if (dataStore.getBlacklistedTypes().isEmpty()) {
            src.sendMessage(plugin.getMessageProvider().getTextMessageWithFormat("command.blacklist.list.none"));
            return CommandResult.empty();
        }

        Text header = plugin.getMessageProvider().getTextMessageWithFormat("blacklist.title");

        List<Text> lt =
                dataStore.getBlacklistedTypes().stream().sorted((x, y) -> x.getTranslation().get().compareTo(y.getTranslation().get())).map(x -> Text.builder()
                        .append(Text.builder(x.getTranslation().get()).color(TextColors.GREEN).style(TextStyles.UNDERLINE)
                                .onHover(TextActions.showText(plugin.getMessageProvider().getTextMessageWithFormat("blacklist.hover", x.getTranslation().get())))
                                .build())
                        .build()).collect(Collectors.toList());

        PaginationService ps = Sponge.getServiceManager().provideUnchecked(PaginationService.class);
        ps.builder().title(Text.of(TextColors.YELLOW, header)).padding(Text.of(TextColors.GREEN, "-")).contents(lt).sendTo(src);

        return CommandResult.success();
    }
}
