/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */
package com.illuzionzstudios.tab.components.loader;

import com.illuzionzstudios.config.ConfigSection;
import com.illuzionzstudios.tab.components.text.DynamicText;
import com.illuzionzstudios.tab.components.text.FrameText;
import com.illuzionzstudios.tab.components.text.ScrollableText;
import lombok.Getter;

import java.util.List;

/**
 * Created instance of a group loader.
 * What this class does is when instantiated, it
 * loads all information from a configuration section
 * then stores it.
 */
public class GroupLoader implements Loader {

    /**
     * Permission required for group
     */
    @Getter
    public String permission;

    /**
     * Weight of the group
     */
    @Getter
    public int weight;

    /**
     * The formatting for each element
     */
    @Getter
    private DynamicText elementText;

    public GroupLoader(ConfigSection section) {
        permission = section.getString("Permission");
        weight = section.getInt("Weight");

        // Add to elements
        List<String> frames = section.getStringList("Display.Animations");
        elementText = section.getBoolean("Display.Scroll.Enabled") ?
                new ScrollableText(section.getInt("Display.Scroll.Interval"), frames.get(0)) :
                new FrameText(section.getInt("Display.Interval"), frames);
    }

}
