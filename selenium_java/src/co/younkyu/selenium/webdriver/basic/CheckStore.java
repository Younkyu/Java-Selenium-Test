package co.younkyu.selenium.webdriver.basic;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CheckStore extends Thread {

	// private fields
	private String searchQuery;
	private String keyString;
	private float threshold;
	private WebDriver driver;

	// default constructor
	public CheckStore() {
	}

	// parameterized constructor
	public CheckStore(String searchQuery, String keyString, float threshold) {
		this.searchQuery = searchQuery;
		this.keyString = keyString;
		this.threshold = threshold;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run() {

		// open window
		System.setProperty("webdriver.chrome.driver", "drivers/chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		// open the web app
		driver.navigate().to("https://www.bestbuy.com/");

		// wait until web app has fully loaded
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		wait.until(webDriver -> "complete"
				.equals(((JavascriptExecutor) webDriver).executeScript("return document.readyState")));
		
		// search the query in the searchbar
		driver.findElement(By.id("gh-search-input")).sendKeys(searchQuery);
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		driver.findElement(By.cssSelector(".header-search-button[title='submit search']")).click();
		
		// wait until web app has fully loaded
		wait.until(webDriver -> "complete"
		.equals(((JavascriptExecutor) webDriver).executeScript("return document.readyState")));
		
		// find the corresponding item to the keyString
		List<WebElement> searchResult = driver.findElements(By.cssSelector(".sku-item-list > li.sku-item"));
		for (WebElement item : searchResult) {
			try {
				item = item.findElement(By.cssSelector(".sku-title > a"));
				if (item.getText().indexOf(keyString) >= 0) {
					item.click();
				}
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
		
		// wait until the web app has fully loaded
		wait.until(webDriver -> "complete"
		.equals(((JavascriptExecutor) webDriver).executeScript("return document.readyState")));
		
		// add to cart if the price is equal or less than the threshold
		float currentPrice = Float.parseFloat(driver.findElement(By.cssSelector(".priceView-customer-price > span")).getText().replace('$', ' '));
		if (currentPrice <= threshold) {
			driver.findElement(By.cssSelector(".add-to-cart-button")).click();
		} else {
			System.out.println("Too expensive!");
		}
		
	}

}
