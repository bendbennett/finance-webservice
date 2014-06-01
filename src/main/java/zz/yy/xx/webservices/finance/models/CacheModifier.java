package zz.yy.xx.webservices.finance.models;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CacheModifier {

    private CacheManager cacheManager;
    @Value("${cache.name}")
    private String cacheName;

    public CacheModifier() {
        cacheManager = CacheManager.getInstance();
    }

    public void setCacheConfiguration(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void setTimeToLive() {
        Cache cache = cacheManager.getCache(cacheName);
        CacheConfiguration cacheConfiguration = cache.getCacheConfiguration();

        Duration duration;
        long secondsUntilCacheExpires;
        DateTime currentDateTime = new DateTime();
        DateTime cacheExpiryTime = new DateTime().withTime(5, 0, 0, 0);

        duration = new Duration(currentDateTime, cacheExpiryTime);
        secondsUntilCacheExpires = (duration.getMillis() / 1000) + 1;

        if (secondsUntilCacheExpires < 0) {
            duration = new Duration(currentDateTime, cacheExpiryTime.plusDays(1));
            secondsUntilCacheExpires = (duration.getMillis() / 1000) + 1;
        }

        cacheConfiguration.setTimeToLiveSeconds(secondsUntilCacheExpires);
    }
}
