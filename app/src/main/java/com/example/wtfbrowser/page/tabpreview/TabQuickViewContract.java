package com.example.wtfbrowser.page.tabpreview;

import com.example.wtfbrowser.entity.bo.TabInfo;

import java.util.List;

public interface TabQuickViewContract {

    interface Subject {
        void attach(Observer observer);

        List<TabInfo> provideInfoList();

        void updateTabInfo(TabInfo info);
    }

    interface Observer {
        void updateQuickView();
    }
}
