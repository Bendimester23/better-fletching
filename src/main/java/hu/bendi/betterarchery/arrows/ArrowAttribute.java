package hu.bendi.betterarchery.arrows;

import net.minecraft.nbt.NbtCompound;

//This can't be a record, because then Gson would fail
@SuppressWarnings("ClassCanBeRecord")
public class ArrowAttribute {
    public static final ArrowAttribute DEFAULT = new ArrowAttribute(0, 1, 1);
    private final float damage;
    private final float speed;
    private final float accuracy;

    private ArrowAttribute(float damage, float speed, float accuracy) {
        this.damage = damage;
        this.speed = speed;
        this.accuracy = accuracy;
    }

    public float getDamage() {
        return damage;
    }

    public static ArrowAttribute fromNbt(NbtCompound tag) {
        float damage = tag.getFloat("Damage");
        float speed = tag.getFloat("Speed");
        float accuracy = tag.getFloat("Accuracy");
        return new ArrowAttribute(damage, speed, accuracy);
    }

    public float getAccuracy() {
        return accuracy;
    }

    public float getSpeed() {
        return speed;
    }

    public NbtCompound toNbt() {
        var tag = new NbtCompound();

        tag.putFloat("Damage", damage);
        tag.putFloat("Speed", speed);
        tag.putFloat("Accuracy", accuracy);

        return tag;
    }

    public static class Builder {
        private float damage = 0;
        private float speed = 0;
        private float accuracy = 0;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder setDamage(float damage) {
            this.damage = damage;
            return this;
        }

        public Builder setSpeed(float speed) {
            this.speed = speed;
            return this;
        }

        public Builder setAccuracy(float accuracy) {
            this.accuracy = accuracy;
            return this;
        }

        public Builder appendAttributes(ArrowAttribute attribute) {
            //Average every value
            this.damage += attribute.damage;
            this.accuracy += attribute.accuracy;
            if (this.accuracy > 1) this.accuracy = 1;
            else if (this.accuracy < 0) this.accuracy = 0;
            this.speed += attribute.speed;
            return this;
        }

        public ArrowAttribute build() {
            return new ArrowAttribute(damage, speed, accuracy);
        }
    }
}
