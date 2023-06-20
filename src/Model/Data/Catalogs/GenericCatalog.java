package Model.Data.Catalogs;

import Model.Types.Articles.Article;
import Model.Types.HasId;

import java.io.Serializable;
import java.util.*;

public abstract class GenericCatalog<T extends HasId & Serializable> implements Serializable {
    protected Map<UUID, T> catalog;

    public GenericCatalog() {
        this.catalog = new HashMap<>();
    }

    public GenericCatalog (Map<UUID, T> items) {
        this.catalog = items;
    }

    public void add (T item) {
        UUID uuid = UUID.randomUUID();
        while (this.catalog.containsKey(uuid)) {
            uuid = UUID.randomUUID();
        }
        item.setId(uuid);
        this.catalog.put(uuid, item);
    }

    public void remove (T item) {
        this.catalog.remove(item.getId());
    }

    public void remove (UUID id) {
        this.catalog.remove(id);
    }

    public T get (UUID id) {
        return this.catalog.get(id);
    }

    public boolean exists (UUID id) { return this.catalog.containsKey(id); }
    public int size () {return this.catalog.size(); }

    public Map<UUID, T> getCatalog() {
        return this.catalog;
    }

    public void setCatalog (Map<UUID, T> catalog) {
        this.catalog = catalog;
    }

    @Override
    public boolean equals (Object obj) {
        if (obj == this) return true;
        if (obj == null || (obj.getClass() != this.getClass())) return false;
        GenericCatalog ac = (GenericCatalog) obj;
        return this.getCatalog().equals(ac.getCatalog());
    }

    public ArrayList<String> getValuesAsStrings() {
        ArrayList<String> articles = new ArrayList<>();
        for (Map.Entry<UUID, T> entry : this.catalog.entrySet()) {
            T elem = entry.getValue();
            articles.add(elem.toString());
        }
        return articles;
    }
}
