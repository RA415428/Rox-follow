package com.roxfollow.rewards;

import android.os.Bundle;
import android.webkit.JavascriptInterface;
import com.getcapacitor.BridgeActivity;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;

public class MainActivity extends BridgeActivity {
    private final String GAME_ID = "800368206";
    private final String REWARDED_PLACEMENT = "Rewarded_Android";
    private final boolean TEST_MODE = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Unity Ads
        UnityAds.initialize(getApplicationContext(), GAME_ID, TEST_MODE, new IUnityAdsInitializationListener() {
            @Override
            public void onInitializationComplete() {
                loadUnityAd();
            }

            @Override
            public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {}
        });

        // JavaScript Bridge for React
        getBridge().getWebView().addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void showRewardedAd() {
                runOnUiThread(() -> {
                    UnityAds.show(MainActivity.this, REWARDED_PLACEMENT, new UnityAdsShowOptions(), new IUnityAdsShowListener() {
                        @Override
                        public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                            if (state == UnityAds.UnityAdsShowCompletionState.COMPLETED) {
                                getBridge().getWebView().evaluateJavascript("window.onUnityAdRewardEarned && window.onUnityAdRewardEarned();", null);
                            }
                            loadUnityAd();
                        }

                        @Override
                        public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
                            loadUnityAd();
                        }

                        @Override
                        public void onUnityAdsShowStart(String placementId) {}

                        @Override
                        public void onUnityAdsShowClick(String placementId) {}
                    });
                });
            }
        }, "UnityAdsNative");
    }

    private void loadUnityAd() {
        UnityAds.load(REWARDED_PLACEMENT, new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String placementId) {}

            @Override
            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {}
        });
    }
}
