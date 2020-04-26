/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */
package com.illuzionzstudios.tab.components.column.list;

/**
 * This dictates how lists are sorted
 */
public enum SortType {

    /**
     * This means the closest people will appear near the top
     */
    DISTANCE,

    /**
     * This will sort by alphabetical order based
     * on a variable. This can be passed in from placeholders
     */
    STRING_VARIABLE,

    /**
     * This will sort with the highest numbers
     * appearing at the top. Can be passed in from placeholders
     */
    NUMBER_VARIABLE,

    /**
     * This sorts by weight, with higher numbers appearing
     * at the top. Usually taken from player groups.
     */
    WEIGHT

}
