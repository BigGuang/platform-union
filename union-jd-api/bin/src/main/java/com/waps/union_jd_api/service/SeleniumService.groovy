package com.waps.union_jd_api.service

import org.openqa.selenium.PageLoadStrategy
import org.openqa.selenium.Platform
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver

import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class SeleniumService {

    public static void webView(String url) {
//        final WebClient webClient = new WebClient(BrowserVersion.CHROME)
//        final HtmlPage page=webClient.getPage(url)
//        TestUtils.outPrint(page)
    }

    public static String localGetCurrentUrl(String url) {

        System.setProperty("webdriver.chrome.driver", "/home/py_script/chromedriver_linux_75");//chromedriver服务地址
//        System.setProperty("webdriver.chrome.driver", "/Users/xguang/works/chromedriver/chromedriver");
//chromedriver服务地址
        ChromeOptions options = new ChromeOptions()

        options.setPageLoadStrategy(PageLoadStrategy.NORMAL)
        options.addArguments("--headless")
        options.addArguments("--no-sandbox")
        WebDriver driver = new ChromeDriver(options) //新建一个WebDriver 的对象，但是new 的是FirefoxDriver的驱动
        driver.get(url)//打开指定的网站
        println "open:" + url
        try {
            /**
             * WebDriver自带了一个智能等待的方法。
             dr.manage().timeouts().implicitlyWait(arg0, arg1）；
             Arg0：等待的时间长度，int 类型 ；
             Arg1：等待时间的单位 TimeUnit.SECONDS 一般用秒作为单位。
             */
            driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS)
        } catch (Exception e) {
            println "SeleniumService error:" + e.getLocalizedMessage()
            driver.quit()
        }
        String currentUrl = driver.getCurrentUrl()
        println url + " ==> " + currentUrl
        /**
         * dr.quit()和dr.close()都可以退出浏览器,简单的说一下两者的区别：第一个close，
         * 如果打开了多个页面是关不干净的，它只关闭当前的一个页面。第二个quit，
         * 是退出了所有Webdriver所有的窗口，退的非常干净，所以推荐使用quit最为一个case退出的方法。
         */
//        driver.close()
        driver.quit()
        return currentUrl

    }


    public static String serverGetCurrentUrl(String url) {

        DesiredCapabilities chrome = new DesiredCapabilities()

        chrome.setBrowserName("chrome")
        chrome.setPlatform(Platform.LINUX)

        ChromeOptions options = new ChromeOptions()
        options.merge(chrome)
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL)
        options.addArguments("--headless")
        options.addArguments("--no-sandbox")

//        WebDriver driver = new RemoteWebDriver(new URL("http://10.1.1.19:4444/wd/hub"), options)
        WebDriver driver = new RemoteWebDriver(new URL("http://127.0.0.1:4444/wd/hub"), options)

        driver.get(url)//打开指定的网站
        try {
            /**
             * WebDriver自带了一个智能等待的方法。
             dr.manage().timeouts().implicitlyWait(arg0, arg1）；
             Arg0：等待的时间长度，int 类型 ；
             Arg1：等待时间的单位 TimeUnit.SECONDS 一般用秒作为单位。
             */
            driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS)
        } catch (Exception e) {
            println "SeleniumService error:" + e.getLocalizedMessage()
            driver.close()
            driver.quit()//Remote模式下是否需要关闭？？？
        }
        String currentUrl = driver.getCurrentUrl()
        println url + " ==> " + currentUrl
        driver.close()
        driver.quit()//Remote模式下是否需要关闭？？？
        return currentUrl
    }


    public static Map<String, String> threadGetCurrentUrl(List<String> urlList) {

        println "开始异步获取URL:" + urlList.size()
        Map<String, String> currentUrlMap = new HashMap<>()
        try {
            // 开始的倒数锁
            final CountDownLatch begin = new CountDownLatch(1)

            // 结束的倒数锁
            final CountDownLatch end = new CountDownLatch(urlList.size())

            // 十名选手
            final ExecutorService exec = Executors.newFixedThreadPool(urlList.size())


            for (int i = 0; i < urlList.size(); i++) {
                final int NO = i
                Runnable run = new Runnable() {
                    public void run() {
                        try {
                            begin.await()
                            String url = urlList.get(NO)
                            println "start:"+url
                            String currentUrl = serverGetCurrentUrl(url)
                            println "currentUrl:"+currentUrl
//
//                            String testU="https://cs.m.jd.com/babelDiy/Zeus/FvKextEWVjwQWtpuw6ep2jX6Akp/index.html?unionActId=31082&d=negdPb&s=&cu=true&utm_source=kong&utm_medium=jingfen&utm_campaign=t_1000440357_&utm_term=8dfd2146cedd40acbe7e94f6462d980a"
//                            String currentUrl = testU
//                            String currentUrl = localGetCurrentUrl(url)
                            currentUrlMap.put(url, currentUrl)
                        } catch (Exception e) {
                            System.out.println("threadGetCurrentUrl:" + e.getLocalizedMessage());
                        } finally {
                            end.countDown();
                        }
                    }
                }
                exec.submit(run);
            }
            begin.countDown()
            end.await()
            exec.shutdown()

        } catch (Exception e) {
            System.out.println("threadGetCurrentUrl Error:" + e.getLocalizedMessage())
        }
        return currentUrlMap
    }
}
