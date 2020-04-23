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

import com.illuzionzstudios.scheduler.util.PresetCooldown;

/**
 * Dynamic updating text
 */
public interface DynamicText {

    /**
     * @return Default text to display
     */
    String getOriginalText();

    /**
     * @return Current frame or visible text
     */
    String getVisibleText();

    /**
     * @return Attempt to change text to next frame
     */
    String changeText();

    /**
     * @return Interval between updates
     */
    PresetCooldown getInterval();

}
