import java.util.Map;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Selenium selenium = new Selenium();
		Scanner sc = new Scanner(System.in);
		Map<String, String> list;
		String resultKeyword = "";

		while (true) {
			System.out.println("------------------------------------");
			System.out.println("1.이미지 가져오기 2.키워드 검색 3.이미지 리사이징 하기 4.상품 등록하기 5.종료");
			System.out.println("------------------------------------");
			int menu = sc.nextInt();
			sc.nextLine();
			switch (menu) {
			case 1:
				System.out.println("가져오고 싶은 번호의 범위를 입력해주세요.");
				System.out.print("최소 : ");
				int min = sc.nextInt();
				System.out.println("\n최대 : ");
				int max = sc.nextInt();
				list = selenium.crawlTitle(min, max);
				selenium.crawlImg(list);
				
				break;
			case 2:
				System.out.print("가져오고 싶은 단어를 입력하세요 : ");
				String keyword = sc.nextLine();
				resultKeyword=selenium.crawlKeyword(keyword);
				System.out.println(resultKeyword);
				break;
				
			case 3:
				System.out.println("리사이징할 폴더 : ");
				int index= sc.nextInt();
				selenium.getDirectoryList(index);
				break;
			case 4:
				System.out.println("등록할 상품의 코드를 입력해주세요 : ");
				String code = sc.nextLine();
				
				System.out.print("등록할 상품의 번호를 입력해주세요 : ");
				int directoryIndex = sc.nextInt();
				sc.nextLine();
				
				System.out.print("사이즈를 입력해주세요 : ");
				String size= sc.nextLine();
				
				System.out.print("색상을 입력해주세요 : ");
				String color = sc.nextLine();
				selenium.insertCommodity(code,directoryIndex,size,color,resultKeyword);
				break;

			default:
				return;
			}
		}

	}

}
