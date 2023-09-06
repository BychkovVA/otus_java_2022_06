package ru.otus.cachehw;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MyCache<K, V> implements HwCache<K, V> {
//Надо реализовать эти методы
    private final Map<K, V> cache;
    private final Map<HwListener<K, V>, Boolean> listeners;

    public MyCache() {
        cache = new HashMap<>();
        listeners = new HashMap<>();
    }



    @Override
    public void put(K key, V value) {
        cache.put(key, value);
        notifyListeners(key, value, EventType.PUT);
    }

    @Override
    public void remove(K key) {
        V value = cache.remove(key);
        if (value != null) {
            notifyListeners(key, value, EventType.REMOVE);
        }
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        if (!listeners.containsKey(listener)) {
            listeners.put(listener, true);
        }
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(K key, V value, EventType eventType) {
        Set<HwListener<K, V>> copy = new HashSet<>(listeners.keySet());
        for (HwListener<K, V> listener : copy) {
            listener.notify(key, value, eventType.toString());
        }
    }

}
