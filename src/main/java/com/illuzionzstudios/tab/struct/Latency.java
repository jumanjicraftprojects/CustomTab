/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */
package com.illuzionzstudios.tab.struct;

/**
 * Possible tab latency
 */
public enum Latency {

    FIVE(0),
    FOUR(250),
    THREE(500),
    TWO(750),
    ONE(1000),
    NONE(-1);

    public int ping;

    Latency(int ping) {
        this.ping = ping;
    }
}
