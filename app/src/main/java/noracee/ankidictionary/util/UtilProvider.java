package noracee.ankidictionary.util;

/**
 * Interface that provides all managers available
 */

public interface UtilProvider {
    interface GetUtilProvider {
        UtilProvider getUtilProvider();
    }

    AnkiManager getAnkiManager();
    HttpManager getHttpManager();
    ResourceManager getResourceManager();
    SharedPreferencesManager getSharedPreferencesManager();
    StatusManager getStatusManager();
}
