package net.createmod.ponder1710.foundation.registration;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

// import net.createmod.catnip.data.Couple; // TODO: catnip not available - using Object[]
// import net.minecraft.client.resources.language.I18n; // different in 1.7.10
// import net.minecraft.resources.ResourceLocation; // different package in 1.7.10

import net.createmod.ponder1710.Ponder;
import net.createmod.ponder1710.api.registration.LangRegistryAccess;
import net.createmod.ponder1710.foundation.PonderIndex;
import net.createmod.ponder1710.foundation.PonderTooltipHandler;
import net.createmod.ponder1710.foundation.ui.AbstractPonderScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class PonderLocalization implements LangRegistryAccess {

    public static final String LANG_PREFIX = "ponder.";
    public static final String UI_PREFIX = "ui.";

    public final Map<ResourceLocation, String> shared = new HashMap<>();
    // TODO: Couple from catnip not available - using String[] {title, description}
    public final Map<ResourceLocation, String[]> tag = new HashMap<>();
    public final Map<ResourceLocation, Map<String, String>> specific = new HashMap<>();

    public void clearAll() {
        shared.clear();
        tag.clear();
        specific.clear();
    }

    public void clearShared() {
        shared.clear();
    }

    public void registerShared(ResourceLocation key, String enUS) {
        shared.put(key, enUS);
    }

    public void registerTag(ResourceLocation key, String title, String description) {
        tag.put(key, new String[]{title, description});
    }

    public void registerSpecific(ResourceLocation sceneId, String key, String enUS) {
        specific.computeIfAbsent(sceneId, $ -> new HashMap<>()).put(key, enUS);
    }

    protected static String langKeyForShared(ResourceLocation k) {
        return k.getResourceDomain() + "." + LANG_PREFIX + "shared." + k.getResourcePath();
    }

    protected static String langKeyForTag(ResourceLocation k) {
        return k.getResourceDomain() + "." + LANG_PREFIX + "tag." + k.getResourcePath();
    }

    protected static String langKeyForTagDescription(ResourceLocation k) {
        return k.getResourceDomain() + "." + LANG_PREFIX + "tag." + k.getResourcePath() + ".description";
    }

    protected static String langKeyForSpecific(ResourceLocation sceneId, String k) {
        return sceneId.getResourceDomain() + "." + LANG_PREFIX + sceneId.getResourcePath() + "." + k;
    }

    @Override
    public String getShared(ResourceLocation key) {
        if (PonderIndex.editingModeActive())
            return shared.containsKey(key) ? shared.get(key) : ("unregistered shared entry: " + key);
        // I18n.get -> StatCollector.translateToLocal in 1.7.10
        return StatCollector.translateToLocal(langKeyForShared(key));
    }

    @Override
    public String getShared(ResourceLocation key, Object... params) {
        if (PonderIndex.editingModeActive())
            return shared.containsKey(key) ? String.format(shared.get(key), params) : ("unregistered shared entry: " + key);
        return StatCollector.translateToLocalFormatted(langKeyForShared(key), params);
    }

    @Override
    public String getTagName(ResourceLocation key) {
        if (PonderIndex.editingModeActive())
            return tag.containsKey(key) ? tag.get(key)[0] : ("unregistered tag entry: " + key);
        return StatCollector.translateToLocal(langKeyForTag(key));
    }

    @Override
    public String getTagDescription(ResourceLocation key) {
        if (PonderIndex.editingModeActive())
            return tag.containsKey(key) ? tag.get(key)[1] : ("unregistered tag entry: " + key);
        return StatCollector.translateToLocal(langKeyForTagDescription(key));
    }

    @Override
    public String getSpecific(ResourceLocation sceneId, String k) {
        if (PonderIndex.editingModeActive())
            try {
                return specific.get(sceneId).get(k);
            } catch (Exception e) {
                return "MISSING_SPECIFIC";
            }
        return StatCollector.translateToLocal(langKeyForSpecific(sceneId, k));
    }

    @Override
    public String getSpecific(ResourceLocation sceneId, String k, Object... params) {
        if (PonderIndex.editingModeActive())
            try {
                return String.format(specific.get(sceneId).get(k), params);
            } catch (Exception e) {
                return "MISSING_SPECIFIC";
            }
        return StatCollector.translateToLocalFormatted(langKeyForSpecific(sceneId, k), params);
    }

    private void recordGeneral(BiConsumer<String, String> consumer) {
        addGeneral(consumer, PonderTooltipHandler.HOLD_TO_PONDER, "Hold [%1$s] to Ponder");
        addGeneral(consumer, PonderTooltipHandler.SUBJECT, "Subject of this scene");
        addGeneral(consumer, AbstractPonderScreen.PONDERING, "Pondering about...");
        addGeneral(consumer, AbstractPonderScreen.PONDERING_TAG, "Pondering about...");
        addGeneral(consumer, AbstractPonderScreen.IDENTIFY_MODE, "Identify mode active.\nUnpause with [%1$s]");
        addGeneral(consumer, AbstractPonderScreen.ASSOCIATED, "Associated Entries");
        addGeneral(consumer, AbstractPonderScreen.CLOSE, "Close");
        addGeneral(consumer, AbstractPonderScreen.IDENTIFY, "Identify");
        addGeneral(consumer, AbstractPonderScreen.NEXT, "Next Scene");
        addGeneral(consumer, AbstractPonderScreen.NEXT_UP, "Up Next:");
        addGeneral(consumer, AbstractPonderScreen.PREVIOUS, "Previous Scene");
        addGeneral(consumer, AbstractPonderScreen.REPLAY, "Replay");
        addGeneral(consumer, AbstractPonderScreen.THINK_BACK, "Think Back");
        addGeneral(consumer, AbstractPonderScreen.SLOW_TEXT, "Comfy Reading");
        addGeneral(consumer, AbstractPonderScreen.EXIT, "Exit");
        addGeneral(consumer, AbstractPonderScreen.WELCOME, "Welcome to Ponder");
        addGeneral(consumer, AbstractPonderScreen.CATEGORIES, "Available Categories for %1$s");
        addGeneral(consumer, AbstractPonderScreen.DESCRIPTION, "Click one of the icons below to learn about its associated Items and Blocks");
        addGeneral(consumer, AbstractPonderScreen.INDEX_TITLE, "Ponder Index");
    }

    private void addGeneral(BiConsumer<String, String> consumer, String key, String enUS) {
        consumer.accept(Ponder.MOD_ID + "." + key, enUS);
    }

    public void generateSceneLang() {
        PonderIndex.getSceneAccess().getRegisteredEntries()
            .forEach(entry -> PonderSceneRegistry.compileScene(this, entry.getValue(), null));
    }

    @Override
    public void provideLang(String modId, BiConsumer<String, String> consumer) {
        PonderIndex.registerAll();
        PonderIndex.gatherSharedText();
        generateSceneLang();

        if (modId.equals(Ponder.MOD_ID))
            recordGeneral(consumer);

        shared.forEach((k, v) -> {
            if (k.getResourceDomain().equals(modId))
                consumer.accept(langKeyForShared(k), v);
        });

        tag.forEach((k, v) -> {
            if (k.getResourceDomain().equals(modId)) {
                consumer.accept(langKeyForTag(k), v[0]);
                consumer.accept(langKeyForTagDescription(k), v[1]);
            }
        });

        specific.entrySet().stream()
            .filter(entry -> entry.getKey().getResourceDomain().equals(modId))
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> entry.getValue().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(subEntry -> consumer.accept(
                    langKeyForSpecific(entry.getKey(), subEntry.getKey()), subEntry.getValue())));
    }
}
