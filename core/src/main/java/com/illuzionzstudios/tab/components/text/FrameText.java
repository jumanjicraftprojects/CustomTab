/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */
package com.illuzionzstudios.tab.components.text;

import com.google.common.collect.Iterators;
import com.illuzionzstudios.mist.scheduler.timer.PresetCooldown;
import com.illuzionzstudios.tab.CustomTab;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Text that iterates through frames
 */
public class FrameText implements DynamicText {

    /**
     * List of original frames before placeholders
     */
    @Getter
    private List<String> resetPlaceholders;

    /**
     * Frames to go through
     */
    @Getter
    private List<String> frames;

    /**
     * Iterator for frames
     */
    private Iterator<String> cycle;

    /**
     * Current displayed frame
     */
    private String visibleText;

    /**
     * Interval in ticks between frame updates
     */
    @Getter
    private PresetCooldown interval;

    /**
     * @param frames Frames of text
     */
    public FrameText(int interval, String... frames) {
        this(interval, Arrays.asList(frames));
    }

    public FrameText(int interval, List<String> frames) {
        // Store our frames
        this.frames = frames;
        this.resetPlaceholders = frames;

        // Create frame cycle
        cycle = Iterators.cycle(this.frames);
        this.interval = new PresetCooldown(interval);
    }

    @Override
    public String getOriginalText() {
        // Get first element
        return frames.get(0);
    }

    @Override
    public String getVisibleText() {
        if (visibleText == null) {
            visibleText = getOriginalText();
            changeText();
        }

        return visibleText;
    }

    @Override
    public String changeText() {
        // Check changing cooldown
        if (!getInterval().isReady()) return visibleText;
        getInterval().reset();
        getInterval().go();

        if (cycle.hasNext()) {
            visibleText = cycle.next();
        } else {
            visibleText = getOriginalText();
        }
        return visibleText;
    }

    @Override
    public void setFrames(List<String> frames) {
        this.frames = frames;

        // Create frame cycle again
        this.cycle = Iterators.cycle(getFrames());
        if (this.cycle.hasNext())
        this.visibleText = cycle.next();
    }

    @Override
    public List<String> placehold(String placeholder, Object replacement) {
        List<String> newFrames = new ArrayList<>();

        // For each frame
        for (int i = 0; i < frames.size(); i++) {
            final String place = Matcher.quoteReplacement(placeholder);
            String frame = frames.get(i);
            // For each frame replace at i
            newFrames.add(frame.replaceAll("%" + place + "%|\\{" + place + "\\}", replacement == null ? "" :
                    Matcher.quoteReplacement(replacement.toString())));
        }

        setFrames(newFrames);
        return newFrames;
    }

    @Override
    public List<String> papi(Player player) {
        List<String> newFrames = new ArrayList<>();

        // Check for plugin here so we don't have to do
        // multiple checks
        if (CustomTab.isPapiEnabled()) {
            // For each frame
            for (int i = 0; i < frames.size(); i++) {
                String frame = frames.get(i);
                String newText = PlaceholderAPI.setPlaceholders(player, frame);
                // For each frame replace at i
                newFrames.add(newText);
            }
        }

        setFrames(newFrames);
        return newFrames;
    }

    @Override
    public void clearPlaceholders() {
        // Reset back to original frames
        setFrames(resetPlaceholders);
    }

}
