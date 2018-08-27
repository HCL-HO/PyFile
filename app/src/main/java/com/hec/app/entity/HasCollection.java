package com.hec.app.entity;

import java.util.Collection;

public interface HasCollection<T>
{
    Collection<T> getList();
}