package uk.co.itstherules.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Assets {
    private Map<String, Asset> assets;

    public Assets() {
        this.assets = new ConcurrentHashMap<String, Asset>();
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
}
