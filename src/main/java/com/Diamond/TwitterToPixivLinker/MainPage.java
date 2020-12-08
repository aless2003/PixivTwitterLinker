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
    public static void main(String[] args) throws InterruptedException {
        writeNewInfo(83955775);
    }

    public static void writeNewInfo(int pictureId) {
        getNewInfo(pictureId);
    }

    public static void getNewInfo(int pictureId) {
        String link = String.format("https://www.pixiv.net/en/artworks/%d", pictureId);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("TwitterAccounts_Creators.txt"))) {
            if (!new File("TwitterAccounts_Creators.txt").exists()) {
                new File("TwitterAccounts_Creators.txt").createNewFile();
            }
            writer.write(getTwitterProfile(link));
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private static String getTwitterProfile(String link) throws InterruptedException {
        if (isWindows()) {
            System.setProperty("webdriver.chrome.driver", "./chromedriver.exe");
        } else {
            System.setProperty("webdriver.chrome.driver", "/usr/lib/chromium-browser/chromedriver");
        }
        WebDriver webDriver = new ChromeDriver();
        webDriver.get(link);
        webDriver.findElement(By.xpath("//a[contains(@href,'/en/users/')]")).click();
        Thread.sleep(5000);
        String current = webDriver.getCurrentUrl();
        Matcher matcher = Pattern.compile("https://www.pixiv.net/en/users/(\\d+)").matcher(current);
        String Id = "";
        if (matcher.matches()) {
            Id = matcher.group(1);
        }
        webDriver.findElement(By.xpath("//a[contains(@href,'/jump.php?url=https%3A%2F%2Ftwitter.com%2F')]")).click();
        Thread.sleep(5000);
        System.out.println(webDriver.getCurrentUrl());
        Set<String> windows = webDriver.getWindowHandles();
        webDriver.switchTo().window((String) windows.toArray()[1]);
        webDriver.findElement(By.xpath("//a[contains(@href,'https://twitter.com/')]")).click();
        Thread.sleep(5000);
        Pattern pattern = Pattern.compile("https://twitter.com/(.*)");
        matcher = pattern.matcher(webDriver.getCurrentUrl());
        if (matcher.matches()) {
            webDriver.quit();
            return String.format("%s\n@%s", Id, matcher.group(1));
        }
        return "Error";
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").equalsIgnoreCase("windows 10");
    }



}
