package uk.co.itstherules.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Assets {
    private final Map<String, Asset> assets;

    public Assets() {
        this.assets = new ConcurrentHashMap<>();
    }

    public Assets add(Asset asset) {
        assets.put(asset.getUuid(), asset);
        return this;
    }

    public Asset get(String uuid) {
        final Asset asset = assets.get(uuid);
        if(asset == null) {
            throw new NotFoundException();
        }
        return asset;
    }

    public boolean contains(String uuid) {
        return assets.containsKey(uuid);
    }

    public void remove(String uuid) {
        assets.remove(uuid);
    }
}
