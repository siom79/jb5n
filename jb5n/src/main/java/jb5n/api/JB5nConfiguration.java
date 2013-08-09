package jb5n.api;

public class JB5nConfiguration {
    private boolean raiseExceptionForMissingResource = false;
    private boolean cacheMessageResources = true;

    public boolean isRaiseExceptionForMissingResource() {
        return raiseExceptionForMissingResource;
    }

    public void setRaiseExceptionForMissingResource(boolean raiseExceptionForMissingResource) {
        this.raiseExceptionForMissingResource = raiseExceptionForMissingResource;
    }

    public boolean isCacheMessageResources() {
        return cacheMessageResources;
    }

    public void setCacheMessageResources(boolean cacheMessageResources) {
        this.cacheMessageResources = cacheMessageResources;
    }
}
