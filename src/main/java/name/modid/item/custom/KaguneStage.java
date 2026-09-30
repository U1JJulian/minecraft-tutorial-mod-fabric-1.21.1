package name.modid.item.custom;

public enum KaguneStage {
    DORMANT("Dormant", 0, 1000),
    DOMINANT("Dominant", 1, 2500); // Puedes agregar más fases aquí a futuro

    private final String name;
    private final int level;
    private final int maxRc;

    KaguneStage(String name, int level, int maxRc) {
        this.name = name;
        this.level = level;
        this.maxRc = maxRc;
    }

    public String getName() { return name; }
    public int getLevel() { return level; }
    public int getMaxRc() { return maxRc; }

    public KaguneStage getNextStage() {
        KaguneStage[] stages = values();
        if (this.ordinal() + 1 < stages.length) {
            return stages[this.ordinal() + 1];
        }
        return this; // Última fase alcanzada
    }
}