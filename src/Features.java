import java.awt.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeMap;

public class Features {
	private static final String dbClassName = "org.mariadb.jdbc.Driver";
    private static final String url = "jdbc:mariadb://140.127.74.226:3306/410477010";
    private static final String username = "410477010";
    private static final String password = "4t78n";
	static Connection connection;
    static Statement smt;
    static ResultSet rs;
    static Scanner sc;    
    private static HashMap<String, Integer> movieMap;
    
	public static void main(String [] args) throws Exception{
		connectDatabase();
		sc = new Scanner(System.in);
		int exit = 1;
		while(exit!=0){
			System.out.println("請輸入功能代碼： 1.查詢    2.查看前10大顧客   3.新增   4.管理會員    5.最新年度前10下載量電影   6.離開");
			String funcCode = sc.nextLine();
			if(funcCode.equals("1")){
				System.out.println("請輸入查詢項目: 1.電影   2.演員");
				String item = sc.nextLine();
				if(item.equals("1")){
					searchMovie();
				}
				else if (item.equals("2")){
					searchActor();
				}
			}
			if(funcCode.equals("2")){
				topTencustomer();
			}
			if(funcCode.equals("3")){
				addAll();
			}
			if(funcCode.equals("4")){
				manageMember();
			}
			if(funcCode.equals("5")){
				topTenMovie();
			}
			if(funcCode.equals("6")){
				exit = 0;
				connection.close();
			}

		}
	}
	
	public static void connectDatabase() throws Exception{
    	connection = DriverManager.getConnection(url, username, password);
    	smt =connection.createStatement();
        System.out.println("已連接!");
    }
	
	private static void searchMovie() throws Exception{
		System.out.println("若要列出所有電影請按1，若要由演員來查詢電影請按2，若要由導演來查詢電影請按3，若要由年份來查詢電影請按4");
		String sCode = sc.nextLine();
		if(sCode.equals("1")){
			ResultSet rs =smt.executeQuery("SELECT * FROM movie");
	        while(rs.next()){
	              String s = "電影名稱：" + rs.getString("movieName") + ",  上映時間："+rs.getString("debut");
	              System.out.println(s);
	        }
	        if (rs.next() == false) {
	        	System.out.println("查無資料");
	        }
		}
		else if(sCode.equals("2")){
			System.out.println("請輸入演員名稱");
			String actName = sc.nextLine();
			System.out.println("請輸入年份範圍(ex:2005~2015 請用'~'符號隔開年份)");
			String movieYear = sc.nextLine();
			String [] year = movieYear.split("~");
			int beginYear = Integer.parseInt(year[0]);
			int endYear = Integer.parseInt(year[1]);
			ResultSet rs =smt.executeQuery("SELECT * FROM actor Natural join perform Natural join movie where actName='"+actName+"'"+" and "+beginYear+"<=debut and debut<="+endYear);
	        while(rs.next()){
	              String s = "電影名稱：" + rs.getString("movieName") +",上映時間："+rs.getString("debut");
	              System.out.println(s);
	        }
	        if (rs.next() == false) {
	        	System.out.println("已查無資料");
	        }
		}
		
		else if(sCode.equals("3")){
			System.out.println("請輸入導演名稱");
			String dirName = sc.nextLine();
			ResultSet rs =smt.executeQuery("SELECT * FROM director Natural join movie where dirName='"+dirName+"'");
	        while(rs.next()){
	              String s = "電影名稱：" + rs.getString("movieName") + ",類型：" + rs.getString("category") +",上映時間："+rs.getString("debut");
	              System.out.println(s);
	        }
	        if (rs.next() == false) {
	        	System.out.println("查無資料");
	        }
		}
		else if(sCode.equals("4")){
			System.out.println("請輸入年份");
			String year = sc.nextLine();
			ResultSet rs =smt.executeQuery("SELECT * FROM movie where debut='"+year+"'");
	        while(rs.next()){
	              String s = "電影名稱：" + rs.getString("movieName") +",上映時間："+rs.getString("debut");
	              System.out.println(s);
	        }
	        if (rs.next() == false) {
	        	System.out.println("查無資料");
	        }
		}
	}
	
	private static void searchActor() throws Exception{
        ResultSet rs =smt.executeQuery("SELECT * FROM actor");
        while(rs.next()){
              String s = "演員名稱：" + rs.getString("actName") + ",演員生日：" + rs.getString("actBirth")+",演員性別："+rs.getString("actSex");
              System.out.println(s);
        }
        if (rs.next() == false) {
        	System.out.println("查無資料");
        }
	}
	
	private static void addAll() throws Exception{
		// 新增演員
		System.out.println("請輸入欲新增項目 1.演員   2.電影    3.會員 ");
		String item = sc.nextLine();
		if(item.equals("1")){
			System.out.println("請輸入演員名稱：");
			String actName = sc.nextLine();
			System.out.println("請輸入演員出生日期：");
			String actBirth = sc.nextLine();
			System.out.println("請輸入演員性別(只要輸入M或是F (分別代表male/female)");
			String actSex = sc.nextLine();
			ResultSet rs =smt.executeQuery("SELECT count(actID) FROM actor");
			rs.last();
			int number = rs.getInt(1)+1;
			PreparedStatement pSmt = connection.prepareStatement("insert into actor(actID,actName,actBirth,actSex) values (?,?,?,?)");
			pSmt.setString(1,Integer.toString(number));
			pSmt.setString(2, actName);
			pSmt.setString(3, actBirth);
			pSmt.setString(4, actSex);
			pSmt.executeUpdate();

			ResultSet rs1 = smt.executeQuery("SELECT * FROM actor where actName='"+actName+"'");
			while(rs1.next()){
				String s = "已新增   演員名稱：" + rs.getString("actName") +",  出生日期："+rs.getString("actBirth")+",  演員性別："+rs.getString("actSex");
				System.out.println(s);
			}
		}
		// 新增電影
		else if(item.equals("2")){
			System.out.println("請輸入電影名稱：");
			String movie = sc.nextLine();
			System.out.println("請輸入電影分類：");
			String cate = sc.nextLine();
			System.out.println("請輸入出版日期：");
			int debut = sc.nextInt();
			System.out.println("請輸入下載費用：");
			String cost = sc.nextLine();
			System.out.println("請輸入本片導演");
			String dir = sc.nextLine();
			System.out.println("請輸入出版商：");
			String publish = sc.nextLine();
			
			PreparedStatement pSmt = connection.prepareStatement("insert into movie(movieName,debut,cost,publish) values(?,?,?,?)");
			pSmt.setString(1,movie);
			pSmt.setInt(2,debut);
			pSmt.setString(3,cost);
			pSmt.setString(4,publish);
			pSmt.executeUpdate();

			PreparedStatement pSmt2 = connection.prepareStatement("insert into genre(movieName,movieGenre) values(?,?)");
			pSmt2.setString(1, movie);
			pSmt2.setString(2,cate);
			pSmt2.executeUpdate();

		}
		else if(item.equals("3")){
			System.out.println("請輸入會員姓名");
			String custName = sc.nextLine();
			ResultSet rs =smt.executeQuery("SELECT * FROM customer");
			rs.last();
			int number = rs.getRow()+1;
			PreparedStatement pSmt = connection.prepareStatement("insert into customer(custID,custName,balance) values(?,?,?)");
			pSmt.setString(1,Integer.toString(number));
			pSmt.setString(2, custName);
			pSmt.setInt(3,0);
			pSmt.executeUpdate(); 
			//ResultSet rs2 = pSmt.executeQuery();
			ResultSet rs1 = smt.executeQuery("SELECT * FROM customer where custName='"+custName+"'");
			while(rs1.next()){
				String s = "已新增   會員名稱：" + rs.getString("custName") +",  餘額："+rs.getString("Balance");
				System.out.println(s);
			}
		}
	}
	
	private static void manageMember() throws SQLException{
		System.out.println("請選擇管理會員功能： 1.查看會員狀況  2.修改會員資料");
		String item = sc.nextLine();
		if(item.equals("1")){
			System.out.println("是否查看所有會員資料?(Y/N?)");
			String yn = sc.nextLine();
			if(yn.equals("Y")|yn.equals("y")){
				ResultSet rs = smt.executeQuery("SELECT * FROM customer");
				while(rs.next()){
					String s = "會員名稱：" + rs.getString("custName") +",  餘額："+rs.getString("Balance");
					System.out.println(s);
				}
			}
			else if (yn.equals("N")|yn.equals("n")){
				System.out.println("請輸入會員姓名");
				String name = sc.nextLine();
				ResultSet rs = smt.executeQuery("SELECT * FROM customer WHERE custName='"+name+"'");
				while(rs.next()){
					String s = "會員名稱：" + rs.getString("custName") +",  餘額："+rs.getString("Balance");
					System.out.println(s);
				}
			}
		}
		else if (item.equals("2")){
			
		}
	}
	
	private static void topTenMovie() throws SQLException{
		ResultSet rs = smt.executeQuery("SELECT * FROM download");
		movieMap = new HashMap<String, Integer>();
		while(rs.next()){
			String key = rs.getString("movieName");
			try{
				int value = movieMap.get(key)+1;
				movieMap.put(key, value);
			}
			catch(NullPointerException e){
				int value = 1;
				movieMap.put(key,value);
			}
		}
		String [] sort = new String[100000];
		int maxTime = 0;
		for (Object key : movieMap.keySet()) {
            	String akey = (String) key;
            	int time = movieMap.get(key);
            	if(sort[time] == null){
            		sort[time] = akey;
            	}
            	else{
            		sort[time] +="\n"+akey;
            	}
            	if (time>maxTime){
            		maxTime = time;
            	}
        }
		int i = 0;
		while(i<10||maxTime>0){
			try{
				String result = sort[maxTime]+"\n下載次數："+maxTime+"\n";
				if(sort[maxTime] ==null){
					continue;
				}
				else{
					System.out.println(result);
					i++;
					maxTime--;
				}
			}
			catch(ArrayIndexOutOfBoundsException e){
				break;
			}
		}	
	}
	
	private static void topTencustomer() throws SQLException{
		HashMap<String, Integer> conMap = new HashMap<String, Integer>();
		ResultSet rs = smt.executeQuery("SELECT * FROM customer ");
		while(rs.next()){
			String custName = rs.getString("custName");
			conMap.put(custName, 0);
		}
		ResultSet rs1 =smt.executeQuery("SELECT * FROM download Natural join customer Natural join movie ");
		while(rs1.next()){
			String custName = rs1.getString("custName");
			int consumption = rs1.getInt("cost");
			consumption = consumption + conMap.get(custName);
			conMap.put(custName,consumption);
		}
		Iterator<String> it = conMap.keySet().iterator();
		while(it.hasNext()){
			Object key =it.next();
			System.out.println(conMap.get(key)+" "+key);
		}
	}
}
