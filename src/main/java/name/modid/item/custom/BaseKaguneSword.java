package name.modid.item.custom;

import name.modid.item.ModItems;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.component.type.CustomModelDataComponent;

import java.util.List;

public abstract class BaseKaguneSword extends SwordItem {

    public BaseKaguneSword(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (hand != Hand.MAIN_HAND) return TypedActionResult.pass(user.getStackInHand(hand));

        ItemStack sword = user.getStackInHand(hand);
        KaguneStage currentStage = getStage(sword);
        int currentRc = getRcPoints(sword);

        if (!world.isClient) {
            // 1. Acumular RC
            if (currentRc < currentStage.getMaxRc()) {
                if (user.getInventory().contains(new ItemStack(ModItems.RC_CELLS))) {
                    consumirItem(user, ModItems.RC_CELLS, 1);
                    int newRc = Math.min(currentStage.getMaxRc(), currentRc + 200);
                    setRcPoints(sword, newRc);

                    world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.5F, 1.2F);
                    user.sendMessage(Text.literal("Progreso RC: " + newRc + " / " + currentStage.getMaxRc()).formatted(Formatting.DARK_RED), true);
                    return TypedActionResult.success(sword);
                }
            }
            // 2. Evolución de Fase (Dormant -> Dominant)
            else if (currentStage != currentStage.getNextStage()) {
                boolean tieneCarne = user.getOffHandStack().isOf(ModItems.INVESTIGATORS_FLESH) || user.getInventory().contains(new ItemStack(ModItems.INVESTIGATORS_FLESH));

                if (tieneCarne) {
                    if (user.getOffHandStack().isOf(ModItems.INVESTIGATORS_FLESH)) {
                        user.getOffHandStack().decrement(1);
                    } else {
                        consumirItem(user, ModItems.INVESTIGATORS_FLESH, 1);
                    }

                    KaguneStage nextStage = currentStage.getNextStage();
                    setStage(sword, nextStage);
                    setRcPoints(sword, 0);

// *** ESTA LÍNEA ES OBLIGATORIA EN 1.21.1 ***
                    user.setStackInHand(hand, sword);

                    // Efectos visuales de evolución
                    if (world instanceof ServerWorld serverWorld) {
                        DustParticleEffect redParticle = new DustParticleEffect(new Vector3f(1.0f, 0.0f, 0.0f), 2.0f);
                        serverWorld.spawnParticles(redParticle, user.getX(), user.getY() + 1.0, user.getZ(), 80, 0.6, 0.8, 0.6, 0.15);
                        serverWorld.spawnParticles(ParticleTypes.CRIMSON_SPORE, user.getX(), user.getY() + 1.0, user.getZ(), 40, 0.5, 0.5, 0.5, 0.05);
                    }

                    world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.PLAYERS, 1.0F, 0.8F);
                    user.sendMessage(Text.literal("¡Tu Kagune ha evolucionado a " + nextStage.getName() + "!").formatted(Formatting.RED), true);

                    return TypedActionResult.success(sword);
                } else {
                    user.sendMessage(Text.literal("¡RC Máximo alcanzado! Usa Carne de Investigador para hacer evolucionar la fase.").formatted(Formatting.GOLD), true);
                    return TypedActionResult.pass(sword);
                }
            } else {
                // 3. Ejecutar Habilidad Activa (Si ya está en fase máxima o según la fase)
                usarHabilidadActiva(world, user, sword, currentStage);
            }
        }

        return TypedActionResult.pass(sword);
    }

    // --- GANCHOS PARA FUTURAS HABILIDADES ---

    /** Sobrescribir para habilidades al hacer clic derecho **/
    protected void usarHabilidadActiva(World world, PlayerEntity user, ItemStack stack, KaguneStage stage) {}

    /** Sobrescribir para efectos pasivos cada tick (por ejemplo, regeneración) **/
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && selected && entity instanceof PlayerEntity player) {
            aplicarEfectosPasivos(world, player, stack, getStage(stack));
        }
    }

    protected void aplicarEfectosPasivos(World world, PlayerEntity player, ItemStack stack, KaguneStage stage) {}

    /** Sobrescribir para efectos al golpear a un enemigo **/
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player) {
            alGolpear(stack, target, player, getStage(stack));
        }
        return super.postHit(stack, target, attacker);
    }

    protected void alGolpear(ItemStack stack, LivingEntity target, PlayerEntity attacker, KaguneStage stage) {}

    // --- MANEJO DE NBT ---

    public static KaguneStage getStage(ItemStack stack) {
        NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        String stageName = nbtComponent.getNbt().getString("KaguneStage");
        try {
            return KaguneStage.valueOf(stageName);
        } catch (Exception e) {
            return KaguneStage.DORMANT; // Por defecto
        }
    }

    public static void setStage(ItemStack stack, KaguneStage stage) {
        // 1. Guardar NBT
        NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = nbtComponent.copyNbt();
        nbt.putString("KaguneStage", stage.name());
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));

        // 2. Custom Model Data (1 para Dormant, 2 para Dominant)
        int modelData = (stage == KaguneStage.DOMINANT) ? 2 : 1;
        stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(modelData));
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

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        KaguneStage stage = getStage(stack);
        int rc = getRcPoints(stack);
        int porcentaje = (rc * 100) / stage.getMaxRc();

        tooltip.add(Text.literal("Fase: " + stage.getName()).formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Células RC: " + rc + " / " + stage.getMaxRc() + " (" + porcentaje + "%)").formatted(Formatting.RED));

        if (rc >= stage.getMaxRc() && stage != stage.getNextStage()) {
            tooltip.add(Text.literal("¡Lista para evolucionar a " + stage.getNextStage().getName() + "!").formatted(Formatting.LIGHT_PURPLE));
        }

        super.appendTooltip(stack, context, tooltip, type);
    }
}