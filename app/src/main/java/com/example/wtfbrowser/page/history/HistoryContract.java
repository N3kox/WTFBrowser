package com.example.wtfbrowser.page.history;


import com.example.wtfbrowser.entity.dao.History;

import java.util.List;

public interface HistoryContract {

    interface View {
        void showHistory(List<History> result);
        void showEmptyResult();
    }

    interface Presenter {
        void getHistory(int pageNo, int pageSize);
        void onDestroy();
    }
}
