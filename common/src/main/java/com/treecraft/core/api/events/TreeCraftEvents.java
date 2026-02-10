package com.treecraft.core.api.events;

import com.treecraft.core.Constants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class TreeCraftEvents {
    private static final List<Consumer<TreeDetectedEvent>> TREE_DETECTED_LISTENERS = Collections.synchronizedList(new ArrayList<>());
    private static final List<Consumer<TreeStructureDetectedEvent>> STRUCTURE_DETECTED_LISTENERS = Collections.synchronizedList(new ArrayList<>());
    private static final List<Consumer<StyleRegisteredEvent>> STYLE_REGISTERED_LISTENERS = Collections.synchronizedList(new ArrayList<>());

    public static void onTreeDetected(Consumer<TreeDetectedEvent> listener) {
        TREE_DETECTED_LISTENERS.add(listener);
    }

    public static void onStructureDetected(Consumer<TreeStructureDetectedEvent> listener) {
        STRUCTURE_DETECTED_LISTENERS.add(listener);
    }

    public static void onStyleRegistered(Consumer<StyleRegisteredEvent> listener) {
        STYLE_REGISTERED_LISTENERS.add(listener);
    }

    public static void post(TreeDetectedEvent event) {
        synchronized(TREE_DETECTED_LISTENERS) {
            TREE_DETECTED_LISTENERS.forEach(l -> {
                try {
                    l.accept(event);
                } catch (Exception e) {
                    Constants.LOG.error("Error in TreeDetectedEvent listener", e);
                }
            });
        }
    }

    public static void post(TreeStructureDetectedEvent event) {
        synchronized(STRUCTURE_DETECTED_LISTENERS) {
            STRUCTURE_DETECTED_LISTENERS.forEach(l -> {
                try {
                    l.accept(event);
                } catch (Exception e) {
                    Constants.LOG.error("Error in TreeStructureDetectedEvent listener", e);
                }
            });
        }
    }

    public static void post(StyleRegisteredEvent event) {
        synchronized(STYLE_REGISTERED_LISTENERS) {
            STYLE_REGISTERED_LISTENERS.forEach(l -> {
                try {
                    l.accept(event);
                } catch (Exception e) {
                    Constants.LOG.error("Error in StyleRegisteredEvent listener", e);
                }
            });
        }
    }
}