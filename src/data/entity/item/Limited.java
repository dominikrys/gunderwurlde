package data.entity.item;

public interface Limited {
	public abstract void replenish();
	public abstract void replenish(int amount);
	public abstract void empty();
	public abstract void empty(int amount);
}
