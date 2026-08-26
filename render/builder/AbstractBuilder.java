package dev.mark.system.render.builder;

public abstract class AbstractBuilder<T> {
    public AbstractBuilder() {
        this.b();
    }

    public final T a() {
        Object object = this.c();
        this.b();
        return (T)object;
    }

    protected abstract void b();

    protected abstract T c();
}
