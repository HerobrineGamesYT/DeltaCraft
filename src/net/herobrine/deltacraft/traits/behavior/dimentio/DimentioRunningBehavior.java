package net.herobrine.deltacraft.traits.behavior.dimentio;

import net.citizensnpcs.api.ai.tree.Behavior;
import net.citizensnpcs.api.ai.tree.BehaviorGoalAdapter;
import net.citizensnpcs.api.ai.tree.BehaviorStatus;

public class DimentioRunningBehavior extends BehaviorGoalAdapter {
    @Override
    public void reset() {

    }

    @Override
    public BehaviorStatus run() {
        return null;
    }

    @Override
    public boolean shouldExecute() {
        return false;
    }
}
