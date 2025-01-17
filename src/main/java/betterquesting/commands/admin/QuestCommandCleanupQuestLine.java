package betterquesting.commands.admin;

import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.IQuestLine;
import betterquesting.api.questing.IQuestLineEntry;
import betterquesting.commands.QuestCommandBase;
import betterquesting.core.BetterQuesting;
import betterquesting.questing.QuestDatabase;
import betterquesting.questing.QuestLineDatabase;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import java.util.*;

public class QuestCommandCleanupQuestLine extends QuestCommandBase {

    private static final Set<ICommandSender> confirm = Collections.newSetFromMap(new WeakHashMap<>());

    @Override
    public String getCommand() {
        return "cleanup_questline";
    }

    @Override
    public String getPermissionNode() {
        return "betterquesting.command.admin.cleanup_invalid_quests";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel() {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public String getPermissionDescription() {
        return "Permission to purge quests with invalid dependencies";
    }

    @Override
    public void runCommand(MinecraftServer server, CommandBase command, ICommandSender sender, String[] args) {
        if (confirm.add(sender)) {
            sender.sendMessage(new TextComponentTranslation("betterquesting.cmd.cleanup_questline.confirm"));
            return;
        }
        int removed = 0;
        synchronized (QuestLineDatabase.INSTANCE) {
            synchronized (QuestDatabase.INSTANCE) {
                for (Map.Entry<UUID, IQuestLine> questLineDBEntry : QuestLineDatabase.INSTANCE.entrySet()) {
                    IQuestLine questLine = questLineDBEntry.getValue();
                    Set<UUID> keysToRemove = new HashSet<>();
                    for (Map.Entry<UUID, IQuestLineEntry> questLineEntryDBEntry : questLine.entrySet()) {
                        IQuest quest = QuestDatabase.INSTANCE.get(questLineEntryDBEntry.getKey());
                        if (quest == null) {
                            removed++;
                            BetterQuesting.logger.info("Removed QuestLineEntry {} in QuestLine {} pointing into nonexistent quest {}", questLineEntryDBEntry.getValue(), questLineDBEntry.getKey(), questLineEntryDBEntry.getKey());
                            keysToRemove.add(questLineEntryDBEntry.getKey());
                        }
                    }

                    keysToRemove.forEach(questLine::remove);
                }
            }
        }
        sender.sendMessage(new TextComponentTranslation("betterquesting.cmd.cleanup_questline.completed", removed));
    }
}