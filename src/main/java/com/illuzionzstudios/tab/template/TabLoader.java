/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */
package com.illuzionzstudios.tab.template;

import com.illuzionzstudios.config.ConfigSection;
import com.illuzionzstudios.tab.components.text.DynamicText;
import com.illuzionzstudios.tab.components.text.FrameText;
import com.illuzionzstudios.tab.components.text.ScrollableText;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created instance of a tab loader.
 * What this class does is when instantiated, it
 * loads all information from a configuration section
 * then stores it.
 */
public class TabLoader {

    /**
     * Display slot of the column
     */
    @Getter
    private int slot;

    /**
     * The title of the column
     */
    @Getter
    private DynamicText title;

    /**
     * The tab's elements
     */
    @Getter
    private List<DynamicText> elements = new ArrayList<>();

    /**
     * @param section Config section we're loading
     */
    public TabLoader(ConfigSection section) {
        // Load slot
        slot = section.getInt("Slot");

        // Load title
        List<String> titleFrames = section.getStringList("Title.Animations");
        title = new FrameText(section.getInt("Title.Interval"), titleFrames);

        // Load text elements
        for (ConfigSection text : section.getSections("Text")) {
            // Add to elements
            List<String> frames = text.getStringList("Animations");
            DynamicText element = text.getBoolean("Scroll.Enabled") ?
                    new ScrollableText(text.getInt("Scroll.Interval"), frames.get(0)) :
                    new FrameText(text.getInt("Interval"), frames);
            elements.add(element);
        }
    }

}
