package com.automation.rediff1.redifftest1;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public class AddDelStock {

    WebDriver driver = null;
    WebDriverWait wait;

    @BeforeSuite
    public void initialization(){
        driver = new FirefoxDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        driver.get("https://www.rediff.com/");
    }

    @Test(priority = 1)
    public void portfolioSignIn(){
        waitForPageload();
        driver.findElement(By.xpath("//a[text()='Money']")).click();
        driver.findElement(By.xpath("//a[text()='Sign In']")).click();
        driver.findElement(By.id("useremail")).sendKeys("******@rediffmail.com");
        driver.findElement(By.id("userpass")).sendKeys("***dfdgf");
        driver.findElement(By.id("loginsubmit")).click();
        wait = new WebDriverWait(driver,Duration.ofSeconds(30));
        wait.until(ExpectedConditions.elementToBeClickable(By.id("createPortfolio")));

        Assert.assertEquals(driver.getTitle(),"Rediff Moneywiz | My Portfolio(s)");
    }

    @Test(priority = 2)
    public void createPortfolio(){
        driver.findElement(By.id("createPortfolio")).click();
        driver.findElement(By.id("create")).clear();
        driver.findElement(By.id("create")).sendKeys("Test portfolio");
        driver.findElement(By.id("createPortfolioButton")).click();
        waitForPageload();
        String val = driver.findElement(By.xpath("//*[@id = 'portfolioid']/option[text()='Test portfolio']")).getAttribute("selected");
       // Assert.assertEquals(val, "selected");
    }

    @Test(priority = 3)
    public void addNewStocks(){
        driver.findElement(By.id("addStock")).click();
        driver.findElement(By.id("addstockname")).sendKeys("HDFC Bank");
        driver.findElement(By.xpath("//*[text()='HDFC Bank Ltd.']")).click();
        driver.findElement(By.id("stockPurchaseDate")).click();
        selectStockPurchaseDate("13/06/2020");
        driver.findElement(By.id("addstockqty")).sendKeys("50");
        driver.findElement(By.id("addstockprice")).sendKeys("1000");
        driver.findElement(By.id("addStockButton")).click();
        waitForPageload();
    }

    @Test(dependsOnMethods = "addNewStocks")
    public void deleteStockAndFolio(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        driver.findElement(By.xpath("//a[text()='HDFC Bank']//ancestor::tr//input[@type='radio']")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.className("deleteEquity")));
        driver.findElement(By.xpath("//input[@class = 'deleteEquity']")).click();
        driver.switchTo().alert().accept();
        waitForPageload();
        driver.findElement(By.id("deletePortfolio")).click();
        driver.switchTo().alert().accept();
    }

    private void selectStockPurchaseDate(String purchaseDate) {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date selectDate = dateFormat.parse(purchaseDate);

            String day = new SimpleDateFormat("dd").format(selectDate);
            String month = new SimpleDateFormat("MMMM").format(selectDate);
            String year = new SimpleDateFormat("yyyy").format(selectDate);
            String expectedMonthYear = month + " " + year;
            System.out.println("Expected dates : "+ expectedMonthYear);

            while (true){
                String displayMonthYear = driver.findElement(By.className("dpTitleText")).getText();

                if(displayMonthYear.equalsIgnoreCase(expectedMonthYear)){
                    break;
                } else if (currentDate.compareTo(selectDate) >0) {
                    driver.findElement(By.xpath("//button[text()='<']")).click();
                } else {
                    driver.findElement(By.xpath("//button[text()='>']")).click();
                }
            }

            driver.findElement(By.xpath("//tbody/tr[@class='dpTR']/td[text()='"+day+"']")).click();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    public void waitForPageload(){
        JavascriptExecutor js = (JavascriptExecutor) driver;

        int i=0;

        while(i<60){
            String pageState = (String)js.executeScript("return document.readyState");
            if(pageState.equals("complete")){
                break;
            }else{
                i++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
