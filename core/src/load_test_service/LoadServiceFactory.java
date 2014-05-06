package load_test_service;

import load_test_service.api.LoadService;

public final class LoadServiceFactory {
    private LoadServiceFactory(){}

    public static LoadService getDefault() {
        return new LoadServiceImpl();
    }
}
