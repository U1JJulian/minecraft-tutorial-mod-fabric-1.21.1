package name.modid.item.custom;

import name.modid.item.ModItems;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.List;

public class UpgradableKakuSword extends SwordItem {
    public static final int MAX_RC_LEVEL_1 = 1000;

    public UpgradableKakuSword(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (hand != Hand.MAIN_HAND) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }

        ItemStack sword = user.getStackInHand(hand);
        ItemStack offhandStack = user.getOffHandStack();

        if (!world.isClient) {
            int currentRc = getRcPoints(sword);

            // 1. ACUMULAR CÉLULAS RC (MENOS DE 1000)
            if (currentRc < MAX_RC_LEVEL_1) {
                if (user.getInventory().contains(new ItemStack(ModItems.RC_CELLS))) {
                    consumirItem(user, ModItems.RC_CELLS, 1);

                    int newRc = Math.min(MAX_RC_LEVEL_1, currentRc + 200);
                    setRcPoints(sword, newRc);

                    int porcentaje = (newRc * 100) / MAX_RC_LEVEL_1;
                    world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.5F, 1.2F);
                    user.sendMessage(Text.literal("Progreso RC: " + newRc + " / " + MAX_RC_LEVEL_1 + " (" + porcentaje + "%)").formatted(Formatting.DARK_RED), true);

                    return TypedActionResult.success(sword);
                } else {
                    user.sendMessage(Text.literal("Necesitas Células RC en tu inventario. Progreso: " + currentRc + "/1000 RC").formatted(Formatting.GRAY), true);
                    return TypedActionResult.pass(sword);
                }
            }

            // 2. EVOLUCIÓN ALEATORIA (1000 RC ALCANZADOS)
            if (currentRc >= MAX_RC_LEVEL_1) {

                boolean tieneCarneOffhand = offhandStack.isOf(ModItems.INVESTIGATORS_FLESH);
                boolean tieneCarneInventario = user.getInventory().contains(new ItemStack(ModItems.INVESTIGATORS_FLESH));

                if (tieneCarneOffhand || tieneCarneInventario) {

                    if (tieneCarneOffhand) {
                        offhandStack.decrement(1);
                    } else {
                        consumirItem(user, ModItems.INVESTIGATORS_FLESH, 1);
                    }

                    int randomKagune = world.getRandom().nextInt(4);
                    ItemStack kaguneResultante;
                    String nombreKagune;

                    switch (randomKagune) {
                        case 0:
                            kaguneResultante = new ItemStack(ModItems.RINKAKU_SWORD);
                            // Inicializar la nueva espada en fase DORMANT
                            BaseKaguneSword.setStage(kaguneResultante, KaguneStage.DORMANT);
                            nombreKagune = "Rinkaku";
                            break;
                        case 1:
                            kaguneResultante = new ItemStack(ModItems.UKAKU_SWORD);
                            nombreKagune = "Ukaku";
                            break;
                        case 2:
                            kaguneResultante = new ItemStack(ModItems.BIKAKU_SWORD);
                            nombreKagune = "Bikaku";
                            break;
                        default:
                            kaguneResultante = new ItemStack(ModItems.KOUKAKU_SWORD);
                            nombreKagune = "Koukaku";
                            break;
                    }

                    user.setStackInHand(hand, kaguneResultante);

                    // --- EFECTOS VISUALES (PARTÍCULAS ROJAS) ---
                    if (world instanceof ServerWorld serverWorld) {
                        // Configurar partículas de polvo rojo (Color RGB en Vector3f: R=1.0, G=0.0, B=0.0, Escala=1.5f)
                        DustParticleEffect redParticle = new DustParticleEffect(new Vector3f(1.0f, 0.0f, 0.0f), 1.5f);

                        // Generar ráfaga circular alrededor del jugador
                        serverWorld.spawnParticles(
                                redParticle,
                                user.getX(), user.getY() + 1.0, user.getZ(), // Posición centrada en el cuerpo
                                50,                                          // Cantidad de partículas
                                0.5, 0.8, 0.5,                              // Dispersión en X, Y, Z
                                0.1                                          // Velocidad/fuerza
                        );

                        // Opcional: Generar también partículas extra como Crimson Spore para mayor profundidad
                        serverWorld.spawnParticles(
                                ParticleTypes.CRIMSON_SPORE,
                                user.getX(), user.getY() + 1.0, user.getZ(),
                                30,
                                0.6, 0.6, 0.6,
                                0.05
                        );
                    }

                    // Efectos sonoros y mensaje
                    world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.PLAYERS, 1.0F, 0.8F);
                    user.sendMessage(Text.literal("¡Despertar Ghoul! Has manifestado un Kagune tipo " + nombreKagune + ".").formatted(Formatting.RED), true);

                    return TypedActionResult.success(kaguneResultante);
                } else {
                    user.sendMessage(Text.literal("¡1000 RC alcanzados! Necesitas Carne de Investigador para despertar tu Kagune.").formatted(Formatting.GOLD), true);
                    return TypedActionResult.pass(sword);
                }
            }
        }

        return TypedActionResult.pass(sword);
    }

    private void consumirItem(PlayerEntity player, Item item, int cantidad) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isOf(item)) {
                int aRemover = Math.min(cantidad, stack.getCount());
                stack.decrement(aRemover);
                cantidad -= aRemover;
                if (cantidad <= 0) break;
            }
        }
    }

    public static int getRcPoints(ItemStack stack) {
        NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        return nbtComponent.getNbt().getInt("RcPoints");
    }

    public static void setRcPoints(ItemStack stack, int points) {
        NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = nbtComponent.copyNbt();
        nbt.putInt("RcPoints", points);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        int rc = getRcPoints(stack);
        int porcentaje = (rc * 100) / MAX_RC_LEVEL_1;

        tooltip.add(Text.literal("Nivel: 1").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Células RC: " + rc + " / " + MAX_RC_LEVEL_1 + " (" + porcentaje + "%)").formatted(Formatting.RED));

        if (rc >= MAX_RC_LEVEL_1) {
            tooltip.add(Text.literal("¡Evolución Lista! Usa Carne de Investigador.").formatted(Formatting.GOLD));
            tooltip.add(Text.literal(" • Generará un Kagune Aleatorio (Rinkaku, Ukaku, Bikaku o Koukaku)").formatted(Formatting.DARK_RED));
        } else {
            tooltip.add(Text.literal("Alimentar con Células RC para evolucionar.").formatted(Formatting.DARK_GRAY));
        }

        super.appendTooltip(stack, context, tooltip, type);
    }
}