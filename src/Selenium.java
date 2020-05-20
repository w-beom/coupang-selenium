import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Selenium {
	// WebDriver
	private WebDriver driver;

	// Properties
	public static final String WEB_DRIVER_ID = "webdriver.chrome.driver";
	public static final String WEB_DRIVER_PATH = "D:\\세레니움\\chromedriver.exe";

	public Selenium() {
		super();

		// System Property SetUp
		System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
		options.addArguments("disable-gpu");
		options.addArguments("disable-infobars");
		options.addArguments("--disable-extensions");
		options.addArguments("headless");

		// Driver SetUp
		driver = new ChromeDriver(options);
	}

	int code = 1;

	public void crawlImg(Map<String, String> list) {
		String title = "";

		try {
			Iterator<String> iterator = list.keySet().iterator();
			BufferedImage bi = null;

			while (iterator.hasNext()) {
				int i = 1;
				title = iterator.next();
				File file = new File("D:\\alrba\\" + title);

				if (!file.exists()) {
					file.mkdirs();
				} else
					continue;

				String href = list.get(title);

				driver.get(href);
				System.out.println(title + " OPEN");
				WebElement sendKey = driver.findElement(By.tagName("body"));


				Thread.sleep(1000);
				sendKey.sendKeys(Keys.END);
				Thread.sleep(1000);
				sendKey.sendKeys(Keys.END);
				Thread.sleep(1000);
				sendKey.sendKeys(Keys.END);
				if (code == 1) {
					List<WebElement> webElements = driver.findElement(By.id("desc-lazyload-container"))
							.findElements(By.tagName("img"));

					System.out.println("사이즈는 ? : " + webElements.size());

					System.out.println("이미지 파싱중");
					// 상세이미지 파싱
					for (WebElement webElement : webElements) {
						String imageLink = webElement.getAttribute("src");
						String filePath = "D:\\alrba\\" + title + "/" + (i++) + ".jpg";
						saveImage(imageLink, filePath);

					}

					System.out.println("파싱완료");
					System.out.println("메인사진 파싱중");
					// 메인사진 파싱
					int mainIndex = 1;
					
					List<WebElement> webElements2 = driver.findElement(By.xpath("//*[@id=\"mod-detail-bd\"]/div[2]/div[13]/div/div/div/div[1]/div[2]/ul")).findElements(By.tagName("li"));
					for (WebElement main : webElements2) {
						main.click();
						//main.findElement(By.className("box-img")).click();

						String filePath = "D:/alrba/" + title + "/m" + (mainIndex++) + ".jpg";
						String imageLink = driver.findElement(By.xpath("//*[@id=\"mod-detail-bd\"]/div[1]/div/div/div/div/div[1]/div/a/img"))
								.getAttribute("src");
						System.out.println(imageLink);
						saveImage(imageLink, filePath);
					}
					System.out.println("메인사진 파싱완료");

				} else if (code == 2) {
					int mainIndex = 1;
					List<WebElement> detailImageList = driver.findElement(By.xpath("//*[@id=\"description\"]"))
							.findElements(By.tagName("img"));
					for (WebElement image : detailImageList) {

						String imageLink = image.getAttribute("src");
						String filePath = "D:\\alrba\\" + title + "/" + (i++) + ".jpg";
						saveImage(imageLink, filePath);
					}

					// 메인사진 파싱
					List<WebElement> mainImageList = driver.findElement(By.xpath("//*[@id=\"J_isku\"]/div/dl[2]/dd/ul"))
							.findElements(By.tagName("li"));
					for (WebElement mainImage : mainImageList) {
						mainImage.click();
						String imageLink = driver.findElement(By.xpath("//*[@id=\"J_ImgBooth\"]")).getAttribute("src");
						String filePath = "D:/alrba/" + title + "/m" + (mainIndex++) + ".jpg";
						saveImage(imageLink, filePath);

					}
				}
			}

		} catch (IllegalArgumentException e) {
			System.out.println("IllegalArgumentException발생 폴더를 삭제후 다시 다운로드 합니다.");
			deleteDirectory(title);
			crawlImg(list);
		} catch (org.openqa.selenium.NoSuchElementException e) {
			e.printStackTrace();
			if (code == 1)
				code = 2;
			else
				code = 1;
			deleteDirectory(title);

			crawlImg(list);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		finally {

		}

	}

	/** 폴더 삭제 **/
	public void deleteDirectory(String title) {
		File deleteDirectory = new File("D:/alrba/" + title);

		System.out.println("삭제경로 : " + deleteDirectory.getPath());

		File[] deleteFileList = deleteDirectory.listFiles();
		for (File deleteFile : deleteFileList) {
			System.out.println(deleteFile.getName());
			deleteFile.delete();
		}
		deleteDirectory.delete();
	}

	/** 이미지 저장 **/
	public void saveImage(String imageLink, String filePath) {
		File file = new File(filePath);
		BufferedImage bi = null;
		URL url;
		try {
			url = new URL(imageLink);
			bi = ImageIO.read(url);
			ImageIO.write(bi, "jpg", file);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Map<String, String> crawlTitle(int min, int max) {
		String url = "https://www.chinatmall.co.kr/user/mypage/one?o=2";
		HashMap<String, String> list = new HashMap<String, String>();
		try {
			driver.get(url);

			List<WebElement> webElements = driver.findElement(By.className("tbl_course"))
					.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));

			for (WebElement webElement : webElements) {
				String title = webElement.findElement(By.className("tdLeft")).findElement(By.tagName("img"))
						.getAttribute("alt");
				String href = webElement.findElement(By.className("subject")).getAttribute("href");

				int num = 0;
				if (isStringInt(title.substring(0, 2))) {
					num = Integer.parseInt(title.substring(0, 2));
				}

				if (min <= num && num <= max) {
					list.put(title, href);
				}
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return list;

	}

	public String crawlKeyword(String keyword) {
		List<WebElement> titleList = new ArrayList<WebElement>();
		String url = "https://www.itemscout.io/keyword/";
		StringBuilder sb = new StringBuilder();
		try {
			driver.get(url);
			WebDriverWait wait = new WebDriverWait(driver, 10);
			WebElement webElement = driver.findElement(By.className("input-keyword"));
			webElement.sendKeys(keyword);

			webElement = driver.findElement(By.className("btn-search-keyword"));
			webElement.click();

			Thread.sleep(1000);
			titleList = driver.findElements(By.className("related-keyword-item"));

			for (WebElement list : titleList) {
				// System.out.print(list.getText() + ",");
				sb.append(list.getText() + ",");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/** 숫자면 true 아니면 false **/
	public static boolean isStringInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/** 파일 목록가져오기 **/
	public void getDirectoryList(int index) {
		String isDir = "D:\\alrba\\";
		File directory = null;

		int newWidth = 500;
		int newHeight = 500;
		Image image;
		// 하위 디렉토리

		for (File info : new File(isDir).listFiles()) {
			if (info.isDirectory()) {
				if (isStringInt(info.getName().substring(0,2))&&index == Integer.parseInt(info.getName().substring(0, 2))) {
					System.out.println(info.getName());
					directory = info;
					break;
				}
			}
		}

		for (File info : directory.listFiles()) {
			if (info.isFile()) {
				if (info.getName().substring(0, 1).equals("m")) {
					try {
						image = ImageIO.read(info);
						
						// 이미지 리사이즈
						// Image.SCALE_DEFAULT : 기본 이미지 스케일링 알고리즘 사용
						// Image.SCALE_FAST : 이미지 부드러움보다 속도 우선
						// Image.SCALE_REPLICATE : ReplicateScaleFilter 클래스로 구체화 된 이미지 크기 조절 알고리즘
						// Image.SCALE_SMOOTH : 속도보다 이미지 부드러움을 우선
						// Image.SCALE_AREA_AVERAGING : 평균 알고리즘 사용
						Image resizeImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

						// 새 이미지 저장하기
						BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
						Graphics g = newImage.getGraphics();
						g.drawImage(resizeImage, 0, 0, null);
						g.dispose();
						ImageIO.write(newImage, "jpg", info);

					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		}

		System.out.println("메인 이미지를 성공적으로 리사이징 했습니다.");

	}

	/** 상품 등록하기 **/
	public void insertCommodity(String code, int index, String size, String color, String keyword) {
		System.out.println("size : " + size);
		String url = "https://wing.coupang.com/tenants/seller-web/vendor-inventory/form?displayCategoryCode=" + code;
		String isDir = "D:\\alrba\\";
		try {
			driver.get(url);

			// 브랜드 입력
			WebElement brand = driver.findElement(By.id("brand"));
			brand.sendKeys("썬왕");

			// 제조사 입력
			WebElement manufacture = driver.findElement(By.id("manufacture"));
			manufacture.sendKeys("썬왕");

			// 제품폴더이름 가져오기
			String directoryName = selectDirectory(isDir, index);

			// 등록상품명 입력
			String productName = directoryName.split("\\(")[0];
			WebElement productNameInput = driver.findElement(By.id("productName"));
			productNameInput.sendKeys(productName);

			// 제품명 입력
			String generalProductName = productName.split("\\.")[1];
			WebElement generalProductNameInput = driver.findElement(By.id("generalProductName"));
			generalProductNameInput.sendKeys(generalProductName);

			// 사이즈 입력
			driver.findElement(By.xpath(
					"//*[@id=\"vendor-inventory-item-section\"]/div[2]/div[2]/div[1]/div/div/fieldset[1]/div/div/div[2]/div/div/span/input"))
					.sendKeys(size);

			Thread.sleep(500);
			// 색상 입력
			driver.findElement(By.xpath(
					"//*[@id=\"vendor-inventory-item-section\"]/div[2]/div[2]/div[1]/div/div/fieldset[2]/div/div/div[2]/div/div/span/input"))
					.sendKeys(color);

			// 옵션 구성하기 클릭
			driver.findElement(By.xpath(
					"//*[@id=\"vendor-inventory-item-section\"]/div[2]/div[2]/div[1]/div/div/div/div/div[2]/button"))
					.click();

			Thread.sleep(1000);

			// table 맨 윗줄 row 가져오기
			WebElement selectInput = driver.findElement(By.id("itemAttribute")).findElements(By.tagName("tr")).get(1);

			// 가격 가져온 후 등록
			String price = directoryName.split("\\(")[1];
			price = price.replaceAll("\\)", "");
			selectInput.findElements(By.tagName("td")).get(13).click();
			selectInput.findElements(By.tagName("td")).get(13).findElement(By.tagName("input")).sendKeys(price);
			driver.findElement(By.xpath("//*[@id=\"itemAttribute_salePrice\"]/div[1]/button")).click();

			// 초기 재고수량 등록
			selectInput.findElements(By.tagName("td")).get(18).click();
			selectInput.findElements(By.tagName("td")).get(18).findElement(By.tagName("input")).sendKeys("100");
			driver.findElement(By.xpath("//*[@id=\"itemAttribute_maximumBuyCount\"]/div[1]/button")).click();

			// 고시정보 팝업
			selectInput.findElements(By.tagName("td")).get(21).click();

			Thread.sleep(1000);
			driver.findElement(By.xpath("//*[@id=\"noticeCategoryName\"]")).click();

			driver.findElement(By.xpath("//*[@id=\"noticeCategoryName\"]/option[2]")).click();
			// 고시정보 팝업에서 상세페이지 참조 쓰기 후 등록
			List<WebElement> list = driver.findElements(By.xpath("//*[@id=\"noticeContents\"]/tbody/tr"));
			for (WebElement content : list) {
				if (!content.findElements(By.tagName("td")).get(1).getText().equals("품질보증기준")) {
					content.findElement(By.tagName("textarea")).sendKeys(Keys.chord(Keys.CONTROL, "a"));
					content.findElement(By.tagName("textarea")).sendKeys(Keys.BACK_SPACE);
					content.findElement(By.tagName("textarea")).sendKeys("상세 페이지 참조");
				}
			}
			driver.findElement(By.xpath("//*[@id=\"applyNoticeToAllRowsButton\"]")).click();

			// 출고 소요기간 등록
			selectInput.findElements(By.tagName("td")).get(23).click();
			selectInput.findElements(By.tagName("td")).get(23).findElement(By.tagName("input")).sendKeys("9");
			driver.findElement(By.xpath("//*[@id=\"itemAttribute_outboundShippingTime\"]/div[1]/button")).click();

			// 바코드 등록
			selectInput.findElements(By.tagName("td")).get(24).click();
			Thread.sleep(500);
			driver.findElement(By.xpath("//*[@id=\"barcodeForm\"]/div[2]/div[1]/label[2]/input")).click();
			Thread.sleep(500);
			// driver.findElement(By.xpath("//*[@id=\"emptyBarcodeReasonSelector\"]"));
			driver.findElement(By.xpath("//*[@id=\"emptyBarcodeReasonSelector\"]/option[3]")).click();
			Thread.sleep(500);
			driver.findElement(By.xpath("//*[@id=\"applyBarcodeToAllRowsButton\"]")).click();

			// 인증 신고등 정보 등록
			selectInput.findElements(By.tagName("td")).get(25).click();
			Thread.sleep(500);
			driver.findElement(By.xpath("//*[@id=\"certificationForm\"]/div[2]/label[3]/input")).click();
			driver.findElement(By.xpath("//*[@id=\"applyCertificationToAllRowsButton\"]")).click();
			Thread.sleep(500);

			// 검색어 등록
			driver.findElement(By.xpath("//*[@id=\"search-tag-section\"]/div[1]/div[2]/button")).click();
			Thread.sleep(500);
			driver.findElement(
					By.xpath("//*[@id=\"search-tag-section\"]/div[2]/div[2]/div/div[1]/div[2]/div/span/input"))
					.sendKeys(keyword);
			driver.findElement(
					By.xpath("//*[@id=\"search-tag-section\"]/div[2]/div[2]/div/div[1]/div[2]/div/span/input"))
					.sendKeys(Keys.ENTER);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String selectDirectory(String isDir, int index) {
		for (File info : new File(isDir).listFiles()) {
			if (info.isDirectory()) {
				if (isStringInt(info.getName().substring(0, 2))&&index==Integer.parseInt(info.getName().substring(0,2))){
					return info.getName();
				}
			}
		}
		return null;
	}

}
