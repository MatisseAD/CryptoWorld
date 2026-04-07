package fr.jachou.cryptoworld.collectible;

public enum CollectibleKind {
    MACHINE_SKIN,
    OPERATOR_BADGE;

    public static CollectibleKind fromName(String name) {
        for (CollectibleKind value : values()) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return MACHINE_SKIN;
    }
}
