package com.example.wtfbrowser.web;

import com.example.wtfbrowser.entity.bo.TabInfo;


public interface IWebView {

    void setOnWebInteractListener(OnWebInteractListener listener);

    void loadUrl(String url);

    void goBack();

    boolean canGoBack();

    void goForward();

    boolean canGoForward();

    void releaseSession();

    void onDestroy();


    interface OnWebInteractListener {
        void onPageTitleChange(TabInfo tabInfo);
    }
}
