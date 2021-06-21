package cinema.utils;

import java.util.HashMap;
import java.util.Map;

public class IdentityMapper {
    private Map<Class<?>, Map<Object, CachedClass>> objectsList;
    private static IdentityMapper instance;

    private IdentityMapper() {
        this.objectsList = new HashMap<>(16);
    }

    public static IdentityMapper getInstance() {
        if (instance == null) instance = new IdentityMapper();
        return instance;
    }

    public void put(CachedClass object) {
        Class<?> currentClass = object.getClass();
        Map<Object, CachedClass> currentClassMap = objectsList.get(currentClass);
        if (currentClassMap == null) currentClassMap = new HashMap<>();
        currentClassMap.put(object.getId(), object);
        objectsList.put(currentClass, currentClassMap);
    }

    public Object get(Class<?> chosenClass, Object id) {
        Map<Object, CachedClass> currentClassMap = objectsList.computeIfAbsent(chosenClass, k -> new HashMap<>());
        return currentClassMap.get(id);
    }

    public void remove(CachedClass object) {
        Class<?> currentClass = object.getClass();
        Map<Object, CachedClass> currentClassMap = objectsList.get(currentClass);
        currentClassMap.remove(object.getId());
        objectsList.put(currentClass, currentClassMap);
    }

    public void clearAll() {
        objectsList.clear();
    }

}