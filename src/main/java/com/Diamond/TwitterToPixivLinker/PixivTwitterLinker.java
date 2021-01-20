package com.Diamond.TwitterToPixivLinker;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PixivTwitterLinker {

    public static int WAIT_TIME = 5000;

    //as example use this or replace with any picture ID
    public static void demo() {
        //writeNewInfo(78898962, new File("TwitterAccounts_Creators.txt"), null, false);
        System.out.println(getTwitterProfileFromImage("https://www.pixiv.net/en/artworks/78898962", null, true));
    }

    /**Writes the User Info in following format in the file specified. Format:
     * Pixiv ID of the Artist newLine
     * Twitter Handle
     *
     * @param pictureId The ID of the Illustration you want to get the Pixiv User from
     * @param exportFile The File where it should be written to.
     * @param driverPath The Path to your ChromeDriver Executable - if this is null, it will use the default values "./chromedriver.exe" for Windows and "/usr/lib/chromium-browser/chromedriver" for everything else.
     * @param showBrowser Defines if the Browser should be visible */
    public static void writeNewInfo(int pictureId, @Nonnull File exportFile, @Nullable String driverPath, boolean showBrowser) {
        String link = String.format("https://www.pixiv.net/en/artworks/%d", pictureId);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(exportFile))) {
            if (!exportFile.exists()) {
                if (!exportFile.createNewFile()) {
                    System.out.println("Error! File can't be created!");
                }
            }
            writer.write(getTwitterProfileFromImage(link, driverPath, showBrowser));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return The Pixiv ID of the Artist and the Twitter Handle - In case a Error occures it will return "Error" instead.
     * @param link The link of the Image formatted like https://www.pixiv.net/en/artworks/IMAGE_ID
     * @param driverPath The Path to your ChromeDriver Executable - if this is null, it will use the default values "./chromedriver.exe" for Windows and "/usr/lib/chromium-browser/chromedriver" for everything else.
     * @param showBrowser Defines if the Browser should be visible
     * */
    public static String getTwitterProfileFromImage(String link, String driverPath, boolean showBrowser) {
    if (driverPath != null) {
        System.setProperty("webdriver.chrome.driver", driverPath);
    } else {
        if (isWindows()) {
            System.setProperty("webdriver.chrome.driver", "./chromedriver.exe");
        } else {
            System.setProperty("webdriver.chrome.driver", "/usr/lib/chromium-browser/chromedriver");
        }
    }
        ChromeOptions chromeOptions = new ChromeOptions();
        if (!showBrowser) {
            chromeOptions.addArguments("--headless");
        }
        WebDriver webDriver = new ChromeDriver(chromeOptions);
        String outString = "";
        try {
            webDriver.get(link);

            webDriver.findElement(By.xpath("//a[contains(@href,'/en/users/')]")).click();
            Thread.sleep(WAIT_TIME);

            String current = webDriver.getCurrentUrl();
            Matcher matcher = Pattern.compile("https://www.pixiv.net/en/users/(\\d+)").matcher(current);
            String Id = "";
            if (matcher.matches()) {
                Id = matcher.group(1);
            }

            webDriver.findElement(By.xpath("//a[contains(@href,'/jump.php?url=https%3A%2F%2Ftwitter.com%2F')]")).click();

            Thread.sleep(WAIT_TIME);
            Set<String> windows = webDriver.getWindowHandles();

            webDriver.switchTo().window((String) windows.toArray()[1]);
            webDriver.findElement(By.xpath("//a[contains(@href,'https://twitter.com/')]")).click();

            Thread.sleep(WAIT_TIME);
            Pattern pattern = Pattern.compile("https://twitter.com/(.*)");
            matcher = pattern.matcher(webDriver.getCurrentUrl());

            if (matcher.matches()) {
                outString = String.format("%s\n@%s", Id, matcher.group(1));
            }
        } catch (Exception e) {
            outString = "Error";
        }
        webDriver.quit();


        return outString;
    }



    //checks if the OS is Windows 10
    private static boolean isWindows() {
        return System.getProperty("os.name").equalsIgnoreCase("windows 10");
    }


    /**sets the amount of milliseconds the Program should wait to let the Webpage load. Default is 5000
     * @param waitTime Waiting time in milliseconds.*/
    public static void setWaitTime(int waitTime) {
        WAIT_TIME = waitTime;
    }
}
