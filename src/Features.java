import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

public class Features {
	private static final String dbClassName = "org.mariadb.jdbc.Driver";
    private static final String url = "jdbc:mariadb://140.127.74.210:3306/410477010"; //(210)
    private static final String username = "410477010";
    private static final String password = "4t78n";
    private static Connection connection;
    private static Statement smt;
    private static Scanner sc;    

    
	public static void main(String [] args) throws Exception{
		connectDatabase();
		sc = new Scanner(System.in);
		int exit = 1;
		while (exit != 0) {
			System.out.println("請輸入功能代碼： 1.查詢    2.查看前10大會員   3.新增   4.管理會員    5.最新年度前10下載量電影     6.離開");
			String funcCode = sc.nextLine();
			if (funcCode.equals("1")) {
				System.out.println("請輸入查詢項目: 1.電影   2.演員    3.銷售狀況    4.會員愛好");
				String item = sc.nextLine();
				if (item.equals("1")) {
					searchMovie();
				}
				else if (item.equals("2")) {
					searchActor();
				}
				else if (item.equals("3")) {
					saleStatus();
				}
				else if (item.equals("4")) {
					memberStatus();
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
	
	// 連接資料庫
	public static void connectDatabase() throws Exception{
		System.out.println("連接資料庫...");
    	connection = DriverManager.getConnection(url, username, password);
    	smt =connection.createStatement();
        System.out.println("已連接!");
    }
	
	// 搜尋電影
	private static void searchMovie() throws Exception{
		System.out.println("若要列出所有電影請輸入1，若要由演員來查詢電影請輸入2，若要由導演來查詢電影請輸入3，若要由年份來查詢電影請輸入4");
		String sCode = sc.nextLine();
		
		// 列出所有電影
		if(sCode.equals("1")){
			int iCount = 1;
			ResultSet rs = smt.executeQuery("SELECT * FROM movie");
	        while(rs.next()){
	              String s = iCount + ". 電影名稱：" + rs.getString("movieName") 
	              					+ ", 上映時間：" + rs.getString("debut");
	              System.out.println(s);
	              iCount++;
	        }
		}
		
		// 輸入演員查詢電影
		else if(sCode.equals("2")){
			System.out.println("請輸入演員名稱");
			String actName = sc.nextLine();
			System.out.println("請輸入年份範圍(ex:2005~2015 請用'~'符號隔開年份)");
			String movieYear = sc.nextLine();
			String [] year = movieYear.split("~");
			int beginYear = Integer.parseInt(year[0]);
			int endYear = 0;
			try {
				endYear = Integer.parseInt(year[1]);
			}
			catch (Exception e){ 
				endYear = Integer.parseInt(year[0]);
			}
			if (beginYear > endYear) {
				int i = beginYear;
				beginYear = endYear;
				endYear = i;
			}
			int iCount = 1;
			ResultSet rs = smt.executeQuery("SELECT * FROM actor Natural join perform Natural join movie " 
										 + "WHERE actName = '"+actName+"' " + " and " + beginYear+"<=debut and debut<="+endYear);
	        while(rs.next()){
	              String s = iCount + ". 電影名稱：" + rs.getString("movieName") 
	              					+ ", 上映時間：" + rs.getString("debut");
	              System.out.println(s);
	              iCount++;
	        }
	        if (rs.first() == false) {
	        	System.out.println("查無資料");
	        }
		}
		
		// 輸入導演查詢電影
		// ***全部導演變成輸入的導演名稱***
		else if(sCode.equals("3")){
			System.out.println("請輸入導演名稱");
			String dirName = sc.nextLine();
			int iCount = 1;
			ResultSet rs =smt.executeQuery("SELECT * FROM director NATURAL JOIN movie "
										 + "WHERE dirName = '"+dirName+"'");
	        while(rs.next()){
	              String s = iCount + ". 電影名稱：" + rs.getString("movieName")
	              					+ ", 上映時間：" + rs.getString("debut")
	              					+ ", 導演:" + rs.getString("dirName");
	              System.out.println(s);
	              iCount++;
	        }
	        if (rs.first() == false) {
	        	System.out.println("查無資料");
	        }
		}
		
		// 由年份來查詢電影
		else if(sCode.equals("4")){
			System.out.println("請輸入年份");
			String year = sc.nextLine();
			int iCount = 1;
			ResultSet rs =smt.executeQuery("SELECT * FROM movie where debut='"+year+"'");
	        while(rs.next()){
	              String s = iCount + ". 電影名稱：" + rs.getString("movieName")
	              					+ ", 上映時間：" + rs.getString("debut");
	              System.out.println(s);
	              iCount++;
	        }
	        if (rs.first() == false) {
	        	System.out.println("查無資料");
	        }
		}
	}
	
	// 搜尋演員
	private static void searchActor() throws Exception{
		int i = 1;
        ResultSet rs =smt.executeQuery("SELECT * FROM actor");
        while(rs.next()){
              String s = i + ". 演員名稱：" + rs.getString("actName") 
              			   + ", 演員生日：" + rs.getString("actBirth")
              			   + ", 演員性別：" + rs.getString("actSex");
              System.out.println(s);
              i++;
        }
        if (rs.first() == false) {
        	System.out.println("查無資料");
        }
	}
	
	// 查看前10大顧客
	private static void topTencustomer() throws SQLException{
		HashMap<String, Integer> conMap = new HashMap<String, Integer>();
		ResultSet rs = smt.executeQuery("SELECT * FROM customer ");
		while(rs.next()){
			String custName = rs.getString("custName");
			conMap.put(custName, 0);
		}
		ResultSet rs1 =smt.executeQuery("SELECT * FROM download Natural join customer Natural join movie where download.customerID = customer.custID and download.movieName = movie.movieName");
		while(rs1.next()){
			String custName = rs1.getString("custName");
			int consumption = rs1.getInt("cost");
			consumption = consumption + conMap.get(custName);
			conMap.put(custName,consumption);
			//System.out.println(custName+":"+consumption);
		}
		Iterator<String> it = conMap.keySet().iterator();
		Comparator<Integer> keyComparator = new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				// TODO Auto-generated method stub
				return o2.compareTo(o1);
			}
	    };
		Map<Integer,String> temp = new TreeMap<Integer,String>(keyComparator);
		while(it.hasNext()){
			Object key =it.next();
			temp.put(conMap.get(key), (String) key);
			//System.out.println(key+" "+conMap.get(key));
		}
		int i = 0;
		for (Entry<Integer, String> entry : temp.entrySet()) {
	        System.out.println(entry.getValue() + "   已消費:" + entry.getKey());
	        i++;
	        if(i>9){
	        	break;
	        }
	    }
	}	
	
	// 新增功能
	private static void addAll() throws Exception{
		System.out.println("請輸入欲新增項目 ：1.演員   2.導演	3.電影    4.會員 ");
		String item = sc.nextLine();
		
		// 新增演員
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

			// 確認已新增新演員
			ResultSet rs1 = smt.executeQuery("SELECT * FROM actor where actName='"+actName+"'");
			while(rs1.next()){
				String s = "已新增   演員名稱：" + rs1.getString("actName") 
						 + ",  出生日期：" + rs1.getString("actBirth") 
						 + ",  演員性別：" + rs1.getString("actSex");
				System.out.println(s);
			}
		}
		
		// 新增導演
		else if(item.equals("2")){
			System.out.println("請輸入導演姓名:");
			String dirName = sc.nextLine();
			System.out.println("請輸入導演出生日期：");
			String dirBirth = sc.nextLine();
			ResultSet rs = smt.executeQuery("SELECT * FROM director");
			rs.last();
			int number = rs.getRow()+1;
			PreparedStatement pSmt = connection.prepareStatement("insert into director(dirID,dirName,dirBirth) values(?,?,?)");
			pSmt.setString(1, Integer.toString(number));
			pSmt.setString(2, dirName);
			pSmt.setString(3, dirBirth);
			pSmt.executeUpdate(); 
			
			// 確認已新增新導演
			ResultSet rs1 = smt.executeQuery("SELECT * FROM director WHERE dirName='"+dirName+"'");
			while(rs1.next()){
				String s = "已新增   導演姓名：" + rs1.getString("dirName") +",  生日："+rs1.getString("dirBirth");
				System.out.println(s);
			}
		}
		
		// 新增電影
		// ***新增有誤***
		else if(item.equals("3")){
			System.out.println("請輸入電影名稱：");
			String movie = sc.nextLine();
			System.out.println("請輸入電影分類：");
			String cate = sc.nextLine();
			System.out.println("請輸入出版年份：");
			String debut = sc.nextLine();
			System.out.println("請輸入下載費用：");
			String cost = sc.nextLine();
		    System.out.println("請輸入本片導演：");
			String dir = sc.nextLine();
			System.out.println("請輸入出版商：");
			String publish = sc.nextLine();
			
			// 新增movie table資料
			PreparedStatement pSmt = connection.prepareStatement("insert into movie(movieName,debut,cost,publish) values(?,?,?,?)");
			pSmt.setString(1, movie);
			pSmt.setInt(2, Integer.parseInt(debut));
			pSmt.setInt(3, Integer.parseInt(cost));
			pSmt.setString(4, publish);
			pSmt.executeUpdate();

			// 新增genre table資料
			PreparedStatement pSmt2 = connection.prepareStatement("insert into genre(movieName,movieGenre) values(?,?)");
			pSmt2.setString(1, movie);
			pSmt2.setString(2, cate);
			pSmt2.executeUpdate();
			
			// 新增direct table資料			
			String number = "dirID";
			ResultSet rs = smt.executeQuery("SELECT * FROM director");
			while(rs.next()){
				String sID = rs.getString("dirID");
				String sName = rs.getString("dirName");
				if (sName.equals(dir)) {
					number = sID;
				}
			}
			PreparedStatement pSmt3 = connection.prepareStatement("insert into direct(dirID,movieName) values(?,?)");
			pSmt3.setString(1, number);
			pSmt3.setString(2, movie);
			pSmt3.executeUpdate();
			
			// 確認已新增新電影
			ResultSet rs1 = smt.executeQuery("SELECT * FROM movie direct JOIN director "
										   + "WHERE movieName='"+movie+"' AND dirName='"+dir+"'");
			while(rs1.next()){
				String s = "已新增   電影名稱：" + rs1.getString("movieName") +",  導演："+rs1.getString("dirName");
				System.out.println(s);
			}
		}
		
		// 新增會員
		else if(item.equals("4")){
			System.out.println("請輸入會員姓名");
			String custName = sc.nextLine();
			ResultSet rs = smt.executeQuery("SELECT * FROM customer");
			rs.last();
			int number = rs.getRow()+1;
			PreparedStatement pSmt = connection.prepareStatement("insert into customer(custID,custName,balance) values(?,?,?)");
			pSmt.setString(1,Integer.toString(number));
			pSmt.setString(2, custName);
			pSmt.setInt(3,0);
			pSmt.executeUpdate(); 
			
			// 確認已新增新會員
			ResultSet rs1 = smt.executeQuery("SELECT * FROM customer WHERE custName='"+custName+"'");
			while(rs1.next()){
				String s = "已新增   會員名稱：" + rs1.getString("custName") +",  餘額："+rs1.getString("Balance");
				System.out.println(s);
			}
		}
	}
	
	// 管理會員
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
	
	// 最新年度前10下載量
	private static void topTenMovie() throws SQLException{
		ResultSet rs = smt.executeQuery("SELECT * FROM download");
		HashMap<String, Integer> movieMap = new HashMap<String, Integer>();
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
		Iterator<String> it = movieMap.keySet().iterator();
		Comparator<Integer> keyComparator = new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				// TODO Auto-generated method stub
				return o2.compareTo(o1);
			}
	    };
		Map<Integer,String> temp = new TreeMap<Integer,String>(keyComparator);
		while(it.hasNext()){
			Object key =it.next();
			Integer value = movieMap.get(key);
			if(temp.get(value) == null){
				temp.put(value, (String) key);
			}
			else{
				String tempS = temp.get(value) +","+ (String) key;
				temp.put(value,tempS);
			}
		}
		int topTen = 0;
		for (Entry<Integer, String> entry : temp.entrySet()) {
	        //System.out.println(entry.getValue() + "   下載次數:" + entry.getKey());
	        int downloadTime = entry.getKey();
	        String sameMovie = entry.getValue();
	        String [] splitSame = sameMovie.split(",");
	        for(int i=0;i<splitSame.length;i++){
	        	System.out.println(splitSame[i]+"   下載次數:"+downloadTime);
	        	topTen++;
	        	if(topTen>9){
		        	break;
		        }
	        }
	        if(topTen>9){
	        	break;
	        }
	    }
			
	}
	
	private static void saleStatus() throws SQLException{
		System.out.println("將會列出輸入的電影類型銷售狀況");
		System.out.println("請輸入電影類型:");
		String type = sc.nextLine();
		String search = "SELECT * FROM genre Natural join download where genre.movieName = download.movieName and movieGenre = '"+type+"'";
		int[] month = new int[13];
		int max=0 ,maxMonth=0;
		ResultSet rs = smt.executeQuery(search);
		while(rs.next()){
			int dMonth = Integer.parseInt(rs.getString("downloadMonth"));
			month[dMonth] +=1;
		}
		for(int i=0 ;i<month.length;i++){
			if(max<month[i]){
				max = month[i];
				maxMonth = i;
			}
			else{continue;}
		}
		System.out.println("電影類型:"+type+" 在"+maxMonth+"月銷售量最高，共"+max+"次。");
	}
	
	private static void memberStatus() throws SQLException{
		System.out.println("請輸入欲查看之會員名稱:");
		String memberName = sc.nextLine();
		System.out.println("請選擇查看項目: 1.最愛男演員   2.最愛女演員   3.最愛電影類型");
		String item = sc.nextLine();
		if(item.equals("1")){
			HashMap<String, Integer> actMap = new HashMap<String, Integer>();
			ResultSet rs = smt.executeQuery("SELECT * FROM download Natural join customer Natural join perform Natural join actor WHERE download.customerID=customer.custID and download.movieName=perform.movieName and perform.actID=actor.actID and actSex='M' and custName='"+memberName+"'");
			while(rs.next()){
				String actName = rs.getString("actName");
				Integer dTime = actMap.get(actName);
				if(dTime == null){
					dTime = 1;
					actMap.put(actName, dTime);
				}
				else{
					dTime +=1;
					actMap.put(actName, dTime);
				}
			}
			int maxTime = 0;
			String favorite ="";
			for (Entry<String, Integer> entry : actMap.entrySet()) {
		        //System.out.println(entry.getValue() + "   下載次數:" + entry.getKey());
		        int tempTime = entry.getValue();
		        if(maxTime<tempTime){
		        	favorite = entry.getKey();
		        	maxTime = tempTime;
		        }
		        else if (maxTime==tempTime){
		        	favorite = favorite+" and "+entry.getKey();
		        }
		    }
			System.out.println(memberName+"最喜歡的男演員:"+favorite);
		}
		else if (item.equals("2")){
			HashMap<String, Integer> actMap = new HashMap<String, Integer>();
			ResultSet rs = smt.executeQuery("SELECT * FROM download Natural join customer Natural join perform Natural join actor WHERE download.customerID=customer.custID and download.movieName=perform.movieName and perform.actID=actor.actID and actSex='F' and custName='"+memberName+"'");
			while(rs.next()){
				String actName = rs.getString("actName");
				Integer dTime = actMap.get(actName);
				if(dTime == null){
					dTime = 1;
					actMap.put(actName, dTime);
				}
				else{
					dTime +=1;
					actMap.put(actName, dTime);
				}
			}
			int maxTime = 0;
			String favorite ="";
			for (Entry<String, Integer> entry : actMap.entrySet()) {
		        //System.out.println(entry.getValue() + "   下載次數:" + entry.getKey());
		        int tempTime = entry.getValue();
		        if(maxTime<tempTime){
		        	favorite = entry.getKey();
		        	maxTime = tempTime;
		        }
		        else if (maxTime==tempTime){
		        	favorite = favorite+" and "+entry.getKey();
		        }
		    }
			System.out.println(memberName+"最喜歡的女演員:"+favorite);
		}
		else if (item.equals("3")){
			HashMap<String, Integer> movieMap = new HashMap<String, Integer>();
			ResultSet rs = smt.executeQuery("SELECT * FROM download Natural join customer Natural join genre WHERE download.customerID=customer.custID and genre.movieName=download.movieName and custName='"+memberName+"'");
			while(rs.next()){
				String mGenre = rs.getString("movieGenre");
				Integer dTime = movieMap.get(mGenre);
				if(dTime == null){
					dTime = 1;
					movieMap.put(mGenre, dTime);
				}
				else{
					dTime = dTime+1;
					movieMap.put(mGenre,dTime);
				}
			}
			int maxTime = 0;
			String favorite ="";
			for (Entry<String, Integer> entry : movieMap.entrySet()) {
		        //System.out.println(entry.getValue() + "   下載次數:" + entry.getKey());
		        int tempTime = entry.getValue();
		        if(maxTime<tempTime){
		        	favorite = entry.getKey();
		        	maxTime = tempTime;
		        }
		        else if (maxTime==tempTime){
		        	favorite = favorite+" and "+entry.getKey();
		        }
		    }
			System.out.println(memberName+"最喜歡的類型:"+favorite+",共下載"+maxTime+"次。");
		}	
	}
}