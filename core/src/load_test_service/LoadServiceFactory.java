package load_test_service;

import load_test_service.api.LoadService;

public final class LoadServiceFactory {
    private LoadServiceFactory(){}

    /**
     * Create default service implementation
     * @param location path to folder with entity store
     * @return
     */
    public static LoadService getDefault(String location) {
        return new LoadServiceImpl(location);
    }
}
