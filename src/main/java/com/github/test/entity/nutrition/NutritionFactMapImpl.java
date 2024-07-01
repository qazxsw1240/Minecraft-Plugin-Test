package com.github.test.entity.nutrition;

import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class NutritionFactMapImpl extends AbstractMap<String, Set<NutritionFact>> implements NutritionFactMap {
    private final ConcurrentMap<String, Set<NutritionFact>> nutritionFacts;

    public NutritionFactMapImpl() {
        this.nutritionFacts = new ConcurrentSkipListMap<>();
    }

    public NutritionFactMapImpl(Map<String, Set<NutritionFact>> nutritionFacts) {
        this.nutritionFacts = new ConcurrentHashMap<>(nutritionFacts);
    }

    @Override
    public int size() {
        return this.nutritionFacts.size();
    }

    @Override
    public boolean isEmpty() {
        return this.nutritionFacts.isEmpty();
    }

    @Override
    public boolean containsValue(Object value) {
        return this.nutritionFacts.containsValue(value);
    }

    @Override
    public boolean containsKey(Object key) {
        return this.nutritionFacts.containsKey(key);
    }

    @Override
    public Set<NutritionFact> get(Object key) {
        return this.nutritionFacts.get(key);
    }

    @Nullable
    @Override
    public Set<NutritionFact> put(String key, Set<NutritionFact> value) {
        return this.nutritionFacts.put(key, value);
    }

    @Override
    public Set<NutritionFact> remove(Object key) {
        return this.nutritionFacts.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Set<NutritionFact>> m) {
        this.nutritionFacts.putAll(m);
    }

    @Override
    public void clear() {
        this.nutritionFacts.clear();
    }

    @Override
    public Set<String> keySet() {
        return this.nutritionFacts.keySet();
    }

    @Override
    public Collection<Set<NutritionFact>> values() {
        return this.nutritionFacts.values();
    }

    @Override
    public Set<Entry<String, Set<NutritionFact>>> entrySet() {
        return this.nutritionFacts.entrySet();
    }
}
