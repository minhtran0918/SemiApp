package org.semi.object;

public interface IHaveIdAndName<T> {
    public T getId();
    public void setId(T id);
    public String getName();
    public void setName(String name);
}
