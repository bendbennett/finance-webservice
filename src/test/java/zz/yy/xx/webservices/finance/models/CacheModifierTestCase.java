package zz.yy.xx.webservices.finance.models;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = {"classpath:root-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class CacheModifierTestCase {

    @Autowired
    private CacheModifier cacheModifier;

    @Before
    public void setUp() {
        cacheModifier.setTimeToLive();
    }

    @Test
    public void testCacheExpirySetToFiveAm() {
        CacheManager cacheManager = CacheManager.getInstance();
        Cache repositoryCache = cacheManager.getCache("Repository");
        CacheConfiguration cacheConfiguration = repositoryCache.getCacheConfiguration();

        DateTime dateTime = new DateTime();

        int second = (int) (dateTime.getSecondOfDay() + cacheConfiguration.getTimeToLiveSeconds());

        if (second > (24 * 60 * 60)) {
            second -= (24 * 60 * 60);
        }

        //18000 == seconds between midnight and 5am
        Assert.assertEquals(18000, second);
    }
}
