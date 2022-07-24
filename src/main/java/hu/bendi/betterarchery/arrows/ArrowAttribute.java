package hu.bendi.betterarchery.arrows;

import net.minecraft.nbt.NbtCompound;

public class ArrowAttribute {
    private float damage;
    private float speedMultiplier;
    private float range;
    private float accuracy;

    public static final ArrowAttribute DEFAULT = new ArrowAttribute(0, 1, 1, 1);

    private ArrowAttribute(float damage, float speedMultipler, float range, float accuracy) {
        this.damage = damage;
        this.speedMultiplier = speedMultipler;
        this.range = range;
        this.accuracy = accuracy;
    }

    public float getDamage() {
        return damage;
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    public float getRange() {
        return range;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public NbtCompound toNbt() {
        var tag = new NbtCompound();

        tag.putFloat("Damage", damage);
        tag.putFloat("Speed", speedMultiplier);
        tag.putFloat("Range", range);
        tag.putFloat("Accuracy", accuracy);

        return tag;
    }

    public static ArrowAttribute fromNbt(NbtCompound tag) {
        float damage = tag.getFloat("Damage");
        float speedMultipler = tag.getFloat("Speed");
        float range = tag.getFloat("Range");
        float accuracy = tag.getFloat("Accuracy");
        return new ArrowAttribute(damage, speedMultipler, range, accuracy);
    }

    public static class Builder {
        private float damage = 0;
        private float speedMultiplier = 1;
        private float range = 0;
        private float accuracy = 1;

        private Builder() {}

        public static Builder create() {
            return new Builder();
        }

        public Builder setDamage(float damage) {
            this.damage = damage;
            return this;
        }

        public Builder setSpeedMultiplier(float speedMultipler) {
            this.speedMultiplier = speedMultipler;
            return this;
        }

        public Builder setRange(float range) {
            this.range = range;
            return this;
        }

        public Builder setAccuracy(float accuracy) {
            this.accuracy = accuracy;
            return this;
        }

        public Builder appendAttributes(ArrowAttribute attribute) {
            //Avarge every value
            this.damage += attribute.damage;
            this.accuracy += attribute.accuracy;
            this.accuracy /= 2;
            this.range += attribute.range;
            this.speedMultiplier += attribute.speedMultiplier;
            return this;
        }

        public ArrowAttribute build() {
            return new ArrowAttribute(damage, speedMultiplier, range, accuracy);
        }
    }
}
