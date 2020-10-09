# UIAutomaterDemo
  
一個簡單的 Android UI Automator 的 sample code。  
  
測試前：  
1、因為需要對畫面操作，建議先把手機語言設定成跟你寫的 test code 一致  
　　(例如，有時候可能會需要比較 button text)  
2、打開 adb。在 Settings 找到 Developer options 後，打開 USB debugging  
3、允許透過 USB 操作你的 Android device  
4、將你的 Android device 透過 usb 線連接電腦  
![image](https://github.com/Chester13/UIAutomaterDemo/blob/master/Screenshot_DeveloperOptions.png)  
  
  
開始測試：  
在 Android Studio 上，將游標移到你寫的 test file 上按滑鼠右鍵，點擊「Run 'Tests in uiautomatordemo''」  
![image](https://github.com/Chester13/UIAutomaterDemo/blob/master/HowToStartTest.png)  
  
  
確認測試結果：  
以此 demo 的情況而言，可以在 Android Studio 下方的 Logcat 欄裡看到 log  
(或你直接下指令 adb logcat 也行，但 log 量會較多)  
  
  
補充：  
在 Android Studio 上成功執行過一次測試後，在你的 UiAutomatorDemo 目錄下，會產生以下兩個 apk：  
UiAutomatorDemo/app/build/outputs/apk/debug/app-debug.apk  
UiAutomatorDemo/app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk  
有了這兩個 apk，之後要跑同樣的測試時就可以不必透過 Android Studio 了。  
  
  
方法：  
1、安裝它們  
$ cd ~/UiAutomatorDemo  
$ adb install -t app/build/outputs/apk/debug/app-debug.apk  
$ adb install -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk  
  
2、這時可以透過以下指令查詢，會發現你的 Android device 裡多了一個 instrumentation  
$ adb shell pm list instrumentation  
instrumentation:com.example.uiautomatordemo.test/androidx.test.runner.AndroidJUnitRunner (target=com.example.uiautomatordemo)  

3、下指令開始測試，有兩種：  
只跑單一測項 (後面那個 useAppContext 就是對應到你程式裡的測試函式名)：  
$ adb shell am instrument -w com.example.uiautomatordemo.test/androidx.test.runner.AndroidJUnitRunner -e com.example.uiautomatordemo.test#useAppContext  
  
測全部 (如果你的 ExampleInstrumentedTest.java 裡定義了一個以上的 test function)  
$ adb shell am instrument -w com.example.uiautomatordemo.test/androidx.test.runner.AndroidJUnitRunner -e com.example.uiautomatordemo.test  
  
![image](https://github.com/Chester13/UIAutomaterDemo/blob/master/Screeshot_command_run_test.png)  
  
  
另外也附上過程中會用到的 UI Automator Viewer 工具的截圖供參考：  
https://developer.android.com/training/testing/ui-automator#ui-automator-viewer  
![image](https://github.com/Chester13/UIAutomaterDemo/blob/master/Screenshot_UiAutomatorViewer.png)  
