package com.example.wtfbrowser.web.webkit;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.ContentLoadingProgressBar;

import com.example.wtfbrowser.R;
import com.example.wtfbrowser.common.TabConst;
import com.example.wtfbrowser.entity.bo.TabInfo;
import com.example.wtfbrowser.entity.dao.History;
import com.example.wtfbrowser.page.browser.IBrowser;
import com.example.wtfbrowser.page.setting.Settings;
import com.example.wtfbrowser.utils.EasyLog;
import com.example.wtfbrowser.utils.SharedPreferencesUtils;
import com.example.wtfbrowser.utils.StringUtils;
import com.example.wtfbrowser.utils.TabHelper;
import com.example.wtfbrowser.web.IWebView;
import com.example.wtfbrowser.web.widget.BrowserNavBar;

import java.io.InputStream;
import java.net.URLEncoder;

public class PageNestedWebView extends LinearLayout implements IWebView {

    private EasyNestedWebView webView;
    private ImageView goButton;
    private EditText webAddress;
    private ContentLoadingProgressBar progressBar;
    private BrowserNavBar browserNavBar;

    private OnWebInteractListener onWebInteractListener;

    private Context mContext;

    private boolean noPicMode;

    private AlertDialog imageActionsDialog = null;
    private AlertDialog urlActionsDialog = null;
    private String hitResultExtra = null;
    private String parseInput = "";
    private InputMethodManager inputMethodManager;

    public PageNestedWebView(Context context) {
        this(context, null);
    }

    public PageNestedWebView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageNestedWebView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.fragment_web_page_v2, this);
        initViews();
    }

    private void initViews() {
        configureWebView();
        inputMethodManager = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        goButton = findViewById(R.id.goto_button);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadInputUrl();
            }
        });

        webAddress = findViewById(R.id.page_url_edittext);
        webAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED
                        || actionId == EditorInfo.IME_ACTION_SEND
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    webAddress.clearFocus();
                    if (getContext() instanceof Activity) {
                        Activity activity = (Activity) getContext();
                        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
                    }

                    loadInputUrl();
                }
                return false;
            }
        });
        webAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    //input.setText(webView.getUrl());
                    webAddress.setText(parseInput);
                    webAddress.setSelection(webAddress.getText().length());
                }else{
                    webAddress.setText(webView.getTitle());
                }
            }
        });
        webAddress.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                    goButton.callOnClick();
                }
                return false;
            }
        });
        progressBar = findViewById(R.id.web_loading_progress_bar);

        browserNavBar = findViewById(R.id.web_nav_bar);
        browserNavBar.setNavListener(new WebNavListener(getContext()));
    }

    private void configureWebView() {
        webView = findViewById(R.id.page_webview);
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.setWebViewClient(new MyWebViewClient());
        webView.setOnLongClickListener(new MyWebLongClickListener());
    }

    private void loadInputUrl() {
        if(webAddress.hasFocus()){
            if(inputMethodManager.isActive())
                inputMethodManager.hideSoftInputFromWindow(webAddress.getApplicationWindowToken(),0);
            String text = webAddress.getText().toString();
            parseInput = text;
            webAddress.setText(parseInput);
            if(!StringUtils.isUrl(text)){
                try{
                    text = URLEncoder.encode(text,"utf-8");
                }catch (Exception e){
                    e.printStackTrace();
                }
                text = Settings.search_baidu(text);
            }else{
                if(!text.startsWith(Settings.headOfHTTP) && !text.startsWith(Settings.headOfHTTPS))
                    text = "https://" + text;
            }
            webView.loadUrl(text);
            webAddress.clearFocus();
        }else{
            webView.clearFocus();
        }
    }

    @Override
    public void loadUrl(String url) {
        SharedPreferences sp = SharedPreferencesUtils.getSettingSP(getContext());
        if (sp != null) {
            noPicMode = sp.getBoolean(SharedPreferencesUtils.KEY_NO_PIC_MODE, false);
        }
        webView.loadUrl(url);
    }

    @Override
    public boolean canGoBack() {
        return webView.canGoBack();
    }

    @Override
    public void goBack() {
        webView.goBack();
    }

    @Override
    public void goForward() {
        webView.goForward();
    }

    @Override
    public boolean canGoForward() {
        return webView.canGoForward();
    }

    @Override
    public void setOnWebInteractListener(OnWebInteractListener listener) {
        this.onWebInteractListener = listener;
    }

    @Override
    public void releaseSession() {
        // donothing, for geckoView
    }

    @Override
    public void onDestroy() {
        webView.stopLoading();
        webView.getSettings().setJavaScriptEnabled(false);
        webView.clearHistory();
        webView.clearCache(true);
        webView.loadUrl("about:blank");
        webView.pauseTimers();
        webView.removeAllViews();
        webView.destroy();
        webView = null;
        onWebInteractListener = null;
    }

    /**
     * 点击图片弹窗
     */
    private void showImageActionsDialog() {
        if (imageActionsDialog != null) {
            imageActionsDialog.show();
            return;
        }
        AlertDialog.Builder imageDialogbuilder = new AlertDialog.Builder(mContext);
        imageDialogbuilder.setItems(R.array.image_actions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == TabConst.TAB_OPEN_ACTION_BACKSTAGE) {
                    TabHelper.createTab(mContext,
                            R.string.new_tab_welcome,
                            hitResultExtra,
                            true);
                } else if (which == TabConst.TAB_OPEN_ACTION_FRONTSTAGE) {
                    TabHelper.createTab(mContext,
                            R.string.new_tab_welcome,
                            hitResultExtra,
                            false);
                }
            }
        });
        imageActionsDialog = imageDialogbuilder.create();
        imageActionsDialog.show();
    }

    /**
     * 网页链接弹窗
     */
    private void showUrlActionsDialog() {
        if (urlActionsDialog != null) {
            urlActionsDialog.show();
            return;
        }
        AlertDialog.Builder urlDialogbuilder = new AlertDialog.Builder(mContext);
        urlDialogbuilder.setItems(R.array.url_actions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == TabConst.TAB_OPEN_ACTION_BACKSTAGE) {
                    TabHelper.createTab(mContext,
                            R.string.new_tab_welcome,
                            hitResultExtra,
                            true);
                } else if (which == TabConst.TAB_OPEN_ACTION_FRONTSTAGE) {
                    TabHelper.createTab(mContext,
                            R.string.new_tab_welcome,
                            hitResultExtra,
                            false);
                }
            }
        });
        urlActionsDialog = urlDialogbuilder.create();
        urlActionsDialog.show();
    }

    class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressBar.setProgress(0);
                progressBar.hide();
                return;
            }

            if ((newProgress > 0) && (progressBar.getVisibility() == View.INVISIBLE
                    || progressBar.getVisibility() == View.GONE)) {
                progressBar.show();
            }
            progressBar.setProgress(newProgress);
            if(!webAddress.hasFocus())
                webAddress.setText(webView.getTitle());

        }
    }

    class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setProgress(0);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            //新网页
            if (request == null)
                return true;
            //普通内容
            if (request.getUrl().toString().startsWith(Settings.headOfHTTP)
                    || request.getUrl().toString().startsWith(Settings.headOfHTTPS)
                    || request.getUrl().toString().startsWith(Settings.headOfWWW)
                    || request.getUrl().toString().endsWith(Settings.endOfCom)) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
            //非http https头重定向
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(request.getUrl().toString()));
                getContext().startActivity(intent);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (onWebInteractListener != null) {
                onWebInteractListener.onPageTitleChange(TabInfo.create("", view.getTitle()));
            }
            boolean isBrowserController = mContext instanceof IBrowser;
            if (!isBrowserController) {
                return;
            }
            //载入完成后添加入历史信息
            if (webView.getProgress() == 100) {
                IBrowser browser = (IBrowser) mContext;
                History history = new History();
                history.setTitle(view.getTitle());
                history.setUrl(url);
                browser.provideHistoryController().addHistory(history);
            }
            progressBar.setVisibility(INVISIBLE);
        }

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            try {
                String targetPath = request.getUrl().getPath();
                if (StringUtils.isEmpty(targetPath)) {
                    return super.shouldInterceptRequest(view, request);
                }
                if (noPicMode && isPicResources(targetPath)) {
                    InputStream placeHolderIS = mContext.getAssets().open("emptyplaceholder.png");
                    return new WebResourceResponse("image/png", "UTF-8", placeHolderIS);
                }
            } catch (Exception e) {

            }

            return super.shouldInterceptRequest(view, request);
        }

        private boolean isPicResources(String path) {
            if (path.endsWith(".jpg")
                    || path.endsWith(".jpeg")
                    || path.endsWith(".png")
                    || path.endsWith(".gif")) {
                return true;
            }
            return false;
        }
    }

    class MyWebLongClickListener implements OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            final WebView.HitTestResult result = ((WebView) v).getHitTestResult();
            if (result == null) {
                return false;
            }
            final int type = result.getType();
            final String extra = result.getExtra();
            hitResultExtra = result.getExtra();
            switch (type) {
                case WebView.HitTestResult.IMAGE_TYPE:
                    EasyLog.i("test", "press image: " + extra);
                    showImageActionsDialog();
                    break;
                case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
                    EasyLog.i("test", "press image anchor: " + extra);
                    break;
                case WebView.HitTestResult.SRC_ANCHOR_TYPE:
                    EasyLog.i("test", "press url: " + extra);
                    showUrlActionsDialog();
                    break;
                default:
                    break;
            }
            return true;
        }
    }
}
