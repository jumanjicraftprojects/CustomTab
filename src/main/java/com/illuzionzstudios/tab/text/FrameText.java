/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */
package com.illuzionzstudios.tab.text;

import com.google.common.collect.Iterators;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Text that iterates through frames
 */
public class FrameText implements DynamicText {

    /**
     * Frames to go through
     */
    private final List<String> frames;

    /**
     * Iterator for frames
     */
    private final Iterator<String> cycle;

    /**
     * Current displayed frame
     */
    private String visibleText;

    /**
     * @param frames Frames of text
     */
    public FrameText(String... frames) {
        // Store our frames
        this.frames = Arrays.asList(frames);

        // Create frame cycle
        cycle = Iterators.cycle(this.frames);
    }

    public FrameText(List<String> frames) {
        // Store our frames
        this.frames = frames;

        // Create frame cycle
        cycle = Iterators.cycle(this.frames);
    }

    @Override
    public String getOriginalText() {
        // Get first element
        return frames.get(0);
    }

    @Override
    public String getVisibleText() {
        return visibleText;
    }

    @Override
    public String changeText() {
        if (cycle.hasNext()) {
            visibleText = cycle.next();
        } else {
            visibleText = getOriginalText();
        }
        return visibleText;
    }
}
