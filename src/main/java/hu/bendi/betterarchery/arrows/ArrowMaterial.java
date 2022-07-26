package hu.bendi.betterarchery.arrows;

public class ArrowMaterial {
    Part part;
    String item;
    ArrowAttribute attributes;

    public Part getPart() {
        return part;
    }

    public String getItem() {
        return item;
    }

    public ArrowAttribute getAttributes() {
        return attributes;
    }

    public enum Part {
        HEAD,
        BODY,
        TAIL
    }
}
