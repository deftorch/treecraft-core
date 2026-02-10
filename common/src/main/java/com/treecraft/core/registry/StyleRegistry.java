package com.treecraft.core.registry;

import com.treecraft.core.Constants;
import com.treecraft.core.api.BlockStyle;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StyleRegistry {
    private static final Map<ResourceLocation, BlockStyle> STYLES = new ConcurrentHashMap<>();
    private static final Map<String, Set<ResourceLocation>> STYLES_BY_MOD = new ConcurrentHashMap<>();
    private static final List<Consumer<BlockStyle>> LISTENERS = Collections.synchronizedList(new ArrayList<>());

    public static void initialize() {
        Constants.LOG.info("Initializing Style Registry...");
    }

    public static void registerListener(Consumer<BlockStyle> listener) {
        LISTENERS.add(listener);
    }

    public static void register(BlockStyle style) {
        ResourceLocation id = style.getId();

        if (STYLES.containsKey(id)) {
            Constants.LOG.warn("Style {} already registered, overwriting", id);
        }

        STYLES.put(id, style);

        String modId = id.getNamespace();
        STYLES_BY_MOD.computeIfAbsent(modId, k -> new HashSet<>()).add(id);

        synchronized (LISTENERS) {
            for (Consumer<BlockStyle> listener : LISTENERS) {
                try {
                    listener.accept(style);
                } catch (Exception e) {
                    Constants.LOG.error("Error in style registration listener", e);
                }
            }
        }

        Constants.LOG.debug("Registered style: {}", id);
    }

    public static Optional<BlockStyle> getStyle(ResourceLocation id) {
        return Optional.ofNullable(STYLES.get(id));
    }

    public static Collection<BlockStyle> getAllStyles() {
        return Collections.unmodifiableCollection(STYLES.values());
    }

    public static List<BlockStyle> getStylesByMod(String modId) {
        Set<ResourceLocation> styleIds = STYLES_BY_MOD.get(modId);
        if (styleIds == null) {
            return Collections.emptyList();
        }

        return styleIds.stream()
            .map(STYLES::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public static List<BlockStyle> findStyles(Predicate<BlockStyle> predicate) {
        return STYLES.values().stream()
            .filter(predicate)
            .collect(Collectors.toList());
    }

    public static boolean hasStyle(ResourceLocation id) {
        return STYLES.containsKey(id);
    }

    public static int getStyleCount() {
        return STYLES.size();
    }

    public static void clear() {
        STYLES.clear();
        STYLES_BY_MOD.clear();
        Constants.LOG.info("Style registry cleared");
    }

    public static Set<ResourceLocation> getAllStyleIds() {
        return Collections.unmodifiableSet(STYLES.keySet());
    }
}