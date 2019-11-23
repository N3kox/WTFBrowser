package com.example.wtfbrowser;
import android.app.Application;
import com.example.wtfbrowser.entity.dao.DaoMaster;
import com.example.wtfbrowser.entity.dao.DaoSession;
import com.example.wtfbrowser.entity.dao.WebSite;
import org.greenrobot.greendao.database.Database;

public class EasyApplication extends Application {

    DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        initDB();
    }

    private void initDB() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "browser-db", null);
        Database db = helper.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();

        long defCount = daoSession.getWebSiteDao().count();
        if (defCount != 0) {
            return;
        }
        try {
            WebSite baidu = new WebSite(null, "Baidu", "www.baidu.com");
            WebSite github = new WebSite(null, "GitHub", "www.github.com");
            WebSite stackOverFlow = new WebSite(null, "SOF", "www.stackoverflow.com");
            WebSite bing = new WebSite(null, "Bing", "bing.com");
            WebSite v2ex = new WebSite(null, "V2ex", "v2ex.com");
            WebSite juejin = new WebSite(null, "掘金", "juejin.im");
            WebSite w36kr = new WebSite(null, "36Kr", "36kr.com");
            WebSite zhihu = new WebSite(null,"知乎","www.zhihu.com");
            daoSession.getWebSiteDao().insert(baidu);
            daoSession.getWebSiteDao().insert(github);
            daoSession.getWebSiteDao().insert(stackOverFlow);
            daoSession.getWebSiteDao().insert(bing);
            daoSession.getWebSiteDao().insert(v2ex);
            daoSession.getWebSiteDao().insert(juejin);
            daoSession.getWebSiteDao().insert(w36kr);
            daoSession.getWebSiteDao().insert(zhihu);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
