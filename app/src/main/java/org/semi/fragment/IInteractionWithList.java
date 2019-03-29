package org.semi.fragment;

public interface IInteractionWithList<T> {
    public void onItemClick(T obj);
    public void onScrollToLimit(T obj, int position);
}
