package com.example.android.adobepassclientlessrefapp.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Taken from NBC Sports app
 */
public class AuthenticationWebView extends WebView {

    CookieManager cookieManager = CookieManager.getInstance();

    private Callback callback;

    public interface Callback{
        void onBack();

        void onComplete();

        void onPageStarted(String url);

        void onPageFinished();

        void onError(WebResourceResponse errorResponse);
    }

    public AuthenticationWebView(Context context) {
        this(context, null);
    }

    public AuthenticationWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AuthenticationWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    protected void onDetachedFromWindow() {
        if (callback != null) {
            callback.onBack();
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        WebViewClient client = new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                callback.onPageStarted(url);
            }

            @Override
            public void onPageFinished( WebView view, String url ) {
                super.onPageFinished( view, url );
                callback.onPageFinished();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //if (url.equals(URLDecoder.decode(AccessEnabler.ADOBEPASS_REDIRECT_URL))) {
                if (isSmartLink(url)){
                    callback.onComplete();
                    setVisibility(View.GONE);
                    return true;
                }
                return false;
            }

            @Override
            public void onReceivedHttpError( WebView view, WebResourceRequest request, WebResourceResponse errorResponse ) {
                super.onReceivedHttpError( view, request, errorResponse );
                callback.onError( errorResponse );
            }
        };
        setWebViewClient(client);

        // enable JavaScript support
        WebSettings browserSettings = getSettings();
        browserSettings.setJavaScriptEnabled(true);
        browserSettings.setJavaScriptCanOpenWindowsAutomatically(true);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }


    private boolean isSmartLink(String url){
        return url.startsWith("https://smart.link");
    }

    public void clearCookie() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
                // a callback which is executed when the cookies have been removed
                @Override
                public void onReceiveValue(Boolean aBoolean) {}
            });
        } else {
            cookieManager.removeAllCookie();
        }
    }

}
