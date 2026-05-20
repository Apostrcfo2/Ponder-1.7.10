package net.createmod.metanip.outliner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import org.joml.Matrix4f;

import net.createmod.metanip.outliner.LineOutline.EndChasingLineOutline;
import net.createmod.metanip.outliner.Outline.OutlineParams;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class Outliner {

    private static final Outliner instance = new Outliner();
    public static Outliner getInstance() { return instance; }

    public Outliner() {}

    private final Map<Object, OutlineEntry> outlines = Collections.synchronizedMap(new HashMap<>());
    private final Map<Object, OutlineEntry> outlinesView = Collections.unmodifiableMap(outlines);

    public OutlineParams showOutline(Object slot, Outline outline) {
        outlines.put(slot, new OutlineEntry(outline));
        return outline.getParams();
    }

    public OutlineParams showLine(Object slot, Vec3 start, Vec3 end) {
        if (!outlines.containsKey(slot))
            addOutline(slot, new LineOutline());
        OutlineEntry entry = outlines.get(slot);
        entry.ticksTillRemoval = 1;
        ((LineOutline) entry.outline).set(start, end);
        return entry.outline.getParams();
    }

    public OutlineParams endChasingLine(Object slot, Vec3 start, Vec3 end, float chasingProgress, boolean lockStart) {
        if (!outlines.containsKey(slot))
            addOutline(slot, new EndChasingLineOutline(lockStart));
        OutlineEntry entry = outlines.get(slot);
        entry.ticksTillRemoval = 1;
        ((EndChasingLineOutline) entry.outline).setProgress(chasingProgress).set(start, end);
        return entry.outline.getParams();
    }

    public OutlineParams showAABB(Object slot, AxisAlignedBB bb, int ttl) {
        createAABBOutlineIfMissing(slot, bb);
        ChasingAABBOutline outline = getAndRefreshAABB(slot, ttl);
        outline.prevBB = outline.targetBB = outline.bb = bb;
        return outline.getParams();
    }

    public OutlineParams showAABB(Object slot, AxisAlignedBB bb) {
        return showAABB(slot, bb, 1);
    }

    public OutlineParams chaseAABB(Object slot, AxisAlignedBB bb) {
        createAABBOutlineIfMissing(slot, bb);
        ChasingAABBOutline outline = getAndRefreshAABB(slot);
        outline.targetBB = bb;
        return outline.getParams();
    }

    public OutlineParams showCluster(Object slot, Iterable<int[]> selection) {
        BlockClusterOutline outline = new BlockClusterOutline(selection);
        addOutline(slot, outline);
        return outline.getParams();
    }

    public OutlineParams showItem(Object slot, Vec3 pos, ItemStack stack) {
        ItemOutline outline = new ItemOutline(pos, stack);
        OutlineEntry entry = new OutlineEntry(outline);
        outlines.put(slot, entry);
        return entry.getOutline().getParams();
    }

    public void keep(Object slot) {
        if (outlines.containsKey(slot))
            outlines.get(slot).ticksTillRemoval = 1;
    }

    public void remove(Object slot) { outlines.remove(slot); }

    public Optional<OutlineParams> edit(Object slot) {
        keep(slot);
        if (outlines.containsKey(slot))
            return Optional.of(outlines.get(slot).getOutline().getParams());
        return Optional.empty();
    }

    public Map<Object, OutlineEntry> getOutlines() { return outlinesView; }

    private void addOutline(Object slot, Outline outline) {
        outlines.put(slot, new OutlineEntry(outline));
    }

    private void createAABBOutlineIfMissing(Object slot, AxisAlignedBB bb) {
        if (!outlines.containsKey(slot) || !(outlines.get(slot).outline instanceof AABBOutline))
            addOutline(slot, new ChasingAABBOutline(bb));
    }

    private ChasingAABBOutline getAndRefreshAABB(Object slot) { return getAndRefreshAABB(slot, 1); }

    private ChasingAABBOutline getAndRefreshAABB(Object slot, int ttl) {
        OutlineEntry entry = outlines.get(slot);
        entry.ticksTillRemoval = ttl;
        return (ChasingAABBOutline) entry.getOutline();
    }

    public void tickOutlines() {
        Iterator<OutlineEntry> iterator = outlines.values().iterator();
        while (iterator.hasNext()) {
            OutlineEntry entry = iterator.next();
            entry.tick();
            if (!entry.isAlive()) iterator.remove();
        }
    }

    public void renderOutlines(Matrix4f ms, Vec3 camera, float pt) {
        outlines.forEach((key, entry) -> {
            Outline outline = entry.getOutline();
            OutlineParams params = outline.getParams();
            params.alpha = 1;
            if (entry.isFading()) {
                int prevTicks = entry.ticksTillRemoval + 1;
                float fadeticks = OutlineEntry.FADE_TICKS;
                float lastAlpha = prevTicks >= 0 ? 1 : 1 + (prevTicks / fadeticks);
                float currentAlpha = 1 + (entry.ticksTillRemoval / fadeticks);
                float alpha = lastAlpha + (currentAlpha - lastAlpha) * pt;
                params.alpha = alpha * alpha * alpha;
                if (params.alpha < 1 / 8f) return;
            }
            outline.render(ms, camera, pt);
        });
    }

    public static class OutlineEntry {
        public static final int FADE_TICKS = 8;
        private final Outline outline;
        int ticksTillRemoval = 1;

        public OutlineEntry(Outline outline) { this.outline = outline; }
        public Outline getOutline() { return outline; }
        public int getTicksTillRemoval() { return ticksTillRemoval; }
        public boolean isAlive() { return ticksTillRemoval >= -FADE_TICKS; }
        public boolean isFading() { return ticksTillRemoval < 0; }
        public void tick() { ticksTillRemoval--; outline.tick(); }
    }
}
