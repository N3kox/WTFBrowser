package com.example.wtfbrowser.page.tabpreview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.wtfbrowser.entity.bo.TabInfo;
import com.example.wtfbrowser.R;
import com.example.wtfbrowser.utils.StringUtils;


public class TabQuickViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements TabQuickViewContract.Observer {

    private static final int VIEW_ADD = 100;
    private static final int VIEW_TAB = 101;

    private Context context;
    private TabQuickViewContract.Subject tabLruCache;
    private OnTabClickListener listener;

    public TabQuickViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == VIEW_ADD) {
            itemView = LayoutInflater.from(context).inflate(R.layout.layout_add_tab_item, parent, false);
            TabAddViewHolder holder = new TabAddViewHolder(itemView);
            return holder;
        } else if (viewType == VIEW_TAB) {
            itemView = LayoutInflater.from(context).inflate(R.layout.layout_tab_item, parent, false);
            TabQuickViewHolder holder = new TabQuickViewHolder(itemView);
            return holder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (tabLruCache == null || tabLruCache.provideInfoList() == null) {
            return;
        }


        if (holder instanceof TabAddViewHolder) {
            bindAddView((TabAddViewHolder) holder);
            return;
        }

        if (holder instanceof TabQuickViewHolder) {
            bindQuickView((TabQuickViewHolder) holder, position);
            return;
        }

    }

    private void bindAddView(TabAddViewHolder holder) {
        holder.addTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onAddTab();
                }
            }
        });
    }

    private void bindQuickView(TabQuickViewHolder holder, final int position) {
        final TabInfo info = tabLruCache.provideInfoList().get(position);
        holder.siteTitle.setText(info.getTitle());
        holder.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tabLruCache == null || tabLruCache.provideInfoList() == null) {
                    return;
                }
                if (!StringUtils.isEmpty(info.getTag()) && listener != null) {
                    listener.onTabClose(info);
                }

            }
        });

        holder.siteTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onTabClick(info);
                }
            }
        });
    }

    public void attachToSubject(TabQuickViewContract.Subject target) {
        tabLruCache = target;
        tabLruCache.attach(this);
    }

    @Override
    public void updateQuickView() {
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (tabLruCache == null || tabLruCache.provideInfoList() == null) {
            return 1;
        }
        return tabLruCache.provideInfoList().size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItemCount() == 1) {
            return VIEW_ADD;
        }

        if (position < (getItemCount() - 1)) {
            return VIEW_TAB;
        } else {
            return VIEW_ADD;
        }
    }

    public OnTabClickListener getListener() {
        return listener;
    }

    public void setListener(OnTabClickListener listener) {
        this.listener = listener;
    }

    static class TabQuickViewHolder extends RecyclerView.ViewHolder {

        TextView siteTitle;
        ImageView closeButton;

        public TabQuickViewHolder(@NonNull View itemView) {
            super(itemView);

            siteTitle = itemView.findViewById(R.id.item_title);
            closeButton = itemView.findViewById(R.id.item_close_button);
        }
    }

    static class TabAddViewHolder extends RecyclerView.ViewHolder {

        ImageView addTabButton;

        public TabAddViewHolder(@NonNull View itemView) {
            super(itemView);

            addTabButton = itemView.findViewById(R.id.add_tab_button);
        }
    }

    public interface OnTabClickListener {

        void onTabClick(TabInfo tag);

        void onTabClose(TabInfo tag);

        void onAddTab();
    }
}
