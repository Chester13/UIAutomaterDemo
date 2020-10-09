package com.example.uiautomatordemo;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    final String TAG = "Demo";
    final String PACKAGE_NAME_GOOGLE_PLAY = "com.android.vending";
    private ArrayList<TestData> mTestData = new ArrayList<>();

    public static class TestData {
        public String url = "";  // url for a specific apk to open.
        public String pkgName = "";  // apk package name.
        public String appDownloadUrl = ""; // url for show apk downloading web page.
        public boolean verifyPass = false;

        TestData(String _url, String _pkgName, String _dlUrl) {
            url = _url;
            pkgName = _pkgName;
            appDownloadUrl = _dlUrl;
        }

        public String toString() {
            return "(url='"+url+"', pkgName="+pkgName+", appDownloadUrl='"+appDownloadUrl+"')";
        }
    }

    // Provide test data.
    private void provideTestData() {
        mTestData.add(new TestData(
                "http://www.google.com",
                "com.google.android.apps.youtube.music",
                "https://play.google.com/store/apps/details?id=com.google.android.apps.youtube.music"
        ));

        mTestData.add(new TestData(
                "http://www.google.com",
                "org.mozilla.firefox",
                "https://play.google.com/store/apps/details?id=org.mozilla.firefox&hl=zh-TW"
                ));

        Log.d(TAG, "mTestData.size()="+mTestData.size());
    }


    @Test
    public void useAppContext() {
        // Provide or collect your test data first.
        provideTestData();

        // Get some necessary objects.
        // 1. Context uses for call startActivity() or other Android standard functions.
        // 2. UiDevice for do UI Automator relevant things.
        final Context context = getInstrumentation().getTargetContext();
        final UiDevice device = UiDevice.getInstance(getInstrumentation());

        // Test start!!
        int pass_count = 0;
        int fail_count = 0;
        for (int i = 0; i < mTestData.size(); i++) {
            // [ Label1 ]
            Uri uri = Uri.parse(mTestData.get(i).url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage(mTestData.get(i).pkgName);
            try {
                // Try to open a specific url by a specific apk.
                context.startActivity(intent);

                // Wait for apk launching.
                Thread.sleep(3000);

                // Get current top screen package name and judge it.
                final String currentPkgName = device.getCurrentPackageName();
                if (mTestData.get(i).pkgName.equals(currentPkgName)) {
                    // Open apk with a specific url successfully?!
                    mTestData.get(i).verifyPass = true;
                } else {
                    throw new Exception("Open apk failed, current package name is " + currentPkgName);
                }
            } catch (ActivityNotFoundException e) {
                // The corresponding apk does not exist.
                // Guide to GooglePlay app for downloading it.

                uri = Uri.parse("market://details?id=" + mTestData.get(i).pkgName); // Provide Uri object with a specific url.
                intent = new Intent(Intent.ACTION_VIEW, uri); // Indicate desired url.
                intent.setPackage(PACKAGE_NAME_GOOGLE_PLAY); // Indicate that use GooglePlay apk to open the url.
                try {
                    // Open GooglePlay apk to a specific apk downloading page.
                    context.startActivity(intent);

                    // To get the resourceId or text, please use uiautomatorviewer tool to see it.
                    // https://developer.android.com/training/testing/ui-automator#ui-automator-viewer
                    UiObject installButton = device.findObject(new UiSelector()
                            .resourceId("com.android.vending:id/0_resource_name_obfuscated")
                            .text("Install"));
                    // 1. It may need time to launch google play, we wait 3 sec.
                    // 2. After get an UiObject, it MUST call wait series functions once. (UI Automator required)
                    installButton.waitForExists(3000);

                    if (installButton.exists()) {
                        // Found install button, click it to start downloading.
                        installButton.click();
                        installButton.waitUntilGone(3000);

                        // After clicked "Install", it will show "Open" button.
                        UiObject openButton = device.findObject(new UiSelector()
                                .resourceId("com.android.vending:id/0_resource_name_obfuscated")
                                .text("Open"));
                        openButton.waitForExists(3000);

                        // Wait for downloading apk.
                        int retry = 10;
                        while(!openButton.isEnabled()) {
                            retry--;
                            if (retry < 0) {
                                throw new Exception("Wait for Open button be enabled timeout!!");
                            }
                            Thread.sleep(3000);
                        }

                        // Apk download finished.
                        // Do what you want, maybe try do Label1 section again?
                        mTestData.get(i).verifyPass = true;
                    } else {
                        throw new Exception("Cannot find Install button!!");
                    }
                } catch (ActivityNotFoundException e1) {
                    // Google play apk does not exist!!
                    // Try guide to GooglePlay web page.

                    uri = Uri.parse(mTestData.get(i).appDownloadUrl);
                    intent = new Intent(Intent.ACTION_VIEW, uri);

                    // Open url and does NOT indicate which apk to open it.
                    context.startActivity(intent);
                } catch (Exception e1) {
                    // Unexpected exception.
                    mTestData.get(i).verifyPass = false;
                    Log.e(TAG, "TestData="+mTestData.get(i).toString()+" got exception: " + e1);
                    e1.printStackTrace();
                }
            } catch (Exception e) {
                // Unexpected exception.
                mTestData.get(i).verifyPass = false;
                Log.e(TAG, "TestData="+mTestData.get(i).toString()+" got exception: " + e);
                e.printStackTrace();
            }

            if (mTestData.get(i).verifyPass) {
                pass_count++;
            } else {
                fail_count++;
            }
        }

        Log.d(TAG, "Test result, total=" + mTestData.size() + ", pass=" + pass_count + ", fail=" + fail_count);
    }
}