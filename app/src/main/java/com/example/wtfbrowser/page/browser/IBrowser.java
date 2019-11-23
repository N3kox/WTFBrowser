package com.example.wtfbrowser.page.browser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.wtfbrowser.entity.dao.History;
import com.example.wtfbrowser.entity.bo.TabInfo;
import com.example.wtfbrowser.page.tabpreview.TabQuickViewContract;


/**
 * 浏览器接口。负责提供导航，历史记录，下载，书签，标签页控制等管理对象
 */
public interface IBrowser {

    @NonNull
    NavController provideNavController();

    @NonNull
    HistoryController provideHistoryController();

    @NonNull
    DownloadController provideDownloadController();

    @NonNull
    BookmarkController provideBookmarkController();

    @NonNull
    TabController provideTabController();


    interface NavController {
        void goBack();

        void goForward();

        void goHome();

        void showTabs();

        void showSetting();

        void showHistory();
    }

    interface HistoryController {
        void addHistory(History entity);
    }

    interface DownloadController {

    }

    interface BookmarkController {

    }

    interface TabController extends TabQuickViewContract.Subject {
        void onTabSelected(TabInfo tabInfo);

        void onTabClose(TabInfo tabInfo);

        void onTabCreate(TabInfo tabInfo, boolean backstage);

        void onTabGoHome();

        void onTabGoForward();

        void onRestoreTabCache(TabInfo infoCopy, @Nullable Fragment fragment);

        void onCloseAllTabs();
    }
}
