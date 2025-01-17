package betterquesting.api2.client.gui.panels.lists;

import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.utils.QuestTranslation;
import betterquesting.questing.QuestDatabase;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("WeakerAccess")
public abstract class CanvasQuestDatabase extends CanvasSearch<Map.Entry<UUID, IQuest>, Map.Entry<UUID, IQuest>> {
    public CanvasQuestDatabase(IGuiRect rect) {
        super(rect);
    }

    @Override
    protected Iterator<Map.Entry<UUID, IQuest>> getIterator() {
        return QuestDatabase.INSTANCE.entrySet().iterator();
    }

    @Override
    protected void queryMatches(Map.Entry<UUID, IQuest> entry, String query, final ArrayDeque<Map.Entry<UUID, IQuest>> results) {
        if (entry.getKey().toString().contains(query) || entry.getValue().getProperty(NativeProps.NAME).toLowerCase().contains(query) || QuestTranslation.translateQuestName(entry).toLowerCase().contains(query)) {
            results.add(entry);
        }
    }
}
