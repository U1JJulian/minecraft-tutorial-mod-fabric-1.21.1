package name.modid.item.custom;

import name.modid.TutorialMod;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class RinkakuSword extends BaseKaguneSword {

    public RinkakuSword(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    // Helper para leer la fase desde el CustomModelData en Fabric 1.21.1
    private KaguneStage obtenerFaseActual(ItemStack stack) {
        CustomModelDataComponent modelData = stack.get(DataComponentTypes.CUSTOM_MODEL_DATA);
        // En algunos mappings de Yarn 1.21.1, la lista de floats se accede con .floats() o .value()
        if (modelData != null) {
            // Si modelData tiene el método value() o es un record con un único float/valor:
            return (modelData.value() == 2) ? KaguneStage.DOMINANT : KaguneStage.DORMANT;
        }
        return KaguneStage.DORMANT;
    }

    // Comprueba si la mano izquierda también sostiene una RinkakuSword Dominante
    private boolean tieneDosEspadasDominantes(PlayerEntity player) {
        ItemStack offHandStack = player.getOffHandStack();
        if (offHandStack.getItem() instanceof RinkakuSword) {
            return obtenerFaseActual(offHandStack) == KaguneStage.DOMINANT;
        }
        return false;
    }

    @Override
    protected void aplicarEfectosPasivos(World world, PlayerEntity player, ItemStack stack, KaguneStage stage) {
        if (stage == KaguneStage.DORMANT) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 20, 0, false, false));
        } else if (stage == KaguneStage.DOMINANT) {
            if (tieneDosEspadasDominantes(player)) {
                // Doble empuñadura: Regeneración II, Fuerza II y Resistencia I
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 20, 1, false, false));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20, 1, false, false));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20, 0, false, false));
            } else {
                // Una sola espada: Regeneración II y Fuerza I
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 20, 1, false, false));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20, 0, false, false));
            }
        }
    }

    @Override
    protected void alGolpear(ItemStack stack, LivingEntity target, PlayerEntity attacker, KaguneStage stage) {
        if (stage == KaguneStage.DOMINANT) {
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 1));
            attacker.heal(1.5F);
        }
    }

    @Override
    protected void usarHabilidadActiva(World world, PlayerEntity user, ItemStack stack, KaguneStage stage) {
        if (stage == KaguneStage.DOMINANT) {
            if (!world.isClient) {
                world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.0F, 0.5F);

                ServerWorld serverWorld = (ServerWorld) world;

                // 2 latigazos con 1 espada / 4 latigazos con 2 espadas
                boolean esDobleEspada = tieneDosEspadasDominantes(user);
                int cantidadLatigos = esDobleEspada ? 4 : 2;

                for (int i = 0; i < cantidadLatigos; i++) {
                    final int whipIndex = i;

                    serverWorld.getServer().execute(() -> {
                        new Thread(() -> {
                            try {
                                Thread.sleep((whipIndex + 1) * 150L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            serverWorld.getServer().execute(() -> {
                                if (user.isAlive()) {
                                    dispararLatigoRojo(serverWorld, user, whipIndex);
                                }
                            });
                        }).start();
                    });
                }
            }
        }
    }

    private void dispararLatigoRojo(ServerWorld world, PlayerEntity user, int whipIndex) {
        Vec3d lookVec = user.getRotationVector();

        Vec3d waistPos = user.getPos().add(0, 0.7, 0);
        Vec3d backOffset = lookVec.multiply(-0.45);

        double offsetY = (whipIndex > 1 ? 0.35 : -0.35);
        Vec3d startPos = waistPos.add(backOffset).add(0, offsetY, 0);

        Vec3d rightVec = new Vec3d(-lookVec.z, 0, lookVec.x).normalize();

        double offsetX = (whipIndex % 2 == 0 ? 1.3 : -1.3);

        double range = 7.0;
        boolean hitAnyEntity = false;

        for (double d = 0.4; d <= range; d += 0.3) {
            double progress = d / range;
            double spreadFactor = Math.sin(progress * Math.PI) * offsetX;

            Vec3d particlePos = startPos
                    .add(lookVec.multiply(d))
                    .add(rightVec.multiply(spreadFactor));

            float grosorTentaculo = (float) (2.5 - progress * 1.5);
            DustParticleEffect redDust = new DustParticleEffect(new Vector3f(0.85f, 0.0f, 0.1f), grosorTentaculo);

            world.spawnParticles(redDust, particlePos.x, particlePos.y, particlePos.z, 2, 0.04, 0.04, 0.04, 0.0);
            world.spawnParticles(ParticleTypes.CRIMSON_SPORE, particlePos.x, particlePos.y, particlePos.z, 1, 0.02, 0.02, 0.02, 0.01);

            Box hitBox = new Box(
                    particlePos.x - 0.75, particlePos.y - 0.75, particlePos.z - 0.75,
                    particlePos.x + 0.75, particlePos.y + 0.75, particlePos.z + 0.75
            );

            for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class, hitBox, e -> e != user)) {
                if (entity.damage(world.getDamageSources().playerAttack(user), 6.0F)) {
                    entity.takeKnockback(0.4, -lookVec.x, -lookVec.z);

                    world.spawnParticles(TutorialMod.RINKAKU_SWEEP, entity.getX(), entity.getBodyY(0.5), entity.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
                    world.spawnParticles(ParticleTypes.FALLING_LAVA, entity.getX(), entity.getBodyY(0.5), entity.getZ(), 6, 0.2, 0.2, 0.2, 0.1);
                    world.playSound(null, entity.getBlockPos(), SoundEvents.ITEM_TRIDENT_HIT, SoundCategory.PLAYERS, 0.8F, 0.7F);

                    hitAnyEntity = true;
                }
            }
        }

        if (!hitAnyEntity) {
            Vec3d tipPos = startPos.add(lookVec.multiply(range)).add(rightVec.multiply(offsetX * 0.2));
            world.spawnParticles(TutorialMod.RINKAKU_SWEEP, tipPos.x, tipPos.y, tipPos.z, 1, 0.0, 0.0, 0.0, 0.0);
        }

        world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS, 0.8F, 1.3F);
    }
}