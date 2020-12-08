package com.Diamond.TwitterToPixivLinker;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainPage {

    public static final int WAIT_TIME = 5000;

    //as example use this or replace with any picture ID
    public static void main(String[] args) {
        writeNewInfo(86131525);
    }

    public static void writeNewInfo(int pictureId) {
        getNewInfo(pictureId);
    }

    //Writes the Info to the File specified.
    //IMPORTANT: It will write Error if there was no Twitter Account found on the Artists page.
    public static void getNewInfo(int pictureId) {
        //constructs Link via pictureID
        String link = String.format("https://www.pixiv.net/en/artworks/%d", pictureId);
        File exportFile = new File("TwitterAccounts_Creators.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(exportFile))) {
            if (!exportFile.exists()) {
                exportFile.createNewFile();
            }
            writer.write(getTwitterProfile(link));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //returns the Artist ID and the Twitter Profile name.
    private static String getTwitterProfile(String link) {
        //replace the paths with your own Paths to the chromedriver.

        if (isWindows()) {
            System.setProperty("webdriver.chrome.driver", "./chromedriver.exe");
        } else {
            System.setProperty("webdriver.chrome.driver", "/usr/lib/chromium-browser/chromedriver");
        }
        WebDriver webDriver = new ChromeDriver();
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

            System.out.println(webDriver.getCurrentUrl());
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


}
