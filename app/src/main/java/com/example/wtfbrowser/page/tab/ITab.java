package com.example.wtfbrowser.page.tab;
import com.example.wtfbrowser.entity.bo.TabInfo;

public interface ITab {
    TabInfo provideTagInfo();

    boolean onBackPressed();

    void goForward();

    void gotoHomePage();
}
