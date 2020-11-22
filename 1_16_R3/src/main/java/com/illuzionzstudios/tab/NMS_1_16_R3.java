/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */
package com.illuzionzstudios.tab;

import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMS_1_16_R3 implements NMSHandler {

    @Override
    public void setAvatar(int x, int y, Player player, Player... players) {
        this.setAvatar(x, y, ((CraftPlayer) player).getProfile(), players);
    }

    @Override
    public void addSkin(Player player, Player... players) {
        this.addSkin(player.getUniqueId(), ((CraftPlayer) player).getProfile(), players);
    }

}
