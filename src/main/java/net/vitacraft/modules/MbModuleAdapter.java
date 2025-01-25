package net.vitacraft.modules;

import net.vitacraft.api.PrimitiveBotEnvironment;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

public class MbModuleAdapter extends Plugin {
    private final MbModule module;

    public MbModuleAdapter(PluginWrapper wrapper, MbModule module) {
        super(wrapper);
        this.module = module;
    }

    public void preEnable(PrimitiveBotEnvironment environment) {
        module.preEnable(environment);
    }

    @Override
    public void start() {
        module.onEnable();
    }

    public void preDisable() {
        module.preDisable();
    }

    @Override
    public void stop() {
        module.onDisable();
    }
}