package net.senti.createspaghetti;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {

    // Das Objekt, das Forge für die Registrierung benötigt
    public static final ForgeConfigSpec SPEC;

    // Beispiel-Configwerte
    public static ForgeConfigSpec.BooleanValue enableSpaghettiMode;
    public static ForgeConfigSpec.IntValue spaghettiCount;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Allgemeine Einstellungen für Create Spaghetti").push("general");

        enableSpaghettiMode = builder
                .comment("Aktiviert den Spaghetti-Spaßmodus")
                .define("enableSpaghettiMode", true);

        spaghettiCount = builder
                .comment("Wie viele Spaghetti pro Portion erzeugt werden")
                .defineInRange("spaghettiCount", 8, 1, 64);

        builder.pop();

        SPEC = builder.build();
    }
}
