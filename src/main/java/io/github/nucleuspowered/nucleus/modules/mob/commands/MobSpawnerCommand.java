/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mob.commands;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.argumentparsers.ImprovedCatalogTypeParser;
import io.github.nucleuspowered.nucleus.argumentparsers.PositiveIntegerArgument;
import io.github.nucleuspowered.nucleus.internal.annotations.Permissions;
import io.github.nucleuspowered.nucleus.internal.annotations.RegisterCommand;
import io.github.nucleuspowered.nucleus.internal.command.CommandBase;
import io.github.nucleuspowered.nucleus.internal.permissions.PermissionInformation;
import io.github.nucleuspowered.nucleus.internal.permissions.SuggestedLevel;
import org.spongepowered.api.CatalogTypes;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Permissions
@RegisterCommand("mobspawner")
public class MobSpawnerCommand extends CommandBase<CommandSource> {

    private final String playerKey = "player";
    private final String mobTypeKey = "mob";
    private final String amountKey = "amount";

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                GenericArguments.optionalWeak(GenericArguments.requiringPermission(GenericArguments.player(Text.of(playerKey)), permissions.getPermissionWithSuffix("others"))),
                new ImprovedCatalogTypeParser(Text.of(mobTypeKey), CatalogTypes.ENTITY_TYPE),
                GenericArguments.optional(new PositiveIntegerArgument(Text.of(amountKey)), 1)
        };
    }

    @Override
    protected Map<String, PermissionInformation> permissionSuffixesToRegister() {
        Map<String, PermissionInformation> m = new HashMap<>();
        m.put("others", new PermissionInformation(Util.getMessageWithFormat("permission.mobspawner.other"), SuggestedLevel.ADMIN));
        return m;
    }

    @Override
    public CommandResult executeCommand(CommandSource src, CommandContext args) throws Exception {
        Optional<Player> opl = this.getUser(Player.class, src, playerKey, args);
        if (!opl.isPresent()) {
            return CommandResult.empty();
        }

        Player player = opl.get();

        EntityType et = args.<EntityType>getOne(mobTypeKey).get();
        if (!Living.class.isAssignableFrom(et.getEntityClass())) {
            src.sendMessage(Util.getTextMessageWithFormat("args.entityparser.livingonly", et.getTranslation().get()));
            return CommandResult.empty();
        }

        int amt = args.<Integer>getOne(amountKey).orElse(1);

        ItemStack mobSpawnerStack = ItemStack.builder().itemType(ItemTypes.MOB_SPAWNER).quantity(amt).build();

        if (!mobSpawnerStack.offer(Keys.SPAWNABLE_ENTITY_TYPE, et).isSuccessful()) {
            src.sendMessage(Util.getTextMessageWithFormat("command.mobspawner.failed", et.getTranslation().get()));
            return CommandResult.empty();
        }

        InventoryTransactionResult itr = player.getInventory().offer(mobSpawnerStack);
        int given = amt;
        if (!itr.getRejectedItems().isEmpty()) {
            ItemStackSnapshot iss = itr.getRejectedItems().stream().findFirst().get();
            if (iss.getCount() == amt) {
                // Failed.
                src.sendMessage(Util.getTextMessageWithFormat("command.mobspawner.rejected"));
                return CommandResult.empty();
            }

            given = amt - iss.getCount();
        }

        if (!src.equals(player)) {
            src.sendMessage(Util.getTextMessageWithFormat("command.mobspawner.givenother", String.valueOf(given), et.getTranslation().get(), player.getName()));
        }

        player.sendMessage(Util.getTextMessageWithFormat("command.mobspawner.given", String.valueOf(given), et.getTranslation().get()));
        return CommandResult.success();
    }
}
