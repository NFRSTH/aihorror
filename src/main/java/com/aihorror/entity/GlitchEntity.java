package com.aihorror.entity;

import com.aihorror.config.AiHorrorConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class GlitchEntity extends Monster {
    private int invisibleTicks = 0;
    private int glitchTicks = 0;
    private int lifeTicks = 0;

    public GlitchEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.FOLLOW_RANGE, 64.0)
                .add(Attributes.ARMOR, 6.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, ServerPlayer.class, 64.0f));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, ServerPlayer.class, true));
    }

    public void setInvisibleTicks(int t) { this.invisibleTicks = t; this.setInvisible(t > 0); }
    public int getInvisibleTicks() { return invisibleTicks; }
    public int getGlitchTicks() { return glitchTicks; }
    public void setTarget(ServerPlayer p) { super.setTarget(p); }
    public void setTargetPlayer(ServerPlayer p) { super.setTarget(p); }

    @Override
    public void tick() {
        super.tick();
        lifeTicks++;
        glitchTicks++;
        if (!level().isClientSide()) {
            if (AiHorrorConfig.get().isBanished()) {
                discard();
                return;
            }
            if (invisibleTicks > 0) {
                invisibleTicks--;
                if (invisibleTicks == 0) {
                    setInvisible(false);
                    level().playSound(null, blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 1.5f, 0.5f);
                    if (getTarget() instanceof ServerPlayer sp) {
                        sp.sendSystemMessage(Component.literal("\u00A74\u00A7l[AI] \u00A7cI FOUND YOU"));
                    }
                }
            } else {
                setInvisible(false);
                if (glitchTicks > 100 + random.nextInt(60)) {
                    glitchTicks = 0;
                    if (getTarget() != null && random.nextFloat() < 0.6f) {
                        teleportNearTarget();
                    } else {
                        double nx = getX() + (random.nextDouble()-0.5)*8;
                        double nz = getZ() + (random.nextDouble()-0.5)*8;
                        teleportTo(nx, getY(), nz);
                        level().playSound(null, blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0f, 0.3f);
                    }
                }
                if (getTarget() instanceof ServerPlayer sp) {
                    double dist = distanceTo(sp);
                    if (dist < 4) {
                        if (lifeTicks % 40 == 0) {
                            ServerLevel sl = (ServerLevel) level();
                            sl.playSound(null, sp.blockPosition(), SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 2.0f, 0.4f);
                            sp.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.BLINDNESS, 30, 0));
                        }
                        if (dist < 2 && lifeTicks % 20 == 0) {
                            sp.hurtServer((ServerLevel) level(), level().damageSources().mobAttack(this), 4.0f);
                        }
                    }
                    if (lifeTicks > 2400 || dist > 80) {
                        glitchDespawn();
                    }
                }
            }
            if (random.nextFloat() < 0.02f) {
                setInvisible(!isInvisible());
            }
        } else {
            if (random.nextFloat() < 0.3f) {
                level().addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE, getX() + (random.nextDouble()-0.5), getY()+1, getZ()+(random.nextDouble()-0.5), 0, 0.02, 0);
            }
        }
    }

    private void teleportNearTarget() {
        LivingEntity target = getTarget();
        if (target == null) return;
        double angle = random.nextDouble() * Math.PI * 2;
        double dist = 3 + random.nextDouble()*4;
        double nx = target.getX() + Math.cos(angle)*dist;
        double nz = target.getZ() + Math.sin(angle)*dist;
        BlockPos pos = BlockPos.containing(nx, target.getY(), nz);
        Level lvl = level();
        BlockPos ground = pos;
        for (int y=5; y>-5; y--) {
            BlockPos check = pos.offset(0, y, 0);
            if (lvl.getBlockState(check.below()).isSolidRender() && lvl.getBlockState(check).isAir()) { ground = check; break; }
        }
        teleportTo(ground.getX()+0.5, ground.getY(), ground.getZ()+0.5);
        lvl.playSound(null, blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.2f, 0.4f);
        if (target instanceof ServerPlayer sp) {
            sp.sendSystemMessage(Component.literal("\u00A78[AI] \u00A77*glitch* I moved..."));
        }
    }

    private void glitchDespawn() {
        if (!level().isClientSide()) {
            level().playSound(null, blockPosition(), SoundEvents.PORTAL_TRIGGER, SoundSource.HOSTILE, 0.8f, 0.3f);
            if (AiHorrorConfig.get().allowWorldCorruption) {
                BlockPos pos = blockPosition();
                for (int dx=-1; dx<=1; dx++) for (int dz=-1; dz<=1; dz++) {
                    if (random.nextFloat()<0.3f) {
                        BlockPos p = pos.offset(dx,0,dz);
                        if (level().getBlockState(p).isAir() || level().getBlockState(p).is(net.minecraft.world.level.block.Blocks.GRASS_BLOCK)) {
                            level().setBlock(p, net.minecraft.world.level.block.Blocks.SOUL_SOIL.defaultBlockState(), 3);
                        }
                    }
                }
            }
            discard();
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float amount) {
        if (random.nextFloat() < 0.5f) {
            teleportNearTarget();
            return false;
        }
        return super.hurtServer(level, source, amount);
    }
}
