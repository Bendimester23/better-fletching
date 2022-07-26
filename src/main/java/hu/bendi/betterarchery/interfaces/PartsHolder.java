package hu.bendi.betterarchery.interfaces;

import net.minecraft.nbt.NbtCompound;

public interface PartsHolder {
    Parts getParts();

    void setParts(Parts parts);

    class Parts {
        public String head;
        public String body;
        public String tail;

        public static PartsHolder.Parts fromNbt(NbtCompound tag) {
            var self = new Parts();
            self.head = tag.getString("Head");
            self.body = tag.getString("Body");
            self.tail = tag.getString("Tail");
            return self;
        }

        public NbtCompound toNbt() {
            var tag = new NbtCompound();
            tag.putString("Head", head);
            tag.putString("Body", body);
            tag.putString("Tail", tail);

            return tag;
        }
    }
}
