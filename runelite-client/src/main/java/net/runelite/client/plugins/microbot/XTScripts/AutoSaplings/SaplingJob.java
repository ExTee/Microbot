package net.runelite.client.plugins.microbot.XTScripts.AutoSaplings;

import lombok.Getter;

public class SaplingJob {
    private final String NAME;
    @Getter
    private final String SEED;
    @Getter
    private final String SEEDLING_UNWATERED;
    @Getter
    private final String SEEDLING_WATERED;
    @Getter
    private final String SAPLING;

    public SaplingJob(String name, String SEED, String SEEDLING_UNWATERED, String SEEDLING_WATERED, String SAPLING) {
        this.NAME = name;
        this.SEED = SEED;
        this.SEEDLING_UNWATERED = SEEDLING_UNWATERED;
        this.SEEDLING_WATERED = SEEDLING_WATERED;
        this.SAPLING = SAPLING;
    }

    @Override
    public String toString() {
        return NAME;
    }
}



